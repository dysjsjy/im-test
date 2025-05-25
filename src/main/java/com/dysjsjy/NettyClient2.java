package com.dysjsjy;

import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient2 {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                });

        ChannelFuture connect = bootstrap.connect("localhost", 8080);

        connect.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("客户端2，连接成功");
                // 这里尝试发送消息
                sendMessage(connect);

            } else {
                System.out.println("客户端2，连接失败");
            }
        });
    }

        // 这里写个方法，让客户端1可以一直与服务端保持连接，并接收用户输入的消息，然后发送给服务端
        public static void sendMessage(ChannelFuture connect) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                connect.channel().writeAndFlush(message);
                
                if (message.equals("exit")) {
                    break;
                }
            }
            scanner.close();
        }
}
