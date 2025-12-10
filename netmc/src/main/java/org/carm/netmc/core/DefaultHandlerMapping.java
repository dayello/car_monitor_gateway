package org.carm.netmc.core;

import org.carm.netmc.core.annotation.Endpoint;
import org.carm.netmc.util.ClassUtils;

import java.util.List;

/**
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public class DefaultHandlerMapping extends AbstractHandlerMapping {

    public DefaultHandlerMapping(String endpointPackage) {
        List<Class> endpointClasses = ClassUtils.getClassList(endpointPackage, Endpoint.class);

        for (Class endpointClass : endpointClasses) {
            try {
                Object bean = endpointClass.getDeclaredConstructor((Class[]) null).newInstance((Object[]) null);
                super.registerHandlers(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}