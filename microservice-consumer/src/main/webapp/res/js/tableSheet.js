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
            setInputValue(item, propertyName)
        } else if (suggestInputs != null) {
            for (var si = 0; si < suggestInputs.length; si++) {
                setInputValue($(suggestInputs[si]), propertyName);
            }
        }

        try {
            if (suggestInputs != null) {
                suggestInputs.coolautosuggestm({
                    url: contextPath + target["uri"],
                    paramName : 'value',
                    showProperty: 'value',

                    getQueryData: function(paramName){
                       return getQueryData(this, propertyName, paramName);
                    },

                    onSelected:function(result){
                        onSelectedSetValue(this, result);
                    }

                });
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    function getQueryData(inputElement, propertyName, paramName) {
        var queryJson = suggestsProperties[propertyName]["json"];
        if ($.trim(inputElement.value) != "") {
            if ($(inputElement).data("input-type") == undefined || $(inputElement).data("input-type") == null ||
                ($(inputElement).data("input-type") != undefined && $(inputElement).data("input-type") != "multiple")){
                queryJson[paramName] = $.trim(inputElement.value);
            }
        } else {
            delete queryJson[paramName];
        }


        var typeSelect = $($(inputElement).parent().parent().find("select")[0]);

        queryJson["types"] = "[{id:" + parseInt(typeSelect.val()) + "}]";
        var childPropertyName = getChildPropertyName(propertyName, typeSelect.find("option:selected").text());
        if (childPropertyName != null) {
            queryJson["name"] = childPropertyName;
        }

        return queryJson;
    }

    function onSelectedSetValue(inputElement, result) {
        if(result!=null && inputElement.tagName != undefined && inputElement.tagName.toLowerCase() == "input"){
            var input = $(inputElement).parent().children('input')[1];
            var value = '{"property":{"id":' + result.id + '},"name":"' + result.name + '","value":"' + result.value + '"}';

            if ($(inputElement).data("input-type") == undefined || $(inputElement).data("input-type") == null) {
                if (input != undefined) {
                    input.value = value;
                }

            } else {
                if ($(inputElement).data("input-type") == "multiple" && input != undefined) {

                    if ($.trim(input.value) == "") {
                        input.value = value;

                    } else {
                        var inputs = $(inputElement).parent().children('input');

                        var isSet = false;
                        for (var ii = 1; ii < inputs.length; ii++) {
                            if ($.trim(value).indexOf(inputs[ii].value) != -1) {
                                isSet = true;
                                break
                            }
                        }

                        console.log("setSelect value:" + value);
                        for (var k = 0; k < inputs.length; k++) {
                            console.log("setSelect inputs:" + inputs[k].value);
                        }

                        if (!isSet) {
                            $($(inputElement).parent()).append("<input type='hidden' name='" + input.name + "' value='" + value + "' data-skip-falsy='true'>");
                        }
                    }
                }
            }
        }
    }

    function setInputValue(inputElement, propertyName) {
        if (inputElement.data("input-type") != undefined && inputElement.data("input-type") != null && inputElement.data("input-type") == "multiple") {
            inputElement.blur(function() {
                var valueArray = inputElement.val().split("#");
                var inputs = inputElement.parent().children('input');

                var setValues = new Array(), notSetValues = new Array();
                var svl = 0, nsvi = 0;
                for (var vai = 0; vai < valueArray.length; vai++) {
                    var isSet = false;

                    for (var ii = 1; ii < inputs.length; ii++) {
                        if ($.trim(valueArray[vai]) != "" && $.trim(inputs[ii].value).indexOf('"' + $.trim(valueArray[vai]) + '"') != -1) {
                            isSet = true;
                            setValues[svl++] = ii;

                            break;
                        }
                    }

                    if (isSet == false) {
                        if ($.trim(valueArray[vai]) != "") {
                            notSetValues[nsvi++] = vai;
                        }
                    }
                }

                for (var k = 0; k < setValues.length; k++) {
                    console.log("setValues:" + setValues[k]);
                }
                for (var k = 0; k < notSetValues.length; k++) {
                    console.log("notSetValues:" + notSetValues[k]);
                }
                for (var k = 0; k < inputs.length; k++) {
                    console.log("inputs:" + inputs[k].value);
                }

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

                } else {
                    if (inputs.length > 1) {
                        inputs[1].value = "";

                        for (ii = 2; ii < inputs.length; ii++) {
                            $(inputs[ii]).remove();
                        }
                    }
                }

                var name = "details[][product[properties[]]:object";
                if (inputs.length > 1) {
                    name = inputs[1].name;
                }

                /**
                 * 添加没有设置的值
                 */
                for (var nsvi = 0; nsvi < notSetValues.length; nsvi++) {
                    var itemValue = '{"name":"' + getChildPropertyName(propertyName, inputElement.parent().parent().find("select").find("option:selected").text()) + '", "value":"' + $.trim(valueArray[notSetValues[nsvi]]) + '"}';
                    inputElement.parent().append("<input type='hidden' name='" + name + "' value='" + itemValue + "' data-skip-falsy='true'>");
                }
            });

        } else {
            inputElement.blur(function() {
                var input = $(inputElement.parent().children('input')[1]);

                /**
                 * 不是建议框里的值，则重新复制
                 */
                if (input.val().indexOf('"' + inputElement.val() + '"') == -1) {
                    input.val('{"name":"' + getChildPropertyName(propertyName, inputElement.parent().parent().find("select").find("option:selected").text()) + '", "value":"' + inputElement.val() + '"}');
                }
            });
        }
    }

    function getChildPropertyName(propertyName, typeName) {
        var name = suggestsProperties[propertyName]["json"]["name"];

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
