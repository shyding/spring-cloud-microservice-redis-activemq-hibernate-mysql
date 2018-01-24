package com.hzg.erp;

import com.hzg.tools.ErpConstant;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_express_deliver_detail")
public class ExpressDeliverDetail implements Serializable {

    private static final long serialVersionUID = 345435245233253L;

    public ExpressDeliverDetail(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="expressNo", length = 32)
    private String expressNo;

    @Column(name="productNo", length = 16)
    private String productNo;

    @Column(name="quantity", length = 5)
    private Float quantity;

    @Column(name="unit",length=8)
    private String unit;

    @Column(name="weight", length = 9, precision = 2)
    private Float weight;

    @Column(name="price", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float price;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="expressDeliverId")
    private ExpressDeliver expressDeliver;

    @Column(name="mailNo",length=64)
    private String mailNo;

    @Column(name="insure", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float insure;

    @OneToMany(mappedBy = "expressDeliverDetail", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<ExpressDeliverDetailProduct> expressDeliverDetailProducts;

    @Transient
    private String origin;

    @Transient
    private String dest;

    @Transient
    private String result;

    @Transient
    private String mailNoBarcode;

    @Transient
    private String custId;

    @Transient
    private String payType;

    @Transient
    private Integer parcelQuantity;

    @Transient
    private String qzoneMailNo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
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

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public ExpressDeliver getExpressDeliver() {
        return expressDeliver;
    }

    public void setExpressDeliver(ExpressDeliver expressDeliver) {
        this.expressDeliver = expressDeliver;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public Float getInsure() {
        return insure;
    }

    public void setInsure(Float insure) {
        this.insure = insure;
    }

    public Set<ExpressDeliverDetailProduct> getExpressDeliverDetailProducts() {
        return expressDeliverDetailProducts;
    }

    public void setExpressDeliverDetailProducts(Set<ExpressDeliverDetailProduct> expressDeliverDetailProducts) {
        this.expressDeliverDetailProducts = expressDeliverDetailProducts;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMailNoBarcode() {
        return mailNoBarcode;
    }

    public void setMailNoBarcode(String mailNoBarcode) {
        this.mailNoBarcode = mailNoBarcode;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Float getTotalWeight() {
        if (weight != null) {
            if (weight.compareTo(0f) != 0) {
                if (unit.equals(ErpConstant.unit_g) || unit.equals(ErpConstant.unit_kg) ||
                        unit.equals(ErpConstant.unit_ct) || unit.equals(ErpConstant.unit_oz)) {
                    return weight;
                } else {
                    return Float.valueOf(String.valueOf(Math.round(weight * quantity * 1000)/1000));
                }
            }
        }

        return null;
    }

    public Integer getParcelQuantity() {
        return parcelQuantity;
    }

    public void setParcelQuantity(Integer parcelQuantity) {
        this.parcelQuantity = parcelQuantity;
    }

    public String getQzoneMailNo() {
        return qzoneMailNo;
    }

    public void setQzoneMailNo(String qzoneMailNo) {
        this.qzoneMailNo = qzoneMailNo;
    }

    @Override
    public String toString() {
        return "ExpressDeliverDetail{" +
                "id=" + id +
                ", expressNo='" + expressNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", weight='" + weight + '\'' +
                ", price=" + price +
                ", state=" + state +
                '}';
    }
}