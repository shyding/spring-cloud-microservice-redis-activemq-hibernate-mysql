package com.hzg.erp;

import com.hzg.sys.Company;
import com.hzg.sys.Dept;
import com.hzg.sys.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "hzg_product_check")
public class ProductCheck implements Serializable {

    private static final long serialVersionUID = 2123349818797174310L;

    public ProductCheck() {
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;  //商品盘点id

    @Column(name="checkDate")
    private Timestamp checkDate;  //盘点日期

    @Column(name="checkNo",length=15)
    private String checkNo;  //盘点单据编号

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouseId")
    private Warehouse warehouse;  //盘点仓库id

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "deptId")
    private Dept dept;  //盘点部门id

    @Column(name="quantity", length = 8, precision = 2)
    private Float quantity;  //盈亏数量

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;  //盈亏金额

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "chartMakerId")
    private User chartMaker;  //制单人id

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;  //公司id

    @Column(name="remark",length=150)
    private String remark;  //备注

    @Column(name="state",length=1)
    private Integer state;  //状态

    @OneToMany(mappedBy = "productCheck", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ProductCheckDetail> details;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Timestamp checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public User getChartMaker() {
        return chartMaker;
    }

    public void setChartMaker(User chartMaker) {
        this.chartMaker = chartMaker;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Set<ProductCheckDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<ProductCheckDetail> details) {
        this.details = details;
    }
}