var tableSheet = (function ($) {
    "use strict";

    var contextPath = "";

    var suggestsProperties = {
        "mountMaterial":{"selector":"[data-property-name=\"mountMaterial\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"镶嵌材质","types":[{"id":-1}]}},
        "quality":{"selector":"[data-property-name=\"quality\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"特性","types":[{"id":-1}]}},
        "color":{"selector":"[data-property-name=\"color\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"颜色","types":[{"id":-1}]}},
        "type":{"selector":"[data-property-name=\"type\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"种类","types":[{"id":-1}]}},
        "size":{"selector":"[data-property-name=\"size\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"尺寸","types":[{"id":-1}]}},
        "weight":{"selector":"[data-property-name=\"weight\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"重量","types":[{"id":-1}]}},
        "flaw":{"selector":"[data-property-name=\"flaw\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"瑕疵","types":[{"id":-1}]}},
        "theme":{"selector":"[data-property-name=\"theme\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"题材","types":[{"id":-1}]}},
        "style":{"selector":"[data-property-name=\"style\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"款式","types":[{"id":-1}]}},
        "transparency":{"selector":"[data-property-name=\"transparency\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"透明度","types":[{"id":-1}]}},
        "carver":{"selector":"[data-property-name=\"carver\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"雕工","types":[{"id":-1}]}},
        "originPlace":{"selector":"[data-property-name=\"originPlace\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"产地","types":[{"id":-1}]}},
        "shape":{"selector":"[data-property-name=\"shape\"]", "uri":"/erp/privateQuery/productProperty", "json": {"name":"形状","types":[{"id":-1}]}}
    };

    var trHtml = "";

    function init(tableId, rowCount, rootPath){
        contextPath = rootPath;

        trHtml = "<tr>" + $("#" + tableId + " tbody:last-child").html() + "</tr>";

        var tbodyHtml = "";
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            tbodyHtml += trHtml;
        }
        $("#" + tableId + " tbody").append(tbodyHtml);

        suggests(null, "mountMaterial", contextPath);
        suggests(null, "quality", contextPath);
        suggests(null, "color", contextPath);
        suggests(null, "type", contextPath);
        suggests(null, "size", contextPath);
        suggests(null, "weight", contextPath);
        suggests(null, "flaw", contextPath);
        suggests(null, "theme", contextPath);
        suggests(null, "style", contextPath);
        suggests(null, "transparency", contextPath);
        suggests(null, "carver", contextPath);
        suggests(null, "originPlace", contextPath);
        suggests(null, "shape", contextPath);
    }

    function addRow(tableId) {
        if (trHtml != null) {
            $("#" + tableId + " tbody").append(trHtml);
        } else {
            $("#" + tableId + " tbody").append("<tr>" + $("#" + tableId + " tbody:last-child").html() + "</tr>");
        }

        var trs = $("#" + tableId + " tbody tr");

        $.each($(trs[trs.length-1]).find("input"), function(ci, item){
            var propertyName = item.dataset.propertyName;
            if (propertyName != undefined) {
                suggests($(item), propertyName, contextPath);
            }
        });
    }

    function suggests(item, propertyName, contextPath) {
        var target = suggestsProperties[propertyName];
        var suggestInputs = null;

        if (item != null) {
            suggestInputs = item;
        } else if(target != undefined) {
            suggestInputs = $(target["selector"]);
        }

        if (item != null) {
            setInputEvent(item, propertyName)
        } else if (suggestInputs != null) {
            for (var si = 0; si < suggestInputs.length; si++) {
                setInputEvent($(suggestInputs[si]), propertyName);
            }
        }

        try {
            if (suggestInputs != null) {
                suggestInputs.coolautosuggestm({
                    url: contextPath + target["uri"],
                    paramName : 'value',
                    showProperty: 'value',

                    getQueryData: function(paramName){
                        var queryJson = suggestsProperties[propertyName]["json"];
                        if ($.trim(this.value) != "" && paramName != null) {
                            queryJson["\"" + paramName + "\""] =  + this.value;
                        }

                        var typeSelect = $($(this).parent().parent().find("select")[0]);

                        queryJson["types"][0]["id"] = parseInt(typeSelect.val());
                        queryJson["name"] = getChildPropertyName(propertyName, typeSelect.find("option:selected").text());

                        return queryJson;
                    },

                    onSelected:function(result){
                        if(result!=null){
                            var input = $(this).parent().children('input')[1];
                            var value = '{"property":{"id":' + result.id + '},"name":"' + result.name + '", "value":"' + result.value + '"}';

                            if ($(this).data("input-type") == undefined || $(this).data("input-type") == null) {
                                input.value = value;

                            } else {
                                if ($(this).data("input-type") == "multiple") {
                                    if ($.trim(input.value) == "") {
                                        input.value = value;
                                    } else {
                                        $($(this).parent()).append('<input type="hidden" name="' + input.name + '" value="' + value + '" data-skip-falsy="true">');
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    function setInputEvent(item, propertyName) {
        var value = item.val();
        var childPropertyName = getChildPropertyName(propertyName,  item.parent().parent().find("select").find("option:selected").text());

        if (item.data("input-type") != undefined && item.data("input-type") != null) {
            if (item.data("input-type") == "multiple") {
                item.blur(function(){
                   var valueArray = value.split("#");
                   var inputs = item.parent().children('input');

                   var setValues = new Array(), notSetValues = new Array();
                   for (var vai = 0; vai < valueArray.length; vai++) {
                       var isSet = false;

                       for (var ii = 1; ii < inputs.length; ii++) {
                           if ($.trim(valueArray[vai]) != "" && inputs[ii].value.indexOf('"' + valueArray[vai] + '"') != -1) {
                               isSet = true;
                               setValues[setValues.length] = ii;

                               break
                           }
                       }

                       if (isSet == false) {
                           if ($.trim(valueArray[vai]) != "") {
                               notSetValues[notSetValues.length] = vai;
                           }
                       }
                   }

                   var name = inputs[1].name;

                    /**
                     * 移除错误的值
                     */
                   if (setValues.length > 0) {
                       for (ii = 1; ii < inputs.length; ii++) {
                           isSet = false;

                           for (var svi = 0; svi < setValues.length; svi++) {
                               if (ii == setValues[svi]) {
                                   isSet = true;
                                   break;
                               }
                           }

                           if (!isSet) {
                               $(inputs[ii]).remove();
                           }
                       }
                   }

                    /**
                     * 添加没有设置的值
                     */
                   for (var nsvi = 0; nsvi < notSetValues.length; nsvi++) {
                       var itemValue = '{"name":"' + childPropertyName + '", "value":"' + notSetValues[nsvi] + '"}';

                       item.parent().append('<input type="hidden" name="' + name + '" value="' + itemValue + '" data-skip-falsy="true">');
                   }
                });
            }

        } else {
            item.blur(function(){
                var input = item.parent().children('input')[1];

                /**
                 * 不是建议框里的值，则重新复制
                 */
                if (input.val().indexOf('"' + value + '"') == -1) {
                    input.val('{"name":"' + childPropertyName + '", "value":"' + value + '"}');
                }
            });
        }
    }

    function getChildPropertyName(propertyName, typeName) {
        var name = "特性";

        if (propertyName == "quality") {

            if (typeName == "翡翠") {
                name = "种水";
            }
            if (typeName == "南红" || typeName == "蜜蜡") {
                name = "性质";
            }
            if (typeName == "绿松石") {
                name = "瓷度";
            }


            if (typeName == "琥珀") {
                name = "净度";
            }
            if (typeName == "珊瑚") {
                name = "属性";
            }
            if (typeName == "和田玉" || typeName == "黄龙玉") {
                name = "料种";
            }


            if (typeName == "青金石") {
                name = "等级";
            }
            if (typeName == "钻石") {
                name = "净度";
            }


            if (typeName == "金丝楠木") {
                name = "料性";
            }
            if (typeName == "金刚菩提") {
                name = "瓣数";
            }

        }


        if (propertyName == "color") {
            if (typeName == "南红") {
                name = "色种";
            }
            if (typeName == "黄花梨" || typeName == "金丝楠木" || typeName == "金刚菩提" || typeName == "凤眼菩提") {
                name = "纹路";
            }
        }


        if (propertyName == "size") {
            if (typeName == "钻石") {
                name = "大小";
            }
            if (typeName == "凤眼菩提") {
                name = "珠径";
            }
        }


        if (propertyName == "originPlace") {
            if (typeName == "沉香" || typeName == "黄花梨") {
                name = "地区";
            }
        }

        return name;
    }

    return {
        init: init,
        addRow: addRow,
        suggests: suggests
    }
})(jQuery);
