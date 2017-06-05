package com.hzg.sys;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "hzg_sys_audit_flow")
public class AuditFlow  implements Serializable {
    private static final long serialVersionUID = 345435245233225L;

    public AuditFlow(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", length = 11)
    private Integer id;

    @Column(name="name",length=64)
    private String name;

    @Column(name="inputDate")
    private Timestamp inputDate;

    @Column(name="state",length = 1)
    private Integer state;

    @Column(name="entity",length=32)
    private String entity;

    @ManyToOne(cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;

    @OneToMany(mappedBy = "auditFlow", cascade=CascadeType.DETACH, fetch = FetchType.LAZY)
    private Set<AuditFlowNode> auditFlowNodes;

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<AuditFlowNode> getAuditFlowNodes() {
        return auditFlowNodes;
    }

    public void setAuditFlowNodes(Set<AuditFlowNode> auditFlowNodes) {
        this.auditFlowNodes = auditFlowNodes;
    }
}
