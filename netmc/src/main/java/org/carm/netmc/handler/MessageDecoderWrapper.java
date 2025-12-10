package org.carm.netmc.handler;

import org.carm.netmc.codec.MessageDecoder;
import org.carm.netmc.core.model.Message;
import org.carm.netmc.session.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础消息解码
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
@ChannelHandler.Sharable
public class MessageDecoderWrapper extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MessageDecoderWrapper.class);

    private final MessageDecoder decoder;

    public MessageDecoderWrapper(MessageDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        ByteBuf input = packet.take();
        try {
            Message message = decoder.decode(input, packet.session);
            if (message != null)
                ctx.fireChannelRead(packet.replace(message));
            input.skipBytes(input.readableBytes());
        } catch (Exception e) {
            log.error("消息解码异常[" + ByteBufUtil.hexDump(input, 0, input.writerIndex()) + "]", e);
            throw new DecoderException(e);
        } finally {
            input.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info("一啛啛喳喳惆怅长岑长："+cause.getMessage());
    }
}