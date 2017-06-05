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
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购标题 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="name" name="name" value="${entity.name}" data-validate-length-range="5,30" data-validate-words="1" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
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
                                    <input type="text" id="text1" name="text1" value="${entity.charger.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                    <input type="hidden" id="charger[id]" name="charger[id]" value="${entity.charger.id}">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购描述 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="describes" name="describes" class="form-control col-md-7 col-xs-12" data-validate-length-range="6,256" data-validate-words="1"required>${entity.describes}</textarea>
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
                                        <input id="5" name="details[][unit]" placeholder="采购单位" data-validate-length-range="1,6" type="text" required>&nbsp;&nbsp;&nbsp;&nbsp;<br>
                                        <input type="text" id="text2" name="text2" placeholder="供应商" style="width:340px;margin-top:6px" required />
                                        <input type="hidden" id="details[][supplier[id]]" name="details[][supplier[id]]:number">
                                     </div>
                                     </c:if>
                                    <c:set var="cursor" value="0" />
                                    <c:forEach items="${entity.details}" var="detail" varStatus="status">
                                        <div id="detail" class="row">
                                            <input id="1<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="details[][productName]" value="${detail.productName}" placeholder="商品名称" data-validate-length-range="6,30" data-validate-words="1" type="text" style="width:240px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                            <input id="2<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="details[][amount]:number" value="${detail.amount}" placeholder="采购金额" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                            <input id="3<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="details[][price]:number" value="${detail.price}" placeholder="采购单价" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                            <input id="4<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="details[][quantity]:number" value="${detail.quantity}" placeholder="采购数量" type="number" style="line-height: 28px" required>&nbsp;&nbsp;&nbsp;&nbsp;
                                            <input id="5<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="details[][unit]" value="${detail.unit}" placeholder="采购单位" data-validate-length-range="1,6" type="text" required>&nbsp;&nbsp;&nbsp;&nbsp;<br>
                                            <input type="text" id="text2<c:if test="${status.count > 1}">${status.count-1}</c:if>" name="text2" value="${detail.supplier.name}" placeholder="供应商" style="width:340px;margin-top:6px" required />
                                            <input type="hidden" id="details[][supplier[id]]<c:if test="${status.count > 1}">${status.count-1}</c:if>" value="${detail.supplier.id}" name="details[][supplier[id]]:number">
                                        </div>
                                        <c:set var="cursor" value="${status.count-1}" />
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
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:if test="${entity.state == 0}">
                                            <button id="send" type="button" class="btn btn-success">更新</button>
                                            <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                            <button id="delete" type="button" class="btn btn-danger">作废</button>
                                        </c:if>
                                        <c:if test="${entity.state == 2}">
                                            <button id="editState" type="button" class="btn btn-primary">编辑</button>
                                            <button id="recover" type="button" class="btn btn-success">恢复</button>
                                        </c:if>
                                    </c:if>
                                </div>
                            </div>
                            <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>0</c:otherwise></c:choose>">
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                            <input type="hidden" id="inputer[id]" name="inputer[id]" value="${userId}">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    <c:if test="${entity != null}">
    setSelect(document.getElementById("type"), "${entity.type}");
    </c:if>

    $('#date').daterangepicker({
        locale: {
            format: 'YYYY-MM-DD',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            customRangeLabel : '自定义',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1
        },
        singleDatePicker: true,
        singleClasses: "picker_3"
    }, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    var cursor = ${cursor};

    if (cursor > 0) {
        for (var ci = 1; ci <= cursor; ci++) {
            $("#text2"+ci).coolautosuggest({
                url:"<%=request.getContextPath()%>/erp/suggest/supplier/name/",
                showProperty: 'name',
                onSelected:function(result){
                    if(result!=null){
                        $(document.getElementById("details[][supplier[id]]"+ci)).val(result.id);
                    }
                }
            });
        }
    }

    $('#addItem').click(function () {
        cursor++;
        var detailHtml = String($("#detail").html());

        var detailItems = document.getElementById("detail").children;
        for (var index in detailItems) {
            var id = String(detailItems[index].id);
            detailHtml = detailHtml.replace('id="' + id + '"', 'id="' + id + String(cursor) +'"');
        }
        var detailsHtmlParts = detailHtml.split("<br>");

        $('#details').append('<div class="row">' +
            detailsHtmlParts[0] +
            '<a href="#minus" onclick="this.parentNode.parentNode.removeChild(this.parentNode);"><i class="fa fa-minus-circle"></i></a>' +
            detailsHtmlParts[1] + "</div>");

        document.getElementById("1" + count).focus();

        $("#text2"+count).coolautosuggest({
            url:"<%=request.getContextPath()%>/erp/suggest/supplier/name/",
            showProperty: 'name',
            onSelected:function(result){
                if(result!=null){
                    $(document.getElementById("details[][supplier[id]]"+count)).val(result.id);
                }
            }
        });
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

    $("#text2").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/supplier/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("details[][supplier[id]]")).val(result.id);
            }
        }
    });

    $("#send").bind("click", function(){
        $('#form').submitForm('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Purchase.class.getSimpleName().toLowerCase()%>');
    });

    $("#delete").click(function(){
        if (confirm("确定作废该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":2}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":0}');
        }
    });

    $(document).unbind("keydown");

    <c:choose><c:when test="${entity != null}">document.title = "采购单查看";</c:when><c:otherwise> document.title = "采购单填写";</c:otherwise></c:choose>
</script>