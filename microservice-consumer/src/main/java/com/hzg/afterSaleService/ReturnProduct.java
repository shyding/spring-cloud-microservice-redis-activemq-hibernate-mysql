package com.hzg.afterSaleService;

import com.hzg.customer.User;
import com.hzg.sys.Action;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity(name = "hzg_returnproduct")
public class ReturnProduct implements Serializable {
    private static final long serialVersionUID = 345435245233260L;

    public ReturnProduct(){
        super();
    }

    public ReturnProduct(Integer id){
        super();
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=16)
    private String no;

    @Column(name="entity",length=32)
    private String entity;

    @Column(name="entityId", length = 11)
    private Integer entityId;

    @Column(name="entityNo", length = 16)
    private String entityNo;

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;

    @Column(name="reason",length=60)
    private String reason;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name="state",length = 2)
    private Integer state;

    @Column(name="date")
    private Timestamp date;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="type", length = 1)
    private Integer type;

    @OneToMany(mappedBy = "returnProduct", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ReturnProductDetail> details;

    @Column(name="fee", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float fee;

    @Transient
    private List<Action> actions;

    @Transient
    private String sessionId;

    @Transient
    private com.hzg.sys.User sysUser;

    @Transient
    private String returnProductUsername;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityNo() {
        return entityNo;
    }

    public void setEntityNo(String entityNo) {
        this.entityNo = entityNo;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Set<ReturnProductDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<ReturnProductDetail> details) {
        this.details = details;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public com.hzg.sys.User getSysUser() {
        return sysUser;
    }

    public void setSysUser(com.hzg.sys.User sysUser) {
        this.sysUser = sysUser;
    }

    public void setReturnProductUsername(String returnProductUsername) {
        this.returnProductUsername = returnProductUsername;
    }

    public String getReturnProductUsername() {
        if (sysUser != null) {
            return sysUser.getUsername();
        } else {
            return user.getUsername();
        }
    }

    public String getStateName() {
        switch (state) {
            case 0 : return "未退款";
            case 1 : return "已退款";
            case 2 : return "取消";
            case 3 : return "销售确认可退";
            case 31 : return ":销售确认不可退";
            case 4 : return "销售总监确认可退";
            case 41 : return "销售总监确认不可退";
            case 5 : return "仓储确认可退";
            case 51 : return "仓储确认不可退";
            case 6 : return "采购退货申请";
            case 61 : return "采购退货已退款";
            case 62 : return "采购退货取消";
            case 7 : return "采购确认退货";
            case 8 : return "仓储确认已邮寄货物";
            case 9 : return "供应商确认收货";
            default : return "";
        }
    }
}
