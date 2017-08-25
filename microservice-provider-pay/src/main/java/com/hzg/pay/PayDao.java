package com.hzg.pay;

import com.hzg.base.Dao;
import com.hzg.tools.DateUtil;
import com.hzg.tools.PayConstants;
import com.hzg.tools.StrUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class PayDao extends Dao {

    Logger logger = Logger.getLogger(PayDao.class);

    @Autowired
    private StrUtil strUtil;


    /**
     * 产生支付编号
     * @return
     */
    public String getNo() {

        String no = PayConstants.no_prefix_pay + strUtil.generateRandomStr(30);

        logger.info("generate no:" + no);

        return no;
    }
}
