package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/4/12.
 */
@Entity(name = "hzg_sys_post")
public class Post implements Serializable {

    private static final long serialVersionUID = 345435245233219L;

    public Post(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=20)
    private String name;

    @Column(name="deptId",length=11)
    private Integer deptId;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Override
    public String toString() {
        return "post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deptId=" + deptId +
                ", inputDate=" + inputDate +
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

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Timestamp getInputDate() {
        return inputDate;
    }

    public void setInputDate(Timestamp inputDate) {
        this.inputDate = inputDate;
    }
}