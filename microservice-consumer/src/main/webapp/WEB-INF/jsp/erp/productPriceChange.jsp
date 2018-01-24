<%--
**
* Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
* 文件名: productPriceChange.java
*
* @author smjie
* @Date  2017/8/30
* @version 1.00
*
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page import="com.hzg.erp.ProductPriceChange" %>
<%@ page import="com.hzg.erp.Product" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>商品调价<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>申请</c:otherwise></c:choose></h3>
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
                        <h2>销售订单 <small>商品调价</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">调价信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="no">调价编码 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="no" name="no" value="<c:choose><c:when test="${entity != null}">${entity.no}</c:when><c:otherwise>${no}</c:otherwise></c:choose>" class="form-control col-md-7 col-xs-12" style="width:40%" required  />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="text">商品编码 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text" name="text" value="${entity.product.no}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入商品编号" required />
                                    <input type="hidden" id="productNo" name="productNo" value="${entity.productNo}">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="text">商品名称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="name" name="name" value="${entity.product.name}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="text">商品价格 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="prePrice" name="prePrice" value="${entity.prePrice}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="price">调整后价格</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="price" name="price" value="${entity.price}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入价格" required />
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                        <button id="doBusiness" type="button" class="btn btn-success">申请</button>
                                    </c:if>
                                    <c:if test="${entity != null && entity.state == 2}">
                                        <button id="send" type="button" class="btn btn-success">修改</button>
                                        <button id="doBusiness" type="button" class="btn btn-success">申请</button>
                                        <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                    </c:if>
                                </div>
                            </div>
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                            <input type="hidden" id="state" name="state" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>2</c:otherwise></c:choose>">
                            <input type="hidden" id="sessionId" name="sessionId" value="${sessionId}">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=ProductPriceChange.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+ProductPriceChange.class.getSimpleName().substring(1)%>');});

    $("#doBusiness").click(function(){
        if (confirm("点击申请后将不再可以修改价格变动，确定申请吗？")) {
            $("#state").val("<%=ErpConstant.product_price_change_state_apply%>");
            $("#send").click();
        }
    });

    $("#text").coolautosuggestm({
        url:"<%=request.getContextPath()%>/erp/privateQuery/<%=Product.class.getSimpleName().toLowerCase()%>",
        showProperty: "no",

        getQueryData: function(paramName){
            var queryJson = {};

            var suggestWord = $.trim(this.value);
            if (suggestWord != "") {
                queryJson["no"] = suggestWord;
            }
            queryJson["state"] = <%=ErpConstant.product_state_onSale%>;

            return queryJson;
        },

        onSelected:function(result){
            if(result!=null){
                $("#name").val(result.name);
                $("#prePrice").val(result.fatePrice);
                $("#productNo").val(result.no);
            }
        }

    });

    <c:choose><c:when test="${entity != null}">document.title = "商品调价修改";</c:when><c:otherwise> document.title = "商品调价申请";</c:otherwise></c:choose>
</script>