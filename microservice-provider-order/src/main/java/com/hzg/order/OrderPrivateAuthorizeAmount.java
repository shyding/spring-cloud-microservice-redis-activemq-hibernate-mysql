package com.hzg.order;

import com.hzg.sys.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_order_private_authorizeamount")
public class OrderPrivateAuthorizeAmount implements Serializable {

    private static final long serialVersionUID = 345435245233248L;

    public OrderPrivateAuthorizeAmount(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderPrivateId")
    private OrderPrivate orderPrivate;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name="date")
    private Timestamp date;

    @Column(name="describes",length=255)
    private String describes;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderPrivate getOrderPrivate() {
        return orderPrivate;
    }

    public void setOrderPrivate(OrderPrivate orderPrivate) {
        this.orderPrivate = orderPrivate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }
}