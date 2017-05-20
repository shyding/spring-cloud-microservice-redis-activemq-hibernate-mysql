(function($){
    "use strict";
    var preFormJson = "";
    var submitSucc = false;

    $.fn.submitForm = function (url) {
        if (!validator.checkAll(this)) {
            return;
        }
        var formJson = JSON.stringify(this.serializeJSON({skipFalsyValuesForFields: ["charger[id]", "text1"]}));
        var mac = faultylabs.MD5(formJson + $.cookie("pin"));
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

            success: function(data){
                if (data.result.indexOf("success") != -1) {
                    submitSucc = true;
                    alert("提交成功");
                } else {
                    submitSucc = false;
                    alert(data.result);
                }
            }
        });

        preFormJson = formJson;
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
    },

    $.fn.preventEnterSubmit = function(){
        this.keypress(function(e){
            if(e.keyCode==13){
                e.preventDefault();
            }
        });
    }
})(jQuery);
