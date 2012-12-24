package com.blog.spring.jms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Daneel Yaitskov
 */
@Controller
public class MsgCtl {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Queue testQueue;

    private static final Logger logger = LoggerFactory.getLogger(MsgCtl.class);


    @RequestMapping("/send")
    public void send(HttpServletResponse response, @RequestParam("text") String text)
            throws JMSException, IOException {
        jmsTemplate.convertAndSend(testQueue, text);

        response.getWriter().append("ok. message '" + text + "' was sent");

    }

    @RequestMapping("/receive")
    public void receive(HttpServletResponse response) throws IOException {
        Object msg = jmsTemplate.receiveAndConvert(testQueue);
        response.getWriter().append("ok. message '" + msg + "' is received");
    }
}
