<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<style>
    .table-sheet > thead > tr > th{
        width: 80px;
        padding: 4px;
    }
    .table-sheet > tbody > tr > td{
        width: 80px;
        padding: 4px;
    }
</style>
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
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">订单所有人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.user.username}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">销售人员 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.saler.name}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>

                            <span class="section" style="margin-top: 40px">商品明细</span>
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table class="table-sheet">
                                        <thead><tr><th>商品编号</th><th>商品名称</th><th>状态</th><th>数量</th><th>单位</th><th>金额</th><th>折扣</th><th>支付金额</th><th>调价金额</th><th>调价编号</th><th>加工配饰费</th><th>寄送</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.details}" var="detail">
                                            <tr>
                                                <td style="width: 120px"><a name="${detail.product.no}" href="#<%=request.getContextPath()%>/erp/view/product/${detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${detail.product.id}')">${detail.product.no}</a></td>
                                                <td style="width:200px">${detail.product.name}</td>
                                                <td>${detail.stateName}</td>
                                                <td>${detail.quantity}</td>
                                                <td>${detail.unit}</td>
                                                <td>${detail.amount}</td>
                                                <td>${detail.discount}</td>
                                                <td>${detail.payAmount}</td>
                                                <td>${detail.priceChange.price}</td>
                                                <td><a href="#<%=request.getContextPath()%>/erp/view/productPriceChange/${detail.priceChange.id}" onclick="render('<%=request.getContextPath()%>/erp/view/productPriceChange/${detail.priceChange.id}')">${detail.priceChange.no}</a></td>
                                                <td><a href="#<%=request.getContextPath()%>/orderManagement/view/orderPrivate/${detail.orderPrivate.id}" onclick="render('<%=request.getContextPath()%>/orderManagement/view/orderPrivate/${detail.orderPrivate.id}')">
                                                    <c:if test="${detail.orderPrivate != null}"><c:choose><c:when test="${detail.orderPrivate.authorize.amount != null}">${detail.orderPrivate.authorize.amount}</c:when><c:otherwise><c:if test="${detail.state == 0}">核定金额</c:if></c:otherwise></c:choose></c:if>
                                                </a></td>
                                                <td style="width: 300px">发货时间：${detail.expressDate}<br/>收货人：${detail.express.receiver}<br/>电话：${detail.express.phone}<br/>邮政编码：${detail.express.postCode}<br/>地址：${detail.express.address}</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <c:if test="${entity.type == 2 || entity.type == 4}">
                            <span class="section" style="margin-top: 40px">商品加工、私人订制明细</span>
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table class="table-sheet">
                                        <thead><tr><th>商品编号</th><th>类型</th><th>描述</th><th>核定金额</th><th>核定描述</th><th>配饰（商品编号&nbsp;&nbsp;/&nbsp;&nbsp;商品名称&nbsp;&nbsp;/&nbsp;&nbsp;数量&nbsp;&nbsp;/&nbsp;&nbsp;单位）</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.details}" var="detail">
                                            <c:if test="${detail.orderPrivate != null}">
                                                <tr>
                                                    <td style="width: 120px"><a href="#${detail.product.no}">${detail.product.no}</a></td>
                                                    <td style="width: 120px">${detail.orderPrivate.typeName}</td>
                                                    <td style="width: 250px">${detail.orderPrivate.describes}</td>
                                                    <td>${detail.orderPrivate.authorize.amount}</td>
                                                    <td style="width: 250px">${detail.orderPrivate.authorize.describes}</td>
                                                    <c:if test="${detail.orderPrivate.accs != null}">
                                                        <td style="width:400px">
                                                        <c:forEach items="${detail.orderPrivate.accs}" var="acc">
                                                            <div style="padding-bottom: 2px"><a href="#<%=request.getContextPath()%>/erp/view/product/${acc.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${acc.product.id}')">${acc.product.no}</a>&nbsp;&nbsp;/&nbsp;&nbsp;
                                                                    ${acc.product.name}&nbsp;&nbsp;/&nbsp;&nbsp;${acc.quantity}&nbsp;&nbsp;/&nbsp;&nbsp;${acc.unit}</div>
                                                        </c:forEach>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            </c:if>

                            <c:if test="${!empty entity.gifts}">
                                <span class="section" style="margin-top: 40px">赠品明细</span>
                                <div class="item form-group" style="margin-top:20px;">
                                    <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                        <table class="table-sheet">
                                            <thead><tr><th>赠品编号</th><th>赠品名称</th><th>采购单价</th><th>结缘价</th><th>数量</th><th>计量单位</th></tr></thead>
                                            <tbody>
                                            <c:forEach items="${entity.gifts}" var="gift">
                                                <tr>
                                                    <td style="width: 120px"><a href="#<%=request.getContextPath()%>/erp/view/product/${gift.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${gift.product.id}')">${gift.product.no}</a></td>
                                                    <td style="width: 120px">${gift.product.name}</td>
                                                    <td>${gift.product.unitPrice}</td>
                                                    <td>${gift.product.fatePrice}</td>
                                                    <td>${gift.quantity}</td>
                                                    <td>${gift.unit}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${entity.type == 3}">
                                <span class="section" style="margin-top: 40px">订金明细</span>
                                <div class="item form-group" style="margin-top:20px;">
                                    <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                        <table class="table-sheet">
                                            <thead><tr><th>订金</th><th>订金付款时间</th><th>状态</th></tr></thead>
                                            <tbody>
                                            <tr>
                                                <td style="width: 120px">${entity.orderBook.deposit}</td>
                                                <td style="width: 200px">${entity.orderBook.payDate}</td>
                                                <td style="width: 200px">${entity.orderBook.stateName}</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:if>

                            <span class="section" style="margin-top: 40px">支付记录</span>
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table class="table-sheet">
                                        <thead><tr><th>支付号</th><th>支付状态</th><th>支付方式</th><th>支付金额</th><th>支付时间</th><th>支付账户</th><th>收款账户</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.pays}" var="pay">
                                            <tr>
                                                <td style="width: 240px"><a href="#<%=request.getContextPath()%>/pay/view/pay/${pay.id}" onclick="render('<%=request.getContextPath()%>/pay/view/pay/${pay.id}')">${pay.no}</a></td>
                                                <td>${pay.stateName}</td>
                                                <td>${pay.payTypeName}</td>
                                                <td>${pay.amount}</td>
                                                <td style="width: 160px">${pay.payDate}</td>
                                                <td style="width: 200px">${pay.payBank}<c:if test="${pay.payBranch != null}"><br/>${pay.payBranch}</c:if><c:if test="${pay.payAccount != null}"><br/>${pay.payAccount}</c:if></td>
                                                <td style="width: 200px">${pay.receiptBank}<c:if test="${pay.receiptBranch != null}"><br/>${pay.receiptBranch}</c:if><c:if test="${pay.receiptAccount != null}"><br/>${pay.receiptAccount}</c:if></td>
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
                            <div class="col-md-12 col-md-offset-1" id="submitDiv">
                                <button id="cancel" type="button" style="margin-right: 10%" class="btn btn-primary">返回</button>
                                <c:if test="${entity.state == 0}">
                                    <c:if test="${fn:contains(resources, '/orderManagement/cancel')}">
                                    <button id="cancelOrder" type="button" class="btn btn-danger">取消订单</button>
                                    </c:if>
                                    <c:if test="${ fn:contains(resources, '/orderManagement/paid')}">
                                        <c:if test="${entity.type != 0}">
                                        <button id="paid" type="button" class="btn btn-success">确认收款</button>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/orderManagement/doBusiness/orderBookPaid')}">
                                        <c:if test="${entity.type == 3 && entity.orderBook.state == 0}">
                                            <button id="orderBookPaid" type="button" class="btn btn-success">确认订金已付款</button>
                                        </c:if>
                                    </c:if>
                                </c:if>
                                <c:if test="${entity.state == 1 && entity.type == 0}">
                                    <c:if test="${fn:contains(resources, '/orderManagement/audit')}">
                                    <button id="paid" type="button" class="btn btn-success">审核通过自助单</button>
                                    </c:if>
                                </c:if>
                                <c:if test="${entity.state == 4}">
                                    <c:if test="${fn:contains(resources, '/afterSaleService/business/returnProduct')}">
                                        <button id="returnProduct" type="button" class="btn btn-danger">申请退货</button>
                                    </c:if>
                                </c:if>
                                <c:if test="${entity.state == 4}">
                                    <c:if test="${fn:contains(resources, '/afterSaleService/business/changeProduct')}">
                                        <button id="changeProduct" type="button" class="btn btn-info">申请换货</button>
                                    </c:if>
                                </c:if>
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

    <c:if test="${fn:contains(resources, '/orderManagement/cancel')}">
        $("#cancelOrder").click(function(){
            if (confirm("订单取消后不可进行支付等后续操作，确认取消订单吗？")) {
                $("#form").sendData('<%=request.getContextPath()%>/orderManagement/cancel', '{"id":"${entity.id}","sessionId":"${sessionId}"}');
            }
        });
    </c:if>

    <c:if test="${fn:contains(resources, '/orderManagement/paid')}">
        <c:if test="${entity.type != 0}">
            $("#paid").click(function(){
                if (confirm("确认收款后将出库及寄送商品，确定订单已付款吗？")) {
                    $("#form").sendData('<%=request.getContextPath()%>/orderManagement/paid', '{"id":"${entity.id}","sessionId":"${sessionId}"}');
                }
            });
        </c:if>
    </c:if>

    <c:if test="${fn:contains(resources, '/orderManagement/doBusiness/orderBookPaid')}">
        <c:if test="${entity.type == 3 && entity.orderBook.state == 0}">
            $("#orderBookPaid").click(function(){
                if (confirm("确认订金已付款吗？")) {
                    $("#form").sendData('<%=request.getContextPath()%>/orderManagement/doBusiness/orderBookPaid', '{"id":"${entity.id}","sessionId":"${sessionId}","type":"orderBookPaid"}');
                }
            });
        </c:if>
    </c:if>

    <c:if test="${entity.state == 4 && fn:contains(resources, '/afterSaleService/business/returnProduct')}">
        $("#returnProduct").click(function(){render("<%=request.getContextPath()%>/afterSaleService/business/returnProduct?json=" + encodeURI('{"entityId":${entity.id}, "entity":"order"}'));});
    </c:if>

    <c:if test="${entity.state == 4 && fn:contains(resources, '/afterSaleService/business/changeProduct')}">
        $("#changeProduct").click(function(){render("<%=request.getContextPath()%>/afterSaleService/business/changeProduct?json=" + encodeURI('{"entityId":${entity.id}, "entity":"order"}'));});
    </c:if>
</script>