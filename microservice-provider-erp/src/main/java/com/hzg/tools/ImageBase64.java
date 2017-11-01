package com.hzg.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 图片 base64 编码
 * from : http://www.cnblogs.com/interdrp/archive/2013/03/04/2942551.html
 * @Date 2017/11/1
 */
@Component
public class ImageBase64 {
    private BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    private BASE64Decoder decoder = new sun.misc.BASE64Decoder();

    public String imageToBase64(String imagePath){
        File f = new File(imagePath);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();

            return encoder.encodeBuffer(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void base64ToImage(String base64String, String imagePath){
        try {
            byte[] bytes = decoder.decodeBuffer(base64String);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage bi1 =ImageIO.read(bais);
            File w2 = new File(imagePath);//可以是jpg,png,gif格式
            ImageIO.write(bi1, "jpg", w2);//不管输出什么格式图片，此处不需改动
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String imageToBase64(byte[] imageBytes){
        return encoder.encodeBuffer(imageBytes).trim();
    }

    public byte[] base64ToImage(String base64String){
        try {
            return decoder.decodeBuffer(base64String);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}