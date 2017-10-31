package com.hzg.tools;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
 * 文件名: SfExpress.java
 * 类的详细说明
 *
 * @author smjie
 * @version 1.00
 * @Date 2017/10/26
 */
@Component
@ConfigurationProperties("sfExpress")
public class SfExpress {
    private String appId;
    private String appKey;
    private String custId;
    private String payArea;

    private String tokenUri;
    private String tokenRefreshUri;
    private String orderUri;
    private String imageUri;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPayArea() {
        return payArea;
    }

    public void setPayArea(String payArea) {
        this.payArea = payArea;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getTokenRefreshUri() {
        return tokenRefreshUri;
    }

    public void setTokenRefreshUri(String tokenRefreshUri) {
        this.tokenRefreshUri = tokenRefreshUri;
    }

    public String getOrderUri() {
        return orderUri;
    }

    public void setOrderUri(String orderUri) {
        this.orderUri = orderUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
