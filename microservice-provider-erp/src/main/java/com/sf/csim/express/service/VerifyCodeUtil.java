//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sf.csim.express.service;

import java.io.FileInputStream;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class VerifyCodeUtil {
    public VerifyCodeUtil() {
    }

    public String loadFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            byte[] e = new byte[fis.available()];
            fis.read(e);
            String res = new String(e);
            fis.close();
            return res;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public String md5EncryptAndBase64(String str) {
        return encodeBase64(md5Encrypt(str));
    }

    private byte[] md5Encrypt(String encryptStr) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(encryptStr.getBytes("utf8"));
            return e.digest();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    private String encodeBase64(byte[] b) {
        String str = (new Base64()).encodeAsString(b);
        return str;
    }
}
