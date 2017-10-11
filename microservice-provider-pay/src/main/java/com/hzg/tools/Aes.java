package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;

import java.security.SecureRandom;



import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;

import javax.crypto.KeyGenerator;

import javax.crypto.NoSuchPaddingException;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;


/**
 * 摘自：http://www.cnblogs.com/bao521/p/6275455.html
 */
@Component
public class Aes {



      /*

     * 算法/模式/填充 16字节加密后数据长度 不满16字节加密后长度

     * AES/CBC/NoPadding 16 不支持

     * AES/CBC/PKCS5Padding 32 16

     * AES/CBC/ISO10126Padding 32 16

     * AES/CFB/NoPadding 16 原始数据长度

     * AES/CFB/PKCS5Padding 32 16

     * AES/CFB/ISO10126Padding 32 16

     * AES/ECB/NoPadding 16 不支持

     * AES/ECB/PKCS5Padding 32 16

     * AES/ECB/ISO10126Padding 32 16

     * AES/OFB/NoPadding 16 原始数据长度

     * AES/OFB/PKCS5Padding 32 16

     * AES/OFB/ISO10126Padding 32 16

     * AES/PCBC/NoPadding 16 不支持

     * AES/PCBC/PKCS5Padding 32 16

     * AES/PCBC/ISO10126Padding 32 16

     *

     * 注：

     * 1、JCE中AES支持五中模式：CBC，CFB，ECB，OFB，PCBC；支持三种填充：NoPadding，PKCS5Padding，ISO10126Padding。 不带模式和填充来获取AES算法的时候，其默认使用ECB/PKCS5Padding。

     * 2、Java支持的密钥长度：keysize must be equal to 128, 192 or 256

     * 3、Java默认限制使用大于128的密钥加密（解密不受限制），报错信息：java.security.InvalidKeyException: Illegal key size or default parameters

     * 4、下载并安装JCE Policy文件即可突破128密钥长度的限制：覆盖jre\lib\security目录下local_policy.jar、US_export_policy.jar文件即可

     * 5、除ECB外，需提供初始向量（IV），如：Cipher.init(opmode, key, new IvParameterSpec(iv)), 且IV length: must be 16 bytes long

     */





    public SecretKeySpec bulidKey(String password) throws NoSuchAlgorithmException {

        KeyGenerator kgen = KeyGenerator.getInstance("AES");     //AES 加密  默认的是这种  ECB/PKCS5Padding

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");    //SHA1PRANG 就是一种算法的名称    这里使用据说有补位的作用可以适应不同机器 所以要用就用着这一种

        secureRandom.setSeed(password.getBytes());        //  根据种子password.getBytes() 获取随机值 可以单用     SecureRandom 继承 Random

        kgen.init(256, secureRandom);

        SecretKey secretKey = kgen.generateKey();               //下面就是生成加密的key了

        byte[] enCodeFormat = secretKey.getEncoded();

        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

        return key;

    }



    /**

     * 加密

     *

     * @param content

     *            需要加密的内容

     * @param password

     *            加密密码

     * @return

     */

    public byte[] encrypt(String content, String password) {

        try {

            SecretKeySpec key = bulidKey(password);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化



            byte[] byteContent = content.getBytes("gbk");    //这里是应编码了 可以不写gbk 就是用的默认编码这样的话 下面的解密gbk 也要去掉

            byte[] result = cipher.doFinal(byteContent);

            return result; // 加密

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (NoSuchPaddingException e) {

            e.printStackTrace();

        } catch (InvalidKeyException e) {

            e.printStackTrace();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (IllegalBlockSizeException e) {

            e.printStackTrace();

        } catch (BadPaddingException e) {

            e.printStackTrace();

        }

        return null;

    }









    //   进行加密 并返回16进制的内容

    public String encrypt2Str(String content, String password) {

        return parseByte2HexStr(encrypt(content,password)); // 加密

    }



    /**

     * 解密

     *

     * @param content

     *            待解密内容

     * @param password

     *            解密密钥

     * @return

     */

    public byte[] decrypt(byte[] content, String password) {

        try {

            SecretKeySpec key = bulidKey(password);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器

            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化

            byte[] result = cipher.doFinal(content);

            return result; // 解密

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (NoSuchPaddingException e) {

            e.printStackTrace();

        } catch (InvalidKeyException e) {

            e.printStackTrace();

        } catch (IllegalBlockSizeException e) {

            e.printStackTrace();

        } catch (BadPaddingException e) {

            e.printStackTrace();

        }

        return null;

    }





    public String decryptString(byte[]content,String password) throws UnsupportedEncodingException {

        return new String(decrypt(content,password),"gbk");

    }






    //将加密后的16进制 转化为二进制 再解密  并编码为字符串

    public String decryptStr(String content, String password) {

        byte[] decryptFrom = parseHexStr2Byte(content);

        byte[] decryptResult = decrypt(decryptFrom, password);

        String result = "--";

        try {

            result = new String(decryptResult,"gbk");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return result;

    }



    /**

     * 将二进制转换成16进制

     *

     * @param buf

     * @return

     */

    public String parseByte2HexStr(byte buf[]) {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < buf.length; i++) {

            String hex = Integer.toHexString(buf[i] & 0xFF);

            if (hex.length() == 1) {

                hex = '0' + hex;

            }

            sb.append(hex.toUpperCase());

        }

        return sb.toString();

    }



    /**

     * 将16进制转换为二进制

     *

     * @param hexStr

     * @return

     */

    public byte[] parseHexStr2Byte(String hexStr) {

        if (hexStr.length() < 1)

            return null;

        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {

            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);

            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),

                    16);

            result[i] = (byte) (high * 16 + low);

        }

        return result;

    }



/*    public void main(String[] args) throws UnsupportedEncodingException {

        String content = "山东省济南市槐荫区中大办事处裕华里";

        String password = "bc29c2f94e0e0355c346892a45bc2b42";

        // 加密971436EC53EED78C0A942FCA7900E1E7F13527CB29268B9B52DA3A6C7F804C1825AF87A0B8F550DE09055D42BED07EB7:长度:96

        System.out.println("加密前：" + content);

        byte[] encryptResult = encrypt(content, password);

        String encryptResultStr = parseByte2HexStr(encryptResult);



        System.out.println("加密后：" + encryptResultStr+":长度:"+encryptResultStr.length());

        // 解密

        String decryptResult = decryptStr(encryptResultStr, password);

        System.out.println("解密后：" + decryptResult);



        //--------------------------------------



        String aa=decryptString(encryptResult,password);

        System.out.println("===直接接受过来的二进制数据解密:"+aa);

    }*/

}