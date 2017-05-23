var dataList = (function($){
    "use strict";

    var titles = {
        "user":"后台用户",
        "post":"岗位",
        "dept":"部门",
        "company":"公司",
        "privilegeResource":"权限",
        "auditFlow":"流程",
        "product":"商品",
        "purchase":"采购",
        "stock":"库存",
        "order":"订单",
        "audit":"事宜"
    };

    var urlTitles = {
        "user":"注册用户",
        "post":"注册岗位",
        "dept":"注册部门",
        "company":"注册公司",
        "privilegeResource":"添加权限",
        "auditFlow":"添加流程",
        "product":"录入商品",
        "purchase":"采购申请",
        "stock":"商品入库、出库"
    };

    var modules = {
        "user":"/sys",
        "post":"/sys",
        "dept":"/sys",
        "company":"/sys",
        "privilegeResource":"/sys",
        "audit":"/sys",
        "auditFlow":"/sys",
        "product":"/product",
        "purchase":"/product",
        "stock":"/product",
        "order":"/sale"
    };

    var addActions = {
        "user":"/view",
        "post":"/view",
        "dept":"/view",
        "company":"/view",
        "privilegeResource":"/view",
        "auditFlow":"/view",
        "product":"/view",
        "purchase":"/view",
        "stock":"/view"
    };

    var queryActions = {
        "user":"/complexQuery",
        "post":"/complexQuery",
        "dept":"/complexQuery",
        "company":"/complexQuery",
        "privilegeResource":"/complexQuery",
        "audit":"/privateQuery",
        "auditFlow":"/complexQuery",
        "product":"/complexQuery",
        "purchase":"/complexQuery",
        "stock":"/complexQuery",
        "order":"/complexQuery"
    };

    var viewActions = {
        "user":"/view",
        "post":"/view",
        "dept":"/view",
        "company":"/view",
        "privilegeResource":"/view",
        "audit":"/view",
        "auditFlow":"/view",
        "product":"/view",
        "purchase":"/view",
        "stock":"/view",
        "order":"/view"
    };

    var tHeaders = {
        "user":"<th>姓名</th><th>性别</th><th>用户名</th><th>email</th><th>岗位</th><th>创建时间</th><th>状态</th>",
        "post":"<th>名称</th><th>所在部门</th><th>所属公司</th>",
        "dept":"<th>名称</th><th>联系电话</th><th>地址</th><th>所属公司</th><th>负责人</th>",
        "company":"<th>名称</th><th>联系电话</th><th>地址</th><th>负责人</th>",
        "privilegeResource":"<th>名称</th><th>URI</th>",
        "audit":"<th>名称</th><th>流转时间</th><th>状态</th>",
        "auditFlow":"<th>名称</th><th>业务类型</th><th>所属公司</th><th>状态</th>",
        "product":"<th>名称</th>",
        "purchase":"<th>名称</th>",
        "stock":"<th>名称</th>",
        "order":"<th>名称</th>"
    };

    var propertiesShowSequences = {
        "user":["name", "gender", "username", "email", "posts[][name]", "inputDate", "state"],
        "post":["name", "dept[name]", "company[name]"],
        "dept":["name", "phone", "address", "company[name]", "charger[name]"],
        "company":["name", "phone", "address", "charger[name]"],
        "privilegeResource":["name", "uri"],
        "audit":["name", "inputDate", "state"],
        "auditFow":["name", "entity", "company[name]", "state"],
        "product":["name"],
        "purchase":["name"],
        "stock":["name"],
        "order":["name"]
    };

    var showTitleNames = {
        "user":{"state":{0: "使用", 1: "注销"}},
        "audit":{"state":{0: "已办", 1: "待办"}},
        "auditFlow":{"state":{0: "在用", 1: "没用"}}
    };

    var totalTableData = [];
    var isLocalSearch = false, searchStr = "", recordsSum = -1, sEcho = 1, tablePageData=[];
    var contextPath = "", preEntity = "", preDataTable = null;

    function setQuery(rootPath, entity){
        contextPath = rootPath;

        var title = (titles[entity]+"列表").toString();
        document.title = title;
        $("#htitle").empty().html(title);
        $("#stitle").empty().html(title);

        if ("user;dept;post;company;privilegeResource;auditFlow".indexOf(entity) != -1) {
            $("#timeLabel").empty().html("录入时间");

        } else if ("audit".indexOf(entity) != -1) {
            $("#entity").css("display", "none");
            $("#selectTitle").html("状态");
            $("#timeLabel").empty().html("流转时间");
            $("#entity").after('<select id="state" name="state" class="form-control col-md-7 col-xs-12"><option value="1">待办</option><option value="0">已办</option></select>');

        }else if ("product".indexOf(entity) != -1) {
            $("#dateItems").empty().html("");
            $("#inputItems").empty().html("");
        }

        if (typeof(addActions[entity]) == "undefined" ) {
            $("#add").css("display", "none");

        } else {
            $("#add").html(urlTitles[entity]).unbind().click(function(){
                render(rootPath + modules[entity] + addActions[entity] + "/" + entity + "/-1")
            });
        }
    }

    function query(table, rootPath, queryJson, entity){
        contextPath = rootPath;

        if (entity == "") {
            alert("请选择类型");
            return false;
        }

        if (!isLocalSearch) {
            totalTableData = [];
            recordsSum = -1;
        }

        if (entity != preEntity && preEntity != "") {
            try{
                preDataTable.fnDestroy();
            }catch(e){

            }
        }

        table.initDataTable(
            contextPath + modules[entity] + queryActions[entity] + "/" + entity,
            queryJson,
            "<thead><tr>" + tHeaders[entity] + "</tr></thead><tbody></tbody>",
            typeof(propertiesShowSequences[entity]) == "undefined" ? [] : propertiesShowSequences[entity],
            typeof(showTitleNames[entity]) == "undefined" ? {} : showTitleNames[entity],
            entity);

        // 设置搜索
        table.before('<div id="dataList_filter" class="dataTables_filter"><label>Search<input value="' + searchStr +'" id="columnSearch" class="" placeholder="" aria-controls="dataList" type="search"></label></div>');
        $('#columnSearch').on( 'keyup keydown change',  function () {
            isLocalSearch = true;
            searchStr = $('#columnSearch').val();
        } );

        preEntity = entity;
        preDataTable = table.dataTable();
    }

    $.fn.initDataTable = function(url, queryJson, header, propertiesShowSequence, showTitleName, entity) {
        this.empty().html(header);

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

                                                        if (ii == 2) {
                                                            tdData = $.trim(tdData) + "..";
                                                            break;
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

                                        if (propertiesShowSequence[i] == "name") {
                                            var queryUrl = contextPath + modules[entity] + viewActions[entity] + "/" + entity + "/" + dataList[key]["id"];
                                            tdData = "<a href='#" + queryUrl + "' onclick='render(\"" + queryUrl + "\")'>" + tdData + "</a>";
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
                                sEcho = resp.sEcho;
                                tablePageData = tableData;
                                resp.aaData = tableData;
                                fnCallback(resp); //把返回的数据传给这个方法就可以了,datatable会自动绑定数据的
                            }
                        });

                    } else {
                        var aaData = [];
                        var localRecordsSum = recordsSum;
                        var sEcho2 = 1;

                        if ($.trim(searchStr) == "") {
                            aaData = tablePageData;
                            sEcho2 = sEcho;
                        } else {
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

                            localRecordsSum = aaData.length;
                        }

                        var resp = {};
                        resp.aaData = aaData;
                        resp.iTotalRecords = localRecordsSum;
                        resp.iTotalDisplayRecords = localRecordsSum;
                        resp.sEcho = sEcho2;
                        fnCallback(resp); //把返回的数据传给这个方法就可以了,datatable会自动绑定数据的

                        isLocalSearch = false;
                    }
                 }
        });
    }

    return {
        setQuery: setQuery,
        query: query,
        modules: modules,
        viewActions: viewActions
    }

})(jQuery);
