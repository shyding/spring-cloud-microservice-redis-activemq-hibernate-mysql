<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.tools.OrderConstant" %>
<%@ page import="com.hzg.tools.PayConstants" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>代下单</h3>
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
                        <h2>销售订单 <small>代下单</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">代下单</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">订单类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="type" name="type:number" class="form-control col-md-7 col-xs-12" style="width:40%" required>
                                        <option value="">请选择类型</option>
                                        <option value="<%=OrderConstant.order_type_assist%>">代下单</option>
                                        <option value="<%=OrderConstant.order_type_assist_process%>">代下加工单</option>
                                        <option value="<%=OrderConstant.order_type_private%>">私人订制</option>
                                        <option value="<%=OrderConstant.order_type_book%>">预定</option>
                                    </select>
                                </div>
                            </div>
                            <div id="orderBookDiv" style="display: none">
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="orderBookDeposit">订金</label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input type="number" id="orderBookDeposit" name="orderBook[deposit]:number" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入预定订金" disabled>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="orderBookPayDate">订金付款时间</label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input type="text" id="orderBookPayDate" name="orderBook[payDate]:string" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入订金付款时间" disabled>
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group" style="padding-top: 40px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="bookUser">订购人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="bookUser" name="bookUser" value="" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名选择订购人" required>
                                    <input type="hidden" id="user[id]" name="user[id]" value="">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">收货信息 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12" id="expressDiv">
                                    <div style="padding-top:8px;padding-bottom:8px">选择&nbsp;&nbsp;收货地址&nbsp;/&nbsp;收货人&nbsp;/&nbsp;收货电话&nbsp;/&nbsp;邮编</div>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="expressDate">发货时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" id="expressDate" name="expressDate" class="form-control" style="width:37%">
                                    </div>
                                </div>
                            </div>

                            <div class="item form-group" style="padding-top: 30px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">支付明细 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div style="padding-top:8px;padding-bottom:8px">支付方式&nbsp;/&nbsp;收款账号&nbsp;/&nbsp;支付银行&nbsp;/&nbsp;支付账号&nbsp;/&nbsp;支付金额</div>
                                    <table id="payList">
                                        <tbody>
                                        <tr>
                                            <td>
                                                <select name="pays[][payType]:number" class="form-control col-md-7 col-xs-12" required>
                                                    <option value="">请选择支付方式</option>
                                                        <option value="<%=PayConstants.type_pay_transfer_accounts%>">转账</option>
                                                        <option value="<%=PayConstants.type_pay_remit%>">汇款</option>
                                                        <option value="<%=PayConstants.type_pay_other%>">其他</option>
                                                </select>
                                            </td>
                                            <td>
                                                <select name="receiptAccountInfo" class="form-control col-md-7 col-xs-12" required>
                                                    <option value="">请选择收款账号</option>
                                                    <c:forEach items="${accounts}" var="account">
                                                        <option value="${account.account}/${account.branch}/${account.bank}">${account.account}/${account.branch}/${account.bank}</option>
                                                    </c:forEach>
                                                </select>
                                                <input type="hidden" name="pays[][receiptAccount]:string">
                                                <input type="hidden" name="pays[][receiptBranch]:string">
                                                <input type="hidden" name="pays[][receiptBank]:string">
                                            </td>
                                            <td>
                                                <select name="pays[][payBank]:string" class="form-control col-md-7 col-xs-12" required>
                                                    <option value="">请选择银行</option>
                                                    <%=PayConstants.bankSelectOptions%>
                                                </select>
                                            </td>
                                            <td><input type="text" class="form-control col-md-7 col-xs-12" name="pays[][payAccount]:string" required></td>
                                            <td><input type="number" class="form-control col-md-7 col-xs-12" name="pays[][amount]:number" required></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <div style="padding-top: 8px"><a href="javascript:void(0)" id="addPayItem">添加支付记录</a></div>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="payAmount">总支付金额 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="payAmount" name="payAmount:number" value="" class="form-control col-md-7 col-xs-12" style="width:40%;background: snow" required readonly>
                                </div>
                            </div>

                            <div class="item form-group" style="padding-top: 38px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">赠品</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" data-property-name="giftsInfo" name="giftsQuantityUnit:string" class="form-control col-md-7 col-xs-12" style="width:40%; background: snow" readonly placeholder="请选择赠品">
                                    <br/><br/><a href="#" data-property-name="chooseGifts" style="width:54px;display: block;padding-top: 4px">选择赠品</a>
                                    <div data-gift-info="itemsInfo" style="display: none"></div>
                                </div>
                            </div>
                            <input type="hidden" id="sessionId" name="sessionId" value="${sessionId}">
                            <input type="hidden" id="amount" name="amount:number" value="">
                        </form>
                    </div>


                    <div class="x_content" style="overflow: auto;padding-top: 10px">
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" style="text-align: right">订单商品明细</label>
                        </div>
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品编号</th><th>商品名称</th><th>结缘价</th><th>价格浮动码</th><th>价格浮动金额</th><th>数量</th><th>计量单位</th><th>支付金额</th><th>价格、私人订制描述</th><th>私人订制配饰</th></tr></thead>
                            <tbody>
                            <tr>
                                <td><input type="text" data-property-name="productNo" name="details[][productNo]:string"  required></td>
                                <td><input type="text" name="details[][product[name]]:string" readonly></td>
                                <td><input type="number" data-property-name="fatePrice" name="details[][product[fatePrice]]:number" readonly></td>
                                <td><input type="text" data-property-name="priceChangeNo" name="details[][priceChange[no]]:string" data-skip-falsy="true"></td>
                                <td><input type="text" name="details[][priceChange[price]]:number" data-skip-falsy="true" readonly></td>
                                <td><input type="text" data-property-name="quantity" name="details[][quantity]:number" value="1" required></td>
                                <td>
                                    <select name="details[][unit]:string" required>
                                        <option value="件">件</option>
                                        <option value="克">克</option>
                                        <option value="克拉">克拉</option>
                                        <option value="只">只</option>
                                        <option value="双">双</option>
                                        <option value="条">条</option>
                                        <option value="枚">枚</option>
                                        <option value="副">副</option>
                                        <option value="其他">其他</option>
                                    </select>
                                </td>
                                <td><input type="text" name="details[][payAmount]:number" readonly></td>
                                <td><input type="text" name="details[][orderPrivate[describes]]:string"></td>
                                <td><input type="text" data-property-name="accsInfo" name="accsQuantityUnit" placeholder="请选择配饰" readonly>
                                    <a href="#" data-property-name="chooseAccs" style="padding-left: 10px;padding-right: 20px;border-left: 1px solid black">选择</a>
                                    <div data-acc-info="itemsInfo" style="display: none"></div>
                                </td>
                                <input type="hidden" name="details[][product[price]]:number">
                                <input type="hidden" data-property-name="productPrice" name="details[][productPrice]:number">
                                <input type="hidden" name="details[][priceChange[id]]:number" data-skip-falsy="true">
                                <input type="hidden" name="details[][priceChange[productNo]]:string" data-skip-falsy="true">
                                <input type="hidden" name="details[][amount]:number">
                                <input type="hidden" name="details[][express[id]]:number">
                                <input type="hidden" name="details[][express[id]]:number">
                                <input type="hidden" name="details[][expressDate]:string">
                            </tr>
                            </tbody>
                        </table>
                        <div class="form-horizontal form-label-left">
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-0" style="margin-top: 10px">
                                    <button id="addItem" type="button" class="btn btn-success">添加订购条目</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <div class="form-group" id="submitDiv">
                                <div class="col-md-6 col-md-offset-10">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <button id="send" type="button" class="btn btn-success">保存订单</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="accChooseDiv">
                        <br>
                        <table id="accList" class="table-sheet" width="100%">
                            <thead><tr><th>配饰名称</th><th>配饰编号</th><th>数量</th><th>计量单位</th></tr></thead>
                            <tbody>
                            <% for(int i = 0; i < 10; i++) { %>
                            <tr>
                                <td><input type="text" name="accName" placeholder="输入名称，选择配饰"></td>
                                <td><input type="text" name="accNo" readonly></td>
                                <td><input type="text" name="accQuantity" value="1"></td>
                                <td>
                                    <select name="accUnit">
                                        <option value="件">件</option>
                                        <option value="克">克</option>
                                        <option value="克拉">克拉</option>
                                        <option value="只">只</option>
                                        <option value="双">双</option>
                                        <option value="条">条</option>
                                        <option value="枚">枚</option>
                                        <option value="副">副</option>
                                        <option value="其他">其他</option>
                                    </select>
                                </td>
                                <input type="hidden" name="accId">
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>

                    <div id="giftChooseDiv">
                        <br>
                        <table id="giftList" class="table-sheet" width="100%">
                            <thead><tr><th>赠品名称</th><th>赠品编号</th><th>采购价</th><th>结缘价</th><th>数量</th><th>计量单位</th></tr></thead>
                            <tbody>
                            <% for(int i = 0; i < 5; i++) { %>
                            <tr>
                                <td><input type="text" name="giftName" placeholder="输入名称，选择赠品"></td>
                                <td><input type="text" name="giftNo" readonly></td>
                                <td><input type="text" name="giftCostPrice" readonly></td>
                                <td><input type="text" name="giftFatePrice" readonly></td>
                                <td><input type="text" name="giftQuantity" value="1"></td>
                                <td>
                                    <select name="giftUnit">
                                        <option value="件">件</option>
                                        <option value="克">克</option>
                                        <option value="克拉">克拉</option>
                                        <option value="只">只</option>
                                        <option value="双">双</option>
                                        <option value="条">条</option>
                                        <option value="枚">枚</option>
                                        <option value="副">副</option>
                                        <option value="其他">其他</option>
                                    </select>
                                </td>
                                <input type="hidden" name="giftId">
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $('#addPayItem').click(function(){assistBook.addPay()});

    assistBook.init("productList", 8, "<%=request.getContextPath()%>");
    $('#addItem').click(function(){assistBook.addRow()});

    $("#send").bind("click", function(){
        assistBook.saveOrder('<%=request.getContextPath()%>/orderManagement/assistBook');
    });

    <c:choose><c:when test="${entity != null}">document.title = "代下单";</c:when><c:otherwise> document.title = "代下单";</c:otherwise></c:choose>
</script>