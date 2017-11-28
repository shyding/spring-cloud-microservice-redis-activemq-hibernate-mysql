var dataList = (function ($) {
    "use strict";

    var titles = {
        "user": "后台用户",
        "post": "岗位",
        "dept": "部门",
        "company": "公司",
        "privilegeResource": "权限",
        "auditFlow": "流程",
        "article": "文章",
        "articleCate": "文章分类",
        "product": "商品",
        "productDescribe": "商品描述",
        "productType": "商品类型",
        "productPriceChange": "商品调价",
        "productCheck": "商品盘点",
        "supplier": "供应商",
        "purchase": "采购",
        "stockInOut": "入库/出库",
        "stock": "库存",
        "warehouse": "仓库",
        "order": "订单",
        "orderPrivate": "商品加工，私人订制",
        "pay": "支付记录",
        "account": "账户",
        "audit": "事宜"
    };

    var dateTitles = {
        "user": "录入时间",
        "post": "录入时间",
        "dept": "录入时间",
        "company": "录入时间",
        "privilegeResource": "录入时间",
        "auditFlow": "录入时间",
        "article": "创建时间",
        "articleCate": "创建时间",
        "product": "入库时间",
        "productDescribe": "编辑时间",
        "productType": "录入时间",
        "productPriceChange": "录入时间",
        "productCheck": "盘点日期",
        "supplier": "录入时间",
        "purchase": "采购时间",
        "stockInOut": "入库时间",
        "stock": "入库时间",
        "warehouse": "录入时间",
        "order": "生成时间",
        "orderPrivate": "订单时间",
        "pay": "支付时间",
        "account": "注册时间",
        "audit": "办理时间"
    };

    var dateInputName = {
        "user": "inputDate",
        "post": "inputDate",
        "dept": "inputDate",
        "company": "inputDate",
        "privilegeResource": "inputDate",
        "auditFlow": "inputDate",
        "article": "inputDate",
        "articleCate": "inputDate",
        "product": "stockInOut[date]",
        "productDescribe": "date",
        "productType": "inputDate",
        "productPriceChange": "inputDate",
        "productCheck": "productCheck[checkDate]",
        "supplier": "inputDate",
        "purchase": "date",
        "stockInOut": "date",
        "stock": "stock[date]",
        "warehouse": "inputDate",
        "order": "date",
        "orderPrivate": "date",
        "pay": "payDate",
        "account": "inputDate",
        "audit": "dealDate"
    };

    var selectTitles = {
        "user": "类别",
        "post": "类别",
        "dept": "类别",
        "company": "类别",
        "privilegeResource": "类别",
        "auditFlow": "类别",
        "article": "类别",
        "articleCate": "类别",
        "product": "类别",
        "productDescribe": "类别",
        "productType": "类别",
        "productPriceChange": "类别",
        "productCheck": "类别",
        "supplier": "类别",
        "purchase": "类别",
        "stockInOut": "类别",
        "warehouse": "类别",
        "order": "类别",
        "orderPrivate": "类别",
        "pay": "类别",
        "account": "类别",
        "audit": "状态"
    };

    var addActions = {
        "user": "/view",
        "post": "/view",
        "dept": "/view",
        "company": "/view",
        "privilegeResource": "/view",
        "auditFlow": "/view",
        "article": "/view",
        "articleCate": "/view",
        "productType": "/view",
        "productPriceChange": "/view",
        "productCheck": "/view",
        "supplier": "/view",
        "purchase": "/view",
        "stockInOut": "/view",
        "warehouse": "/view",
        "account": "/view"
    };

    var urlTitles = {
        "user": "注册用户",
        "post": "注册岗位",
        "dept": "注册部门",
        "company": "注册公司",
        "privilegeResource": "添加权限",
        "auditFlow": "添加流程",
        "article": "添加文章",
        "articleCate": "添加文章分类",
        "productType": "添加商品类型",
        "productPriceChange": "添加商品调价",
        "productCheck": "添加商品盘点",
        "supplier": "添加供应商",
        "purchase": "采购申请",
        "warehouse": "添加仓库",
        "account": "注册银行账户",
        "stockInOut": "商品入库、出库"
    };

    var modules = {
        "user": "/sys",
        "post": "/sys",
        "dept": "/sys",
        "company": "/sys",
        "privilegeResource": "/sys",
        "audit": "/sys",
        "auditFlow": "/sys",
        "article": "/sys",
        "articleCate": "/sys",
        "product": "/erp",
        "productDescribe": "/erp",
        "productType": "/erp",
        "productPriceChange": "/erp",
        "productCheck": "/erp",
        "supplier": "/erp",
        "purchase": "/erp",
        "stockInOut": "/erp",
        "stock": "/erp",
        "warehouse": "/erp",
        "order": "/orderManagement",
        "orderPrivate": "/orderManagement",
        "pay": "/pay",
        "account": "/pay"
    };

    var queryActions = {
        "user": "/complexQuery",
        "post": "/complexQuery",
        "dept": "/complexQuery",
        "company": "/complexQuery",
        "privilegeResource": "/complexQuery",
        "audit": "/privateQuery",
        "auditFlow": "/complexQuery",
        "article": "/complexQuery",
        "articleCate": "/complexQuery",
        "product": "/complexQuery",
        "productDescribe": "/complexQuery",
        "productType": "/complexQuery",
        "productPriceChange": "/complexQuery",
        "productCheck": "/complexQuery",
        "supplier": "/complexQuery",
        "purchase": "/complexQuery",
        "stockInOut": "/complexQuery",
        "stock": "/complexQuery",
        "warehouse": "/complexQuery",
        "order": "/unlimitedComplexQuery",
        "orderPrivate": "/unlimitedComplexQuery",
        "pay": "/complexQuery",
        "account": "/complexQuery"
    };

    var viewActions = {
        "user": "/view",
        "post": "/view",
        "dept": "/view",
        "company": "/view",
        "privilegeResource": "/view",
        "audit": "/view",
        "auditFlow": "/view",
        "article": "/view",
        "articleCate": "/view",
        "product": "/view",
        "productDescribe": "/view",
        "productType": "/view",
        "productPriceChange": "/view",
        "productCheck": "/view",
        "supplier": "/view",
        "purchase": "/view",
        "stockInOut": "/view",
        "stock": "/view",
        "warehouse": "/view",
        "order": "/view",
        "orderPrivate": "/view",
        "pay": "/view",
        "account": "/view"
    };

    var tHeaders = {
        "user": "<th>姓名</th><th>性别</th><th>用户名</th><th>email</th><th>岗位</th><th>创建时间</th><th>状态</th>",
        "post": "<th>名称</th><th>所在部门</th><th>所属公司</th>",
        "dept": "<th>名称</th><th>联系电话</th><th>地址</th><th>所属公司</th><th>负责人</th>",
        "company": "<th>名称</th><th>联系电话</th><th>地址</th><th>负责人</th>",
        "privilegeResource": "<th>名称</th><th>URI</th>",
        "audit": "<th>名称</th><th>流转时间</th><th>状态</th>",
        "auditFlow": "<th>名称</th><th>业务类型</th><th>所属公司</th><th>状态</th>",
        "article": "<th>标题</th><th>分类</th><th>摘要</th><th>阅读量</th><th>发布人</th><th>创建时间</th><th>状态</th><th>文章优化关键词</th>",
        "articleCate": "<th>文章分类名称</th><th>分类名称简写</th><th>文章分类优化关键词</th>",
        "product": "<th>名称</th><th>编号</th><th>类别</th><th>结缘价</th><th>市场价</th><th>成本价</th><th>特性</th><th>入库时间</th><th>供应商</th><th>图片</th><th>状态</th>",
        "productDescribe": "<th>标题</th><th>关键词</th><th>编辑时间</th>",
        "productType": "<th>名称</th><th>缩写</th><th>优化标题</th><th>优化关键字</th><th>优化描述</th><th>所属父类</th>",
        "productPriceChange": "<th>编号</th><th>商品编号</th><th>商品名称</th><th>商品价格</th><th>调整后价格</th><th>状态</th>",
        "productCheck": "<th>盘点单据编号</th><th>盘点日期</th><th>盘点仓库</th><th>盘点部门</th><th>盈亏总数量</th><th>盈亏总金额</th><th>制单人</th><th>公司</th><th>备注</th><th>状态</th>",
        "supplier": "<th>名称</th><th>主要供货类型</th><th>等级</th><th>负责人</th><th>地址</th><th>电话</th><th>合作日期</th><th>商家类型</th>",
        "purchase": "<th>名称</th><th>编号</th><th>状态</th><th>类型</th><th>采购时间</th><th>采购人</th><th>录入人</th>",
        "stockInOut": "<th>单号</th><th>状态</th><th>类型</th><th>入/出库时间</th><th>入/出库人</th><th>仓库</th><th>已执行操作</th>",
        "stock": "<th>库存编号</th><th>商品名称</th><th>商品编号</th><th>商品数量</th><th>商品单位</th><th>入库时间</th><th>仓库</th>",
        "warehouse": "<th>名称</th><th>负责人</th><th>地址</th><th>所属公司</th>",
        "order": "<th>订单编号</th><th>状态</th><th>类型</th><th>金额</th><th>实际支付金额</th><th>折扣</th><th>创建时间</th><th>订单所有人</th><th>销售人</th>",
        "orderPrivate": "<th>商品名称</th><th>商品编号</th><th>订单编号</th><th>类型</th><th>加工、订制描述</th><th>加工、订制核定金额</th><th>核定描述</th><th>核定时间</th><th>核定人</th>",
        "pay": "<th>支付号</th><th>状态</th><th>金额</th><th>支付时间</th><th>支付类型</th><th>支付账户</th><th>支付开户行</th><th>支付银行</th><th>收款账户</th><th>订单类型</th><th>订单编号</th>",
        "account": "<th>账户</th><th>所属银行</th><th>开户人</th><th>开户行</th><th>账户金额</th><th>创建时间</th>"
    };

    var propertiesShowSequences = {
        "user": ["name", "gender", "username", "email", "posts[][name]", "inputDate", "state"],
        "post": ["name", "dept[name]", "dept[company[name]]"],
        "dept": ["name", "phone", "address", "company[name]", "charger[name]"],
        "company": ["name", "phone", "address", "charger[name]"],
        "privilegeResource": ["name", "uri"],
        "audit": ["name", "inputDate", "state"],
        "auditFlow": ["name", "entity", "company[name]", "state"],
        "article": ["title","articleCate[name]", "shortContent", "hits", "author[name]", "inputDate", "state", "seoKeyword"],
        "articleCate": ["name", "nickname", "articleKeyword"],
        "product": ["product[name]", "product[no]", "product[type[name]]", "product[fatePrice]", "product[price]", "product[costPrice]", "product[feature]", "stockInOut[date]", "product[supplier[name]]", "product[describe[imageParentDirPath]]", "product[state]"],
        "productDescribe": ["seoTitle", "seoKeyword", "date"],
        "productType": ["name", "abbreviate", "title", "keyword", "describes", "parent[name]"],
        "productPriceChange": ["no", "product[name]", "product[no]", "product[fatePrice]", "price", "state"],
        "productCheck": ["checkNo", "checkDate", "warehouse[name]", "dept[name]", "quantity", "amount", "chartMaker[name]", "company[name]", "remark","state"],
        "supplier": ["name", "mainProductType[name]", "level", "charger", "address", "phone", "cooperateDate", "types[]"],
        "purchase": ["name", "no", "state", "type", "date", "charger[name]", "inputer[name]"],
        "stockInOut": ["no", "state", "type", "date", "inputer[name]", "warehouse[name]", "actions[][type]"],
        "stock": ["no", "product[name]", "productNo", "quantity", "unit", "date", "warehouse[name]"],
        "warehouse": ["name", "charger[name]", "address", "company[name]"],
        "order": ["no", "state", "type", "amount", "payAmount", "discount", "date", "user[username]", "saler[name]"],
        "orderPrivate": ["product[name]", "product[no]", "order[no]", "orderPrivate[type]", "orderPrivate[describes]", "orderPrivate[authorize[amount]]", "orderPrivate[authorize[describes]]", "orderPrivate[authorize[date]]", "orderPrivate[authorize[user[name]]]"],
        "pay": ["no", "state", "amount", "payDate", "payType", "payAccount", "payBranch", "payBank", "receiptAccount", "entity", "entityNo"],
        "account": ["account", "bank", "owner[name]", "branch", "amount", "inputDate"]
    };

    var linkTitles = {
        "user": "name",
        "post": "name",
        "dept": "name",
        "company": "name",
        "privilegeResource": "name",
        "audit": "name",
        "auditFlow": "name",
        "article": "title",
        "articleCate": "name",
        "product": "product[name]",
        "productDescribe": "seoTitle",
        "productType": "name",
        "productPriceChange": "no",
        "productCheck": "checkNo",
        "supplier": "name",
        "purchase": "name",
        "stockInOut": "no",
        "stock": "no",
        "warehouse": "name",
        "order": "no",
        "pay": "no",
        "orderPrivate": "product[name]",
        "account": "account"
    };

    var idColumns = {
        "user": "id",
        "post": "id",
        "dept": "id",
        "company": "id",
        "privilegeResource": "id",
        "audit": "id",
        "auditFlow": "id",
        "article": "id",
        "articleCate": "id",
        "product": "product[id]",
        "productDescribe": "id",
        "productType": "id",
        "productPriceChange": "id",
        "productCheck": "id",
        "supplier": "id",
        "purchase": "id",
        "stockInOut": "id",
        "stock": "id",
        "warehouse": "id",
        "order": "id",
        "orderPrivate": "orderPrivate[id]",
        "pay": "id",
        "account": "id"
    };

    var imageTitles = {
        "product": "product[describe[imageParentDirPath]]"
    };

    var entityStateNames = {
        "user": {"state": {0: "使用", 1: "注销"}},
        "audit": {"state": {0: "待办", 1: "已办"}},
        "auditFlow": {"state": {0: "在用", 1: "没用"}},
        "article": {"state": {0: "保存", 1: "发布",2:"删除"}},
        "product": {"product[state]": {0: "采购", 10: "采购审核通过", 11: "采购完成", 1: "入库", 2: "出库", 3: "在售", 4: "售完", 5: "无效", 6: "编辑", 7: "多媒体文件已上传"}},
        "productPriceChange": {"state": {0: "申请", 1: "在用", 2: "保存"}},
        "productCheck": {"state": {0: "保存", 1: "盘点"}},
        "purchase": {"state": {0: "正常", 1: "关闭", 2: "作废"}, "type": {0: "正常采购", 1: "临时采购", 2: "应急采购", 3: "现金采购", 4: "押金采购"}},
        "stockInOut": {
            "state": {0: "申请", 1: "完成", 2: "作废", 3: "归档"},
            "type": {
                0: "现金入库",
                1: "代销入库",
                2: "增量入库",
                3: "加工入库",
                4: "押金入库",
                5: "修补入库",
                6: "调仓入库",
                10: "虚拟出库",
                11: "系统自动出库",
                12: "报损出库",
                13: "调仓出库",
                14: "内购出库",
                15: "正常出库"
            },
            "actions[][type]": {0: "打印商品条码", 1: "打印出库单", 2: "打印快递单", 3: "打印入库单", 4: "入库", 5: "出库"}
        },
        "order": {
            "state": {0: "未支付", 1: "已支付", 2: "取消", 3: "已退款", 4:"财务确认已支付"},
            "type": {
                0: "自助下单",
                1: "代下单",
                2: "私人订制",
                3: "预定",
                4: "代下单加工"
            }
        },
        "orderPrivate": {"orderPrivate[type]": { 0: "商品加工", 1: "私人订制"}},
        "supplier": {
            "level": {"A": "A级", "B": "B级", "C": "C级", "D": "D级"},
            "types[]": {0: "供应商", 1: "加工商"}
        },
        "pay": {"state": {0: "未支付", 1: "已支付", 2: "支付失败"}, "entity": {"purchase": "采购单", "order": "销售订单"}}
    };

    var entityRelations = {
        "user": "user",
        "post": "post",
        "dept": "dept",
        "company": "company",
        "privilegeResource": "privilegeResource",
        "audit": "audit",
        "auditFlow": "auditFlow",
        "article":"article",
        "articleCate":"articleCate",
        "product": "product",
        "stockInNotify": "purchase",
        "stockInNotifyCaiwu": "purchase",
        "productDescribe": "productDescribe",
        "productType": "productType",
        "productPriceChange": "productPriceChange",
        "productCheck": "productCheck",
        "priceChangeSaler": "productPriceChange",
        "priceChangeCharger": "productPriceChange",
        "priceChangeManager": "productPriceChange",
        "priceChangeDirector": "productPriceChange",
        "supplier": "supplier",
        "purchase": "purchase",
        "purchaseEmergency": "purchase",
        "stockInOut": "stockInOut",
        "stockInOutDepositCangchu": "stockInOut",
        "stockInOutDepositCaiwu": "stockInOut",
        "stockOutBreakage": "stockInOut",
        "printExpressWaybillNotify": "stockInOut",
        "stock": "stock",
        "order": "order",
        "orderPrivate": "orderPrivate",
        "pay": "pay"
    };

    var totalTableData = [];
    var isLocalSearch = false, searchStr = "", recordsSum = -1, sEcho = 1, tablePageData = [];
    var contextPath = "", imageServerUrl = "", preEntity = "", preDataTable = null;

    function setQuery(rootPath, imageServerPath, entity, visitEntitiesOptions) {
        contextPath = rootPath;
        imageServerUrl = imageServerPath;

        var title = (titles[entity] + "列表").toString();
        document.title = title;
        $("#htitle").empty().html(title);
        $("#stitle").empty().html(title);
        if(entity=="article"){
            $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">名称</label>' +
                '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="title" name="title" class="form-control col-md-7 col-xs-12" placeholder="输入名称" /></div></div>');
        }else{
            $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">名称</label>' +
                '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="name" name="name" class="form-control col-md-7 col-xs-12" placeholder="输入名称" /></div></div>');
        }

        if (modules[entity] == "/sys") {
            if ("audit" != entity) {
                $("#entity").empty()
                    .append(visitEntitiesOptions["user"])
                    .append(visitEntitiesOptions["privilegeResource"])
                    .append(visitEntitiesOptions["auditFlow"])
                    .append(visitEntitiesOptions["post"])
                    .append(visitEntitiesOptions["dept"])
                    .append(visitEntitiesOptions["company"])
                    .append(visitEntitiesOptions["article"])
                    .append(visitEntitiesOptions["articleCate"]);
            } else {
                $("#entity").css("display", "none").empty().append(visitEntitiesOptions["audit"]).after('<select id="state" name="state" class="form-control col-md-7 col-xs-12"><option value="0">待办</option><option value="1">已办</option></select>');
            }

        } else if (modules[entity] == "/erp") {
            $("#entity").empty()
                .append(visitEntitiesOptions["product"])
                .append(visitEntitiesOptions["productDescribe"])
                .append(visitEntitiesOptions["productType"])
                .append(visitEntitiesOptions["supplier"])
                .append(visitEntitiesOptions["purchase"])
                .append(visitEntitiesOptions["stockInOut"])
                .append(visitEntitiesOptions["stock"])
                .append(visitEntitiesOptions["productPriceChange"])
                .append(visitEntitiesOptions["productCheck"])
                .append(visitEntitiesOptions["warehouse"]);

            if (entity == "stockInOut") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">入/出库单号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="no" name="no" class="form-control col-md-7 col-xs-12" placeholder="输入单号" /></div></div>');
            }

            if (entity == "stock") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="product[name]">商品名称</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="product[name]" name="product[name]" class="form-control col-md-7 col-xs-12" placeholder="输入商品名称" /></div></div>' +
                    '<input type="hidden" name="stock[state]" value="0">');
            }

            if (entity == "product") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="product[state]">状态</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><select id="product[state]" name="product[state]" data-value-type="number" data-skip-falsy="true" class="form-control col-md-7 col-xs-12">' +
                    '<option value="">所有</option><option value="1">入库</option><option value="2">出库</option><option value="3">在售</option>' +
                    '<option value="4">售完</option><option value="10">采购审核通过</option><option value="7">编辑</option><option value="7">多媒体文件已上传</option>' +
                    '</select></div></div>' +
                    '<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="product[name]">商品名称</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="product[name]" name="product[name]" class="form-control col-md-7 col-xs-12" placeholder="输入名称" /></div></div>' +
                    '<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="product[no]">商品编号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="product[no]" name="product[no]" class="form-control col-md-7 col-xs-12" placeholder="输入编号" /></div></div>');
            }

            if (entity == "productDescribe") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoTitle">商品优化标题</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="seoTitle" name="seoTitle" class="form-control col-md-7 col-xs-12" placeholder="输入标题" /></div></div>');
            }

            if (entity == "productPriceChange") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoTitle">调价编号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="no" name="no" class="form-control col-md-7 col-xs-12" placeholder="输入编号" /></div></div>');
            }

            if (entity == "productCheck") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="checkNo">盘点单据编号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="productCheck[checkNo]" name="productCheck[checkNo]" class="form-control col-md-7 col-xs-12" placeholder="输入编号" /></div></div>'+
                    '<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="checker">盘点人</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="chartMaker[name]" name="chartMaker[name]" class="form-control col-md-7 col-xs-12" placeholder="输入名字" /></div></div>');
            }

        } else if (modules[entity] == "/pay") {
            $("#entity").empty()
                .append(visitEntitiesOptions["pay"])
                .append(visitEntitiesOptions["account"]);

            if (entity == "pay") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">支付号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="no" name="no" class="form-control col-md-7 col-xs-12" placeholder="输入支付号" /></div></div>');
            }

            if (entity == "account") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="account">帐户号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="account" name="account" class="form-control col-md-7 col-xs-12" placeholder="输入账户号" /></div></div>');
            }

        } else if (modules[entity] == "/orderManagement") {
            $("#entity").empty()
                .append(visitEntitiesOptions["order"])
                .append(visitEntitiesOptions["orderPrivate"]);

            if (entity == "order") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">订单编号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="no" name="no" class="form-control col-md-7 col-xs-12" placeholder="输入订单号" /></div></div>');
            }

            if (entity == "orderPrivate") {
                $("#inputItems").html('<div class="item form-group"><label class="control-label col-md-3 col-sm-3 col-xs-12" for="account">订单编号</label>' +
                    '<div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="no" name="no" class="form-control col-md-7 col-xs-12" placeholder="输入订单号" /></div></div>');
            }
        }

        $("#selectTitle").html(selectTitles[entity]);
        $("#timeLabel").empty().html(dateTitles[entity]);
        $("#inputDate").attr("name", dateInputName[entity]);
        setSelect(document.getElementById("entity"), entity);

        if (typeof(addActions[entity]) == "undefined") {
            $("#add").hide();

        } else {
            $("#add").html(urlTitles[entity]).unbind().click(function () {
                if (entity=="productCheck"){
                    render(rootPath + modules[entity] + addActions[entity] + "/productCheckInput/-1")
                } else {
                    render(rootPath + modules[entity] + addActions[entity] + "/" + entity + "/-1")
                }
            });
            $("#add").show();
        }
    }

    function query(table, rootPath, imageServerPath, queryJson, entity) {
        contextPath = rootPath;
        imageServerUrl = imageServerPath;

        if (entity == "") {
            alert("请选择类型");
            return false;
        }

        if (!isLocalSearch) {
            totalTableData = [];
            recordsSum = -1;
        }

        if (entity != preEntity && preEntity != "") {
            try {
                preDataTable.fnDestroy();
            } catch (e) {

            }
        }

        initDataTable(table, queryJson, entity);

        //设置搜索框事件
        $('#columnSearch').on('keyup keydown change', function () {
            isLocalSearch = true;
            searchStr = $('#columnSearch').val();
        }).keydown(function (event) {
            if (event.keyCode == 13) { //绑定回车
                initDataTable(table, queryJson, entity);
                return false;
            }
        });

        preEntity = entity;
        preDataTable = table.dataTable();

        preDataTable.on("click", ".lightbox", function(){
            lightboxm.initialize();
            lightboxm.showLightbox(this);
            return false;
        });
    }

    function initDataTable(table, queryJson, entity) {
        if (document.getElementById("dataList_filter") == null) {
            // 设置本地搜索框
            table.before('<div id="dataList_filter" class="dataTables_filter"><label>Search&nbsp;<input value="' + searchStr + '" id="columnSearch" class="" placeholder="" aria-controls="dataList" type="search"></label></div>');
        }

        table.initDataTable(contextPath + modules[entity] + queryActions[entity] + "/" + entity,
            queryJson,
            "<thead><tr>" + tHeaders[entity] + "</tr></thead><tbody></tbody>",
            typeof(propertiesShowSequences[entity]) == "undefined" ? [] : propertiesShowSequences[entity],
            typeof(entityStateNames[entity]) == "undefined" ? {} : entityStateNames[entity],
            entity);
    }

    $.fn.initDataTable = function (url, queryJson, header, propertiesShowSequence, entityStateName, entity) {
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

            "oLanguage": { // 汉化
                "sLengthMenu": "显示_MENU_条 ",
                "sZeroRecords": "没有您要搜索的内容",
                "sInfo": "从_START_ 到 _END_ 条记录——总记录数为 _TOTAL_ 条",
                "sInfoEmpty": "记录数为0",
                "sInfoFiltered": "(全部记录数 _MAX_  条)",
                "sInfoPostFix": "",
                "sSearch": "Search",
                "sUrl": "",
                "oPaginate": {
                    "sFirst": "<a href=\"#/fast-backward\"><i class=\"fa fa-fast-backward\"></i></a>",
                    "sPrevious": "<a href=\"#/backward\"><i class=\"fa fa-backward\"></i></a>",
                    "sNext": "<a href=\"#/forward\"><i class=\"fa fa-forward\"></i></a>",
                    "sLast": "<a href=\"#/fast-forward\"><i class=\"fa fa-fast-forward\"></i></a>"
                }
            },
            //"defaultContent":  " ",
            "bJQueryUI": true,
            "bPaginate": true,// 分页按钮
            "bFilter": false,// 搜索栏
            "bLengthChange": true,// 每行显示记录数
            "bSort": false,// 排序
            //"aLengthMenu": [[50,100,500,1000,10000], [50,100,500,1000,10000]],//定义每页显示数据数量
            //"iScrollLoadGap":400,//用于指定当DataTable设置为滚动时，最多可以一屏显示多少条数据
            //"aaSorting": [[4, "desc"]],
            "bInfo": true,// Showing 1 to 10 of 23 entries 总记录数没也显示多少等信息
            "bWidth": true,
            //"sScrollY": "62%",
            //"sScrollX": "210%",
            "bScrollCollapse": true,
            "sPaginationType": "full_numbers", // 分页，一共两种样式 另一种为two_button 是datatables默认
            "bSortCellsTop": true,

            iDisplayLength: 30, //每页显示条数
            bServerSide: true, //这个用来指明是通过服务端来取数据
            sAjaxSource: url,
            fnServerData: function (url, aoData, fnCallback) {   //获取数据的处理函数
                if (!isLocalSearch) {
                    $.ajax({
                        type: "post",
                        url: url,
                        contentType: "application/x-www-form-urlencoded; charset=utf-8",
                        dataType: "json",
                        data: {
                            dataTableParameters: JSON.stringify(aoData),
                            json: queryJson,
                            recordsSum: recordsSum
                        },

                        "success": function (resp) {
                            var tableData = [];
                            var dataList = $.parseJSON(resp.aaData);

                            for (var key in dataList) {
                                var rowData = [];

                                for (var i in propertiesShowSequence) {
                                    var tdData = "";

                                    var pos = propertiesShowSequence[i].indexOf("[]");

                                    if (pos != -1) {
                                        var parentArrayProperty = propertiesShowSequence[i].substr(0, pos);

                                        if (pos + 3 < propertiesShowSequence[i].length) {
                                            /**
                                             * dataList[key][propertiesShowSequence[i]] 是数组对象,propertiesShowSequence[i]值如：posts[][name]
                                             */

                                            var childElementProperty = propertiesShowSequence[i].substr(pos + 3);
                                            childElementProperty = childElementProperty.substr(0, childElementProperty.length - 1);

                                            if (dataList[key][parentArrayProperty] != undefined) {
                                                for (var ii in dataList[key][parentArrayProperty]) {
                                                    var childElementValue = dataList[key][parentArrayProperty][ii][childElementProperty];

                                                    if (dataList[key][parentArrayProperty][ii] != undefined) {
                                                        if (entityStateName[propertiesShowSequence[i]] != undefined) {
                                                            tdData += entityStateName[propertiesShowSequence[i]][childElementValue] + " ";
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
                                                        if (entityStateName[propertiesShowSequence[i]] != undefined) {
                                                            tdData += entityStateName[propertiesShowSequence[i]][childElementValue] + " ";
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
                                         * dataList[key][propertiesShowSequence[i]] 是对象,propertiesShowSequence[i]值如：dept[company[name]]
                                         */
                                        if (pos != -1) {
                                            var childValue = getPropertiesValue(dataList[key], propertiesShowSequence[i]);
                                            if (childValue != null) {
                                                if (entityStateName[propertiesShowSequence[i]] != undefined) {
                                                    tdData = entityStateName[propertiesShowSequence[i]][childValue];
                                                } else {
                                                    tdData = childValue;
                                                }
                                            } else {
                                                tdData = "";
                                            }

                                        } else {
                                            /**
                                             * dataList[key][propertiesShowSequence[i]] 是属性,propertiesShowSequence[i]值如：name
                                             */
                                            var value = dataList[key][propertiesShowSequence[i]];

                                            if (entityStateName[propertiesShowSequence[i]] != undefined) {
                                                tdData = entityStateName[propertiesShowSequence[i]][value];
                                            } else {
                                                if (typeof(value) == "undefined") {
                                                    tdData = "";
                                                } else {
                                                    tdData = value;
                                                }
                                            }
                                        }
                                    }

                                    if (propertiesShowSequence[i] == linkTitles[entity]) {
                                        var queryUrl = contextPath + modules[entity] + viewActions[entity] + "/" + entity + "/" + getPropertiesValue(dataList[key], idColumns[entity]);
                                        tdData = "<a href='#" + queryUrl + "' onclick='render(\"" + queryUrl + "\")'>" + tdData + "</a>";
                                    }

                                    if (propertiesShowSequence[i] == imageTitles[entity]) {
                                        var imageUrl = imageServerUrl + "/" + tdData + "/snapshoot.jpg";
                                        tdData = "<a href='" + imageUrl + "' class='lightbox'>查看</a>";
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
                                    searchColumn = searchColumn.substring(searchColumn.indexOf(">") + 1, searchColumn.indexOf("</"));
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

    function getPropertiesValue(json, propertiesStr) {
        return getValue(json, getProperties(propertiesStr));
    }

    function getProperties(propertiesStr) {
        var properties = new Array();

        var tempProperties = propertiesStr.replace(/]/g, "").split("[");
        var j = 0;
        for (var i = 0; i < tempProperties.length; i++) {
            if (tempProperties[i] != "") {
                properties[j++] = tempProperties[i];
            }
        }

        return properties;
    }

    function getValue(json, properties) {
        var newJson = null;

        for (var i = 0; i < properties.length; i++) {
            if (json[properties[i]] != undefined && json[properties[i]] != null) {
                newJson = json[properties[i]];
                json = newJson;
            } else {
                return null;
            }
        }

        return newJson;
    }


    return {
        setQuery: setQuery,
        query: query,
        modules: modules,
        viewActions: viewActions,
        entityRelations: entityRelations,
        entityStateNames: entityStateNames
    }

})(jQuery);
