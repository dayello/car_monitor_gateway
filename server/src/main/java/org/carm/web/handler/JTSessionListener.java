package org.carm.web.handler;

import org.carm.netmc.core.model.Message;
import org.carm.netmc.session.Session;
import org.carm.netmc.session.SessionListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;
import org.carm.protocol.basics.JTMessage;
import org.carm.web.model.entity.DeviceDO;
import org.carm.web.model.enums.SessionKey;

import java.util.function.BiConsumer;

/**
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
@Component
public class JTSessionListener implements SessionListener {

    /**
     * 下行消息拦截器
     */
    private static final BiConsumer<Session, Message> requestInterceptor = (session, message) -> {
        JTMessage request = (JTMessage) message;
        request.setClientId(session.getClientId());
        request.setSerialNo(session.nextSerialNo());

        if (request.getMessageId() == 0) {
            request.setMessageId(request.reflectMessageId());
        }

        DeviceDO device = session.getAttribute(SessionKey.Device);
        if (device != null) {
            int protocolVersion = device.getProtocolVersion();
            if (protocolVersion > 0) {
                request.setVersion(true);
                request.setProtocolVersion(protocolVersion);
            }
        }
    };

    /**
     * 设备连接
     */
    @Override
    public void sessionCreated(Session session) {
        session.requestInterceptor(requestInterceptor);
    }

    /**
     * 设备注册
     */
    @Override
    public void sessionRegistered(Session session) {
        // 保存映射：clientId -> 网关实例（ip:port）
        try {
            String instance = session.getLocalAddressStr();
            String clientKey = "jt:gateway:client:" + session.getClientId();
            String sessionKey = "jt:gateway:session:" + session.getId();
            redis.opsForValue().set(clientKey, instance);
            redis.opsForValue().set(sessionKey, instance);
        } catch (Exception ignored) {
        }
    }

    /**
     * 设备离线
     */
    @Override
    public void sessionDestroyed(Session session) {
        try {
            String clientKey = "jt:gateway:client:" + session.getClientId();
            String sessionKey = "jt:gateway:session:" + session.getId();
            redis.delete(clientKey);
            redis.delete(sessionKey);
        } catch (Exception ignored) {
        }
    }

    @Resource
    private StringRedisTemplate redis;
}