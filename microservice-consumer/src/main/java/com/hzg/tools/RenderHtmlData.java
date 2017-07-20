package com.hzg.tools;

import com.google.gson.reflect.TypeToken;
import com.hzg.erp.ErpClient;
import com.hzg.erp.Purchase;
import com.hzg.sys.Audit;
import com.hzg.sys.SysClient;
import com.hzg.sys.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RenderHtmlData {

    @Autowired
    private SysClient sysClient;

    @Autowired
    private ErpClient erpClient;

    @Autowired
    private Writer writer;

    public String getRefuseUserOptions(User currentUser, List<Audit> audits) {
        String refuseUserOptions = "";

        for (int i = 0; i < audits.size(); i++) {
            if (audits.get(i).getUser() != null &&
                    !refuseUserOptions.contains("'" + audits.get(i).getUser().getId() + "'") &&
                    audits.get(i).getUser().getId().compareTo(currentUser.getId()) != 0) {

                refuseUserOptions += "<option value='" + audits.get(i).getUser().getId() + "'>" + audits.get(i).getUser().getName() + "</option>";
            }
        }

        /**
         * 添加发起人
         */
        if (audits.get(0).getEntity().equals(AuditFlowConstant.business_purchase) ||
                audits.get(0).getEntity().equals(AuditFlowConstant.business_purchaseEmergency)) {

            List<Purchase> purchases = writer.gson.fromJson(erpClient.query(Purchase.class.getSimpleName().toLowerCase(),
                    "{\"id\":" + audits.get(0).getEntityId() + "}"),
                    new TypeToken<List<Purchase>>() {}.getType());

            if (!purchases.isEmpty() && !refuseUserOptions.contains("'" + purchases.get(0).getInputer().getId() + "'")) {
                refuseUserOptions = "<option value='" + purchases.get(0).getInputer().getId() + "'>" + purchases.get(0).getInputer().getName() + "</option>"
                        + refuseUserOptions;
            }
        }

        if (!audits.isEmpty() && audits.size() > 1) {
            for (int i = audits.size(); i > 0; i--) {
                if (audits.get(i-1).getUser() != null && !refuseUserOptions.contains("上一节点")) {
                    refuseUserOptions = "<option value='" + audits.get(i - 1).getUser().getId() + "'>上一节点</option>" + refuseUserOptions;
                    break;
                }
            }
        }

        if (audits.get(0).getPreFlowAuditNo() != null) {
            List<Audit> preFlowAudits = writer.gson.fromJson(sysClient.query(Audit.class.getSimpleName().toLowerCase(), "{\"no\":" + audits.get(0).getPreFlowAuditNo() + "}"),
                    new TypeToken<List<Audit>>() {}.getType());
            refuseUserOptions += getRefuseUserOptions(currentUser, preFlowAudits);
        }

        return refuseUserOptions;
    }
}
