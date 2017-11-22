package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_stock_inout_detail")
public class StockInOutDetail implements Serializable {

    private static final long serialVersionUID = 345435245233238L;

    public StockInOutDetail(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "stockInOutId")
    private StockInOut stockInOut;

    @Column(name="productNo",length=16)
    private String productNo;

    @Column(name="quantity", length = 8, precision = 2)
    private Float quantity;

    @Column(name="unit",length=8)
    private String unit;

    @OneToMany(mappedBy = "stockInOutDetail", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<StockInOutDetailProduct> stockInOutDetailProducts;

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

    public StockInOut getStockInOut() {
        return stockInOut;
    }

    public void setStockInOut(StockInOut stockInOut) {
        this.stockInOut = stockInOut;
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

    public Set<StockInOutDetailProduct> getStockInOutDetailProducts() {
        return stockInOutDetailProducts;
    }

    public void setStockInOutDetailProducts(Set<StockInOutDetailProduct> stockInOutDetailProducts) {
        this.stockInOutDetailProducts = stockInOutDetailProducts;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}