<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>查看订单</h3>
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
                        <h2>订单 <small>信息</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">订单信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">订单号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.no}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">状态 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.stateName}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.typeName}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">总金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.amount}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">折扣 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.discount}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">实际支付金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.payAmount}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">生成时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.date}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">用户 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.user.name}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">销售人员 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.saler.name}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>

                            <div class="item form-group" style="margin-top: 30px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">订单明细</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <table class="table-sheet product-property-input">
                                        <thead><tr><th>商品编号</th><th>商品名称</th><th>状态</th><th>数量</th><th>单位</th><th>金额</th><th>折扣</th><th>支付金额</th><th>调价编号/金额</th><th>加工配饰费</th><th>寄送</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.details}" var="detail">
                                            <tr>
                                                <td><a href="#<%=request.getContextPath()%>/erp/view/product/${detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${detail.product.id}')">${detail.product.no}</a></td>
                                                <td>${detail.product.name}</td>
                                                <td>${detail.stateName}</td>
                                                <td>${detail.quantity}</td>
                                                <td>${detail.unit}</td>
                                                <td>${detail.amount}</td>
                                                <td>${detail.discount}</td>
                                                <td>${detail.payAmount}</td>
                                                <td><a href="#<%=request.getContextPath()%>/erp/view/productPriceChange/${detail.priceChange.id}" onclick="render('<%=request.getContextPath()%>/erp/view/productPriceChange/${detail.priceChange.id}')">${detail.priceChange.no}/${detail.priceChange.price}</a></td>
                                                <td><a href="#<%=request.getContextPath()%>/orderManagement/view/orderPrivate/${detail.orderPrivate.id}" onclick="render('<%=request.getContextPath()%>/orderManagement/view/orderPrivate/${detail.orderPrivate.id}')">
                                                <c:if test="${detail.orderPrivate != null}"><c:choose><c:when test="${detail.orderPrivate.amount != null}">${detail.orderPrivate.amount}</c:when><c:otherwise>核定金额</c:otherwise></c:choose></c:if>
                                                </a></td>
                                                <td>收货人：${detail.express.receiver}<br/>电话：${detail.express.phone}<br/>邮政编码：${detail.express.postCode}<br/>地址：${detail.express.address}</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <div class="col-md-6 col-md-offset-3" id="submitDiv">
                                <button id="cancel" type="button" class="btn btn-primary">返回</button>
                                <button id="cancelOrder" type="button" class="btn btn-primary">取消订单</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $("#cancelOrder").click(function(){
        $("#form").sendData('<%=request.getContextPath()%>/orderManagement/unlimitedCancel/${entity.id}', '{"sessionId":${sessionId}}');
    });
</script>