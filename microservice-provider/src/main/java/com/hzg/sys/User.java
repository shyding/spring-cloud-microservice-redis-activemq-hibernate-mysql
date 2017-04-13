package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/4/12.
 */
@Entity(name = "hzg_sys_user")
public class User  implements Serializable {

    private static final long serialVersionUID = 345435245233221L;

    public User(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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

    @Column(name="postId", length = 11)
    private Integer postId;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="passModifyDate")
    private Timestamp passModifyDate;

    @Column(name="state",length = 1)
    private Integer state;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", postId=" + postId +
                ", inputDate=" + inputDate +
                ", passModifyDate=" + passModifyDate +
                ", state=" + state +
                '}';
    }

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

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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
}