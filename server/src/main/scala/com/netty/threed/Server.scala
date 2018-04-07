package com.netty.threed

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class Server {

  val Port = 8080

  def run: ChannelFuture = {
    //a multithreaded event loop that handles I/O operation
    val bossGroup = new NioEventLoopGroup()

    //accepts an incoming connection
    val workerGroup = new NioEventLoopGroup()

    //NioServerSocketChannel used to instantiate a new Channel to accept incoming connections
    val b = new ServerBootstrap()
    b.group(bossGroup, workerGroup)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new ChannelInitializer[SocketChannel]() {

        override def initChannel(ch: SocketChannel) {
          ch.pipeline().addLast(new ServerRequestHandler())
        }
      })
      .option[Integer](ChannelOption.SO_BACKLOG, 128)
      .childOption[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)

    val f = b.bind(Port).sync()

    f.channel().closeFuture().sync()

  }
}

object Server {
  def main(args: Array[String]): Unit = {
    new Server().run
  }
}
