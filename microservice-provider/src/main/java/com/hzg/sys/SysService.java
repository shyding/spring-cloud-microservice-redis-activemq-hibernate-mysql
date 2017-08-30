package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.AuditFlowConstant;
import com.hzg.tools.CommonConstant;
import com.hzg.tools.DateUtil;
import com.hzg.tools.Writer;
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

    @Autowired
    private DateUtil dateUtil;

    /**
     * 获取审核节点
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
        audit.setInputDate(dateUtil.getSecondCurrentTimestamp());
        audit.setAction(auditFlowNode.getAction());
        audit.setRefusedAction(auditFlowNode.getRefusedAction());

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

        if (audit.getPost() == null) {
            audit.setPost(auditFlowNodes[0].getPost());
        }

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

                    if (auditFlowNode == null) {
                        return null;
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
                    nextAudit.setNo(audit.getNo());
                    nextAudit.setState(AuditFlowConstant.audit_state_todo);
                    nextAudit.setInputDate(dateUtil.getSecondCurrentTimestamp());

                    nextAudit.setName(audit.getName());
                    nextAudit.setAction(nextAuditFlowNode.getAction());
                    nextAudit.setRefusedAction(nextAuditFlowNode.getRefusedAction());
                    nextAudit.setPost(nextAuditFlowNode.getPost());

                    if ((direct.equals(AuditFlowConstant.flow_direct_forward))) {
                        if (audit.getRefusePost() != null) {
                            nextAudit.setUser(audit.getRefuseUser());
                        }
                    } else if ((direct.equals(AuditFlowConstant.flow_direct_backwards))) {
                        nextAudit.setRefusePost(audit.getPost());
                        nextAudit.setRefuseUser(audit.getUser());
                    }

                    nextAudit.setCompany(dbAuditFlow.getCompany());
                    nextAudit.setEntity(dbAuditFlow.getEntity());
                    nextAudit.setEntityId(audit.getEntityId());
                    nextAudit.setPreFlowAuditNo(audit.getPreFlowAuditNo());

                    return nextAudit;
                }
            }
        }

        return null;
    }

    public Post getPostByAuditUser(Audit audit) {
        Post post = null;

        User user = (User)sysDao.queryById(audit.getToRefuseUser().getId(), User.class);

        if (user != null) {
            if (user.getPosts().size() == 1) {
                post =  (Post) user.getPosts().toArray()[0];

            } else {
                AuditFlow auditFlow = getAuditFlow(audit);

                for (Post post1 : user.getPosts()) {
                    for (AuditFlowNode auditFlowNode : auditFlow.getAuditFlowNodes()) {

                        if (auditFlowNode.getPost().getId().compareTo(post1.getId()) == 0) {
                            post = auditFlowNode.getPost();
                            break;
                        }
                    }

                    if (post != null) {
                        break;
                    }
                }
            }
        }

        return post;
    }

    /**
     * 执行节点动作
     * @param direct
     * @param audit
     */
    public String doAction(String direct, Audit audit) {
        String result = CommonConstant.fail;

        String action;
        if (direct.equals(AuditFlowConstant.flow_direct_forward)) {
            action = audit.getAction();
        } else {
            action = audit.getRefusedAction();
        }

        if (action != null) {
            String realAction = audit.getAction();
            audit.setAction(action);

            Map<String, String> result1 = writer.gson.fromJson(erpClient.auditAction(writer.gson.toJson(audit)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result = result1.get(CommonConstant.result);

            audit.setAction(realAction);
        } else {
            result = CommonConstant.success;
        }

        return result;
    }

    /**
     * 执行流程结束动作
     * @param audit
     */
    public String doFlowAction(Audit audit) {
        String result = CommonConstant.fail;

        AuditFlow auditFlow = getAuditFlow(audit);
        if (auditFlow != null && auditFlow.getAction() != null) {
            result = launchNotifyFlow(AuditFlowConstant.flow_action_entity_mapping.get(auditFlow.getAction()), audit);
        } else {
            result = CommonConstant.success;
        }

        return result;
    }

    /**
     * 发起流程
     * @param businessEntity
     * @param audit
     * @return
     */
    public String launchNotifyFlow(String businessEntity, Audit audit) {
        String result = CommonConstant.fail;

        Audit nextFlowAudit = null;

        Audit temp = new Audit();
        temp.setEntity(businessEntity);
        temp.setEntityId(audit.getEntityId());
        temp.setCompany(audit.getCompany());
        temp.setPost(audit.getPost());
        temp.setNo(sysDao.getNo(AuditFlowConstant.no_prefix_audit));
        temp.setPreFlowAuditNo(audit.getNo());

        nextFlowAudit = getNextAudit(temp, AuditFlowConstant.flow_direct_forward);

        if (nextFlowAudit == null) {
            nextFlowAudit = getFirstAudit(temp);
        }

        if (nextFlowAudit != null) {
            switch (nextFlowAudit.getEntity()) {
                case AuditFlowConstant.business_stockIn_notify:
                    setStockInAudit(nextFlowAudit, "purchase", audit.getEntityId());break;

                case AuditFlowConstant.business_stockIn_notify_caiwu:
                    setStockInAudit(nextFlowAudit, "purchase", audit.getEntityId());break;

                case AuditFlowConstant.business_product: {}break;
            }


            result = sysDao.save(nextFlowAudit);
        }

        return result;
    }

    public void setStockInAudit(Audit audit, String entity, Integer entityId){
        List<Map<String, Object>> purchaseInfo = writer.gson.fromJson(erpClient.query(entity, "{\"id\":" + entityId + "}"),
                new TypeToken<List<Map<String, java.lang.Object>>>(){}.getType());

        audit.setName("入库采购单：" + purchaseInfo.get(0).get("no").toString() + "里的商品");
        audit.setContent("采购单：" + purchaseInfo.get(0).get("no").toString() + "已审核完毕，采购的商品可以入库");
    }

}
