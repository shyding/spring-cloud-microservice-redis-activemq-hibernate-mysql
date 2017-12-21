package com.hzg.customer;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_customer_degree")
public class Degree implements Serializable {
    private static final long serialVersionUID = 345435245233243L;

    public Degree(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="loyalty",length=3)
    private Integer loyalty;

    @Column(name="credit",length=3)
    private Integer credit;

    @Column(name="level",length=12)
    private String level;

    @Column(name="spendAmount",length=32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float spendAmount;

    @Column(name="spendCount",length=3)
    private Integer spendCount;

    @Column(name="spendPoints",length=8)
    private Integer spendPoints;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(Integer loyalty) {
        this.loyalty = loyalty;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Float getSpendAmount() {
        return spendAmount;
    }

    public void setSpendAmount(Float spendAmount) {
        this.spendAmount = spendAmount;
    }

    public Integer getSpendCount() {
        return spendCount;
    }

    public void setSpendCount(Integer spendCount) {
        this.spendCount = spendCount;
    }

    public Integer getSpendPoints() {
        return spendPoints;
    }

    public void setSpendPoints(Integer spendPoints) {
        this.spendPoints = spendPoints;
    }
}
