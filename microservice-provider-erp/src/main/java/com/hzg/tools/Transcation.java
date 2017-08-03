package com.hzg.tools;

import com.hzg.base.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class Transcation {

    @Autowired
    private HibernateTransactionManager transactionManager;

    @Autowired
    public Dao dao;


    /**
     * 回滚数据库事务，及删除缓存里的对象
     *
     * 1、PROPAGATION_REQUIRED:如果存在一个事务，则支持当前事务。如果没有事务则开启。
     * 2、PROPAGATION_SUPPORTS:如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。
     * 3、PROPAGATION_MANDATORY:如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。
     * 4、PROPAGATION_REQUIRES_NEW:总是开启一个新的事务。如果一个事务存在，则将这个存在的事务挂起。
     * 5、PROPAGATION_NOT_SUPPORTED:总是非事务地执行，并挂起任何存在的事务。
     * 6、PROPAGATION_NEVER:总是非事务地执行，如果存在一个活动事务，则抛出异常。
     * 7、PROPAGATION_NESTED:如果一个活动的事务存在，则运行在一个嵌套的事务中，如果没有活动事务，则按TransactionDefinition.PROPAGATION_REQUIRED属性执行
     */
    public void rollback(){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        transactionManager.rollback(status);

        Object keys = dao.getFromRedis(String.valueOf(Thread.currentThread().getId()));
        if (keys != null) {
            String[] keysArr = String.valueOf(keys).split(dao.key_delimiter);
            for (String key : keysArr) {
                dao.deleteFromRedis(key);
            }
        }
    }

    public String dealResult(String result) {
        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

        if (result.contains(CommonConstant.fail)) {
            result = result.replace(CommonConstant.success, "");
            rollback();

        } else {
            result = CommonConstant.success;
        }

        return result;
    }
}
