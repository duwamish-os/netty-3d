import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public ChannelFuture run() throws InterruptedException {
        //a multithreaded event loop that handles I/O operation
        var bossGroup = new NioEventLoopGroup();

        //accepts an incoming connection
        var workerGroup = new NioEventLoopGroup();

        //NioServerSocketChannel used to instantiate a new Channel
        // to accept incoming connections
        var b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ServerRequestHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        var f = b.bind(9090).sync();

        return f.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().run();
    }
}
