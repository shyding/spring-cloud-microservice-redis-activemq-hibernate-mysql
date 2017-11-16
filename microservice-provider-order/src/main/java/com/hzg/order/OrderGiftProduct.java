package com.hzg.order;

import com.hzg.erp.Product;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_order_gift_product")
public class OrderGiftProduct implements Serializable {

    private static final long serialVersionUID = 345435245233258L;

    public OrderGiftProduct(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderGiftId")
    private OrderGift orderGift;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
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

    public OrderGift getOrderGift() {
        return orderGift;
    }

    public void setOrderGift(OrderGift orderGift) {
        this.orderGift = orderGift;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}