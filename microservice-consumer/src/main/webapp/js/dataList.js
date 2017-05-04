var dataList = (function($){
    "use strict";

    var titles = {
        "user":"后台用户",
        "post":"岗位",
        "dept":"部门",
        "company":"公司",
        "privilege":"权限",
        "product":"商品",
        "order":"订单",
        "purchase":"采购",
        "stock":"库存"
    }

    function setQuery(){
        var entity = $("#entity").val();

        var title = (titles[entity]+"列表").toString();
        document.title = title;
        $("#htitle").empty().html(title);
        $("#stitle").empty().html(titles[entity]);

        if ("user;dept;post;company;privilege".indexOf(entity) != -1) {
            $("#timeLabel").empty().html("录入时间");

        } else if ("product".indexOf(entity) != -1) {
            $("#dateItems").empty().html("");
            $("#inputItems").empty().html("");
        }
    }

    function query(contextPath){
        var entity = $("#entity").val();
        if (entity == "") {
            alert("请选择类型");
            return false;
        }

        var url;
        var module = "";
        var tHeader = "<thead><tr>";
        if ("user;dept;post;company;privilege".indexOf(entity) != -1) {
            module = "/sys";

            if ("user" == entity) {
                tHeader += "<th>姓名</th><th>用户名</th><th>email</th><th>岗位</th><th>创建时间</th><th>状态</th>";
            }

            if ("dept" == entity) {
                tHeader += "<th>Name</th><th>Position</th><th>Office</th><th>Age</th><th>Start date</th><th>Salary</th>";
            }

            if ("post" == entity) {
                tHeader += "<th>Name</th><th>Position</th><th>Office</th><th>Age</th><th>Start date</th><th>Salary</th>";
            }

            if ("company" == entity) {
                tHeader += "<th>Name</th><th>Position</th><th>Office</th><th>Age</th><th>Start date</th><th>Salary</th>";
            }

            if ("privilege" == entity) {
                tHeader += "<th>Name</th><th>Position</th><th>Office</th><th>Age</th><th>Start date</th><th>Salary</th>";
            }
        } else if ("product".indexOf(entity) != -1) {
            module = "/product";
        }

        url = contextPath + module + "/complexQuery/" + entity;
        var queryJson = JSON.stringify($("#form").serializeJSON());
        tHeader += "</tr></thead><tbody id='tBody'></tbody>";

        $("#dataList").initDatatable(url, queryJson, tHeader);
    }

    $.fn.initDatatable = function(url, queryJson, header) {
        $("#dataList").empty().html(header);
        this.DataTable({
            dom: "Bfrtip",
            buttons: [
                {
                    extend: "copy",
                    className: "btn-sm"
                },
                {
                    extend: "csv",
                    className: "btn-sm"
                },
                {
                    extend: "excel",
                    className: "btn-sm"
                },
                {
                    extend: "pdfHtml5",
                    className: "btn-sm"
                },
                {
                    extend: "print",
                    className: "btn-sm"
                },
            ],
            responsive: true,
            destroy: true,

            iDisplayLength: 30, //每页显示条数
            bServerSide: true, //这个用来指明是通过服务端来取数据
            sAjaxSource: url,
            fnServerData: function(url, aoData, fnCallback) {   //获取数据的处理函数
                            $.ajax({
                                type : "post",
                                url : url,
                                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                                dataType : "json",
                                data : {
                                    dataTableParameters: JSON.stringify(aoData),
                                    json: queryJson
                                },
                                "success" : function(resp) {
                                    //fnCallback(resp); //把返回的数据传给这个方法就可以了,datatable会自动绑定数据的
                                    var tBody = "";
                                    $.each(resp.aaData, function(id, item){
                                        tBody += "<tr>";
                                        $.each(item, function (propertyName, propertyValue){
                                            tBody += "<td>" + propertyValue + "</td>";
                                        });
                                        tBody += "</tr>";
                                    });alert(tBody);
                                    $("#tBody").append(tBody);
                                }
                            });
                         }
        });
    }

    return {
        setQuery: setQuery,
        query: query
    }

})(jQuery);
