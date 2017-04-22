var selector = (function($){
    "use strict";
    var isInited = false;
    var setFuncIndex = "";

    function setSelect(selectIds, showProperties, valueProperties, initPropertyValues, urls, queryJson, queryProperties, index) {
        var select = $(selectIds[index]);
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
                        if (initPropertyValues[index] != null && initPropertyValues[index] != undefined &&
                            initPropertyValues[index] == valuePropertyValue) {
                            selected = "selected";
                        }

                        select.append("<option value=\'" + valuePropertyValue +"\' " + selected + ">" + showPropertyValue + "</option>");
                    }
                });
                
                //设置级联下拉框函数
                if (selectIds[index+1] != null && selectIds[index+1] != undefined) {
                    var funcIndex = "#"+index+"#";
                    if (setFuncIndex.indexOf(funcIndex) == -1) {
                        $(selectIds[index]).change(function () {

                            $(selectIds[index + 1]).empty();
                            var queryJson = "{\"" + queryProperties[index] + "\":\"" + $(selectIds[index]).val() + "\"}";
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
                            queryJson1 = "{\"" + queryProperties[index] + "\":\"" + initPropertyValues[index] + "\"}";
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
