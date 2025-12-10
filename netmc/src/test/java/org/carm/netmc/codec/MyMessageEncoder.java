package org.carm.netmc.codec;

import org.carm.netmc.model.MyHeader;
import org.carm.netmc.model.MyMessage;
import org.carm.netmc.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class MyMessageEncoder implements MessageEncoder<MyMessage> {

    @Override
    public ByteBuf encode(MyMessage message, Session session) {
        MyHeader header = message.getHeader();

        StringBuilder msg = new StringBuilder();
        msg.append(header.getClientId()).append(',');
        msg.append(header.getType()).append(',');
        msg.append(header.getSerialNo()).append(';');
        msg.append(message.getBody());

        byte[] bytes = msg.toString().getBytes(StandardCharsets.UTF_8);
        return Unpooled.wrappedBuffer(bytes);
    }
}