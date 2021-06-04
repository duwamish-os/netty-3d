package com.netty.microservice.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HeartbeatServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final ObjectMapper encoder = new ObjectMapper();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            FullHttpResponse httpResponse = null;

            try {
                byte[] responseBytes = encoder.writeValueAsBytes(new HeartbeatResponse(
                        "netty-microservice",
                        "1.0"
                ));

                httpResponse = new DefaultFullHttpResponse(
                        req.protocolVersion(),
                        OK,
                        Unpooled.wrappedBuffer(responseBytes)
                );
//                System.out.println("responding response: ");
//                System.out.println("responding response: " + new String(responseBytes));
            } catch (JsonProcessingException e) {

                httpResponse = new DefaultFullHttpResponse(
                        req.protocolVersion(),
                        INTERNAL_SERVER_ERROR,
                        Unpooled.wrappedBuffer(("{\"error\": " + e.getMessage() + "}").getBytes())
                );

            }

            boolean keepAlive = HttpUtil.isKeepAlive(req);

            httpResponse.headers()
                    .set(CONTENT_TYPE, APPLICATION_JSON)
                    .setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());

            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
                }
            } else {
                // Tell the client we're going to close the connection.
                httpResponse.headers().set(CONNECTION, CLOSE);
            }

            ChannelFuture f = ctx.write(httpResponse);

            if (!keepAlive) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
