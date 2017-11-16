package com.hzg.order;

import com.hzg.erp.Product;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_order_private_acc_product")
public class OrderPrivateAccProduct implements Serializable {

    private static final long serialVersionUID = 345435245233259L;

    public OrderPrivateAccProduct(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderPrivateAccId")
    private OrderPrivateAcc orderPrivateAcc;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
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

    public OrderPrivateAcc getOrderPrivateAcc() {
        return orderPrivateAcc;
    }

    public void setOrderPrivateAcc(OrderPrivateAcc orderPrivateAcc) {
        this.orderPrivateAcc = orderPrivateAcc;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}