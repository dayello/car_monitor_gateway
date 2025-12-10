package org.carm.web.endpoint;

import org.carm.netmc.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.carm.commons.spring.SSEService;
import org.carm.protocol.codec.JTMessageDecoder;
import org.carm.protocol.codec.JTMessageEncoder;
import org.carm.web.handler.JTMessagePushAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 测试JTMessageLoggingAdapter的消息记录功能
 */
public class JTMessageLoggingAdapterTest {

    @Test
    public void testMessageLogging() {
        // 创建模拟对象
        JTMessageEncoder encoder = Mockito.mock(JTMessageEncoder.class);
        JTMessageDecoder decoder = Mockito.mock(JTMessageDecoder.class);
        SSEService sseService = Mockito.mock(SSEService.class);
        Session session = Mockito.mock(Session.class);
        
        // 模拟session返回值
        Mockito.when(session.getClientId()).thenReturn("test-client");
        Mockito.when(session.toString()).thenReturn("Session[test-client]");
        
        // 创建测试适配器
        JTMessagePushAdapter adapter = new JTMessagePushAdapter(encoder, decoder, sseService);
        
        // 创建测试数据 - 模拟一个无效的JT808消息
        byte[] testData = {0x7e, 0x01, 0x02, 0x03, 0x04, 0x7e}; // 简单的测试数据
        ByteBuf input = Unpooled.wrappedBuffer(testData);
        
        // 测试解码 - 应该记录原始消息和解码失败
        try {
            adapter.decode(input, session);
            System.out.println("测试完成：消息记录功能正常工作");
        } catch (Exception e) {
            System.out.println("测试异常：" + e.getMessage());
        } 
        
        // 验证SSE服务被调用
        Mockito.verify(sseService, Mockito.atLeastOnce()).send(Mockito.eq("test-client"), Mockito.anyString());
    }
}