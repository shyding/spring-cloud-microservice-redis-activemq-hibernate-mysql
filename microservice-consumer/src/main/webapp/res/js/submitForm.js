(function($){
    "use strict";
    var preFormJson = "", preJson = "";
    var submitSucc = false;

    $.fn.submitForm = function (url) {
        if (!validator.checkAll(this)) {
            return;
        }
        var formJson = JSON.stringify(this.serializeJSON({skipFalsyValuesForFields: ["charger[id]", "text1"]}));
        var mac = faultylabs.MD5(formJson + localStorage.getItem("hzg_sys_user_pin"));
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: formJson, mac: mac},
            dataType: "json",

            beforeSend: function(){
                if (preFormJson == formJson && submitSucc) {
                    alert("不能重复提交");
                    return false;
                }
            },

            success: function(result){
                if (result.result.indexOf("success") != -1) {
                    submitSucc = true;
                    alert("提交成功");
                } else {
                    submitSucc = false;
                    alert(result.result);
                }
            }
        });

        preFormJson = formJson;
    },

    $.fn.submitForm = function (url, callBack, isShowResult) {
        if (!validator.checkAll(this)) {
            return;
        }
        var formJson = JSON.stringify(this.serializeJSON({skipFalsyValuesForFields: ["charger[id]", "text1"]}));
        var mac = faultylabs.MD5(formJson + localStorage.getItem("hzg_sys_user_pin"));
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: formJson, mac: mac},
            dataType: "json",

            beforeSend: function(){
                if (preFormJson == formJson && submitSucc) {
                    alert("不能重复提交");
                    return false;
                }
            },

            success: function(result){
                if (callBack != undefined) {
                    callBack(result);
                }

                var msg = "";

                if (result.result.indexOf("success") != -1) {
                    submitSucc = true;
                    $("#send").attr("disabled","disabled");
                    msg = "提交成功";

                } else {
                    submitSucc = false;
                    msg = result.result;
                }

                if (isShowResult != undefined) {
                    if (isShowResult == true) {
                        alert(msg);
                    } else {
                        if (msg != "提交成功") {
                            alert(msg);
                        }
                    }
                } else {
                    alert(msg);
                }
            }
        });

        preFormJson = formJson;
    },

    $.fn.sendData = function (url, json, callBack, isShowResult) {
        var mac = faultylabs.MD5(json + localStorage.getItem("hzg_sys_user_pin"));
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: json, mac: mac},
            dataType: "json",

            beforeSend: function(){
                if (preJson == json && submitSucc) {
                    alert("不能重复提交");
                    return false;
                }
            },

            success: function(result){
                if (callBack != undefined) {
                    callBack(result);
                }

                var msg = "";

                if (result.result.indexOf("success") != -1) {
                    submitSucc = true;
                    msg = "提交成功";
                } else {
                    submitSucc = false;
                    msg = result.result;
                }

                if (isShowResult != undefined) {
                    if (isShowResult == true) {
                        alert(msg);
                    } else {
                        if (msg != "提交成功") {
                            alert(msg);
                        }
                    }
                } else {
                    alert(msg);
                }
            }
        });

        preJson = json;
    },

    $.fn.ajaxPost = function (url, json, callback) {
        var mac = faultylabs.MD5(json + localStorage.getItem("hzg_sys_user_pin"));
        $.ajax({
            type: "post",
            url: url+"?"+Math.random(),
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: json, mac: mac},
            dataType: "json",

            success: function(result){
                if (callback != undefined) {
                    callback(result);
                }
            }
        });
    },

    //生成凭证时条件查询的ajax请求
    $.fn.ajaxPost1 = function (url, json,position, callback) {
        var mac = faultylabs.MD5(json + localStorage.getItem("hzg_sys_user_pin"));
        $.ajax({
            type: "post",
            url: url+"?"+Math.random(),
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: json,position:position, mac: mac},
            dataType: "json",

            success: function(result){
                if (callback != undefined) {
                    callback(result);
                }
            }
        });
    },

     //jQuery 方式发送 FormData 请求
     $.fn.sendFormData = function(url, formData, callBack) {
         $.ajax({
             type: "post",
             url: url,
             data: formData,
             /**
              * 必须false才会自动加上正确的Content-Type
              */
             contentType: false,
             /**
              * 必须false才会避开jQuery对 formdata 的默认处理
              * XMLHttpRequest会对 formdata 进行正确的处理
              */
             processData: false,
             success: function (result) {
                 if (callBack != undefined) {
                     callBack(result);
                 }
             }
         });
     },

    $.fn.isFullSet = function(){
        var isFullSet = true;
        var nameValues = {};

        this.find(':input').filter('[required=required], .required, .optional').not('[disabled=disabled]').each(function() {
            if (nameValues[$(this).attr("name")] == null || nameValues[$(this).attr("name")] == undefined) {
                if ($(this).val() == null || $(this).val() == undefined) {
                    nameValues[$(this).attr("name")] = "";

                } else {
                    nameValues[$(this).attr("name")] = $(this).val();
                }

            } else {
                if ($(this).val() != null || $(this).val() != undefined) {
                    nameValues[$(this).attr("name")] += $(this).val();
                }
            }
        });

        $.each(nameValues, function(name, value){
            if ($.trim(value) == "") {
                isFullSet = false;
            }
        });

        return isFullSet;
    }
})(jQuery);
