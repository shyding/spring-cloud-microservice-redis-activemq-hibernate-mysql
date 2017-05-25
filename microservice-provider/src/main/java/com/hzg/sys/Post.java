package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "hzg_sys_post")
public class Post implements Serializable {

    private static final long serialVersionUID = 345435245233220L;

    public Post(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=20)
    private String name;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "deptId")
    private Dept dept;

    @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "hzg_sys_privilege_relation"
            , joinColumns = {@JoinColumn(name = "postId")}
            , inverseJoinColumns = {@JoinColumn(name = "resourceId")})
    private Set<PrivilegeResource> privilegeResources;

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

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public Set<PrivilegeResource> getPrivilegeResources() {
        return privilegeResources;
    }

    public void setPrivilegeResources(Set<PrivilegeResource> privilegeResources) {
        this.privilegeResources = privilegeResources;
    }
}