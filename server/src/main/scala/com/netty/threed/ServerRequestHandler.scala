package com.netty.threed

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}

class ServerRequestHandler extends ChannelInboundHandlerAdapter {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    val response = ctx.alloc().buffer(8) //64 bit int
    response.writeLong(System.currentTimeMillis())

    val channelTask = ctx.writeAndFlush(response)

    channelTask.addListener(new ChannelFutureListener {
      override def operationComplete(future: ChannelFuture): Unit = {
        println(s"futureTask: $future")
        ctx.close()
      }
    })

  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
