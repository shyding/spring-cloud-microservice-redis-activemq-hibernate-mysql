package com.hzg.erp;

import com.hzg.pay.Pay;
import com.hzg.sys.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity(name = "hzg_purchase")
public class Purchase implements Serializable {
    private static final long serialVersionUID = 345435245233226L;

    public Purchase(){
        super();
    }

    public Purchase(Integer id){
        super();
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=16)
    private String no;

    @Column(name="name",length=60)
    private String name;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User charger;

    @Column(name="type",length=1)
    private Integer type;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="amount", length = 10, precision = 2)
    private Float amount;

    @Column(name="date")
    private Timestamp date;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "inputerId")
    private User inputer;

    @Column(name="describes",length=256)
    private String describes;

    @OneToMany(mappedBy = "purchase", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<PurchaseDetail> details;

    @Transient
    private List<Pay> pays;

    @Transient
    private PurchaseBook purchaseBook;

    @Transient
    private Integer temporaryPurchasePayKind;

    @Transient
    private Boolean purchaseBookPaid;

    @Transient
    private Boolean toPayBalance;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCharger() {
        return charger;
    }

    public void setCharger(User charger) {
        this.charger = charger;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public Set<PurchaseDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<PurchaseDetail> details) {
        this.details = details;
    }

    public List<Pay> getPays() {
        return pays;
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
    }

    public PurchaseBook getPurchaseBook() {
        return purchaseBook;
    }

    public void setPurchaseBook(PurchaseBook purchaseBook) {
        this.purchaseBook = purchaseBook;
    }

    public Integer getTemporaryPurchasePayKind() {
        return temporaryPurchasePayKind;
    }

    public void setTemporaryPurchasePayKind(Integer temporaryPurchasePayKind) {
        this.temporaryPurchasePayKind = temporaryPurchasePayKind;
    }

    public Boolean getPurchaseBookPaid() {
        return purchaseBookPaid;
    }

    public void setPurchaseBookPaid(Boolean purchaseBookPaid) {
        this.purchaseBookPaid = purchaseBookPaid;
    }

    public Boolean getToPayBalance() {
        return toPayBalance;
    }

    public void setToPayBalance(Boolean toPayBalance) {
        this.toPayBalance = toPayBalance;
    }
}
