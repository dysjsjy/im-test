package com.dysjsjy;

import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.dysjsjy.model.entity.Message;
import com.dysjsjy.model.entity.User;
import com.dysjsjy.model.enums.MessageEnum;
import com.dysjsjy.utils.DateTimeUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {

    // 当前用户
    private static User currentUser;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Channel channel = connectToServer();
        if (channel != null) {
            showOperationMenu(channel);
        }
    }

    private static Channel connectToServer() {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChatClientHandler());
                    }
                });

        try {
            ChannelFuture connect = bootstrap.connect("localhost", 8080).sync();
            System.out.println("你好我是客户端，连接成功");
            return connect.channel();
        } catch (Exception e) {
            System.out.println("你好我是客户端，连接失败: " + e.getMessage());
            return null;
        }
    }

    private static void showOperationMenu(Channel channel) {
        while (true) {
            printMenuOptions();
            int choice = getMenuChoice();

            if (choice == 0) {
                closeChannel(channel);
                break;
            }
            handleMenuChoice(choice, channel);
        }
    }

    public static void login(Channel channel) {
        if (currentUser != null) {
            System.out.println("你已经登录了");
            return;
        }

        System.out.println("请输入用户Id");
        String userId = scanner.nextLine();
        System.out.println("请输入用户名:");
        String userName = scanner.nextLine();
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);

        Message message = new Message();
        message.setType(MessageEnum.LOGIN);
        message.setUserId(userId);
        message.setDatetime(DateTimeUtil.getCurrentDateTime());
        channel.writeAndFlush(JSON.toJSONString(message));

        currentUser = user;
    }

    public static void logout(Channel channel) {
        if (currentUser == null) {
            System.out.println("你还没有登录");
            return;
        }

        Message message = new Message();
        message.setType(MessageEnum.LOGOUT);
        message.setUserId(currentUser.getUserId());
        message.setRoomId(currentUser.getRoomId());
        message.setDatetime(DateTimeUtil.getCurrentDateTime());
        channel.writeAndFlush(JSON.toJSONString(message));
        currentUser = null;
    }

    public static void createRoom(Channel channel) {
        System.out.println("请输入房间Id:");
        String roomId = scanner.nextLine();
        Message message = new Message();
        message.setType(MessageEnum.CREATE_ROOM);
        message.setRoomId(roomId);
        message.setDatetime(DateTimeUtil.getCurrentDateTime());
        channel.writeAndFlush(JSON.toJSONString(message));
    }

    public static void joinRoom(Channel channel) {
        System.out.println("请输入房间id:");
        String roomId = scanner.nextLine();
        Message message = new Message();
        message.setType(MessageEnum.JOIN_ROOM);
        message.setRoomId(roomId);
        message.setDatetime(DateTimeUtil.getCurrentDateTime());
        channel.writeAndFlush(JSON.toJSONString(message));

        if (currentUser != null) {
            currentUser.setRoomId(roomId);
        }
    }

    public static void sendMessage(Channel channel) {
        Message message = new Message();
        message.setType(MessageEnum.SEND_MESSAGE);
        message.setUserId(currentUser.getUserId()); // 确保设置用户ID
        message.setRoomId(currentUser.getRoomId());

        System.out.println("请输入消息内容:");
        String content = scanner.nextLine();
        message.setContent(content);
        message.setDatetime(DateTimeUtil.getCurrentDateTime());
        channel.writeAndFlush(JSON.toJSONString(message));

    }

    private static void printMenuOptions() {
        System.out.println("请选择操作:");
        System.out.println("1. 登录(LOGIN)");
        System.out.println("2. 创建房间(CREATE_ROOM)");
        System.out.println("3. 加入房间(JOIN_ROOM)");
        System.out.println("4. 发送消息(SEND_MESSAGE)");
        System.out.println("5. 登出(LOGOUT)");
        System.out.println("0. 退出(EXIT)");
        System.out.print("请输入选项数字: ");
    }

    private static int getMenuChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // 添加这行消费掉换行符
            return choice;
        } catch (Exception e) { // 改为捕获更通用的Exception
            System.out.println("无效的输入，请输入数字！");
            scanner.nextLine(); // 消费掉无效输入
            return -1;
        }
    }

    private static void handleMenuChoice(int choice, Channel channel) {
        switch (choice) {
            case 1:
                login(channel);
                break;
            case 2:
                createRoom(channel);
                break;
            case 3:
                joinRoom(channel);
                break;
            case 4:
                sendMessage(channel);
                break;
            case 5:
                logout(channel);
                closeChannel(channel);
                break;
            default:
                System.out.println("无效的命令类型！");
        }
    }

    private static void closeChannel(Channel channel) {
        try {
            scanner.close();
            channel.close().sync();
        } catch (InterruptedException e) {
            System.err.println("关闭通道时出错: " + e.getMessage());
        }
    }
}
