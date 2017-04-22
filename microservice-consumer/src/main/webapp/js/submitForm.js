/**
 * Created by Administrator on 2017/4/17.
 */
(function($){
    "use strict";
    var preFormJson = "";
    var submitSucc = false;

    $.fn.submitForm = function (url) {
        if (!validator.checkAll(this)) {
            return;
        }
        var formJson = JSON.stringify(this.serializeJSON());
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: formJson},
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
    }
})(jQuery);
