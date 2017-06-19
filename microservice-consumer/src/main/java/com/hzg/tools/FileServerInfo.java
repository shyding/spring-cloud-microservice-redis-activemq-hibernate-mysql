package com.hzg.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("fileServerInfo")
public class FileServerInfo {
    @Autowired
    public static String uploadFilesUrl;

    @Autowired
    public static String imageServerUrl;

    public static String getUploadFilesUrl() {
        return uploadFilesUrl;
    }

    public static void setUploadFilesUrl(String uploadFilesUrl) {
        FileServerInfo.uploadFilesUrl = uploadFilesUrl;
    }

    public static String getImageServerUrl() {
        return imageServerUrl;
    }

    public static void setImageServerUrl(String imageServerUrl) {
        FileServerInfo.imageServerUrl = imageServerUrl;
    }
}
