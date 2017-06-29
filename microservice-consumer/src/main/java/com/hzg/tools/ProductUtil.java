package com.hzg.tools;

import org.springframework.stereotype.Component;

@Component
public class ProductUtil {
    public String getPropertyName(String propertyName, String typeName) {
        String name = null;

        if (propertyName.equals("特性")) {

            if (typeName.equals("翡翠")) {
                name = "种水";
            }
            if (typeName.equals("南红") || typeName.equals("蜜蜡")) {
                name = "性质";
            }
            if (typeName.equals("绿松石")) {
                name = "瓷度";
            }


            if (typeName.equals("琥珀")) {
                name = "净度";
            }
            if (typeName.equals("珊瑚")) {
                name = "属性";
            }
            if (typeName.equals("和田玉") || typeName.equals("黄龙玉")) {
                name = "料种";
            }


            if (typeName.equals("青金石")) {
                name = "等级";
            }
            if (typeName.equals("钻石")) {
                name = "净度";
            }


            if (typeName.equals("金丝楠木")) {
                name = "料性";
            }
            if (typeName.equals("金刚菩提")) {
                name = "瓣数";
            }

        }


        if (propertyName.equals("颜色")) {
            if (typeName.equals("南红")) {
                name = "色种";
            }
            if (typeName.equals("黄花梨") || typeName.equals("金丝楠木") || typeName.equals("金刚菩提") || typeName.equals("凤眼菩提")) {
                name = "纹路";
            }
        }


        if (propertyName.equals("尺寸")) {
            if (typeName.equals("钻石")) {
                name = "大小";
            }
            if (typeName.equals("凤眼菩提")) {
                name = "珠径";
            }
        }


        if (propertyName.equals("产地")) {
            if (typeName.equals("沉香") || typeName.equals("黄花梨")) {
                name = "地区";
            }
        }

        return name == null ? propertyName : name;
    }
}
