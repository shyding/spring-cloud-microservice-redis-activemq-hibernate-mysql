package com.hzg.erp;

import com.hzg.sys.Audit;
import com.hzg.tools.AuditFlowConstant;
import com.hzg.tools.ErpConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ErpService {
    @Autowired
    private ErpDao erpDao;

    public String productPass(Audit audit){
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Product temp = new Product();
        Set<PurchaseDetail> details = purchase.getDetails();
        for (PurchaseDetail detail : details) {

            temp.setId(detail.getProduct().getId());
            temp.setState(ErpConstant.product_state_purchase_pass);
            erpDao.updateById(temp.getId(), temp);
        }

        return "success";
    }

    public String purchaseClose(Audit audit) {
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        if (audit.getToRefuseAction() == null) {
            Purchase temp = new Purchase();
            temp.setId(purchase.getId());
            temp.setState(ErpConstant.purchase_state_close);

            erpDao.updateById(temp.getId(), temp);

        } else {
            switch (audit.getToRefuseAction()) {
                case AuditFlowConstant.action_purchase_product_pass: {
                    Product temp = new Product();
                    Set<PurchaseDetail> details = purchase.getDetails();
                    for (PurchaseDetail detail : details) {

                        temp.setId(detail.getProduct().getId());
                        temp.setState(ErpConstant.product_state_purchase);
                        erpDao.updateById(temp.getId(), temp);
                    }
                }
                break;
            }
        }

        return "success";
    }
}
