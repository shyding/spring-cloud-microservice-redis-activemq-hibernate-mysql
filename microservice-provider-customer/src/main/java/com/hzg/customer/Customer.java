package com.hzg.customer;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "hzg_customer")
public class Customer implements Serializable {
    private static final long serialVersionUID = 345435245233242L;

    public Customer(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=20)
    private String name;

    @Column(name="phone",length=64)
    @Type(type = "com.hzg.tools.StringDesType")
    private String phone;

    @Column(name="mobile",length=64)
    @Type(type = "com.hzg.tools.StringDesType")
    private String mobile;

    @Column(name="address",length=256)
    @Type(type = "com.hzg.tools.StringDesType")
    private String address;

    @Column(name="gender",length=6)
    private String gender;

    @Column(name="birthday",length=32)
    @Type(type = "com.hzg.tools.StringDesType")
    private String birthday;

    @Column(name="email",length=128)
    @Type(type = "com.hzg.tools.StringDesType")
    private String email;

    @Column(name="creType",length=10)
    private String creType;

    @Column(name="creNo",length=128)
    @Type(type = "com.hzg.tools.StringDesType")
    private String creNo;

    @Column(name="photoUrl",length=30)
    private String photoUrl;

    @Column(name="position",length=16)
    private String position;

    @Column(name="hirer",length=30)
    private String hirer;

    @Column(name="jobType",length=16)
    private String jobType;

    @Column(name="otherContact",length=40)
    private String otherContact;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @OneToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "degreeId")
    private Degree degree;

    @OneToMany(mappedBy = "customer", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<User> users;

    @OneToMany(mappedBy = "customer", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<Express> expresses;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreType() {
        return creType;
    }

    public void setCreType(String creType) {
        this.creType = creType;
    }

    public String getCreNo() {
        return creNo;
    }

    public void setCreNo(String creNo) {
        this.creNo = creNo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHirer() {
        return hirer;
    }

    public void setHirer(String hirer) {
        this.hirer = hirer;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Express> getExpresses() {
        return expresses;
    }

    public void setExpresses(Set<Express> expresses) {
        this.expresses = expresses;
    }
}
