package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_express_deliver_detail_product")
public class ExpressDeliverDetailProduct implements Serializable {

    private static final long serialVersionUID = 345435245233256L;

    public ExpressDeliverDetailProduct(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "expressDeliverDetailId")
    private ExpressDeliverDetail expressDeliverDetail;

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

    public ExpressDeliverDetail getExpressDeliverDetail() {
        return expressDeliverDetail;
    }

    public void setExpressDeliverDetail(ExpressDeliverDetail expressDeliverDetail) {
        this.expressDeliverDetail = expressDeliverDetail;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}