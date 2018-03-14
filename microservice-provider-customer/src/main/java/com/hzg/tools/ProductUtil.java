package com.hzg.tools;

import org.springframework.stereotype.Component;

@Component
public class ProductUtil {
    public String getPropertyCode(String typeName, String propertyName){
        switch (propertyName) {
            case "镶嵌材质": return "mountMaterial";
            case "特性": return "quality";
            case "颜色": return "color";
            case "种类": return "type";
            case "尺寸": return "size";
            case "重量": return "weight";
            case "瑕疵": return "flaw";
            case "题材": return "theme";
            case "款式": return "style";
            case "透明度": return "transparency";
            case "雕工": return "carver";
            case "产地": return "originPlace";
            case "形状": return "shape";
        }

        switch (typeName) {
            case "翡翠": {
                switch (propertyName) {
                    case "种水": return "quality";
                }
            }

            case "南红": {
                switch (propertyName) {
                    case "性质": return "quality";
                    case "色种": return "color";
                }
            }

            case "蜜蜡": {
                switch (propertyName) {
                    case "性质": return "quality";
                }
            }

            case "绿松石": {
                switch (propertyName) {
                    case "瓷度": return "quality";
                }
            }

            case "琥珀": {
                switch (propertyName) {
                    case "净度": return "quality";
                }
            }

            case "珊瑚": {
                switch (propertyName) {
                    case "瓷度": return "quality";
                }
            }

            case "和田玉": {
                switch (propertyName) {
                    case "料种": return "quality";
                }
            }

            case "黄龙玉": {
                switch (propertyName) {
                    case "料种": return "quality";
                }
            }

            case "青金石": {
                switch (propertyName) {
                    case "等级": return "quality";
                }
            }

            case "钻石": {
                switch (propertyName) {
                    case "净度": return "quality";
                    case "大小": return "size";
                }
            }

            case "金丝楠木": {
                switch (propertyName) {
                    case "料性": return "quality";
                    case "纹路": return "color";
                }
            }

            case "金刚菩提": {
                switch (propertyName) {
                    case "瓣数": return "quality";
                    case "纹路": return "color";
                }
            }

            case "凤眼菩提": {
                switch (propertyName) {
                    case "瓷度": return "quality";
                    case "纹路": return "color";
                    case "珠径": return "size";
                }
            }

            case "黄花梨": {
                switch (propertyName) {
                    case "瓷度": return "quality";
                    case "纹路": return "color";
                    case "地区": return "originPlace";
                }
            }

            case "沉香": {
                switch (propertyName) {
                    case "瓷度": return "quality";
                    case "地区": return "originPlace";
                }
            }
        }

        return propertyName;
    }

    public String getPropertyQuantityType(String propertyName){
        switch (propertyName) {
            case "形状": return "multiple";
        }

        return "single";
    }

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
