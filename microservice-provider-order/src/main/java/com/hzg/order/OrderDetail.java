package com.hzg.order;

import com.hzg.customer.Express;
import com.hzg.erp.Product;
import com.hzg.erp.ProductPriceChange;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "hzg_order_detail")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 345435245233240L;

    public OrderDetail(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    @Column(name = "productNo")
    private String productNo;

    @Column(name="productPrice", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float productPrice;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="quantity", length = 8, precision = 2)
    private Float quantity;

    @Column(name="unit",length=6)
    private String unit;

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;

    @Column(name="discount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float discount;

    @Column(name="payAmount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float payAmount;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "expressId")
    private Express express;

    @Column(name="expressDate")
    private Timestamp expressDate;

    @Column(name="date")
    private Timestamp date;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="priceChangeId")
    private ProductPriceChange priceChange;

    @OneToMany(mappedBy = "orderDetail", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<OrderDetailProduct> orderDetailProducts;

    @Transient
    private OrderPrivate orderPrivate;

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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Float payAmount) {
        this.payAmount = payAmount;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public Timestamp getExpressDate() {
        return expressDate;
    }

    public void setExpressDate(Timestamp expressDate) {
        this.expressDate = expressDate;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public OrderPrivate getOrderPrivate() {
        return orderPrivate;
    }

    public void setOrderPrivate(OrderPrivate orderPrivate) {
        this.orderPrivate = orderPrivate;
    }

    public ProductPriceChange getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(ProductPriceChange priceChange) {
        this.priceChange = priceChange;
    }

    public Set<OrderDetailProduct> getOrderDetailProducts() {
        return orderDetailProducts;
    }

    public void setOrderDetailProducts(Set<OrderDetailProduct> orderDetailProducts) {
        this.orderDetailProducts = orderDetailProducts;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getStateName() {
        switch (state) {
            case 0 : return "未售";
            case 1 : return "已售";
            case 2 : return "预定";
            case 3 : return "退货";
            case 4 : return "换货";
            default : return "";
        }
    }
}
