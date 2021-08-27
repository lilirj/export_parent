package com.itit.domain.system;

import org.springframework.stereotype.Component;

@Component
public class Dept {

    private String id;
    private String deptName;
    private String state;
    private String companyId;
    private String companyName;

    //当前部门 关联的上级部门
    private Dept parent;

    //构造方法
    public Dept() {
    }

    public Dept(String id, String deptName, String state, String companyId, String companyName, Dept parent) {
        this.id = id;
        this.deptName = deptName;
        this.state = state;
        this.companyId = companyId;
        this.companyName = companyName;
        this.parent = parent;
    }

    //get set 方法

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Dept getParent() {
        return parent;
    }

    public void setParent(Dept parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "id='" + id + '\'' +
                ", deptName='" + deptName + '\'' +
                ", state='" + state + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", parent=" + parent +
                '}';
    }
}
