<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.tools.CommonConstant" %>
<%@ page import="com.hzg.tools.OrderConstant" %>
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
                                    <select id="type" name="type:number" class="form-control col-md-7 col-xs-12" required>
                                        <option value="">请选择类型</option>
                                        <option value="<%=OrderConstant.order_type_assist%>">代下单</option>
                                        <option value="<%=OrderConstant.order_type_assist_process%>">代下加工单</option>
                                        <option value="<%=OrderConstant.order_type_private%>">私人订制</option>
                                        <option value="<%=OrderConstant.order_type_book%>">预定</option>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="bookUser">订购人</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="bookUser" name="bookUser" value="" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                    <input type="hidden" id="user[id]" name="user[id]" value="">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="payAmount">总支付金额</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="payAmount" name="payAmount" value="" class="form-control col-md-7 col-xs-12" style="width:40%;background: snow" required readonly>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">收货信息</label>
                                <div class="col-md-6 col-sm-6 col-xs-12" id="expressDiv">

                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">订单商品明细</label>
                            </div>
                            <input type="hidden" id="sessionId" name="sessionId" value="${sessionId}">
                            <input type="hidden" id="amount" name="amount" value="">
                        </form>
                    </div>

                    <div class="x_content" style="overflow: auto">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品编号</th><th>商品名称</th><th>结缘价</th><th>价格浮动码</th><th>价格浮动金额</th><th>数量</th><th>计量单位</th><th>支付金额</th><th>价格、私人订制描述</th><th>私人订制配饰</th></tr></thead>
                            <tbody>
                            <tr>
                                <td><input type="text" data-property-name="productNo" name="details[][product[no]]:string"  required></td>
                                <td><input type="text" name="details[][product[name]]:string" readonly></td>
                                <td><input type="text" name="details[][product[fatePrice]]:number" readonly></td>
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
                                <td><input type="text" data-property-name="accsInfo" name="accsQuantityUnit">
                                    <a href="#" data-property-name="chooseAccs" style="padding-left: 10px;padding-right: 20px;border-left: 1px solid black">选择</a>
                                    <div data-acc-info="itemsInfo" style="display: none"></div>
                                </td>
                                <input type="hidden" data-property-name="productId" name="details[][product[id]]:number">
                                <input type="hidden" name="details[][product[price]]:number">
                                <input type="hidden" name="details[][priceChange[id]]:number" data-skip-falsy="true">
                                <input type="hidden" name="details[][priceChange[product[id]]]:number" data-skip-falsy="true">
                                <input type="hidden" name="details[][priceChange[product[no]]]:string" data-skip-falsy="true">
                                <input type="hidden" name="details[][amount]:number">
                                <input type="hidden" name="details[][express[id]]:number">
                            </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-0" style="margin-top: 10px">
                                    <button id="addItem" type="button" class="btn btn-success">添加订购条目</button>
                                </div>
                            </div>

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

    assistBook.init("productList", 15, "<%=request.getContextPath()%>");
    $('#addItem').click(function(){assistBook.addRow()});

    $("#send").bind("click", function(){
        assistBook.saveOrder('<%=request.getContextPath()%>/orderManagement/assistBook');
    });

    <c:choose><c:when test="${entity != null}">document.title = "代下单";</c:when><c:otherwise> document.title = "代下单";</c:otherwise></c:choose>
</script>