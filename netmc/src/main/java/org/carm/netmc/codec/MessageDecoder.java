package org.carm.netmc.codec;

import org.carm.netmc.core.model.Message;
import org.carm.netmc.session.Session;
import io.netty.buffer.ByteBuf;

/**
 * 基础消息解码
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public interface MessageDecoder<T extends Message> {

    T decode(ByteBuf buf, Session session);

}