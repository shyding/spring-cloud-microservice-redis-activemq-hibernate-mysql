package com.hzg.erp;

import com.hzg.sys.User;
import org.hibernate.annotations.Type;
import org.hibernate.type.ArrayType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;

/**
 * Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
 * 文件名: Supplier.java
 *
 * @author smjie
 * @version 1.00
 * @Date 2017/5/25
 */
@Entity(name = "hzg_supplier")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 345435245233230L;

    public Supplier(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=30)
    private String name;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "mainProductTypeId")
    private ProductType mainProductType;

    @Column(name="level",length=10)
    private String level;

    @Column(name="charger",length=20)
    private String charger;

    @Column(name="address",length=60)
    private String address;

    @Column(name="phone",length=16)
    private String phone;

    @Column(name="cooperateDate")
    private Timestamp cooperateDate;

    @Column(name="account",length=20)
    private String account;

    @Column(name="branch",length=30)
    private String branch;

    @Column(name="bank",length=30)
    private String bank;

    @Column(name="payTypes",length=30)
    @Type(type = "com.hzg.tools.IntegersType")
    private Integer[] payTypes;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "inputerId")
    private User inputer;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="types", length=20)
    @Type(type = "com.hzg.tools.IntegersType")
    private Integer[] types;

    @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "hzg_supplierproducttype_relation"
            , joinColumns = {@JoinColumn(name = "supplierId")}
            , inverseJoinColumns = {@JoinColumn(name = "productTypeId")})
    private Set<ProductType> productTypes;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getMainProductType() {
        return mainProductType;
    }

    public void setMainProductType(ProductType mainProductType) {
        this.mainProductType = mainProductType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCharger() {
        return charger;
    }

    public void setCharger(String charger) {
        this.charger = charger;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCooperateDate() {
        return cooperateDate;
    }

    public void setCooperateDate(Timestamp cooperateDate) {
        this.cooperateDate = cooperateDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Integer[] getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(Integer[] payTypes) {
        this.payTypes = payTypes;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public User getInputer() {
        return inputer;
    }

    public void setInputer(User inputer) {
        this.inputer = inputer;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Integer[] getTypes() {
        return types;
    }

    public void setTypes(Integer[] types) {
        this.types = types;
    }

    public Set<ProductType> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(Set<ProductType> productTypes) {
        this.productTypes = productTypes;
    }

    public String getPayTypesStr() {
        return Arrays.deepToString(payTypes);
    }

    public String getTypesStr() {
        return Arrays.deepToString(types);
    }
}