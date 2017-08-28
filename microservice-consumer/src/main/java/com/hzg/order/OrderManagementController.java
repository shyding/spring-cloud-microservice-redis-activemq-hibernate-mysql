package com.hzg.order;

import com.hzg.tools.CommonConstant;
import com.hzg.tools.StrUtil;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/orderManagement")
public class OrderManagementController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(OrderManagementController.class);

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private Writer writer;

    @Autowired
    private StrUtil strUtil;

    @Autowired
    private Queue orderQueue;

    public OrderManagementController(OrderClient orderClient) {
        super(orderClient);
    }

    @PostMapping("/cancel/{" + CommonConstant.id + "}")
    public void cancel(HttpServletResponse response, @PathVariable( CommonConstant.id) Integer id) {
        logger.info("cancel order start:" + id);

        String json = "{\"id\":" + id + "}";
        writer.writeObjectToJsonAccessAllow(response, orderClient.cancel(json));

        logger.info("cancel order end");
    }


}
