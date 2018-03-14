<%@ page import="com.hzg.tools.PayConstants" %>
<%@ page import="com.hzg.pay.Pay" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hzg.afterSaleService.ChangeProduct" %>
<%@ page import="com.hzg.pay.Account" %>
<%@ page import="com.google.gson.Gson" %>
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
                <h3>查看换货</h3>
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
                        <h2>换货 <small>信息</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">换货信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="no">换货单号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="no" type="text" name="no" value="${entity.no}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <c:if test="${entity.state != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="stateName">状态 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="stateName" type="text" value="${entity.stateName}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">换货关联单 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <a data-entity-no-a="entityNoA" data-entity-no="${entity.entityNo}" data-entity-id="${entity.entityId}" href="#">${entity.entityNo}</a>
                                    <input type="hidden" name="entity" value="${entity.entity}" />
                                    <input type="hidden" name="entityId" value="${entity.entityId}" />
                                </div>
                            </div>

                            <c:if test="${entity.state == null}">
                            <%
                                List<Pay> pays = ((ChangeProduct)request.getAttribute("entity")).getPays();
                                Integer defaultPayType = pays.get(0).getPayType();
                                String defaultReceiptAccount = pays.get(0).getReceiptAccount();
                                String defaultReceiptBranch = pays.get(0).getReceiptBranch();
                                String defaultReceiptBank = pays.get(0).getReceiptBank();

                                String defaultReceiptAccountInfo = defaultReceiptAccount + "/" + defaultReceiptBranch + "/" + defaultReceiptBank;
                                String defaultPayBank = pays.get(0).getPayBank();
                                String defaultPayAccount = pays.get(0).getPayAccount();

                                String payTypeOptions = "<option value='" + PayConstants.pay_type_transfer_accounts_alipay +"'>支付宝转账</option>" +
                                        "<option value='" + PayConstants.pay_type_transfer_accounts_weixin + "'>微信转账</option>" +
                                        "<option value='" + PayConstants.pay_type_transfer_accounts + "'>转账</option>" +
                                        "<option value='" + PayConstants.pay_type_remit + "'>汇款</option>" +
                                        "<option value='" + PayConstants.pay_type_other +"'>其他</option>";

                                List<Account> accounts = (List<Account>) request.getAttribute("accounts");
                                String accountOptions = "";
                                for (Account account : accounts) {
                                    String accountInfo = account.getAccount() + "/" + account.getBranch() + "/" + account.getBank();
                                    accountOptions += "<option value='" + accountInfo + "'>" + accountInfo + "</option>";
                                }

                            %>
                            <div id="payDiv" class="item form-group" style="padding-top: 30px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">支付明细 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div style="padding-top:8px;padding-bottom:8px">支付方式&nbsp;/&nbsp;收款账号&nbsp;/&nbsp;支付银行&nbsp;/&nbsp;支付账号&nbsp;/&nbsp;支付金额</div>
                                    <table id="payList">
                                        <tbody>
                                        <tr>
                                            <td>
                                                <select name="pays[][payType]:number" class="form-control col-md-7 col-xs-12" required>
                                                <%=payTypeOptions.replace("'" + defaultPayType + "'", "'" + defaultPayType + "' selected")%>
                                                </select>
                                            </td>
                                            <td>
                                                <select name="receiptAccountInfo" class="form-control col-md-7 col-xs-12" required>
                                                <%=accountOptions.replace("'" + defaultReceiptAccountInfo + "'", "'" + defaultReceiptAccountInfo + "' selected")%>
                                                </select>
                                                <input type="hidden" name="pays[][receiptAccount]:string" value="<%=defaultReceiptAccount%>">
                                                <input type="hidden" name="pays[][receiptBranch]:string" value="<%=defaultReceiptBranch%>">
                                                <input type="hidden" name="pays[][receiptBank]:string" value="<%=defaultReceiptBank%>">
                                            </td>
                                            <td>
                                                <select name="pays[][payBank]:string" class="form-control col-md-7 col-xs-12" required>
                                                    <%=PayConstants.bankSelectOptions.replace("'" + defaultPayBank + "'", "'" + defaultPayBank + "' selected")%>
                                                </select>
                                            </td>
                                            <td><input type="text" class="form-control col-md-7 col-xs-12" data-account="account" data-account-suggest="account" name="pays[][payAccount]:string" value="<%=defaultPayAccount%>" required></td>
                                            <td><input type="number" class="form-control col-md-7 col-xs-12" name="pays[][amount]:number" required>
                                                <input type="hidden" name="pays[][balanceType]:number" value="0" required/>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <div style="padding-top: 8px"><a href="javascript:void(0)" id="addPayItem">添加支付记录</a></div>
                                </div>
                            </div>
                            </c:if>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="amount">换货费 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="fee" name="fee" type="text" value="<c:if test="${entity.fee != null}">${entity.fee}</c:if><c:if test="${entity.fee == null}">0</c:if>" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="amount">换货差价(要换商品金额 + 换货费 - 要退商品金额) <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="amount" name="amount" type="text" value="${entity.amount}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="username">换货人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" id="username" value="${entity.user.username}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            <c:if test="${entity.inputDate != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="inputDate">换货申请时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="inputDate" type="text" value="${entity.inputDate}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <c:if test="${entity.date != null}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="date">换货完成时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="date" type="text" value="${entity.date}" class="form-control col-md-7 col-xs-12" readonly></div>
                            </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="reason">换货原因 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><textarea class="form-control col-md-7 col-xs-12" id="reason" name="reason" <c:if test="${entity.reason != null}">readonly</c:if> required>${entity.reason}</textarea></div>
                            </div>
                            <input name="sessionId" type="hidden" value="${sessionId}">

                            <span class="section" style="margin-top: 40px">要退商品（客户已买商品）</span>
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table data-table-name="returnProductList" class="table-sheet">
                                        <thead><tr><c:if test="${entity.state == null}"><th>选择</th></c:if><th>商品编号</th><th>商品名称</th><c:if test="${entity.state != null}"><th>状态</th></c:if><th>退货数量</th><th>退货单位</th><th>退货单价</th><th>退货金额</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.details}" var="detail">
                                            <c:if test="${detail.type == 1}">
                                                <tr>
                                                    <c:if test="${entity.state == null}"><td align="center"><input type="checkbox" data-property-name="productNo" name="details[][productNo]" value="${detail.productNo}" class="flat" checked></td></c:if>
                                                    <td style="width: 120px"><a href="#<%=request.getContextPath()%>/erp/view/product/${detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${detail.product.id}')">${detail.productNo}</a></td>
                                                    <td style="width:200px"><input type="text" value="${detail.product.name}" readonly></td>
                                                    <c:if test="${entity.state != null}"><td><input type="text" value="${detail.stateName}"></td></c:if>
                                                    <td><input type="text" data-property-name="quantity" name="details[][quantity]:number" value="${detail.quantity}" required></td>
                                                    <td><input type="text" name="details[][unit]:string" value="${detail.unit}" readonly></td>
                                                    <td><input type="text" name="details[][price]:number" value="${detail.price}" readonly></td>
                                                    <td><input type="text" name="details[][amount]:number" value="${detail.amount}" readonly>
                                                        <input type="hidden" name="details[][type]:number" value="1" required/></td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <span class="section" style="margin-top: 40px">要换商品（商家在售商品）</span>
                            <c:set var="count" value="0"/>
                            <c:if test="${entity.details != null}">
                                <c:forEach items="${entity.details}" var="detail"><c:if test="${detail.type == 0}"><c:set var="count" value="${count+1}" /></c:if></c:forEach>
                            </c:if>
                            <c:if test="${count != 0}">
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table data-table-name="changeProductList" class="table-sheet">
                                        <thead><tr><th>商品编号</th><th>商品名称</th><th>状态</th><th>换货数量</th><th>换货单位</th><th>换货单价</th><th>换货金额</th></tr></thead>
                                        <tbody>
                                        <c:forEach items="${entity.details}" var="detail">
                                            <c:if test="${detail.type == 0}">
                                                <tr>
                                                    <td style="width: 120px"><a href="#<%=request.getContextPath()%>/erp/view/product/${detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${detail.product.id}')">${detail.productNo}</a></td>
                                                    <td style="width:200px"><input type="text" value="${detail.product.name}" readonly></td>
                                                    <td><input type="text" value="${detail.stateName}"></td>
                                                    <td><input type="text" data-property-name="quantity" name="details[][quantity]:number" value="${detail.quantity}" required></td>
                                                    <td><input type="text" name="details[][unit]:string" value="${detail.unit}" readonly></td>
                                                    <td><input type="text" name="details[][price]:number" value="${detail.price}" readonly></td>
                                                    <td><input type="text" name="details[][amount]:number" value="${detail.amount}" readonly>
                                                        <input type="hidden" name="details[][type]:number" value="0" required/></td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            </c:if>
                            <c:if test="${count == 0}">
                            <div class="item form-group" style="margin-top:20px;">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="width:1400px;margin-left: 150px;margin-top: 10px">
                                    <table data-table-name="changeProductList" class="table-sheet">
                                        <thead><tr><th>商品编号</th><th>商品名称</th><th>换货数量</th><th>换货单位</th><th>换货单价</th><th>换货金额</th></tr></thead>
                                        <tbody>
                                            <tr>
                                                <td style="width: 120px"><input type="text" suggest-name="productNo" data-property-name="productNo" name="details[][productNo]:string"></td>
                                                <td style="width: 200px"><input type="text" name="details[][product[name]]:string" readonly></td>
                                                <td><input type="text" data-property-name="quantity" name="details[][quantity]:number" value="1" required></td>
                                                <td><input type="text" name="details[][unit]:string" readonly></td></td>
                                                <td><input type="text" name="details[][price]:number" readonly></td>
                                                <td><input type="text" name="details[][amount]:number" readonly>
                                                    <input type="hidden" name="details[][type]:number" value="0" required></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            </c:if>
                        </form>
                    </div>

                    <div class="x_content">
                        <c:if test="${entity.state != null}">
                        <span class="section" style="margin-top: 40px">换货审核记录</span>
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

                    <c:if test="${(fn:contains(resources, '/afterSaleService/doBusiness/changeProductSaleAudit') && entity.state == 0) ||
                    (fn:contains(resources, '/afterSaleService/doBusiness/changeProductDirectorAudit') && entity.state == 3) ||
                    (fn:contains(resources, '/afterSaleService/doBusiness/changeProductWarehousingAudit') && entity.state == 4) ||
                    (fn:contains(resources, '/afterSaleService/doBusiness/changeProductComplete') && entity.state == 5) ||
                    entity.state == null}">
                    <div class="x_content">
                        <span class="section" style="margin-top: 40px">审核</span>
                        <div class="item form-group" style="margin-top:20px;">
                            <div class="col-md-6 col-sm-6 col-xs-12" style="margin-left: 150px;margin-top: 10px">
                                <form id="actionForm">
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" style="width: 80px" for="remark">批语 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12"><textarea class="form-control col-md-7 col-xs-12" style="width: 600px" id="remark" name="remark" required></textarea></div>
                                    </div>
                                    <input type="hidden" name="auditResult" id="auditResult">
                                    <input type="hidden" name="entityId:number" id="entityId" value="${entity.id}">
                                    <input type="hidden" name="sessionId" value="${sessionId}">
                                </form>
                            </div>
                        </div>
                    </div>
                    </c:if>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <c:if test="${(fn:contains(resources, '/afterSaleService/doBusiness/changeProductSaleAudit') && entity.state == 0) ||
                            (fn:contains(resources, '/afterSaleService/doBusiness/changeProductDirectorAudit') && entity.state == 3) ||
                            (fn:contains(resources, '/afterSaleService/doBusiness/changeProductWarehousingAudit') && entity.state == 4) ||
                            (fn:contains(resources, '/afterSaleService/doBusiness/changeProductComplete') && entity.state == 5) ||
                            entity.state == null}">
                            <span class="section" style="margin-top: 40px">换货要退商品审批</span>
                            </c:if>
                            <div class="col-md-12 col-md-offset-1" id="submitDiv">
                                <button id="cancel" type="button" style="margin-right: 10%" class="btn btn-primary">返回</button>
                                <c:if test="${entity.state == null}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/save/changeProduct')}">
                                    <button id="changeProduct" type="button" class="btn btn-success">提交换货申请</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state != null}">
                                <c:if test="${entity.state == 0}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductSaleAudit')}">
                                <button id="saleAuditPass" type="button" style="margin-right: 2%" class="btn btn-success">可以退货</button>
                                <button id="saleAuditNotPass" type="button" class="btn btn-danger">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 3}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductDirectorAudit')}">
                                <button id="directorAuditPass" type="button" style="margin-right: 2%" class="btn btn-success">可退</button>
                                <button id="directorAuditNotPass" type="button" class="btn btn-danger">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 4}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductWarehousingAudit')}">
                                <button id="warehousingAuditPass" type="button" style="margin-right: 2%" class="btn btn-success">可退</button>
                                <button id="warehousingAuditNotPass" type="button" class="btn btn-danger">不可退</button>
                                </c:if>
                                </c:if>
                                <c:if test="${entity.state == 5}">
                                <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductComplete')}">
                                <button id="changeProductComplete" type="button" class="btn btn-success">确认换货完成</button>
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
    var payInfo = {};
    <c:if test="${entity.state == null}">
    payInfo = <%=new Gson().toJson(((ChangeProduct)request.getAttribute("entity")).getPays())%>;
    </c:if>
    $("#cancel, #return").unbind("click").click(function(){
        render(getPreUrls());
        returnPage = true;
    });
    changeProduct.init(<c:out value="${entity.state == null}"/>, '[data-table-name="changeProductList"]', 2, payInfo, "<%=request.getContextPath()%>");

    <c:if test="${entity.state == null}">
        <c:if test="${fn:contains(resources, '/afterSaleService/save/changeProduct')}">
            $("#changeProduct").click(function(){
                if (!validator.checkAll($("#actionForm"))) {
                    return;
                }
                changeProduct.save('<%=request.getContextPath()%>/afterSaleService/save/changeProduct');
            });
        </c:if>
    </c:if>

    <c:if test="${entity.state != null}">
        <c:if test="${entity.state == 0}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductSaleAudit')}">
                $("#saleAuditPass").click(function(){
                    changeProduct.saleAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductSaleAudit');
                });

                $("#saleAuditNotPass").click(function(){
                    changeProduct.saleAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductSaleAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 3}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductDirectorAudit')}">
                $("#directorAuditPass").click(function(){
                    changeProduct.directorAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductDirectorAudit');
                });

                $("#directorAuditNotPass").click(function(){
                    changeProduct.directorAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductDirectorAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 4}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductWarehousingAudit')}">
                $("#warehousingAuditPass").click(function(){
                    changeProduct.warehousingAudit('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductWarehousingAudit');
                });

                $("#warehousingAuditNotPass").click(function(){
                    changeProduct.warehousingAudit('N', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductWarehousingAudit');
                });
            </c:if>
        </c:if>

        <c:if test="${entity.state == 5}">
            <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductComplete')}">
                $("#changeProductComplete").click(function(){
                    changeProduct.changeProductComplete('Y', '<%=request.getContextPath()%>/afterSaleService/doBusiness/changeProductComplete');
                });
            </c:if>
        </c:if>
    </c:if>
</script>