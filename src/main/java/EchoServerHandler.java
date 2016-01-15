import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * Created by haoyifen on 2016/1/14 0014.
 */

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter{
    Logger logger= LogManager.getLogger("EchoServerHandler");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("new client"+ctx.channel().remoteAddress().toString());
        final ByteBuf time = ctx.alloc().buffer(8);
        time.writeLong(System.currentTimeMillis());
        final ChannelFuture writeFuture = ctx.writeAndFlush(time);
        writeFuture.addListener(future ->ctx.close());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.writeAndFlush(msg);
    }
}