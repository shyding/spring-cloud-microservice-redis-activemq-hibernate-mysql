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
                <h3>查看退货</h3>
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
                        <h2>退货 <small>信息</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">退货信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">退货单号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="no" type="text" name="no" value="${entity.no}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <c:if test="${entity.state != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="stateName">状态 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="stateName" type="text" value="${entity.stateName}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">退货关联单 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <a data-entity-no-a="entityNoA" data-entity-no="${entity.entityNo}" data-entity-id="${entity.entityId}" href="#">${entity.entityNo}</a>
                                    <input type="hidden" name="entity" value="${entity.entity}" />
                                    <input type="hidden" name="entityId" value="${entity.entityId}" />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="amount">退款金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="amount" type="text" value="${entity.amount}" class="form-control col-md-7 col-xs-12"></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="username">退货人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="username" value="${entity.user.username}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <c:if test="${entity.inputDate != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="inputDate">退货申请时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="inputDate" type="text" value="${entity.inputDate}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <c:if test="${entity.date != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="date">退货完成时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="date" type="text" value="${entity.date}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="reason">退货原因 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><textarea class="form-control col-md-7 col-xs-12" id="reason" name="reason" <c:if test="${entity.reason != null}">readonly</c:if> required>${entity.reason}</textarea></div>
                            </div>
                            <input name="sessionId" type="hidden" value="${sessionId}">
                        </form>

                        <span class="section" style="margin-top: 40px">退货明细</span>
                        <div class="item form-group" style="margin-top:20px;">
                            <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                <table id="productList" class="table-sheet">
                                    <thead><tr><c:if test="${entity.state == null}"><th>选择</th></c:if><th>商品编号</th><th>商品名称</th><c:if test="${entity.state != null}"><th>状态</th></c:if><th>退货数量</th><th>退货单位</th><th>退货单价</th><th>退款金额</th></tr></thead>
                                    <tbody>
                                    <c:forEach items="${entity.details}" var="detail">
                                        <tr>
                                            <c:if test="${entity.state == null}"><td align="center"><input type="checkbox" data-property-name="productNo" name="details[][productNo]" value="${detail.productNo}" class="flat" checked></td></c:if>
                                            <td style="width: 120px"><a href="#<%=request.getContextPath()%>/erp/view/product/${detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${detail.product.id}')">${detail.productNo}</a></td>
                                            <td style="width:200px"><input type="text" value="${detail.product.name}" readonly></td>
                                            <c:if test="${entity.state != null}"><td><input type="text" value="${detail.stateName}"></td></c:if>
                                            <td><input type="text" data-property-name="quantity" name="details[][quantity]:number" value="${detail.quantity}" required></td>
                                            <td><input type="text" value="${detail.unit}" required readonly></td>
                                            <td><input type="text" name="details[][price]:number" value="${detail.price}" required readonly></td>
                                            <td><input type="text" name="details[][amount]:number" value="${detail.amount}" required readonly></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div class="x_content">
                        <c:if test="${entity.state != null}">
                        <span class="section" style="margin-top: 40px">退货审核记录</span>
                        <div class="item form-group" style="margin-top:20px;">
                            <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                <table class="table-sheet">
                                    <thead><tr><th>审核人</th><th>审核时间</th><th>审核结果</th><th>备注</th></tr></thead>
                                    <tbody>
                                    <c:forEach items="${entity.actions}" var="action">
                                        <tr>
                                            <td style="width: 120px">${action.inputer.name}</td>
                                            <td style="width: 120px">${action.inputDate}</td>
                                            <td style="width: 200px">${action.typeName}</td>
                                            <td style="width: 250px">${action.remark}</td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        </c:if>
                    </div>

                    <div class="x_content">
                        <span class="section" style="margin-top: 40px">审核</span>
                        <div class="item form-group" style="margin-top:20px;">
                            <div class="col-md-6 col-sm-6 col-xs-12" style="margin-left: 150px;margin-top: 10px">
                                <form id="actionForm">
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" style="width: 80px" for="remark">批语 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12"><textarea class="form-control col-md-7 col-xs-12" style="width: 600px" id="remark" name="remark"></textarea></div>
                                    </div>
                                    <input type="hidden" name="auditResult" id="auditResult">
                                    <input type="hidden" name="entityId:number" id="entityId" value="${entity.id}">
                                    <input type="hidden" name="sessionId" value="${sessionId}">
                                </form>
                            </div>
                        </div>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <div class="col-md-12 col-md-offset-1" id="submitDiv">
                                <button id="cancel" type="button" style="margin-right: 10%" class="btn btn-primary">返回</button>
                                <c:if test="${entity.state == null}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/save/returnProduct')}">
                                <button id="returnProduct" type="button" class="btn btn-success">提交退货申请</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state != null}">
                                <c:if test="${entity.state == 0}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductSaleAudit')}">
                                <button id="saleAuditPass" type="button" class="btn btn-success">可以退货</button>
                                <button id="saleAuditNotPass" type="button" class="btn btn-success">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 3}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductDirectorAudit')}">
                                <button id="directorAuditPass" type="button" class="btn btn-success">可退</button>
                                <button id="directorAuditNotPass" type="button" class="btn btn-success">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 4}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductWarehousingAudit')}">
                                <button id="warehousingAuditPass" type="button" class="btn btn-success">可退</button>
                                <button id="warehousingAuditNotPass" type="button" class="btn btn-success">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 5}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductRefund')}">
                                <button id="refund" type="button" class="btn btn-success">确认退款</button>
                                </c:if>
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

    returnProduct.init(<c:out value="${entity.state == null}"/>, "<%=request.getContextPath()%>");

    <c:if test="${entity.state == null}">
        <c:if test="${fn:contains(resources, '/afterSaleService/save/returnProduct')}">
            $("#returnProduct").click(function(){
                returnProduct.save('<%=request.getContextPath()%>/afterSaleService/save/returnProduct');
            });
        </c:if>
    </c:if>

    <c:if test="${entity.state != null}">
        <c:if test="${entity.state == 0}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductSaleAudit')}">
                $("#saleAuditPass").click(function(){
                    returnProduct.saleAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductSaleAudit');
                });

                $("#saleAuditNotPass").click(function(){
                    returnProduct.saleAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductSaleAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 3}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductDirectorAudit')}">
                $("#directorAuditPass").click(function(){
                    returnProduct.directorAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductDirectorAudit');
                });

                $("#directorAuditNotPass").click(function(){
                    returnProduct.directorAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductDirectorAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 4}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductWarehousingAudit')}">
                $("#warehousingAuditPass").click(function(){
                    returnProduct.warehousingAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductWarehousingAudit');
                });

                $("#warehousingAuditNotPass").click(function(){
                    returnProduct.warehousingAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductWarehousingAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 5}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductRefund')}">
                $("#refund").click(function(){
                    returnProduct.refund('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/returnProductRefund');
                });
            </c:if>
        </c:if>
    </c:if>
</script>