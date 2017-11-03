package com.hzg.erp;

import com.hzg.sys.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "hzg_stock_inout")
public class StockInOut implements Serializable {
    private static final long serialVersionUID = 345435245233237L;

    public StockInOut(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=16)
    private String no;

    @Column(name="type",length=2)
    private Integer type;

    @Column(name="describes",length=256)
    private String describes;

    @Column(name="date")
    private Timestamp date;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "inputerId")
    private User inputer;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="state",length = 1)
    private Integer state;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "depositId")
    private StockDeposit deposit;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "processRepairId")
    private StockProcessRepair processRepair;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "changeWarehouseId")
    private StockChangeWarehouse changeWarehouse;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehoustId")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "stockInOut", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<StockInOutDetail> details;

    @OneToMany(mappedBy = "stockInOut", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<StockInOutAction> actions;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public User getInputer() {
        return inputer;
    }

    public void setInputer(User inputer) {
        this.inputer = inputer;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public StockDeposit getDeposit() {
        return deposit;
    }

    public void setDeposit(StockDeposit deposit) {
        this.deposit = deposit;
    }

    public StockProcessRepair getProcessRepair() {
        return processRepair;
    }

    public void setProcessRepair(StockProcessRepair processRepair) {
        this.processRepair = processRepair;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public StockChangeWarehouse getChangeWarehouse() {
        return changeWarehouse;
    }

    public void setChangeWarehouse(StockChangeWarehouse changeWarehouse) {
        this.changeWarehouse = changeWarehouse;
    }

    public Set<StockInOutDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<StockInOutDetail> details) {
        this.details = details;
    }

    public Set<StockInOutAction> getActions() {
        return actions;
    }

    public void setActions(Set<StockInOutAction> actions) {
        this.actions = actions;
    }
}
