package org.carm.netmc.endpoint;

import org.carm.netmc.core.annotation.Endpoint;
import org.carm.netmc.core.annotation.Mapping;
import org.carm.netmc.model.MyHeader;
import org.carm.netmc.model.MyMessage;
import org.carm.netmc.session.Session;

@Endpoint
public class MyEndpoint {

    @Mapping(types = 1, desc = "注册")
    public MyMessage register(MyMessage request, Session session) {
        session.register(request);
        return new MyMessage(new MyHeader(2, "123", 2), "ack");
    }
}
