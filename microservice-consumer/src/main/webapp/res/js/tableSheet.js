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

        var trs = $("#" + tableId + " tbody tr");

        trHtml = "<tr>" + $(trs[trs.length - 1]).html() + "</tr>";

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

        /*var nos = document.getElementsByName("details[][product[no]]:string");
        for (var i = 0; i < nos.length; i++) {
            valueRepeatJudge(contextPath + '/erp/isValueRepeat/product', nos[i], "no", "details[][product[id]]:number");
        }*/
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

            /*if (item.name == "details[][product[no]]:string") {
                valueRepeatJudge(contextPath + '/erp/isValueRepeat/product', item, "no", "details[][product[id]]:number");
            }*/
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

        if ($(inputElement).parent().parent().find("select").length == 0) {
            typeSelect = $("#type");
        }

        queryJson["types"] = "[{id:" + parseInt(typeSelect.val()) + "}]";
        var childPropertyName = getChildPropertyName(propertyName, typeSelect.find("option:selected").text());
        if (childPropertyName != null) {
            queryJson["name"] = childPropertyName;
        }

        return queryJson;
    }

    function onSelectedSetValue(inputElement, result) {
        if(result!=null && inputElement.is('input')){
            var input = inputElement.parent().children('input')[1];
            var value = '{"property":{"id":' + result.id + '},"name":"' + result.name + '","value":"' + result.value + '"}';

            if (inputElement.data("input-type") == undefined || inputElement.data("input-type") == null || inputElement.data("input-type") == "single") {
                if (input != undefined) {
                    input.value = value;
                }

            } else {
                if (inputElement.data("input-type") == "multiple" && input != undefined) {

                    if ($.trim(input.value) == "") {
                        input.value = value;

                    } else {
                        var inputs = inputElement.parent().children('input');

                        var isSet = false;
                        for (var ii = 1; ii < inputs.length; ii++) {
                            if (value.indexOf(inputs[ii].value) != -1) {
                                isSet = true;
                                break
                            }
                        }

                        if (!isSet) {
                            $(inputElement.parent()).append("<input type='hidden' name='" + input.name + "' value='" + value + "' data-skip-falsy='true'>");
                        }
                    }
                }
            }

            if (inputElement.parent().next() != undefined && inputElement.parent().next().children('input')[0] != undefined) {
                inputElement.parent().next().children('input')[0].focus();
            }
        }
    }

    function setInputValue(inputElement, propertyName) {
        if (inputElement.is('input')) {
            if (inputElement.data("input-type") != undefined && inputElement.data("input-type") != null && inputElement.data("input-type") == "multiple") {
                inputElement.blur(function () {
                    var valueArray = inputElement.val().split("#");
                    var inputs = inputElement.parent().children('input');

                    var setValuesIndex = new Array(), notSetValuesIndex = new Array();
                    var setValueIndex = 0, notSetValueIndex = 0;
                    for (var vai = 0; vai < valueArray.length; vai++) {
                        var isSet = false;

                        for (var ii = 1; ii < inputs.length; ii++) {
                            if ($.trim(valueArray[vai]) != "" && $.trim(inputs[ii].value).indexOf('"' + $.trim(valueArray[vai]) + '"') != -1  && $.trim(inputs[ii].value) != "") {
                                isSet = true;
                                setValuesIndex[setValueIndex++] = ii;

                                break;
                            }
                        }

                        if (isSet == false) {
                            if ($.trim(valueArray[vai]) != "") {
                                notSetValuesIndex[notSetValueIndex++] = vai;
                            }
                        }
                    }

                    /**
                     * 移除错误的值
                     */
                    if (setValuesIndex.length > 0) {
                        for (ii = 1; ii < inputs.length; ii++) {
                            isSet = false;

                            for (var setValueIndex = 0; setValueIndex < setValuesIndex.length; setValueIndex++) {
                                if (ii == setValuesIndex[setValueIndex]) {
                                    isSet = true;
                                    break;
                                }
                            }

                            if (!isSet) {
                                $(inputs[ii]).remove();
                                ii--;
                            }
                        }

                    } else {
                        if (inputs.length > 1) {
                            for (ii = 2; ii < inputs.length; ii++) {
                                $(inputs[ii]).remove();
                                ii--;
                            }

                            inputs[1].value = "";
                        }
                    }

                    var name = "details[][product[properties[]]:object";
                    if (inputs.length > 1) {
                        name = inputs[1].name;
                    }

                    /**
                     * 添加没有设置的值
                     */
                    for (var notSetValueIndex = 0; notSetValueIndex < notSetValuesIndex.length; notSetValueIndex++) {
                        var typeSelect = inputElement.parent().parent().find("select");
                        if (inputElement.parent().parent().find("select").length == 0) {
                            typeSelect = $("#type");
                        }

                        var itemValue = '{"name":"' + getChildPropertyName(propertyName, typeSelect.find("option:selected").text()) + '","value":"' + $.trim(valueArray[notSetValuesIndex[notSetValueIndex]]) + '"}';

                        var isSet = false;
                        for (var ii = 1; ii < inputs.length; ii++) {
                            if (inputs[ii].value.indexOf(itemValue) != -1 && $.trim(inputs[ii].value) != "") {
                                isSet = true;
                                break
                            }
                        }

                        if (!isSet) {
                            if ($.trim(inputs[1].value) == "") {
                                $(inputs[1]).val(itemValue);
                            } else {
                                inputElement.parent().append("<input type='hidden' name='" + name + "' value='" + itemValue + "' data-skip-falsy='true'>");
                            }
                        }

                    }
                });

            } else {
                inputElement.blur(function () {
                    var input = $(inputElement.parent().children('input')[1]);

                    /**
                     * 不是建议框里的值，则重新复制
                     */
                    if (input.val().indexOf('"' + inputElement.val() + '"') == -1) {
                        var typeSelect = inputElement.parent().parent().find("select");
                        if (inputElement.parent().parent().find("select").length == 0) {
                            typeSelect = $("#type");
                        }

                        input.val('{"name":"' + getChildPropertyName(propertyName, typeSelect.find("option:selected").text()) + '","value":"' + inputElement.val() + '"}');
                    }
                });
            }
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

    var uploadFilesUrl = "", imageServerUrl = "";

    function addPurchase(uri, uploadFilesUrl, imageServerUrl){
        tableSheet.uploadFilesUrl = uploadFilesUrl;
        tableSheet.imageServerUrl = imageServerUrl;

        var $form = $("#form");
        if (!validator.checkAll($form)) {
            return;
        }

        var json = JSON.stringify($form.serializeJSON());
        json = json.substring(0, json.length-1) + ',"details":[';


        var trs = $("#productList tbody tr");

        for (var i = 0; i < trs.length; i++) {
            var textInputs = $(trs[i]).find("input");
            var tds = $(trs[i]).find("td");

            if (tds.length > 0) {
                var inputsHalfCount = (tds.length-4)/2,  notEmptyCounts = 0;

                for (var j = 0; j < textInputs.length; j++) {
                    if ($.trim(textInputs[j].value) != "" && textInputs[j].type == "text") {
                        notEmptyCounts++;
                    }
                }

                if (notEmptyCounts > inputsHalfCount) {
                    console.log($(trs[i]).html());
                }

                var useType = getInputByNameInTr("details[][product[useType]]:string", trs[i]);

                if (notEmptyCounts > inputsHalfCount || $(useType).val() == "acc" || $(useType).val() == "materials") {
                    for (var j = 0; j < textInputs.length; j++) {
                        if ($.trim(textInputs[j].value) == "" && $(textInputs[j]).attr("required") != undefined) {
                            alert("请输入值");
                            $(textInputs[j]).focus();

                            return false;
                        }
                    }

                    var inputs = $(trs[i]).find(":input"), validInputs = new Array();
                    var validIndex = 0;
                    var imageParentDirPath, imageTopDirPath, no;
                    for (var x = 0; x < inputs.length; x++) {
                        if ($(inputs[x]).attr("name") != undefined) {
                            if ($(inputs[x]).attr("name") == "details[][product[describe[imageParentDirPath]]]:string") {
                                imageParentDirPath = inputs[x];
                            }

                            if ($(inputs[x]).attr("name") == "details[][product[no]]:string") {
                                no = inputs[x];
                            }

                            if ($(inputs[x]).attr("name") == "imageTopDirPath") {
                                imageTopDirPath = inputs[x];
                            }

                            if ($(inputs[x]).attr("name") != "propertyValue") {
                                if ($.trim($(inputs[x]).val()) != "" && $(inputs[x]).val().indexOf('"value":""') == -1) {
                                    validInputs[validIndex++] = inputs[x];
                                }
                            }
                        }
                    }

                    imageParentDirPath.value = imageTopDirPath.value + "/" + no.value;
                    validInputs[validIndex++] = imageParentDirPath;

                    json += JSON.stringify($(validInputs).serializeJSON()["details"][0]) + ",";
                }
            }

        }

        if (json.substring(json.length-1) == "[") {
            alert("请输入采购单明细");
            return false;

        } else {
            json = json.substring(0, json.length-1) + ']}';
        }

        $form.sendData(uri, json, function(result){
            if (result.result.indexOf("success") != -1) {
                for (var i = 0; i < trs.length; i++) {
                    var fileInfo = getUploadFileInfo(trs[i]);
                    if (fileInfo != null) {
                        sendFormData("snapshoot", fileInfo["dir"], fileInfo["file"], uploadFilesUrl, imageServerUrl);
                    }
                }

            }
        });
    }


    function uploadFile(theItem, uploadFilesUrl, imageServerUrl){
        var fileInfo = getUploadFileInfo(theItem.parentNode.parentNode);
        if (fileInfo != null) {
            sendFormData("snapshoot", fileInfo["dir"], fileInfo["file"], uploadFilesUrl, imageServerUrl);
        }
    }

    function sendFormData(name, dir, file, uploadFilesUrl, imageServerUrl){
        var fd = new FormData();
        fd.append("name", name);
        fd.append("dir", dir);
        fd.append("file", $(file)[0].files[0]);

        $("#form").sendFormData(uploadFilesUrl, fd, function(result){

            var resultTd = $(file).parent().next();
            if (result.result.indexOf("success") == -1) {
                resultTd.html(result.result + ',请选择文件后，点击<a href="#uploadFile" onclick="tableSheet.uploadFile(this, tableSheet.uploadFilesUrl, tableSheet.imageServerUrl);">上传</a>');

            } else {
                resultTd.html('<a id="' + dir + '" href="' + imageServerUrl + '/' + result.filePath + '" class="lightbox">查看图片</a>');
                $(document.getElementById(dir)).lightbox({
                    fitToScreen: true,
                    imageClickClose: false
                });

            }
        });
    }

    function getUploadFileInfo(node){
        var inputs = $(node).find("input");

        var file = null, imageParentDirPath = null;
        if (inputs.length > 0) {
            for (var x = 0; x < inputs.length; x++) {

                if ($(inputs[x]).attr("name") != undefined) {
                    if ($(inputs[x]).attr("name") == "file" && $.trim(inputs[x].value) != "") {
                        file = inputs[x];
                    }

                    if ($(inputs[x]).attr("name") == "details[][product[describe[imageParentDirPath]]]:string" && $.trim(inputs[x].value) != "") {
                        imageParentDirPath = inputs[x];
                    }
                }
            }
        }

        if (file != null && imageParentDirPath != null) {
            return {"file":file, "dir":imageParentDirPath.value};
        } else {
            null;
        }
    }

    function getInputByNameInTr(name, tr) {
        var inputs = $(tr).find(":input");
        for (var x = 0; x < inputs.length; x++) {
            if ($(inputs[x]).attr("name") != undefined) {
                if ($(inputs[x]).attr("name") == name) {
                    return inputs[x];
                }
            }
        }

        return null;
    }

    function valueRepeatJudge(url, item, field, idNodeName) {
        $(item).blur(function(){
            var jItem = $(this);

            var id = -1;

            var inputs = jItem.parent().parent().find(":input");
            for (var x = 0; x < inputs.length; x++) {
                if ($(inputs[x]).attr("name") != undefined) {
                    if ($(inputs[x]).attr("name") == idNodeName) {
                        id = inputs[x].value;
                        break;
                    }
                }
            }

            if ($.trim(jItem.val()) != "") {
                var json = '{"id":' + id + ',"field":"' + field + '","value":"' + jItem.val() + '"}';

                $.ajax({
                    type: "post",
                    url: url,
                    contentType: "application/x-www-form-urlencoded; charset=utf-8",
                    data: {json: json},
                    dataType: "json",

                    success: function(result){
                        if (result.result == true) {
                            alert("商品编码：" + jItem.val() + "重复");
                            jItem.focus();
                        }
                    }
                });
            }
        });
    }

    return {
        init: init,
        addRow: addRow,
        suggests: suggests,
        addPurchase: addPurchase,
        uploadFile: uploadFile,
        uploadFilesUrl: uploadFilesUrl,
        imageServerUrl: imageServerUrl
    }
})(jQuery);
