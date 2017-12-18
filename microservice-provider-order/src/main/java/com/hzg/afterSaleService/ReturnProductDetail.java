package com.hzg.afterSaleService;

import com.hzg.erp.Product;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_returnproduct_detail")
public class ReturnProductDetail implements Serializable {
    private static final long serialVersionUID = 345435245233261L;

    public ReturnProductDetail(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "returnProductId")
    private ReturnProduct returnProduct;

    @Column(name = "productNo", length = 16)
    private String productNo;

    @Column(name="price", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float price;

    @Column(name="quantity", length = 8, precision = 2)
    private Float quantity;

    @Column(name="unit",length=6)
    private String unit;

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;

    @Column(name="state",length = 1)
    private Integer state;

    @OneToMany(mappedBy = "returnProductDetail", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ReturnProductDetailProduct> returnProductDetailProducts;

    @Transient
    private Product product;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ReturnProduct getReturnProduct() {
        return returnProduct;
    }

    public void setReturnProduct(ReturnProduct returnProduct) {
        this.returnProduct = returnProduct;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
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

    public Set<ReturnProductDetailProduct> getReturnProductDetailProducts() {
        return returnProductDetailProducts;
    }

    public void setReturnProductDetailProducts(Set<ReturnProductDetailProduct> returnProductDetailProducts) {
        this.returnProductDetailProducts = returnProductDetailProducts;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getStateName() {
        switch (state) {
            case 0 : return "未退货";
            case 1 : return "已退货";
            case 2 : return "不能退货";
            default : return "";
        }
    }
}
