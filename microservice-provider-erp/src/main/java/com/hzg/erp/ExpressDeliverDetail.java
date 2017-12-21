package com.hzg.erp;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_express_deliver_detail")
public class ExpressDeliverDetail implements Serializable {

    private static final long serialVersionUID = 345435245233253L;

    public ExpressDeliverDetail(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="expressNo", length = 32)
    private String expressNo;

    @Column(name="productNo", length = 16)
    private String productNo;

    @Column(name="quantity", length = 5)
    private Float quantity;

    @Column(name="unit",length=8)
    private String unit;

    @Column(name="weight", length = 9, precision = 2)
    private String weight;

    @Column(name="price", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float price;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="expressDeliverId")
    private ExpressDeliver expressDeliver;

    @Column(name="mailNo",length=64)
    private String mailNo;

    @Column(name="insure", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float insure;

    @OneToMany(mappedBy = "expressDeliverDetail", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ExpressDeliverDetailProduct> expressDeliverDetailProducts;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public ExpressDeliver getExpressDeliver() {
        return expressDeliver;
    }

    public void setExpressDeliver(ExpressDeliver expressDeliver) {
        this.expressDeliver = expressDeliver;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public Float getInsure() {
        return insure;
    }

    public void setInsure(Float insure) {
        this.insure = insure;
    }

    public Set<ExpressDeliverDetailProduct> getExpressDeliverDetailProducts() {
        return expressDeliverDetailProducts;
    }

    public void setExpressDeliverDetailProducts(Set<ExpressDeliverDetailProduct> expressDeliverDetailProducts) {
        this.expressDeliverDetailProducts = expressDeliverDetailProducts;
    }

    @Override
    public String toString() {
        return "ExpressDeliverDetail{" +
                "id=" + id +
                ", expressNo='" + expressNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", weight='" + weight + '\'' +
                ", price=" + price +
                ", state=" + state +
                '}';
    }
}