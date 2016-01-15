import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by haoyifen on 2015/12/26.
 */
public class NettyServer {
    private static int port = 8081;
    private static ChannelHandler serverHandler = null;
    private Logger logger;

    public static void main(String[] args) {
//        serverBootStrap(port, discardServerHandler);
    }

    @Before
    public void setUp() throws Exception {
        logger= LogManager.getLogger("NettyServer");
        logger.debug("set up");
    }

    @Test
    public void testTimeEcho() {
        serverHandler = new EchoServerHandler();
        serverBootStrap(port, serverHandler);
    }

    private  void serverBootStrap(int port, ChannelHandler handler) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup);
//        ChannelHandler handler = channelHandler.newInstance();
        serverBootstrap.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handler);
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture bindFuture = serverBootstrap.bind(port).sync();
            logger.debug("start on:"+port);
            bindFuture.channel().closeFuture().sync();
            logger.debug("closed");
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}