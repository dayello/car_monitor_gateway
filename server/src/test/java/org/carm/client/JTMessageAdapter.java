package org.carm.client;

import org.carm.netmc.codec.MessageDecoder;
import org.carm.netmc.codec.MessageEncoder;
import org.carm.netmc.session.Session;
import io.netty.buffer.ByteBuf;
import org.carm.protocol.basics.JTMessage;
import org.carm.protocol.codec.JTMessageDecoder;
import org.carm.protocol.codec.JTMessageEncoder;

/**
 * JT消息编解码适配器
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public class JTMessageAdapter implements MessageEncoder<JTMessage>, MessageDecoder<JTMessage> {

    private JTMessageEncoder messageEncoder;

    private JTMessageDecoder messageDecoder;

    public JTMessageAdapter(JTMessageEncoder messageEncoder, JTMessageDecoder messageDecoder) {
        this.messageEncoder = messageEncoder;
        this.messageDecoder = messageDecoder;
    }

    public ByteBuf encode(JTMessage message, Session session) {
        ByteBuf output = messageEncoder.encode(message);
        return output;
    }

    @Override
    public JTMessage decode(ByteBuf input, Session session) {
        JTMessage message = messageDecoder.decode(input);
        return message;
    }
}
