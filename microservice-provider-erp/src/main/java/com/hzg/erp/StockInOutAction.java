package com.hzg.erp;

import com.hzg.sys.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_stock_inout_action")
public class StockInOutAction implements Serializable {
    private static final long serialVersionUID = 345435245233251L;

    public StockInOutAction(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="stockInOutId")
    private StockInOut stockInOut;

    @Column(name="type",length=1)
    private Integer type;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "inputerId")
    private User inputer;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public User getInputer() {
        return inputer;
    }

    public void setInputer(User inputer) {
        this.inputer = inputer;
    }
}
