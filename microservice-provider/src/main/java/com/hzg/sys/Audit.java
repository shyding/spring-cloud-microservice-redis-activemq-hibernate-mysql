package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "hzg_sys_audit")
public class Audit implements Serializable {

    private static final long serialVersionUID = 345435245233223L;

    public Audit(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=64)
    private String name;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Column(name="result",length=1)
    private String result;

    @Column(name="dealDate")
    private Timestamp dealDate;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="remark",length=64)
    private String remark;

    @Column(name="entity",length=32)
    private String entity;

    @Column(name="entityId", length = 11)
    private Integer entityId;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "refusePostId")
    private Post refusePost;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Timestamp getDealDate() {
        return dealDate;
    }

    public void setDealDate(Timestamp dealDate) {
        this.dealDate = dealDate;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Post getRefusePost() {
        return refusePost;
    }

    public void setRefusePost(Post refusePost) {
        this.refusePost = refusePost;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getStateName() {
        if(state == 0){
            return "待办";
        }else if(state == 1){
            return "已办";
        }

        return "";
    }

    public String getResultName() {
        if  (result != null){
            if (result.equals("Y")) {
                return "已通过";
            } else if (result.equals("N")) {
                return "未通过";
            } else if (result.equals("L")) {
                return "锁定";
            }
        }
        return "";
    }
}