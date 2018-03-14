package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * 摘自：http://www.javanb.com/java/1/17816.html
 */
@Component
public class Des {
    /** 加密、解密key. */
    private final String PASSWORD_CRYPT_KEY = "l3bxztxDjQLIYRPh8ntzSakMGezqBUPtzNBHK0j7fzqwg5DzDdlS1MnXfwe34eF9l3bxztxDjQLdbcvb56DDsBUPtzNBHK0j7fzqwg5DzDdlS1MnXfwe34eF9vcd45SL";

    /** 加密算法,可用 DES,DESede,Blowfish. */
    private final String ALGORITHM = "DES";

    /** 从原始密匙数据创建一个DESKeySpec对象 */
    private final DESKeySpec dks = new DESKeySpec(PASSWORD_CRYPT_KEY.getBytes());
    /** 创建一个密匙工厂，然后用它把DESKeySpec对象转换成一个SecretKey对象 */
    private final SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(dks);
    /** Cipher对象实际完成解密操作 */
    private final Cipher cipher = Cipher.getInstance(ALGORITHM);

    public Des() throws Exception {}

    /**
     * 对数据进行DES解密.
     * @param data 待进行DES加密的数据
     * @return 返回经过DES加密后的数据
     * @throws Exception
     * @author <a href="mailto:xiexingxing1121@126.com">AmigoXie</a>
     * Creation date: 2007-7-31 - 下午12:06:24
     */
    public String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对数据进行DES加密.
     * @param data DES加密数据
     * @return 返回解密后的数据
     * @throws Exception
     * @author <a href="mailto:xiexingxing1121@126.com">AmigoXie</a>
     * Creation date: 2007-7-31 - 下午12:07:54
     */
    public String encrypt(String data) {
        try {
            return byte2hex(encrypt(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 用指定的key对数据进行DES加密.
     * @param data 待加密的数据
     * @return 返回DES加密后的数据
     * @throws Exception
     * @author <a href="mailto:xiexingxing1121@126.com">AmigoXie</a>
     * Creation date: 2007-7-31 - 下午12:09:03
     */
    private byte[] encrypt(byte[] data) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(data);
    }

    /**
     * 用指定的key对数据进行DES解密.
     * @param data 待解密的数据
     * @return 返回DES解密后的数据
     * @throws Exception
     * @author <a href="mailto:xiexingxing1121@126.com">AmigoXie</a>
     * Creation date: 2007-7-31 - 下午12:10:34
     */
    private byte[] decrypt(byte[] data) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(data);
    }

    public byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }
}