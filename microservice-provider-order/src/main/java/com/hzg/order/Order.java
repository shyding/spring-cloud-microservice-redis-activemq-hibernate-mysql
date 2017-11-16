package com.hzg.order;

import com.hzg.customer.User;
import com.hzg.pay.Pay;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity(name = "hzg_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 345435245233238L;

    public Order(){
        super();
    }

    public Order(Integer id){
        super();
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="no",length=16)
    private String no;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="type",length = 1)
    private Integer type;

    @Column(name="amount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float amount;

    @Column(name="discount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float discount;

    @Column(name="payAmount", length = 32)
    @Type(type = "com.hzg.tools.FloatDesType")
    private Float payAmount;

    @Column(name="date")
    private Timestamp date;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "salerId")
    private com.hzg.sys.User saler;

    @OneToMany(mappedBy = "order", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<OrderDetail> details;

    @OneToMany(mappedBy = "order", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<OrderGift> gifts;

    @Transient
    private List<Pay> pays;

    @Transient
    private String sessionId;

    @Transient
    private String orderSessionId;

    @Transient
    private OrderBook orderBook;


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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Float payAmount) {
        this.payAmount = payAmount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public com.hzg.sys.User getSaler() {
        return saler;
    }

    public void setSaler(com.hzg.sys.User saler) {
        this.saler = saler;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOrderSessionId() {
        return orderSessionId;
    }

    public void setOrderSessionId(String orderSessionId) {
        this.orderSessionId = orderSessionId;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public Set<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<OrderDetail> details) {
        this.details = details;
    }

    public Set<OrderGift> getGifts() {
        return gifts;
    }

    public void setGifts(Set<OrderGift> gifts) {
        this.gifts = gifts;
    }

    public List<Pay> getPays() {
        return pays;
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
    }

    public String getStateName() {
        switch (state) {
            case 0 : return "未支付";
            case 1 : return "已支付";
            case 2 : return "取消";
            case 3 : return "已退款";
            default : return "";
        }
    }

    public String getTypeName() {
        switch (type) {
            case 0 : return "自助下单";
            case 1 : return "代下单";
            case 2 : return "私人订制";
            case 3 : return "预定";
            case 4 : return "代下单加工";
            default : return "";
        }
    }
}
