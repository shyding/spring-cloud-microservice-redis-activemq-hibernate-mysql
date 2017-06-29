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
        "productType":"商品类型",
        "supplier":"供应商",
        "purchase":"采购",
        "stock":"库存",
        "order":"订单",
        "audit":"事宜"
    };

    var dateTitles = {
        "user":"录入时间",
        "post":"录入时间",
        "dept":"录入时间",
        "company":"录入时间",
        "privilegeResource":"录入时间",
        "auditFlow":"录入时间",
        "product":"入库时间",
        "productType":"录入时间",
        "supplier":"录入时间",
        "purchase":"采购时间",
        "stock":"录入时间",
        "order":"生成时间",
        "audit":"流转时间"
    };

    var selectTitles = {
        "user":"类别",
        "post":"类别",
        "dept":"类别",
        "company":"类别",
        "privilegeResource":"类别",
        "auditFlow":"类别",
        "product":"类别",
        "productType":"类别",
        "supplier":"类别",
        "purchase":"类别",
        "stock":"类别",
        "order":"类别",
        "audit":"状态"
    };

    var urlTitles = {
        "user":"注册用户",
        "post":"注册岗位",
        "dept":"注册部门",
        "company":"注册公司",
        "privilegeResource":"添加权限",
        "auditFlow":"添加流程",
        "product":"录入商品",
        "productType":"添加商品类型",
        "supplier":"添加供应商",
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
        "product":"/erp",
        "productType":"/erp",
        "supplier":"/erp",
        "purchase":"/erp",
        "stock":"/erp",
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
        "productType":"/view",
        "supplier":"/view",
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
        "productType":"/complexQuery",
        "supplier":"/complexQuery",
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
        "productType":"/view",
        "supplier":"/view",
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
        "productType":"<th>名称</th><th>缩写</th><th>优化标题</th><th>优化关键字</th><th>优化描述</th><th>所属父类</th>",
        "supplier":"<th>名称</th><th>主要供货类型</th><th>等级</th><th>负责人</th><th>地址</th><th>电话</th><th>合作日期</th><th>商家类型</th>",
        "purchase":"<th>名称</th><th>状态</th>",
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
        "auditFlow":["name", "entity", "company[name]", "state"],
        "product":["name"],
        "productType":["name", "abbreviate", "title", "keyword", "describes", "parent[name]"],
        "supplier":["name", "mainProductType[name]", "level", "charger", "address", "phone", "cooperateDate", "types[]"],
        "purchase":["name", "state"],
        "stock":["name"],
        "order":["name"]
    };

    var linkTitle = {
        "user":"name",
        "post":"name",
        "dept":"name",
        "company":"name",
        "privilegeResource":"name",
        "audit":"name",
        "auditFlow":"name",
        "product":"name",
        "productType":"name",
        "supplier":"name",
        "purchase":"name",
        "stock":"stockNo",
        "order":"orderNo"
    };

    var showTitleNames = {
        "user":{"state":{0: "使用", 1: "注销"}},
        "audit":{"state":{0: "待办", 1: "已办"}},
        "auditFlow":{"state":{0: "在用", 1: "没用"}},
        "purchase":{"state":{0: "正常", 1: "关闭", 2: "作废"}},
        "supplier":{"level":{"A": "A级", "B": "B级", "C": "C级", "D": "D级"},
        "types[]":{0: "供应商", 1: "加工商"}}
    };

    var totalTableData = [];
    var isLocalSearch = false, searchStr = "", recordsSum = -1, sEcho = 1, tablePageData=[];
    var contextPath = "", preEntity = "", preDataTable = null;

    function setQuery(rootPath, entity, visitEntitiesOptions){
        contextPath = rootPath;

        var title = (titles[entity]+"列表").toString();
        document.title = title;
        $("#htitle").empty().html(title);
        $("#stitle").empty().html(title);

        if (modules[entity] == "/sys") {
            if ("audit" != entity) {
                $("#entity").empty()
                    .append(visitEntitiesOptions["user"])
                    .append(visitEntitiesOptions["privilegeResource"])
                    .append(visitEntitiesOptions["auditFlow"])
                    .append(visitEntitiesOptions["post"])
                    .append(visitEntitiesOptions["dept"])
                    .append(visitEntitiesOptions["company"]);

            } else {
                $("#entity").css("display", "none").empty().append(visitEntitiesOptions["audit"]).
                after('<select id="state" name="state" class="form-control col-md-7 col-xs-12"><option value="1">待办</option><option value="0">已办</option></select>');
            }

        } else if (modules[entity] == "/erp") {
            $("#entity").empty()
                .append(visitEntitiesOptions["product"])
                .append(visitEntitiesOptions["productType"])
                .append(visitEntitiesOptions["supplier"])
                .append(visitEntitiesOptions["purchase"])
                .append(visitEntitiesOptions["stock"]);

        }

        $("#selectTitle").html(selectTitles[entity]);
        $("#timeLabel").empty().html(dateTitles[entity]);
        setSelect(document.getElementById("entity"), entity);

        if (typeof(addActions[entity]) == "undefined") {
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

        initDataTable(table, queryJson, entity);

        //设置搜索框事件
        $('#columnSearch').on( 'keyup keydown change',  function () {
            isLocalSearch = true;
            searchStr = $('#columnSearch').val();
        }).keydown(function(event){
            if(event.keyCode == 13){ //绑定回车
                initDataTable(table, queryJson, entity);
                return false;
            }
        });

        preEntity = entity;
        preDataTable = table.dataTable();
    }

    function initDataTable(table, queryJson, entity){
        if (document.getElementById("dataList_filter") == null){
            // 设置本地搜索框
            table.before('<div id="dataList_filter" class="dataTables_filter"><label>Search&nbsp;<input value="' + searchStr +'" id="columnSearch" class="" placeholder="" aria-controls="dataList" type="search"></label></div>');
        }

        table.initDataTable(contextPath + modules[entity] + queryActions[entity] + "/" + entity,
                            queryJson,
                            "<thead><tr>" + tHeaders[entity] + "</tr></thead><tbody></tbody>",
                            typeof(propertiesShowSequences[entity]) == "undefined" ? [] : propertiesShowSequences[entity],
                            typeof(showTitleNames[entity]) == "undefined" ? {} : showTitleNames[entity],
                            entity);
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

                                        if (pos != -1) {
                                            var parentArrayProperty = propertiesShowSequence[i].substr(0, pos);

                                            if (pos+3 < propertiesShowSequence[i].length) {
                                                /**
                                                 * dataList[key][propertiesShowSequence[i]] 是数组对象,propertiesShowSequence[i]值如：posts[][name]
                                                 */

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
                                                /**
                                                 * dataList[key][propertiesShowSequence[i]] 是数组对象,propertiesShowSequence[i]值如：types[]
                                                 */

                                                if (dataList[key][parentArrayProperty] != undefined) {
                                                    for (var ii in dataList[key][parentArrayProperty]) {
                                                        var childElementValue = dataList[key][parentArrayProperty][ii];

                                                        if (dataList[key][parentArrayProperty] != undefined) {
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

                                        if (propertiesShowSequence[i] == linkTitle[entity]) {
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
                            localRecordsSum = recordsSum;

                        } else {
                            var index = 0;
                            for (var i in totalTableData) {
                                for (var ii in totalTableData[i]) {

                                    var searchColumn = totalTableData[i][ii];
                                    if (searchColumn.indexOf("<a") != -1) {
                                        searchColumn = searchColumn.substring(searchColumn.indexOf(">")+1, searchColumn.indexOf("</"));
                                    }

                                    if (searchColumn.indexOf(searchStr) != -1) {
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
