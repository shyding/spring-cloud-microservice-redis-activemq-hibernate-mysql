package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "hzg_product_own_property")
public class ProductOwnProperty implements Serializable {

    private static final long serialVersionUID = 345435245233234L;

    public ProductOwnProperty(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "propertyId")
    private ProductProperty property;

    @Column(name="name",length=10)
    private String name;

    @Column(name="value",length=40)
    private String value;

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

    public ProductProperty getProperty() {
        return property;
    }

    public void setProperty(ProductProperty property) {
        this.property = property;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}