package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_purchase_detail_product")
public class PurchaseDetailProduct implements Serializable {

    private static final long serialVersionUID = 345435245233254L;

    public PurchaseDetailProduct(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseDetailId")
    private PurchaseDetail purchaseDetail;

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

    public PurchaseDetail getPurchaseDetail() {
        return purchaseDetail;
    }

    public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
        this.purchaseDetail = purchaseDetail;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}