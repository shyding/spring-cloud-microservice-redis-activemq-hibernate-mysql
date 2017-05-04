package com.hzg.base;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;

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

    @GetMapping("/suggest/{entity}/{properties}/{word}")
    public void suggest(HttpServletResponse response, @PathVariable("entity") String entity,
                        @PathVariable("properties") String properties, @PathVariable("word") String word) {
        logger.info("suggest start, entity:" + entity + ",properties:" + properties + ", word:" + word);

        String json = "";
        String[] propertiesArr = properties.split("#");
        for (String property:propertiesArr) {
            if (property.trim().length() > 0)
                json += "\"" + property + "\":\"" + word + "\",";
        }
        json = "{" + json.substring(0, json.length()-1) + "}";

        writer.writeStringToJson(response, client.suggest(entity, json));
        logger.info("suggest end");
    }
}
