package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_stock_processrepair")
public class StockProcessRepair implements Serializable {

    private static final long serialVersionUID = 345435245233242L;

    public StockProcessRepair(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="type",length=2)
    private Integer type;

    @Column(name="expense", length = 8, precision = 2)
    private Float expense;

    @Column(name="date")
    private Timestamp date;

    @Column(name="saleExpense", length = 8, precision = 2)
    private Float saleExpense;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Float getExpense() {
        return expense;
    }

    public void setExpense(Float expense) {
        this.expense = expense;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Float getSaleExpense() {
        return saleExpense;
    }

    public void setSaleExpense(Float saleExpense) {
        this.saleExpense = saleExpense;
    }
}