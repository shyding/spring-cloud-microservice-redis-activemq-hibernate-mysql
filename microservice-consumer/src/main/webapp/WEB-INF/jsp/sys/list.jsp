<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3 id="htitle">列表</h3>
            </div>

            <div class="title_right">
                <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
                    <div class="input-group">
                        <input type="text" class="form-control" placeholder="Search for...">
                        <span class="input-group-btn">
                      <button class="btn btn-default" type="button">Go!</button>
                  </span>
                    </div>
                </div>
            </div>
        </div>
        <div class="clearfix"></div>

        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                    <div class="x_title">
                        <h2>查询 <small id="stitle"></small></h2>
                        <ul class="nav navbar-right panel_toolbox">
                            <li><button id="add" type="button" class="btn btn-success"></button></li>
                        </ul>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <div class="item form-group">
                                <label id="selectTitle" class="control-label col-md-3 col-sm-3 col-xs-12"  for="entity">类别<span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="entity" name="entity" class="form-control col-md-7 col-xs-12" required>
                                    </select>
                                </div>
                            </div>
                            <div id="dateItems">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="inputDate" id="timeLabel">时间</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" name="inputDate" id="inputDate" class="form-control" value="" />
                                    </div>
                                </div>
                            </div>
                            </div>
                            <div id="inputItems">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">名称</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="name" name="name" class="form-control col-md-7 col-xs-12" placeholder="输入名称" />
                                </div>
                            </div>
                            </div>
                            <div class="item form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="send" type="button" class="btn btn-success">查询</button>
                                </div>
                            </div>
                        </form>
                        <div class="ln_solid"></div>
                    </div>
                    <div class="x_content">
                        <table id="dataList" class="table table-striped table-bordered jambo_table bulk_action"></table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->
<script type="text/javascript">
    $('#inputDate').daterangepicker({locale:{
        format: 'YYYY/MM/DD',
        applyLabel : '确定',
        cancelLabel : '取消',
        fromLabel : '起始时间',
        toLabel : '结束时间',
        customRangeLabel : '自定义',
        daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
        monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
        firstDay : 1
    }}, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    var visitEntitiesOptions = {};
    <c:if test="${resources != null}">
    <c:if test="${fn:contains(resources, '/product')}">
    visitEntitiesOptions["product"] = '<option value="product">商品</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/productType/')}">
    visitEntitiesOptions["productType"] = '<option value="productType">商品类型</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/supplier')}">
    visitEntitiesOptions["supplier"] = '<option value="supplier">供应商</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/stock')}">
    visitEntitiesOptions["stock"] = '<option value="stock">库存</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/purchase')}">
    visitEntitiesOptions["purchase"] = '<option value="purchase">采购</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/order')}">
    visitEntitiesOptions["order"] = '<option value="order">订单</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/returnGoods')}">
    visitEntitiesOptions["returnGoods"] = '<option value="returnGoods">退货</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/pay')}">
    visitEntitiesOptions["pay"] = '<option value="pay">支付</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/customer')}">
    visitEntitiesOptions["customer"] = '<option value="customer">顾客</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/save/user')}">
    visitEntitiesOptions["user"] = '<option value="user">用户</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/privilegeResource')}">
    visitEntitiesOptions["privilegeResource"] = '<option value="privilegeResource">权限</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/auditFlow')}">
    visitEntitiesOptions["auditFlow"] = '<option value="auditFlow">流程</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/post')}">
    visitEntitiesOptions["post"] = '<option value="post">岗位</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/dept')}">
    visitEntitiesOptions["dept"] = '<option value="dept">部门</option>';
    </c:if>
    <c:if test="${fn:contains(resources, '/company')}">
    visitEntitiesOptions["company"] = '<option value="company">公司</option>';
    </c:if>
    </c:if>
    visitEntitiesOptions["audit"] = '<option value="audit">事宜</option>';

    $("#entity").change(function(){dataList.setQuery("<%=request.getContextPath()%>", $("#entity").val(), visitEntitiesOptions);});
    $("#send").click(function(){
        dataListQueryEntity = $("#entity").val();
        var formJson = $("#form").serializeJSON();
        delete formJson["entity"];
        dataListQueryJson = JSON.stringify(formJson);
        dataList.query($("#dataList"),"<%=request.getContextPath()%>", dataListQueryJson, dataListQueryEntity);
    });

   <c:if test="${entity != null}">
    if (!returnPage) {
        dataListQueryEntity = "${entity}";
        dataListQueryJson = '${json}';
    } else {
        returnPage = false;
    }
    </c:if>

    dataList.setQuery("<%=request.getContextPath()%>", dataListQueryEntity, visitEntitiesOptions);
    setSelect(document.getElementById("entity"), dataListQueryEntity);
    dataList.query($("#dataList"), "<%=request.getContextPath()%>", dataListQueryJson, dataListQueryEntity);
</script>
