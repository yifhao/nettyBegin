package netty.Echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by haoyifen on 2016/1/15 0015.
 */
public class NettyClient {

    @Test
    public void testClient() throws UnknownHostException, InterruptedException {
        List<ChannelHandler> handlers = Stream.of(new Handler(), new DecoderHandler()).collect(Collectors.toList());
        bootStrap(8081, handlers, InetAddress.getLocalHost());
    }

    public void bootStrap(int port, List<ChannelHandler> channelHandlers, InetAddress address) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).handler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(channelHandlers.get(0)).addLast(channelHandlers.get(1));
                        }
                    });
            ChannelFuture connectSyncFuture = bootstrap.connect(address, port).sync();
            connectSyncFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}


class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buf = (ByteBuf) msg;
            long currentTimeMillis = buf.readLong();
            System.out.println(new Date(currentTimeMillis));
            System.out.println("success");
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

class DecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }
        out.add(in.readBytes(8));
    }
}