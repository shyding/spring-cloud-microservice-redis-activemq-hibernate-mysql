package com.hzg.erp;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_product_check_detail")
public class ProductCheckDetail implements Serializable {

    private static final long serialVersionUID = 8399778925810628911L;

    public ProductCheckDetail() {
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;  //商品盘点详细id

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;  //商品id

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "checkId")
    private ProductCheck productCheck;  //盘点单id

    @Column(name="itemNo")
    private String itemNo;  //存货编码

    @Column(name="checkQuantity", length = 8, precision = 2)
    private Float checkQuantity;  //盘点数量

    @Column(name="checkAmount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float checkAmount;  //盘点金额

    @Column(name="unit", length = 6)
    private String unit;  //计量单位

    @Column(name="quantity", length = 8, precision = 2)
    private Float quantity;  //盈亏数量

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;  //盈亏金额

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

    public ProductCheck getProductCheck() {
        return productCheck;
    }

    public void setProductCheck(ProductCheck productCheck) {
        this.productCheck = productCheck;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public Float getCheckQuantity() {
        return checkQuantity;
    }

    public void setCheckQuantity(Float checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    public Float getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(Float checkAmount) {
        this.checkAmount = checkAmount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}