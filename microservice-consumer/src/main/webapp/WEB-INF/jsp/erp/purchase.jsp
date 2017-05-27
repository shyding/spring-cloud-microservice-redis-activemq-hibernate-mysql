<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.sys.*" %>
<%@ page import="com.hzg.erp.Purchase" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3><c:choose><c:when test="${entity != null}">查看</c:when><c:otherwise>填写</c:otherwise></c:choose>采购单</h3>
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
                        <h2>采购 <small>采购单</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section"><c:choose><c:when test="${entity != null}">查看</c:when><c:otherwise>填写</c:otherwise></c:choose>采购单</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">采购类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="type" name="type:number" class="form-control col-md-7 col-xs-12" required>
                                        <option value="">请选择类型</option>
                                        <option value="0">正常采购</option>
                                        <option value="1">临时采购</option>
                                        <option value="2">应急采购</option>
                                        <option value="3">现金采购</option>
                                        <option value="4">押金采购</option>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">采购时间 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" name="date" id="date" class="form-control" value="${entity.date}">
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购金额 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="amount" name="amount:number" value="${entity.amount}" class="form-control col-md-7 col-xs-12" required type="number">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">采购人</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" <c:if test="${entity.charger != null}">value="${entity.charger.name}"</c:if> class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                    <input type="hidden" id="charger[id]" name="charger[id]" <c:if test="${entity.charger != null}">value="${entity.charger.id}"</c:if>>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">采购条目</label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><div class="item form-group" id="details">
                                    <c:if test="${entity == null}">
                                    <div id="detail" class="row">
                                        <input id="1" name="details[][productName]" placeholder="商品名称" data-validate-length-range="6,30" data-validate-words="1" type="text" style="width:240px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <input id="2" name="details[][amount]:number" placeholder="采购金额" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <input id="3" name="details[][price]:number" placeholder="采购单价" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <input id="4" name="details[][quantity]:number" placeholder="采购数量" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <input id="5" name="details[][unit]" placeholder="采购单位" data-validate-length-range="1,6" type="text" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                     </div>
                                     </c:if>
                                    <c:forEach items="${entity.details}" var="detail" varStatus="status">

                                    </c:forEach>
                                </div></div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="addItem" type="button" class="btn btn-success">添加采购条目</button>
                                </div>
                            </div>

                            <div class="ln_solid"></div>
                            <div class="form-group" id="submitDiv">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <button id="send" type="button" class="btn btn-success">保存</button>
                                </div>
                            </div>
                            <input type="hidden" id="state" name="state:number" value="0">
                            <input type="hidden" id="inputer[id]" name="inputer[id]" <c:if test="${userId != null}">value="${userId}"</c:if>>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $('#date').daterangepicker({
        locale: {format: 'YYYY-MM-DD'},
        singleDatePicker: true,
        singleClasses: "picker_3"
    }, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    var count = 0;
    $('#addItem').click(function () {
        count++;
        var detailHtml = String($("#detail").html());

        var detailItems = document.getElementById("detail").children;
        for (var index in detailItems) {
            var id = String(detailItems[index].id);
            detailHtml = detailHtml.replace('id="' + id + '"', 'id="' + id + String(count) +'"');
        }

        $('#details').append('<div class="row">' +
            detailHtml +
            '<a href="#minus" onclick="this.parentNode.parentNode.removeChild(this.parentNode);"><i class="fa fa-minus-circle"></i></a></div>');

        document.getElementById("1" + count).focus();
    });

    $("#text1").coolautosuggest({
        url:"<%=request.getContextPath()%>/sys/suggest/user/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("charger[id]")).val(result.id);
            }
        }
    });

    $("#send").bind("click", function(){
        $('#form').submitForm('<%=request.getContextPath()%>/erp/save/<%=Purchase.class.getSimpleName().toLowerCase()%>');
    });

    $(document).unbind("keydown");

    <c:choose><c:when test="${entity != null}">document.title = "采购单查看";</c:when><c:otherwise> document.title = "采购单填写";</c:otherwise></c:choose>
</script>