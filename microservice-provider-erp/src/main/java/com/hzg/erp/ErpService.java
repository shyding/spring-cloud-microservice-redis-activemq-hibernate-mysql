package com.hzg.erp;

import com.hzg.sys.Audit;
import com.hzg.tools.ErpConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ErpService {
    @Autowired
    private ErpDao erpDao;

    public String purchaseStateModify(Audit audit, Integer purchaseState, Integer productState) {
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Purchase temp = new Purchase();
        temp.setId(purchase.getId());
        temp.setState(purchaseState);

        erpDao.updateById(temp.getId(), temp);


        Product temp1 = new Product();
        Set<PurchaseDetail> details = purchase.getDetails();
        for (PurchaseDetail detail : details) {

            temp1.setId(detail.getProduct().getId());
            temp1.setState(productState);
            erpDao.updateById(temp1.getId(), temp1);
        }

        return "success";
    }

    public String purchaseProductsStateModify(Audit audit, Integer productState) {
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Product temp1 = new Product();
        Set<PurchaseDetail> details = purchase.getDetails();
        for (PurchaseDetail detail : details) {

            temp1.setId(detail.getProduct().getId());
            temp1.setState(productState);
            erpDao.updateById(temp1.getId(), temp1);
        }

        return "success";
    }
}
