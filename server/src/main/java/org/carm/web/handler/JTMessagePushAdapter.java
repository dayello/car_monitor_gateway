package org.carm.web.handler;

import org.carm.netmc.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.carm.commons.spring.SSEService;
import org.carm.commons.util.JsonUtils;
import org.carm.protocol.basics.JTMessage;
import org.carm.protocol.codec.JTMessageAdapter;
import org.carm.protocol.codec.JTMessageDecoder;
import org.carm.protocol.codec.JTMessageEncoder;
import org.carm.protocol.commons.JT808;
import org.carm.protocol.commons.MessageId;
import org.carm.web.service.PulsarService;

import java.util.HashSet;

@Slf4j
public class JTMessagePushAdapter extends JTMessageAdapter {

    private final SSEService sseService;
    private final PulsarService pulsarService;
    private static final HashSet<Integer> ignoreMsgs = new HashSet<>();

    static {
        ignoreMsgs.add(JT808.平台通用应答);
        ignoreMsgs.add(JT808.定位数据批量上传);
    }

    public JTMessagePushAdapter(JTMessageEncoder messageEncoder, JTMessageDecoder messageDecoder, SSEService sseService) {
        super(messageEncoder, messageDecoder);
        this.sseService = sseService;
        this.pulsarService = null;
    }

    public JTMessagePushAdapter(JTMessageEncoder messageEncoder, JTMessageDecoder messageDecoder, SSEService sseService, PulsarService pulsarService) {
        super(messageEncoder, messageDecoder);
        this.sseService = sseService;
        this.pulsarService = pulsarService;
    }

    @Override
    public void encodeLog(Session session, JTMessage message, ByteBuf output) {
        int messageId = message.getMessageId();
        String hex = ByteBufUtil.hexDump(output, 0, output.writerIndex());
        String data = MessageId.getName(messageId) + JsonUtils.toJson(message) + ",hex:" + hex;
        sseService.send(message.getClientId(), data);
        if (!ignoreMsgs.contains(messageId))
            log.info("{}\n>>>>>-{}", session, data);

        if (pulsarService != null) {
            String sessionInfo = session == null ? "" : session.toString();
            pulsarService.publishDown(message, hex, sessionInfo);
        }
    }

    @Override
    public void decodeLog(Session session, JTMessage message, ByteBuf input) {
        if (message != null) {
            int messageId = message.getMessageId();
            String hex = ByteBufUtil.hexDump(input, 0, input.writerIndex());
            String data = MessageId.getName(messageId) + JsonUtils.toJson(message) + ",hex:" + hex;
            sseService.send(message.getClientId(), data);
            if (!ignoreMsgs.contains(messageId))
                log.info("{}\n<<<<<-{}", session, data);

            if (!message.isVerified())
                log.error("<<<<<校验码错误session={},payload={}", session, data);

            if (pulsarService != null) {
                String sessionInfo = session == null ? "" : session.toString();
                pulsarService.publishUp(message, hex, sessionInfo);
            }
        }
    }

    public static void clearMessage() {
        synchronized (ignoreMsgs) {
            ignoreMsgs.clear();
        }
    }

    public static void addMessage(int messageId) {
        if (!ignoreMsgs.contains(messageId)) {
            synchronized (ignoreMsgs) {
                ignoreMsgs.add(messageId);
            }
        }
    }

    public static void removeMessage(int messageId) {
        if (ignoreMsgs.contains(messageId)) {
            synchronized (ignoreMsgs) {
                ignoreMsgs.remove(messageId);
            }
        }
    }
}