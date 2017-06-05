package com.hzg.mq;

import com.hzg.user.UserController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Created by Administrator on 2017/3/28.
 */
@Controller
@RequestMapping("/mq")
public class MQReceiver {

    Logger logger = Logger.getLogger(MQReceiver.class);

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    // 自动触发接受
    @JmsListener(destination = "userQueue")
    public void receiveQueue(String text) {
        logger.info("receive message from userQueue: " + text);
    }

    // 从 userQueue 队列接受消息
    @RequestMapping(value="/receiveMQ",method= RequestMethod.GET)
    public String receiveMQ(){
        String message = jmsMessagingTemplate.receive(queue).toString();
        String queueName = "";
        try {
            queueName = queue.getQueueName();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        logger.info("receive message from " + queueName + ":" + message);

        return message;
    }
}
