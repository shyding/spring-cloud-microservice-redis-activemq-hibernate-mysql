package com.hzg.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("httpProxyDiscovery")
public class HttpProxyDiscovery {
    private int queryCount = 0;
    private String httpProxyService;
    private String httpProxyIP;
    private int httpProxyPort;
    private String httpProxyPath;

    @Autowired
    private DiscoveryClient discoveryClient;

    public String getHttpProxyAddress(){
        if (httpProxyIP == null || queryCount % 100 == 0) {
            EurekaDiscoveryClient.EurekaServiceInstance serviceInstance =
                    (EurekaDiscoveryClient.EurekaServiceInstance)discoveryClient.getInstances(httpProxyService).get(0);
            httpProxyIP = serviceInstance.getInstanceInfo().getIPAddr();
            httpProxyPort = serviceInstance.getPort();

            queryCount = 0;
        }

        queryCount++;

        return "http://" + httpProxyIP + ":" + httpProxyPort + httpProxyPath;
    }

    public String getHttpProxyService() {
        return httpProxyService;
    }

    public void setHttpProxyService(String httpProxyService) {
        this.httpProxyService = httpProxyService;
    }

    public String getHttpProxyPath() {
        return httpProxyPath;
    }

    public void setHttpProxyPath(String httpProxyPath) {
        this.httpProxyPath = httpProxyPath;
    }

    @Override
    public String toString() {
        return "HttpProxyDiscovery{" +
                "queryCount=" + queryCount +
                ", httpProxyService='" + httpProxyService + '\'' +
                ", httpProxyIP='" + httpProxyIP + '\'' +
                ", httpProxyPort=" + httpProxyPort +
                ", httpProxyPath='" + httpProxyPath + '\'' +
                ", discoveryClient=" + discoveryClient +
                '}';
    }
}
