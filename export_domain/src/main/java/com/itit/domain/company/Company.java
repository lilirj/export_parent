package com.itit.domain.company;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class Company implements Serializable {
    /**
     * 对象唯一标记，对应数据库主键
     */
    private String id;
    /**
     * 公司名称
     */
    private String name;

    /**
     * 到期时间
     */
    private Date expirationDate;

    /**
     * 公司地址
     */
    private String address;
    /**
     * 营业执照
     */
    private String licenseId;
    /**
     * 法人代表
     */
    private String representative;
    /**
     * 公司电话
     */
    private String phone;
    /**
     * 公司规模
     */
    private String companySize;
    /**
     * 所属行业
     */
    private String industry;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态
     */
    private Integer state;
    /**
     * 当前余额
     */
    private Double balance;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 城市
     */
    private String city;

    //构造方法
    public Company() {
    }

    public Company(String id, String name, Date expirationDate, String address, String licenseId, String representative, String phone, String companySize, String industry, String remarks, Integer state, Double balance, Date createTime, String city) {
        this.id = id;
        this.name = name;
        this.expirationDate = expirationDate;
        this.address = address;
        this.licenseId = licenseId;
        this.representative = representative;
        this.phone = phone;
        this.companySize = companySize;
        this.industry = industry;
        this.remarks = remarks;
        this.state = state;
        this.balance = balance;
        this.createTime = createTime;
        this.city = city;
    }

    //get set方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    //toString方法

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", expirationDate=" + expirationDate +
                ", address='" + address + '\'' +
                ", licenseId='" + licenseId + '\'' +
                ", representative='" + representative + '\'' +
                ", phone='" + phone + '\'' +
                ", companySize='" + companySize + '\'' +
                ", industry='" + industry + '\'' +
                ", remarks='" + remarks + '\'' +
                ", state=" + state +
                ", balance=" + balance +
                ", createTime=" + createTime +
                ", city='" + city + '\'' +
                '}';
    }
}
