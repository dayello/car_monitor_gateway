package org.carm.protocol.codec;

import org.carm.netmc.codec.MessageDecoder;
import org.carm.netmc.codec.MessageEncoder;
import org.carm.netmc.session.Session;
import io.github.yezhihao.protostar.SchemaManager;
import io.github.yezhihao.protostar.util.Explain;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.carm.protocol.basics.JTMessage;

/**
 * JT消息编解码适配器
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
@Slf4j
public class JTMessageAdapter implements MessageEncoder<JTMessage>, MessageDecoder<JTMessage> {

    private final JTMessageEncoder messageEncoder;

    private final JTMessageDecoder messageDecoder;

    public JTMessageAdapter(String... basePackages) {
        this(new SchemaManager(basePackages));
    }

    public JTMessageAdapter(SchemaManager schemaManager) {
        this(new JTMessageEncoder(schemaManager), new MultiPacketDecoder(schemaManager, new MultiPacketListener(20)));
    }

    public JTMessageAdapter(JTMessageEncoder messageEncoder, JTMessageDecoder messageDecoder) {
        this.messageEncoder = messageEncoder;
        this.messageDecoder = messageDecoder;
    }

    public ByteBuf encode(JTMessage message, Explain explain) {
        return messageEncoder.encode(message, explain);
    }

    public JTMessage decode(ByteBuf input, Explain explain) {
        return messageDecoder.decode(input, explain);
    }

    public ByteBuf encode(JTMessage message) {
        return messageEncoder.encode(message);
    }

    public JTMessage decode(ByteBuf input) {
        return messageDecoder.decode(input);
    }

    @Override
    public ByteBuf encode(JTMessage message, Session session) {
        ByteBuf output = messageEncoder.encode(message);
        encodeLog(session, message, output);
        return output;
    }

    @Override
    public JTMessage decode(ByteBuf input, Session session) {
        // 先记录原始消息，确保即使解码失败也能看到消息内容
        if (log.isInfoEnabled())
            log.info("{}\n<<<<<-原始消息,hex[{}]", session, ByteBufUtil.hexDump(input, 0, input.writerIndex()));
        
        try {
            JTMessage message = messageDecoder.decode(input);
            if (message != null)
                message.setSession(session);
            decodeLog(session, message, input);
            return message;
        } catch (Exception e) {
            // 捕获解码异常并记录
            log.error("{}\n<<<<<-解码失败,hex[{}],错误信息:{}", session, 
                    ByteBufUtil.hexDump(input, 0, input.writerIndex()), e.getMessage(), e);
            return null;
        }
    }

    public void encodeLog(Session session, JTMessage message, ByteBuf output) {
        if (log.isInfoEnabled())
            log.info("{}\n>>>>>-{},hex[{}]", session, message, ByteBufUtil.hexDump(output));
    }

    public void decodeLog(Session session, JTMessage message, ByteBuf input) {
        if (log.isInfoEnabled())
            log.info("{}\n<<<<<-{},hex[{}]", session, message, ByteBufUtil.hexDump(input, 0, input.writerIndex()));
    }
}