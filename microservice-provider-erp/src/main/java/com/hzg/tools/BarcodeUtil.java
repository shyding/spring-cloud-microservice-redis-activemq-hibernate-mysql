package com.hzg.tools;
 
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
 
import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.stereotype.Component;

/**
 * 条形码工具类
 *
 * @author tangzz
 * @createDate 2015年9月17日
 * from: http://www.cnblogs.com/littleatp/p/4815921.html
 * @date 2017/11/1
 */
@Component
public class BarcodeUtil {
 
    /**
     * 生成文件
     *
     * @param codeStr
     * @param path
     * @return
     */
    public File generateFile(String codeStr, String path) {
        File file = new File(path);
        try {
            generate(codeStr, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
 
    /**
     * 生成字节
     *
     * @param codeStr
     * @return
     */
    public byte[] generate(String codeStr) {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        generate(codeStr, ous);
        return ous.toByteArray();
    }
 
    /**
     * 生成到流
     *
     * @param codeStr
     * @param ous
     */
    public void generate(String codeStr, OutputStream ous) {
        if (StringUtils.isEmpty(codeStr) || ous == null) {
            return;
        }
 
        Code39Bean bean = new Code39Bean();
 
        // 精细度
        final int dpi = 150;
        // module宽度
        final double moduleWidth = UnitConv.in2mm(1.0f / dpi);
 
        // 配置对象
        bean.setModuleWidth(moduleWidth);
        bean.setWideFactor(3);
        bean.doQuietZone(false);
 
        String format = "image/png";
        try {
 
            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
 
            // 生成条形码
            bean.generateBarcode(canvas, codeStr);
 
            // 结束绘制
            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}