<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .table-sheet > tbody > tr > td{
        width: 200px;
        padding:4px;
    }
</style>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>加工费、私人订制费用核定</h3>
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
                        <h2>销售订单 <small>加工费、私人订制费用核定</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品订购信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="no">编号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12" style="padding-top: 6px">
                                    <a id="no" href="#<%=request.getContextPath()%>/erp/view/product/${entity.detail.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${entity.detail.product.id}')">${entity.detail.product.no}</a>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">名称 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="name" value="${entity.detail.product.name}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="quantity">数量 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="quantity" value="${entity.detail.quantity}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="payAmount">支付金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="payAmount" value="${entity.detail.payAmount}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="amount" value="${entity.detail.amount}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="discount">折扣 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="discount" value="${entity.detail.discount}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <span class="section">费用核定</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="typeName">加工、私人订制类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="typeName" value="${entity.typeName}" class="form-control col-md-7 col-xs-12" style="width:40%" readonly />
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">加工、配置描述 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="describes" name="describes" class="form-control col-md-7 col-xs-12" style="width:40%" readonly>${entity.describes}</textarea>
                                </div>
                            </div>
                            <c:if test="${entity.accs != null}">
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12">配饰 <span class="required">*</span></label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <table class="table-sheet">
                                            <thead><tr><th>商品编号</th><th>商品名称</th><th>数量</th><th>单位</th></tr></thead>
                                            <tbody>
                                            <c:forEach items="${entity.accs}" var="acc">
                                                <tr>
                                                    <td><a href="#<%=request.getContextPath()%>/erp/view/product/${acc.product.id}" onclick="render('<%=request.getContextPath()%>/erp/view/product/${acc.product.id}')">${acc.product.no}</a></td>
                                                    <td>${acc.product.name}</td>
                                                    <td>${acc.quantity}</td>
                                                    <td>${acc.unit}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="authorizeAmount">核定金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="authorizeAmount" name="authorize[amount]" type="number" value="${entity.authorize.amount}" class="form-control col-md-7 col-xs-12" style="width:40%" required/>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="authorizeDescribes">核定描述 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="authorizeDescribes" name="authorize[describes]" class="form-control col-md-7 col-xs-12" style="width:40%" required>${entity.authorize.describes}</textarea>
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity.detail.state == 0}">
                                    <button id="send" type="button" class="btn btn-success">核定</button>
                                    </c:if>
                                </div>
                            </div>
                            <input type="hidden" id="id" name="id" value="${entity.id}">
                            <input type="hidden" id="detailId" name="detail[id]" value="${entity.detail.id}">
                            <input type="hidden" id="authorizeSessionId" name="authorize[sessionId]" value="${sessionId}">
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
    $('#authorizeAmount, #authorizeDescribes').attr("readonly",false).css("border", "1px solid #ccc");
    $('#send').attr("disabled", false);
    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/orderManagement/doBusiness/authorizeOrderPrivateAmount');});
    <c:choose><c:when test="${entity != null}">document.title = "加工费、私人订制费用核定";</c:when><c:otherwise> document.title = "加工费、私人订制费用核定";</c:otherwise></c:choose>
</script>