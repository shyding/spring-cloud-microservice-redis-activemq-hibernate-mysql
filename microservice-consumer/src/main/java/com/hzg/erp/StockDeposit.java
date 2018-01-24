package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_stock_deposit")
public class StockDeposit implements Serializable {

    private static final long serialVersionUID = 345435245233241L;

    public StockDeposit(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="amount", length = 8, precision = 2)
    private Float amount;

    @Column(name="returnGoodsDate")
    private Timestamp returnGoodsDate;

    @Column(name="returnDepositDate")
    private Timestamp returnDepositDate;

    @Column(name="state",length = 1)
    private Integer state;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseId")
    private Purchase purchase;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Timestamp getReturnGoodsDate() {
        return returnGoodsDate;
    }

    public void setReturnGoodsDate(Timestamp returnGoodsDate) {
        this.returnGoodsDate = returnGoodsDate;
    }

    public Timestamp getReturnDepositDate() {
        return returnDepositDate;
    }

    public void setReturnDepositDate(Timestamp returnDepositDate) {
        this.returnDepositDate = returnDepositDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}