package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
 * 文件名: User.java
 *
 * @author smjie
 * @version 1.00
 * @Date 2017/4/12
 */
@Entity(name = "hzg_sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 345435245233221L;

    public User(){
        super();
    }

    public User(String username){
        super();
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=20)
    private String name;

    @Column(name="gender",length=6)
    private String gender;

    @Column(name="username",length=20)
    private String username;

    @Column(name="password",length=32)
    private String password;

    @Column(name="email",length=30)
    private String email;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="passModifyDate")
    private Timestamp passModifyDate;

    @Column(name="state",length = 1)
    private Integer state;

    @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "hzg_sys_userpost_relation"
            , joinColumns = {@JoinColumn(name = "userId")}
            , inverseJoinColumns = {@JoinColumn(name = "postId")})
    private Set<Post> posts = new HashSet<Post>();

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Timestamp getPassModifyDate() {
        return passModifyDate;
    }

    public void setPassModifyDate(Timestamp passModifyDate) {
        this.passModifyDate = passModifyDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateName() {
        if(state == 0){
            return "使用";
        }else if(state == 1){
            return "注销";
        }

        return "";
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}