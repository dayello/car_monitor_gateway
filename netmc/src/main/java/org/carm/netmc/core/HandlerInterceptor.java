package org.carm.netmc.core;

import org.carm.netmc.core.model.Message;
import org.carm.netmc.session.Session;

/**
 * 消息拦截器
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public interface HandlerInterceptor<T extends Message> {
    /** @return Response 未找到对应的Handle */
    T notSupported(T request, Session session);

    /** @return boolean 调用之前 */
    boolean beforeHandle(T request, Session session);

    /** @return Response 调用之后，返回值为void的 */
    T successful(T request, Session session);

    /** 调用之后，有返回值的 */
    void afterHandle(T request, T response, Session session);

    /** @return Response 调用之后抛出异常的 */
    T exceptional(T request, Session session, Throwable e);
}