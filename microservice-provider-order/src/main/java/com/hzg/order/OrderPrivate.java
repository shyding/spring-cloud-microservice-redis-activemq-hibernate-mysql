package com.hzg.order;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_order_private")
public class OrderPrivate implements Serializable {
    private static final long serialVersionUID = 345435245233245L;

    public OrderPrivate(){
        super();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", length=11)
    private Integer id;

    @Column(name="type",length=1)
    private Integer type;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderDetailId")
    private OrderDetail detail;

    @Column(name="describes",length=255)
    private String describes;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizerId")
    private OrderPrivateAuthorize authorize;

    @OneToMany(mappedBy = "orderPrivate", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<OrderPrivateAcc> accs;

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

    public OrderDetail getDetail() {
        return detail;
    }

    public void setDetail(OrderDetail detail) {
        this.detail = detail;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public OrderPrivateAuthorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(OrderPrivateAuthorize authorize) {
        this.authorize = authorize;
    }

    public Set<OrderPrivateAcc> getAccs() {
        return accs;
    }

    public void setAccs(Set<OrderPrivateAcc> accs) {
        this.accs = accs;
    }

    public String getTypeName() {
        switch (type) {
            case 0 : return "商品加工";
            case 1 : return "私人订制";
            default : return "";
        }
    }
}
