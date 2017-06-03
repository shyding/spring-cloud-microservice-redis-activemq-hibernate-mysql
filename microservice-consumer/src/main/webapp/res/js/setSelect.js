var selector = (function($){
    "use strict";
    var isInited = false;
    var setFuncIndex = "";

    function setSelect(selectIds, showProperties, valueProperties, initPropertyValues, urls, queryJson, queryProperties, index) {
        if (index == 0) {
            isInited = false;
            setFuncIndex = "";
        }

        var select = $(document.getElementById(selectIds[index]));
        select.append("<option value=\"\">请选择</option>");

        $.ajax({
            type: "get",
            url: urls[index],
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: queryJson},
            dataType: "json",

            success: function(items){
                $.each(items, function(id, item){
                    var showPropertyValue = null, valuePropertyValue = null;

                    $.each(item, function (propertyName, propertyValue){
                        if (showProperties[index] == propertyName) {
                            showPropertyValue = propertyValue;
                        }

                        if (valueProperties[index] == propertyName) {
                            valuePropertyValue = propertyValue;
                        }
                    });

                    if (showPropertyValue != null && valuePropertyValue != null) {
                        var selected = "";
                        if (initPropertyValues[index] != null && initPropertyValues[index] != undefined) {
                            var initPropertyValuesArr = initPropertyValues[index].toString().split(",");

                            for (var key in initPropertyValuesArr) {
                                if (initPropertyValuesArr[key] == valuePropertyValue) {
                                    selected = "selected";
                                    break;
                                }
                            }
                        }

                        select.append("<option value=\'" + valuePropertyValue +"\' " + selected + ">" + showPropertyValue + "</option>");
                    }
                });

                //设置级联下拉框函数
                if (selectIds[index+1] != null && selectIds[index+1] != undefined) {
                    var funcIndex = "#"+index+"#";
                    if (setFuncIndex.indexOf(funcIndex) == -1) {
                        $(document.getElementById(selectIds[index])).change(function () {

                            $(document.getElementById(selectIds[index+1])).empty();

                            var queryJson;
                            var startPos = queryProperties[index].indexOf("["), endPos = queryProperties[index].indexOf("]");

                            if (startPos != -1 && endPos != -1) {
                                var entity = queryProperties[index].substring(0, startPos),
                                    property = queryProperties[index].substring(startPos+1, endPos);

                                queryJson = "{\"" + entity + "\":{\"" + property +"\":\"" + $(document.getElementById(selectIds[index])).val() + "\"}}";
                            } else {
                                queryJson = "{\"" + queryProperties[index] + "\":\"" + $(document.getElementById(selectIds[index])).val() + "\"}";
                            }

                            setSelect(selectIds, showProperties, valueProperties, initPropertyValues, urls, queryJson, queryProperties, ++index);
                            --index;
                        });

                        setFuncIndex += funcIndex;
                    }

                    //初始化下拉框
                    if (!isInited) {
                        var queryJson1 = queryJson;
                        if (initPropertyValues[index]  != null && initPropertyValues[index] != undefined &&
                            initPropertyValues[index] != "") {
                            var initPropertyValuesArr = initPropertyValues[index].toString().split(",");
                            var entitiesStr = "";

                            var startPos = queryProperties[index].indexOf("["), endPos = queryProperties[index].indexOf("]");
                            if (startPos != -1 && endPos != -1) {
                                var entity = queryProperties[index].substring(0, startPos),
                                    property = queryProperties[index].substring(startPos+1, endPos);

                                for (var key in initPropertyValuesArr) {
                                    if (initPropertyValuesArr[key] != "") {
                                        entitiesStr += "{\"" + property +"\":\"" + initPropertyValuesArr[key] + "\"},";
                                    }
                                }

                                entitiesStr = entitiesStr.substr(0, entitiesStr.length-1);
                                if (entitiesStr.indexOf("},{") > 0) {
                                    queryJson1 = "{\"" + entity + "\":[" + entitiesStr + "]}";
                                } else {
                                    queryJson1 = "{\"" + entity + "\":" + entitiesStr + "}";
                                }

                            } else {
                                for (var key in initPropertyValuesArr) {
                                    if (initPropertyValuesArr[key] != "") {
                                        entitiesStr += "{\"" + queryProperties[index] +"\":\"" + initPropertyValuesArr[key] + "\"},";
                                    }
                                }

                                entitiesStr = entitiesStr.substr(0, entitiesStr.length-1);
                                if (entitiesStr.indexOf("},{") > 0) {
                                    queryJson1 = "[" + entitiesStr + "]";
                                } else {
                                    queryJson1 = entitiesStr;
                                }
                            }
                        }

                        setSelect(selectIds, showProperties, valueProperties, initPropertyValues, urls, queryJson1, queryProperties, ++index);
                        if(selectIds[index+1] == null || selectIds[index+1] == undefined){
                            isInited = true;
                        }
                        --index;
                    }
                }
            }
        });
    }

    return {
        setSelect  : setSelect
    }
})(jQuery);
