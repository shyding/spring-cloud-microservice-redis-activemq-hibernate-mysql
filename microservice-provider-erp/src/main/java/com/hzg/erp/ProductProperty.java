package com.hzg.erp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "hzg_product_property")
public class ProductProperty implements Serializable {

    private static final long serialVersionUID = 345435245233233L;

    public ProductProperty(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=10)
    private String name;

    @Column(name="value",length=40)
    private String value;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private ProductProperty parent;

    @ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "hzg_producttypeproperty_relate"
            , joinColumns = {@JoinColumn(name = "propertyId")}
            , inverseJoinColumns = {@JoinColumn(name = "typeId")})
    private Set<ProductType> types;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ProductProperty getParent() {
        return parent;
    }

    public void setParent(ProductProperty parent) {
        this.parent = parent;
    }

    public Set<ProductType> getTypes() {
        return types;
    }

    public void setTypes(Set<ProductType> types) {
        this.types = types;
    }
}