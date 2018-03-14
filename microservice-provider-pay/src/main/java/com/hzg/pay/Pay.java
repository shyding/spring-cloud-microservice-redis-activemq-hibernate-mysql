package com.hzg.pay;

import com.hzg.customer.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_money_pay")
public class Pay implements Serializable {
    private static final long serialVersionUID = 345435245233235L;

    public Pay(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=32)
    private String no;

    @Column(name="entity", length = 32)
    private String entity;

    @Column(name="entityId", length = 11)
    private Integer entityId;

    @Column(name="entityNo", length = 16)
    private String entityNo;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="amount", length = 11, precision = 2)
    private Float amount;

    @Column(name="settleAmount", length = 11, precision = 2)
    private Float settleAmount;

    @Column(name="payDate")
    private Timestamp payDate;

    @Column(name="payType",length = 1)
    private Integer payType;

    @Column(name="payAccount", length =25)
    private String payAccount;

    @Column(name="payBranch",length=30)
    private String payBranch;

    @Column(name="payBank",length=20)
    private String payBank;

    @Column(name="bankBillNo",length=35)
    private String bankBillNo;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name="receiptAccount",length=25)
    private String receiptAccount;

    @Column(name="receiptBranch",length=30)
    private String receiptBranch;

    @Column(name="receiptBank",length=20)
    private String receiptBank;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="balanceType", length = 1)
    private Integer balanceType;

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(Float settleAmount) {
        this.settleAmount = settleAmount;
    }

    public Timestamp getPayDate() {
        return payDate;
    }

    public void setPayDate(Timestamp payDate) {
        this.payDate = payDate;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getPayBranch() {
        return payBranch;
    }

    public void setPayBranch(String payBranch) {
        this.payBranch = payBranch;
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getBankBillNo() {
        return bankBillNo;
    }

    public void setBankBillNo(String bankBillNo) {
        this.bankBillNo = bankBillNo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReceiptAccount() {
        return receiptAccount;
    }

    public void setReceiptAccount(String receiptAccount) {
        this.receiptAccount = receiptAccount;
    }

    public String getReceiptBranch() {
        return receiptBranch;
    }

    public void setReceiptBranch(String receiptBranch) {
        this.receiptBranch = receiptBranch;
    }

    public String getReceiptBank() {
        return receiptBank;
    }

    public void setReceiptBank(String receiptBank) {
        this.receiptBank = receiptBank;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Integer getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(Integer balanceType) {
        this.balanceType = balanceType;
    }
}