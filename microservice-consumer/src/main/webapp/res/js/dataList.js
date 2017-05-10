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

    var totalTableData = [];
    var isLocalSearch = false, searchStr = "", recordsSum = -1, preEntity = "";

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
        var propertiesShowSequence = [], showTitleName={};
        if ("user;dept;post;company;privilege".indexOf(entity) != -1) {
            module = "/sys";

            if ("user" == entity) {
                tHeader += "<th>姓名</th><th>性别</th><th>用户名</th><th>email</th><th>岗位</th><th>创建时间</th><th>状态</th>";
                propertiesShowSequence = ["name", "gender", "username", "email", "posts[][name]", "inputDate", "state"];
                showTitleName = {"state":{0: "使用", 1: "注销"}};
            }

            if ("dept" == entity) {
                tHeader += "<th>名称</th><th>联系电话</th><th>地址</th><th>所属公司</th><th>负责人</th>";
                propertiesShowSequence = ["name", "phone", "address", "company[name]", "charger[name]"];
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
        tHeader += "</tr></thead><tbody></tbody>";

        totalTableData = [];
        recordsSum = -1;

        if (entity != preEntity && preEntity != "") {
            $("#dataList").dataTable().fnDestroy();
        }
        $("#dataList").initDatatable(url, queryJson, tHeader, propertiesShowSequence, showTitleName);

        // 设置搜索
        $('#dataList').before('<div id="dataList_filter" class="dataTables_filter"><label>Search<input id="columnSearch" class="" placeholder="" aria-controls="dataList" type="search"></label></div>');
        $('#columnSearch').on( 'keyup keydown change',  function () {
            isLocalSearch = true;
            searchStr = $('#columnSearch').val();
        } );

        preEntity = entity;
    }

    $.fn.initDatatable = function(url, queryJson, header, propertiesShowSequence, showTitleName) {
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
            destroy: true,

            "oLanguage" : { // 汉化
                "sLengthMenu" : "显示_MENU_条 ",
                "sZeroRecords" : "没有您要搜索的内容",
                "sInfo" : "从_START_ 到 _END_ 条记录——总记录数为 _TOTAL_ 条",
                "sInfoEmpty" : "记录数为0",
                "sInfoFiltered" : "(全部记录数 _MAX_  条)",
                "sInfoPostFix" : "",
                "sSearch" : "Search",
                "sUrl" : "",
                "oPaginate" : {
                    "sFirst" : "<a href=\"#/fast-backward\"><i class=\"fa fa-fast-backward\"></i></a>",
                    "sPrevious" : "<a href=\"#/backward\"><i class=\"fa fa-backward\"></i></a>",
                    "sNext" : "<a href=\"#/forward\"><i class=\"fa fa-forward\"></i></a>",
                    "sLast" : "<a href=\"#/fast-forward\"><i class=\"fa fa-fast-forward\"></i></a>"
                }
            },
            "bJQueryUI": true,
            "bPaginate" : true,// 分页按钮
            "bFilter" : false,// 搜索栏
            "bLengthChange" : true,// 每行显示记录数
            "bSort" : false,// 排序
            //"aLengthMenu": [[50,100,500,1000,10000], [50,100,500,1000,10000]],//定义每页显示数据数量
            //"iScrollLoadGap":400,//用于指定当DataTable设置为滚动时，最多可以一屏显示多少条数据
            //"aaSorting": [[4, "desc"]],
            "bInfo" : true,// Showing 1 to 10 of 23 entries 总记录数没也显示多少等信息
            "bWidth":true,
            //"sScrollY": "62%",
            //"sScrollX": "210%",
            "bScrollCollapse": true,
            "sPaginationType" : "full_numbers", // 分页，一共两种样式 另一种为two_button 是datatables默认
            "bSortCellsTop": true,

            iDisplayLength: 30, //每页显示条数
            bServerSide: true, //这个用来指明是通过服务端来取数据
            sAjaxSource: url,
            fnServerData:
                function(url, aoData, fnCallback) {   //获取数据的处理函数
                    if (!isLocalSearch) {
                        $.ajax({
                            type : "post",
                            url : url,
                            contentType: "application/x-www-form-urlencoded; charset=utf-8",
                            dataType : "json",
                            data : {
                                dataTableParameters: JSON.stringify(aoData),
                                json: queryJson,
                                recordsSum: recordsSum
                            },

                            "success" : function(resp) {
                                var tableData = [];
                                var dataList = $.parseJSON(resp.aaData);

                                for (var key in dataList) {
                                    var rowData=[];

                                    for (var i in propertiesShowSequence){
                                        var tdData = "";

                                        var pos = propertiesShowSequence[i].indexOf("[]");
                                        /**
                                         * dataList[key][propertiesShowSequence[i]] 是数组对象,propertiesShowSequence[i]值如：posts[][name]
                                         */
                                        if (pos != -1) {
                                            var parentArrayProperty = propertiesShowSequence[i].substr(0, pos);
                                            var childElementProperty = propertiesShowSequence[i].substr(pos+3);
                                                childElementProperty = childElementProperty.substr(0, childElementProperty.length-1);

                                            if (dataList[key][parentArrayProperty] != undefined) {
                                                for (var ii in dataList[key][parentArrayProperty]) {
                                                    var childElementValue = dataList[key][parentArrayProperty][ii][childElementProperty];

                                                    if (dataList[key][parentArrayProperty][ii] != undefined) {
                                                        if (showTitleName[propertiesShowSequence[i]] != undefined) {
                                                            tdData += showTitleName[propertiesShowSequence[i]][childElementValue] + " ";
                                                        } else {
                                                            tdData += childElementValue + " ";
                                                        }

                                                        if (ii == 1) {
                                                            tdData = $.trim(tdData) + "..";
                                                        }
                                                    }
                                                }
                                            }

                                        } else {
                                            pos = propertiesShowSequence[i].indexOf("[");
                                            /**
                                             * dataList[key][propertiesShowSequence[i]] 是对象,propertiesShowSequence[i]值如：company[name]
                                             */
                                            if (pos != -1) {
                                                var parentProperty = propertiesShowSequence[i].substr(0, pos);
                                                var childProperty = propertiesShowSequence[i].substr(pos+1);
                                                     childProperty = childProperty.substr(0, childProperty.length-1);

                                                var childValue = "";
                                                if (dataList[key][parentProperty] != undefined) {
                                                    childValue = dataList[key][parentProperty][childProperty];

                                                    if (showTitleName[propertiesShowSequence[i]] != undefined) {
                                                        tdData = showTitleName[propertiesShowSequence[i]][childValue];
                                                    } else {
                                                        tdData = childValue;
                                                    }
                                                }

                                            } else {
                                                /**
                                                 * dataList[key][propertiesShowSequence[i]] 是属性,propertiesShowSequence[i]值如：name
                                                 */
                                                var value = dataList[key][propertiesShowSequence[i]];

                                                if (showTitleName[propertiesShowSequence[i]] != undefined) {
                                                    tdData = showTitleName[propertiesShowSequence[i]][value];
                                                } else {
                                                    tdData = value;
                                                }
                                            }
                                        }

                                        rowData[i] = tdData;
                                    }

                                    tableData[key] = rowData;
                                }

                                for (var i in tableData) {
                                    var isSame = false;
                                    for (var ii in totalTableData) {
                                        if (tableData[i].toString() == totalTableData[ii].toString())
                                            isSame = true;
                                    }

                                    if (!isSame) {
                                        totalTableData.push(tableData[i]);
                                    }
                                }

                                recordsSum = resp.iTotalRecords;
                                resp.aaData = tableData;
                                fnCallback(resp); //把返回的数据传给这个方法就可以了,datatable会自动绑定数据的
                            }
                        });

                    } else {
                        var aaData = [];

                        var index = 0;
                        for (var i in totalTableData) {
                            for (var ii in totalTableData[i]) {
                                if (totalTableData[i][ii].indexOf(searchStr) != -1) {
                                    aaData[index] = totalTableData[i];

                                    index++;
                                    break;
                                }
                            }
                        }

                        var resp = {};
                        resp.aaData = aaData;
                        fnCallback(resp); //把返回的数据传给这个方法就可以了,datatable会自动绑定数据的

                        isLocalSearch = false;
                    }
                 }
        });
    }

    return {
        setQuery: setQuery,
        query: query
    }

})(jQuery);
