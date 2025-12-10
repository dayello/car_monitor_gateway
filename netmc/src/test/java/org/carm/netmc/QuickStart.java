package org.carm.netmc;

import org.carm.netmc.codec.MyMessageDecoder;
import org.carm.netmc.codec.MyMessageEncoder;
import org.carm.netmc.core.DefaultHandlerMapping;
import org.carm.netmc.endpoint.MyHandlerInterceptor;
import org.carm.netmc.handler.DispatcherHandler;
import org.carm.netmc.session.SessionManager;

import java.nio.charset.StandardCharsets;

/**
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public class QuickStart {

    public static final int port = 7611;

    public static void main(String[] args) {
        DispatcherHandler.STOPWATCH = true;

        Server udpServer = new NettyConfig.Builder()
                .setPort(port)
//                .setThreadGroup(0, 1)
                .setDelimiters(new byte[][]{"|".getBytes(StandardCharsets.UTF_8)})
                .setDecoder(new MyMessageDecoder())
                .setEncoder(new MyMessageEncoder())
                .setHandlerMapping(new DefaultHandlerMapping("org.carm.netmc.endpoint"))
                .setHandlerInterceptor(new MyHandlerInterceptor())
                .setSessionManager(new SessionManager())
                .setEnableUDP(true)
                .build();
        udpServer.start();

        Server tcpServer = new NettyConfig.Builder()
                .setPort(port)
                .setMaxFrameLength(2048)
                .setDelimiters(new byte[][]{"|".getBytes(StandardCharsets.UTF_8)})
                .setDecoder(new MyMessageDecoder())
                .setEncoder(new MyMessageEncoder())
                .setHandlerMapping(new DefaultHandlerMapping("org.carm.netmc.endpoint"))
                .setHandlerInterceptor(new MyHandlerInterceptor())
                .setSessionManager(new SessionManager())
                .build();
        tcpServer.start();
    }
}