package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;

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

    @Column(name="productId", length = 11)
    private Product product;

    @Column(name="quantity", length = 5)
    private Integer quantity;

    @Column(name="unit",length=8)
    private String unit;

    @Column(name="weight", length = 9, precision = 2)
    private String weight;

    @Column(name="amount", length = 6, precision = 2)
    private Float amount;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="expressDeliverId")
    private ExpressDeliver expressDeliver;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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
}