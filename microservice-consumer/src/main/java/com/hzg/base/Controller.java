package com.hzg.base;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @author smjie
 * @version 1.00
 * @Date 2017/4/20
 */
public class Controller {
    Logger logger = Logger.getLogger(Controller.class);

    public Client client;

    @Autowired
    private Writer writer;

    public Controller(Client client) {
        this.client = client;
    }

    @PostMapping("/save/{entity}")
    public void save(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("save start, entity:" + entity + ", json:" + json);
        writer.writeStringToJson(response, client.save(entity, json));
        logger.info("save end");
    }

    @PostMapping("/update/{entity}")
    public void update(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("update start, entity:" + entity + ", json:" + json);
        writer.writeStringToJson(response, client.update(entity, json));
        logger.info("update end");
    }

    @GetMapping("/query/{entity}")
    public void query(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("query start, entity:" + entity + ", json:" + json);
        writer.writeStringToJson(response, client.query(entity, json));
        logger.info("query end");
    }
}
