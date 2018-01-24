package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

@Entity(name = "hzg_express_deliver")
public class ExpressDeliver implements Serializable {

    private static final long serialVersionUID = 345435245233252L;

    public ExpressDeliver(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="deliver",length=20)
    private String deliver;

    @Column(name="type",length=16)
    private String type;

    @Column(name="date")
    private Timestamp date;

    @Column(name="sender",length=20)
    private String sender;

    @Column(name="senderAddress",length=60)
    private String senderAddress;

    @Column(name="senderCity",length=16)
    private String senderCity;

    @Column(name="senderProvince",length=16)
    private String senderProvince;

    @Column(name="senderCountry",length=16)
    private String senderCountry;

    @Column(name="senderPostCode",length=7)
    private String senderPostCode;

    @Column(name="senderCompany",length=30)
    private String senderCompany;

    @Column(name="senderTel",length=20)
    private String senderTel;

    @Column(name="senderMobile",length=20)
    private String senderMobile;

    @Column(name="receiver",length=20)
    private String receiver;

    @Column(name="receiverAddress",length=60)
    private String receiverAddress;

    @Column(name="receiverCity",length=16)
    private String receiverCity;

    @Column(name="receiverProvince",length=16)
    private String receiverProvince;

    @Column(name="receiverCountry",length=16)
    private String receiverCountry;

    @Column(name="receiverPostCode",length=7)
    private String receiverPostCode;

    @Column(name="receiverCompany",length=30)
    private String receiverCompany;

    @Column(name="receiverTel",length=20)
    private String receiverTel;

    @Column(name="receiverMobile",length=20)
    private String receiverMobile;

    @Column(name="state", length = 1)
    private Integer state;

    @Column(name="fee", length = 6, precision = 2)
    private Float fee;

    @Column(name="remark",length=255)
    private String remark;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @OneToMany(mappedBy = "expressDeliver", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ExpressDeliverDetail> details;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeliver() {
        return deliver;
    }

    public void setDeliver(String deliver) {
        this.deliver = deliver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }

    public String getSenderProvince() {
        return senderProvince;
    }

    public void setSenderProvince(String senderProvince) {
        this.senderProvince = senderProvince;
    }

    public String getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(String senderCountry) {
        this.senderCountry = senderCountry;
    }

    public String getSenderPostCode() {
        return senderPostCode;
    }

    public void setSenderPostCode(String senderPostCode) {
        this.senderPostCode = senderPostCode;
    }

    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }

    public String getSenderTel() {
        return senderTel;
    }

    public void setSenderTel(String senderTel) {
        this.senderTel = senderTel;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCountry() {
        return receiverCountry;
    }

    public void setReceiverCountry(String receiverCountry) {
        this.receiverCountry = receiverCountry;
    }

    public String getReceiverPostCode() {
        return receiverPostCode;
    }

    public void setReceiverPostCode(String receiverPostCode) {
        this.receiverPostCode = receiverPostCode;
    }

    public String getReceiverCompany() {
        return receiverCompany;
    }

    public void setReceiverCompany(String receiverCompany) {
        this.receiverCompany = receiverCompany;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Set<ExpressDeliverDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<ExpressDeliverDetail> details) {
        this.details = details;
    }

    public String getCnDate() {
        return new SimpleDateFormat("M月d日").format(date);
    }

    @Override
    public String toString() {
        return "ExpressDeliver{" +
                "id=" + id +
                ", deliver='" + deliver + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", sender='" + sender + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", senderCity='" + senderCity + '\'' +
                ", senderProvince='" + senderProvince + '\'' +
                ", senderCountry='" + senderCountry + '\'' +
                ", senderPostCode='" + senderPostCode + '\'' +
                ", senderCompany='" + senderCompany + '\'' +
                ", senderTel='" + senderTel + '\'' +
                ", senderMobile='" + senderMobile + '\'' +
                ", receiver='" + receiver + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", receiverCity='" + receiverCity + '\'' +
                ", receiverProvince='" + receiverProvince + '\'' +
                ", receiverCountry='" + receiverCountry + '\'' +
                ", receiverPostCode='" + receiverPostCode + '\'' +
                ", receiverCompany='" + receiverCompany + '\'' +
                ", receiverTel='" + receiverTel + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", state=" + state +
                ", fee=" + fee +
                ", remark='" + remark + '\'' +
                ", details=" + details +
                '}';
    }
}