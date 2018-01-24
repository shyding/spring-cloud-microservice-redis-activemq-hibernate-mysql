package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_stock_changewarehouse")
public class StockChangeWarehouse implements Serializable {

    private static final long serialVersionUID = 345435245233247L;

    public StockChangeWarehouse(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "targetWarehouseId")
    private Warehouse targetWarehouse;

    @Column(name="state",length = 1)
    private Integer state;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Warehouse getTargetWarehouse() {
        return targetWarehouse;
    }

    public void setTargetWarehouse(Warehouse targetWarehouse) {
        this.targetWarehouse = targetWarehouse;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateName() {
        switch (state) {
            case 0 : return "调仓未完成";
            case 1 : return "调仓完成";
            default : return "";
        }
    }
}