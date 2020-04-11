package com.netty.microservice;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("ChannelHandlerContext: " + ctx);
        var response = ctx.alloc().buffer(8); //64 bit int
        response.writeLong(System.currentTimeMillis());

        var channelTask = ctx.writeAndFlush(response);

        channelTask.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) {
                ctx.close();
            }
        });

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
