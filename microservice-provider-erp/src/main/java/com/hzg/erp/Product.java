package com.hzg.erp;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 345435245233232L;

    public Product(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=15)
    private String no;

    @Column(name="name",length=30)
    private String name;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId")
    private ProductType type;

    @Column(name="certificate",length=60)
    private String certificate;

    @Column(name="price", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float price;

    @Column(name="fatePrice", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float fatePrice;

    @Column(name="feature",length=6)
    private String feature;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "describeId")
    private ProductDescribe describe;

    @Column(name="costPrice", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float costPrice;

    @Column(name="unitPrice", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float unitPrice;

    @OneToMany(mappedBy = "product", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ProductOwnProperty> properties;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getFatePrice() {
        return fatePrice;
    }

    public void setFatePrice(Float fatePrice) {
        this.fatePrice = fatePrice;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ProductDescribe getDescribe() {
        return describe;
    }

    public void setDescribe(ProductDescribe describe) {
        this.describe = describe;
    }

    public Float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Set<ProductOwnProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<ProductOwnProperty> properties) {
        this.properties = properties;
    }
}
