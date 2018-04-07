package com.netty.threed

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil

class ServerRequestHandler extends ChannelInboundHandlerAdapter {

  override def channelRead(ctx: ChannelHandlerContext, event: scala.Any): Unit = {
    event match {
      case m: ByteBuf =>
        try {
          while (m.isReadable) {
            println(m.readByte())
          }
        } finally {
          ReferenceCountUtil.release(event)
        }
      case _ => println("oops")
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
