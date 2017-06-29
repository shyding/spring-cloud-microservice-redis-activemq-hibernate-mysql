package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.AuditFlowConstant;
import com.hzg.tools.Writer;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class SysService {

    @Autowired
    private SysDao sysDao;

    @Autowired
    private ErpClient erpClient;

    @Autowired
    private Writer writer;

    /**
     * 获取第一个审核节点
     * @param audit
     * @return
     */
    public Audit getAudit(Audit audit) {
        AuditFlow dbAuditFlow = getAuditFlow(audit);
        AuditFlowNode auditFlowNode = null;

        for (AuditFlowNode temp : dbAuditFlow.getAuditFlowNodes()) {
            if (temp.getPost().getId().compareTo(audit.getPost().getId()) == 0) {
                auditFlowNode = temp;
                break;
            }
        }

        audit.setState(AuditFlowConstant.audit_state_todo);
        audit.setInputDate(new Timestamp(System.currentTimeMillis()));
        audit.setAction(auditFlowNode.getAction());

        return audit;

    }

    /**
     * 获取流程
     * @param audit
     * @return
     */
    public AuditFlow getAuditFlow(Audit audit) {
        AuditFlow auditFlow = new AuditFlow();
        auditFlow.setEntity(audit.getEntity());
        auditFlow.setCompany(audit.getCompany());
        auditFlow.setState(AuditFlowConstant.flow_state_use);

        List<AuditFlow> auditFlows = sysDao.query(auditFlow);
        if (!auditFlows.isEmpty()) {
            return auditFlows.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取第一个审核节点
     * @param audit
     * @return
     */
    public Audit getFirstAudit(Audit audit) {
        AuditFlow dbAuditFlow = getAuditFlow(audit);

        AuditFlowNode[] auditFlowNodes = new AuditFlowNode[dbAuditFlow.getAuditFlowNodes().size()];
        dbAuditFlow.getAuditFlowNodes().toArray(auditFlowNodes);

        Arrays.sort(auditFlowNodes, new Comparator<AuditFlowNode>() {
            @Override
            public int compare(AuditFlowNode o1, AuditFlowNode o2) {
                if (o1.getId().compareTo(o2.getId()) > 0) {
                    return 1;
                } else if(o1.getId().compareTo(o2.getId()) < 0) {
                    return -1;
                }

                return 0;
            }
        });

        audit.setPost(auditFlowNodes[0].getPost());

        /**
         * 流程中的第一个节点为事务发起节点，不属于审核节点
         * 流程中的第二个节点才为审核的第一个节点
         */
        return getNextAudit(audit, AuditFlowConstant.flow_direct_forward);
    }

    /**
     * 获取下一个审核节点
     * @param audit
     * @param direct 向前或向后获取
     * @return
     */
    public Audit getNextAudit(Audit audit, String direct) {
        AuditFlow dbAuditFlow = getAuditFlow(audit);

        if (dbAuditFlow != null) {
            /**
             * 获取当前流程节点
             */
            Set<AuditFlowNode> auditFlowNodes =  dbAuditFlow.getAuditFlowNodes();
            if (!auditFlowNodes.isEmpty()) {

                /**
                 * 获取下一个流程节点
                 */
                AuditFlowNode nextAuditFlowNode = null;

                /**
                 * 如果事宜、审核节点不是被退回来的，则获取对应流程节点的下一个节点
                 */
                if (audit.getRefusePost() == null) {
                    AuditFlowNode auditFlowNode = null;

                    for (AuditFlowNode temp : auditFlowNodes) {
                        if (temp.getPost().getId().compareTo(audit.getPost().getId()) == 0) {
                            auditFlowNode = temp;
                            break;
                        }
                    }

                    if (direct.equals(AuditFlowConstant.flow_direct_forward) && auditFlowNode.getNextPost() == null) {
                        return null;
                    }

                    if (direct.equals(AuditFlowConstant.flow_direct_forward)) {
                        for (AuditFlowNode temp : auditFlowNodes) {
                            if (temp.getPost().getId().compareTo(auditFlowNode.getNextPost().getId()) == 0) {
                                nextAuditFlowNode = temp;
                                break;
                            }
                        }

                    } else if ((direct.equals(AuditFlowConstant.flow_direct_backwards))) {
                        for (AuditFlowNode temp : auditFlowNodes) {
                            if (temp.getNextPost().getId().compareTo(auditFlowNode.getNextPost().getId()) == 0) {
                                nextAuditFlowNode = temp;
                                break;
                            }
                        }
                    }
                }

                /**
                 * 如果是被退回来的事宜、审核节点，则获取退回这个节点的流程节点
                 */
                else {
                    for (AuditFlowNode temp : auditFlowNodes) {
                        if (temp.getPost().getId().compareTo(audit.getRefusePost().getId()) == 0) {
                            nextAuditFlowNode = temp;
                            break;
                        }
                    }
                }

                if (nextAuditFlowNode != null) {
                    /**
                     * 设置下一个事宜、审核节点
                     */
                    Audit nextAudit = new Audit();
                    nextAudit.setState(AuditFlowConstant.audit_state_todo);
                    nextAudit.setInputDate(new Timestamp(System.currentTimeMillis()));

                    nextAudit.setName(audit.getName());
                    nextAudit.setAction(nextAuditFlowNode.getAction());
                    nextAudit.setPost(nextAuditFlowNode.getPost());

                    if ((direct.equals(AuditFlowConstant.flow_direct_backwards))) {
                        nextAudit.setRefusePost(audit.getPost());
                    }

                    nextAudit.setCompany(dbAuditFlow.getCompany());
                    nextAudit.setEntity(dbAuditFlow.getEntity());
                    nextAudit.setEntityId(audit.getEntityId());

                    return nextAudit;
                }
            }
        }

        return null;
    }

    /**
     * 执行节点动作
     * @param audit
     * @param direct
     */
    public void doAction(Audit audit, String direct) {
        if ((direct.equals(AuditFlowConstant.flow_direct_forward) && audit.getAction() != null) ||
                (direct.equals(AuditFlowConstant.flow_direct_backwards) && audit.getToRefuseAction() != null)) {
            String doBusinessResult = "fail";

            if (audit.getEntity().equals(AuditFlowConstant.business_purchase)) {
                doBusinessResult = erpClient.auditAction(writer.gson.toJson(audit));
            }

            /**
             * 调用相应业务动作失败，则触发异常，回滚事务
             */
            if (!doBusinessResult.contains("success")) {
                int t = 1 / 0;
            }
        }
    }

    /**
     * 执行流程结束动作
     * @param audit
     */
    public void doFlowAction(Audit audit) {
        String doBusinessResult = "fail";

        AuditFlow auditFlow = getAuditFlow(audit);
        if (auditFlow != null) {
            switch (auditFlow.getAction()) {
                case AuditFlowConstant.action_flow_purchase: doBusinessResult = launchStockInFlow(audit);break;
                case AuditFlowConstant.action_flow_StockIn: doBusinessResult = launchOnSaleFlow(audit);break;
            }
        }

        /**
         * 调用相应业务动作失败，则触发异常，回滚事务
         */
        if (!doBusinessResult.contains("success")) {
            int t = 1 / 0;
        }
    }

    public String launchStockInFlow(Audit audit) {

        Audit temp = new Audit();
        temp.setEntity(AuditFlowConstant.business_stockIn);
        temp.setCompany(audit.getCompany());
        temp.setPost(audit.getPost());

        Audit nextFlowAudit = getNextAudit(temp, AuditFlowConstant.flow_direct_forward);
        if (nextFlowAudit != null) {
            Map<String, Object> purchaseInfo = writer.gson.fromJson(erpClient.query("purchase", "{\"id\":" + audit.getEntityId() + "}"),
                    new TypeToken<Map<String, java.lang.Object>>(){}.getType());

            nextFlowAudit.setEntity(audit.getEntity());
            nextFlowAudit.setName("入库采购单：" + purchaseInfo.get("no").toString() + "里的商品");
            nextFlowAudit.setContent("采购单：" + purchaseInfo.get("no").toString() + "已审核完毕，采购的商品可以入库");

            sysDao.save(nextFlowAudit);
        }

        return "success";
    }

    public String launchOnSaleFlow(Audit audit) {

        return "success";
    }

}
