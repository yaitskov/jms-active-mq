package com.blog.spring.jms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private JdbcTemplate jdbcTemplate;

    @Resource
    private Queue testQueue;

    private static final Logger logger = LoggerFactory.getLogger(MsgCtl.class);


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RequestMapping("/send")
    public void send(HttpServletResponse response, @RequestParam("text") String text)
            throws JMSException, IOException {

        jdbcTemplate.update("insert into msg (txt) values(?)", new Object[] { text });
        int id = jdbcTemplate.queryForInt( "select last_insert_id()" );
        jmsTemplate.convertAndSend(testQueue, id);
        response.getWriter().append("ok. message '" + text + "' was sent and id = " + id);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RequestMapping("/receive")
    public void receive(HttpServletResponse response) throws IOException {
        Object msg = jmsTemplate.receiveAndConvert(testQueue);
        logger.debug("got id " + msg);
        int id = (Integer)msg;
        String tmsg = (String) jdbcTemplate.queryForObject("select txt from msg where id = ?", String.class, id);
        jdbcTemplate.update("delete from msg where id = ?", id);
        response.getWriter().append("ok. message '" + tmsg + "' is received with id = " + id);
    }
}
