package com.hzg.erp;

import com.hzg.sys.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "hzg_product_describe")
public class ProductDescribe implements Serializable {

    private static final long serialVersionUID = 345435245233231L;

    public ProductDescribe(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="describes",length=256)
    private String describes;

    @Column(name="imageParentDirPath",length=30)
    private String imageParentDirPath;

    @Column(name="videoParentDirPath",length=30)
    private String videoParentDirPath;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "photographerId")
    private User photographer;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "editorId")
    private User editor;

    @Column(name="date")
    private Timestamp date;

    @Column(name="seoTitle",length=80)
    private String seoTitle;

    @Column(name="seoKeyword",length=80)
    private String seoKeyword;

    @Column(name="seoDesc",length=100)
    private String seoDesc;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getImageParentDirPath() {
        return imageParentDirPath;
    }

    public void setImageParentDirPath(String imageParentDirPath) {
        this.imageParentDirPath = imageParentDirPath;
    }

    public String getVideoParentDirPath() {
        return videoParentDirPath;
    }

    public void setVideoParentDirPath(String videoParentDirPath) {
        this.videoParentDirPath = videoParentDirPath;
    }

    public User getPhotographer() {
        return photographer;
    }

    public void setPhotographer(User photographer) {
        this.photographer = photographer;
    }

    public User getEditor() {
        return editor;
    }

    public void setEditor(User editor) {
        this.editor = editor;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoKeyword() {
        return seoKeyword;
    }

    public void setSeoKeyword(String seoKeyword) {
        this.seoKeyword = seoKeyword;
    }

    public String getSeoDesc() {
        return seoDesc;
    }

    public void setSeoDesc(String seoDesc) {
        this.seoDesc = seoDesc;
    }
}