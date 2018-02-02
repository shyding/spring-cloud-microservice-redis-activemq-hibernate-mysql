<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.StockInOut" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page import="com.hzg.erp.Warehouse" %>
<%@ page import="com.hzg.erp.StockInOutDetail" %>
<%@ page import="com.hzg.erp.StockInOutDetailProduct" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main" id="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3><c:if test="${entity != null && entity.type < 10}">入库单</c:if>
                    <c:if test="${entity != null && entity.type >= 10}">出库单</c:if>
                    <c:if test="${entity == null}">入库单/出库单填写</c:if></h3>
            </div>

            <div class="title_right" id="search">
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
                        <h2>库存 <small id="smallTitle">入库单</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <form class="form-horizontal form-label-left" novalidate id="form">
                    <div class="x_content">
                        <span id="sectionTitle" class="section">入库信息</span>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="no">单号 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="no" name="no" value="<c:choose><c:when test="${entity != null}">${entity.no}</c:when><c:otherwise>${no}</c:otherwise></c:choose>" data-validate-length-range="5,30" data-validate-words="1" class="form-control col-md-7 col-xs-12" readonly required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">类型 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <select id="type" name="type" class="form-control col-md-7 col-xs-12" required>
                                    <option value="">请选择入库/出库类型</option>
                                    <option value="<%=ErpConstant.stockInOut_type_cash%>">现金入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_consignment%>">代销入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_increment%>">增量入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_process%>">加工入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_deposit%>">押金入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_repair%>">修补入库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_changeWarehouse%>">调仓入库</option>
                                    <c:if test="${entity != null && entity.type == 7}">
                                        <option value="<%=ErpConstant.stockInOut_type_returnProduct%>">系统自动出库</option>
                                    </c:if>
                                    <option value="<%=ErpConstant.stockInOut_type_virtual_outWarehouse%>">虚拟出库</option>
                                    <c:if test="${entity != null && entity.type == 11}">
                                        <option value="<%=ErpConstant.stockInOut_type_normal_outWarehouse%>">系统自动出库</option>
                                    </c:if>
                                    <option value="<%=ErpConstant.stockInOut_type_normal_outWarehouse_manual%>">正常出库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_breakage_outWarehouse%>">报损出库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_changeWarehouse_outWarehouse%>">调仓出库</option>
                                    <option value="<%=ErpConstant.stockInOut_type_innerBuy_outWarehouse%>">内购出库</option>

                                </select>
                            </div>
                        </div>

                        <div id="deposit">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">预计退还货品时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" id="returnGoodsDate" name="deposit[returnGoodsDate]" class="form-control" value="${entity.deposit.returnGoodsDate}">
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">预计退还押金时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" id="returnDepositDate" name="deposit[returnDepositDate]" class="form-control" value="${entity.deposit.returnDepositDate}">
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" id="amount" name="deposit[amount]" value="${entity.deposit.amount}"/>
                            <input type="hidden" id="purchaseId" name="deposit[purchase[id]]" value="${entity.deposit.purchase.id}"/>
                            <input type="hidden" id="purchaseNo" name="deposit[purchase[no]]" value="${entity.deposit.purchase.no}"/>
                            <input type="hidden" id="depositId" name="deposit[id]" value="${entity.deposit.id}" data-value-type="number" data-skip-falsy="true" />
                        </div>

                        <div id="processRepair">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">加工类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="processRepairType" name="processRepair[type]" class="form-control col-md-7 col-xs-12" data-value-type="number" data-skip-falsy="true" required>
                                        <option value="">请选择加工类型</option>
                                        <option value="0">自己加工</option>
                                        <option value="1">第三方加工</option>
                                        <option value="10">修补</option>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" id="processRepairExpenseLabel" for="describes">加工费 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="expense" type="number" name="processRepair[expense]" value="${entity.processRepair.expense}" class="form-control col-md-7 col-xs-12">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" id="processRepairDateLabel" for="date">加工时间 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" id="processRepairDate" name="processRepair[date]" class="form-control" value="${entity.processRepair.date}">
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group" id="saleExpenseDiv">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">销售时标注的加工费 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="number" id="saleExpense" name="processRepair[saleExpense]" value="${entity.processRepair.saleExpense}" class="form-control col-md-7 col-xs-12">
                                </div>
                            </div>
                            <input type="hidden" id="processRepairId" name="processRepair[id]" value="${entity.processRepair.id}" data-value-type="number" data-skip-falsy="true" />
                        </div>

                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">备注 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <textarea id="describes" name="describes" class="form-control col-md-7 col-xs-12" data-validate-length-range="6,256" data-validate-words="1"required>${entity.describes}</textarea>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label id="labelDate" class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">入库时间 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <div class="input-prepend input-group" style="margin-bottom:0">
                                    <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                    <input type="text" name="date" id="date" class="form-control" value="${entity.date}">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" id="warehouseTitle">入库仓库<span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text2" name="text2" value="${entity.warehouse.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择仓库" required />
                                <input type="hidden" id="warehouse[id]" name="warehouse[id]" value="${entity.warehouse.id}">
                            </div>
                        </div>
                        <div id="changeWarehouse" class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">调仓出库目的仓库<span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text3" name="text3" value="${entity.changeWarehouse.targetWarehouse.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择仓库" required />
                                <input type="hidden" id="changeWarehouse[targetWarehouse[id]]" name="changeWarehouse[targetWarehouse[id]]" value="${entity.changeWarehouse.targetWarehouse.id}">
                            </div>
                        </div>
                        <c:if test="${entity != null && entity.type == 13}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">调仓出库入库状态 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                   <input type="text" style="width:40%" class="form-control col-md-7 col-xs-12" value="${entity.changeWarehouse.stateName}" />
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${entity == null || entity.state == 0}">
                        <div class="item form-group">
                            <label id="labelNo" class="control-label col-md-3 col-sm-3 col-xs-12">采购单编号、商品编号<span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text1" name="text1" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择编号添加商品条目" />
                            </div>
                        </div>
                        </c:if>

                        <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise><%=ErpConstant.stockInOut_state_apply%></c:otherwise></c:choose>">
                        <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                        <input type="hidden" id="inputer[id]" name="inputer[id]" value="${userId}">
                    </div>

                    <div class="x_content" style="overflow: auto;margin-top: 30px">
                        <table id="stockInOutProductList" class="table-sheet" width="100%">
                            <thead><tr><th>商品名称</th><th>编号</th><th>状态</th><th>种类</th><th>数量</th><th>计量单位</th><th>采购价</th><th>图片</th></tr></thead>
                            <tbody id="tbody">
                            <c:forEach items="${entity.details}" var="detail">
                                <tr id="tr${detail.product.no}">
                                    <td>${detail.product.name}
                                        <%
                                            StockInOutDetail detail = (StockInOutDetail)pageContext.getAttribute("detail");
                                            String detailProducts = "[";
                                            Object[] detailProductsArr = detail.getStockInOutDetailProducts().toArray();

                                            for (int i = 0; i < detailProductsArr.length; i++) {
                                                detailProducts += "{\"product\":{\"id\":" + ((StockInOutDetailProduct)detailProductsArr[i]).getProduct().getId() + "}}";
                                                if (i < detailProductsArr.length-1) {
                                                    detailProducts += ",";
                                                }
                                            }

                                            detailProducts += "]";
                                         %>
                                        <input type='hidden' name='details[][stockInOutDetailProducts]:array' value='<%=detailProducts%>'>
                                    </td>
                                    <td><input type="text" name='details[][productNo]:string' value='${detail.productNo}' readonly></td>
                                    <td>${detail.product.stateName}</td>
                                    <td>${detail.product.type.name}</td>
                                    <td><input type="text" name='details[][quantity]:number' value='${detail.quantity}'></td>
                                    <td><input type="text" name="details[][unit]:string" value="${detail.unit}"></td>
                                    <td>${detail.product.unitPrice}</td>
                                    <td><a id="${detail.product.no}" href="<%=FileServerInfo.imageServerUrl%>/${detail.product.describe.imageParentDirPath}/snapshoot.jpg" class="lightbox">查看图片</a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    </form>

                    <div class="x_content" id="tail">
                        <div class="form-horizontal form-label-left">
                            <div id="delDiv" class="form-group">
                                <div class="col-md-6 col-md-offset-0" style="margin-top: 10px">
                                    <button id="delItem" type="button" class="btn btn-success">减少条目</button>
                                </div>
                            </div>

                            <div class="ln_solid"></div>
                            <div class="form-group" id="submitDiv">
                                <div class="col-md-6 col-md-offset-8">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                        <button id="doBusiness" type="button" class="btn btn-success">入库</button>
                                        <button id="doBusinessOut" type="button" class="btn btn-success" style="display:none">出库</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:if test="${entity.state == 0}">
                                            <button id="send" type="button" class="btn btn-success">更新</button>
                                            <button id="doBusiness" type="button" class="btn btn-success">入库</button>
                                            <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                            <button id="delete" type="button" class="btn btn-danger">作废</button>
                                        </c:if>
                                        <c:if test="${entity.state == 2}">
                                            <button id="editState" type="button" class="btn btn-primary">编辑</button>
                                            <button id="recover" type="button" class="btn btn-success">恢复</button>
                                        </c:if>
                                    </c:if>
                                    <button class="btn btn-default" id="printBarcodeBtn"><i class="fa fa-print"></i> 打印商品条码</button>
                                    <button class="btn btn-default" id="printStockOutBtn"><i class="fa fa-print"></i> 打印出库单</button>
                                    <button class="btn btn-default" id="printExpressBtn"><i class="fa fa-print"></i> 打印快递单</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="right_col" role="dialog">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel" style="border: 0px">
                <div class="x_content">
                    <div id="expressReceiverInfoDiv">
                        <form id="expressReceiverInfoForm">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="receiver">收件人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiver" name="receiver" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">发送时间 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="expressDate" name="date" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverMobile">手机 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverMobile" name="receiverMobile" class="form-control col-md-7 col-xs-12" data-validate-length-range="11,20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverTel">电话 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverTel" name="receiverTel" class="form-control col-md-7 col-xs-12" data-validate-length-range="7,20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverAddress">地址 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverAddress" name="receiverAddress" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,60" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverCity">城市 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverCity" name="receiverCity" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,16" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverProvince">省份 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverProvince" name="receiverProvince" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,16" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverCountry">国家 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverCountry" name="receiverCountry" value="中国" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,16" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverPostCode">邮编 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverPostCode" name="receiverPostCode" class="form-control col-md-7 col-xs-12" data-validate-length-range="4,7" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="receiverCompany">公司 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="receiverCompany" name="receiverCompany" class="form-control col-md-7 col-xs-12" data-validate-length-range="1,30" data-validate-words="1" required type="text">
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<form id="actionForm" style="display: none">
    <input type="hidden" id="entityId" name="entityId:number" value="${entity.id}">
    <input type="hidden" name="sessionId" value="${sessionId}">
    <input type="hidden" id="expressReceiverInfo" name="expressReceiverInfo">
    <input type="hidden" id="printContent" name="printContent">
    <input type="hidden" id="random" name="random">
</form>
<form id="printForm" method="post" style="display: none" target="_blank">
    <input type="hidden" id="jsonn" name="json">
</form>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $("#edit").unbind("click").click(function(){
        editable = true;
        $('#form :input').attr("readonly",false).css("border", "1px solid #ccc");
        $('#stockInOutProductList :input').css("border", "1px");
        $('#send, #delete, #doBusiness, #recover').attr("disabled", false);
        $("#delDiv").show();
    });

    $("#editState").unbind("click").click(function(){
        $('#delete, #recover').attr("disabled", false);
        $("#editState").attr("disabled", "disabled");
    });

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

    $('#returnGoodsDate').daterangepicker({
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

    $('#returnDepositDate').daterangepicker({
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

    $('#processRepairDate').daterangepicker({
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

    $("#deposit").hide();
    $("#processRepair").hide();
    $("#changeWarehouse").hide();

    setDisabled($("#deposit").find(":input"), true);
    setDisabled($("#processRepair").find(":input"), true);
    setDisabled($("#changeWarehouse").find(":input"), true);

    $("#printBarcodeBtn").hide();
    $("#printStockOutBtn").hide();
    $("#printExpressBtn").hide();

    <c:if test="${entity != null}">
        setSelect(document.getElementById("type"), "${entity.type}");

        if (parseInt("${entity.type}") < <%=ErpConstant.stockInOut_type_virtual_outWarehouse%>) {
            $("#smallTitle").html("入库单");
            $("#sectionTitle").html("入库信息");
            $("#labelDate").html("入库时间");
            $("#warehouseTitle").html("入库仓库");
            $("#labelNo").html("采购单编号、商品编号");

            $("#send").show();
            $("#doBusiness").show();
            $("#doBusinessOut").hide();

        } else {
            $("#smallTitle").html("出库单");
            $("#sectionTitle").html("出库信息");
            $("#labelDate").html("出库时间");
            $("#warehouseTitle").html("出库仓库");
            $("#labelNo").html("商品编号");

            $("#send").hide();
            $("#doBusiness").hide();
            $("#doBusinessOut").show();
        }

        <c:if test="${entity.deposit != null}">
            $("#deposit").show();
            setDisabled($("#deposit").find(":input"), false);
        </c:if>

        <c:if test="${entity.processRepair != null}">
            $("#processRepair").show();
            setDisabled($("#processRepair").find(":input"), false);

            setSelect(document.getElementById("processRepairType"), "${entity.processRepair.type}");
        </c:if>

        <c:if test="${entity.changeWarehouse != null}">
            $("#changeWarehouse").show();
            setDisabled($("#changeWarehouse").find(":input"), false);
        </c:if>

        <c:if test="${entity.details != null}">
            $(".lightbox").lightbox({
                fitToScreen: true,
                imageClickClose: false
            });
        </c:if>

        $("#delDiv").hide();

        <c:if test="${entity.state == 1}">
            <c:if test="${entity.type < 10}">
                $("#printBarcodeBtn").show();
            </c:if>

            <c:if test="${entity.type >= 10}">
                $("#printStockOutBtn").show();
            </c:if>

        /**
         * 自动正常出库和人工正常出库，都显示打印快递单按钮
          */
        <c:if test="${entity.type == 11 || entity.type == 15}">
                $("#printExpressBtn").show();
            </c:if>

        </c:if>
    </c:if>

    $("#type").change(function(){
        $("#deposit").hide();
        $("#processRepair").hide();
        $("#changeWarehouse").hide();

        setDisabled($("#deposit").find(":input"), true);
        setDisabled($("#processRepair").find(":input"), true);
        setDisabled($("#changeWarehouse").find(":input"), true);

        $("#text2").attr("readonly", false);

       if (this.value == <%=ErpConstant.stockInOut_type_deposit%>) {
           $("#deposit").show();
           setDisabled($("#deposit").find(":input"), false);

       } else if (this.value == <%=ErpConstant.stockInOut_type_process%> ||
           this.value == <%=ErpConstant.stockInOut_type_repair%>) {
           $("#processRepair").show();
           setDisabled($("#processRepair").find(":input"), false);

       } else if (this.value == <%=ErpConstant.stockInOut_type_changeWarehouse%>) {
           $("#text2").attr("readonly", true);

       } else if (this.value == <%=ErpConstant.stockInOut_type_changeWarehouse_outWarehouse%>) {
           $("#changeWarehouse").show();
           setDisabled($("#changeWarehouse").find(":input"), false);
       }

       if (this.value < <%=ErpConstant.stockInOut_type_virtual_outWarehouse%>) {
           $("#smallTitle").html("入库单");
           $("#sectionTitle").html("入库信息");
           $("#labelDate").html("入库时间");
           $("#warehouseTitle").html("入库仓库");
           $("#labelNo").html("采购单编号、商品编号");

           $("#text2").val("");
           document.getElementById("warehouse[id]").value = "";

           $("#send").show();
           $("#doBusiness").show();
           $("#doBusinessOut").hide();

       } else {
           $("#smallTitle").html("出库单");
           $("#sectionTitle").html("出库信息");
           $("#labelDate").html("出库时间");
           $("#warehouseTitle").html("出库仓库");
           $("#labelNo").html("商品编号");

           $("#text2").val("");
           document.getElementById("warehouse[id]").value = "";

           $("#send").hide();
           $("#doBusiness").hide();
           $("#doBusinessOut").show();
       }

        $("#tbody").empty();
    });

    $("#processRepairType").change(function(){
        if (this.value == <%=ErpConstant.stockInOut_type_process%>) {
            $("#processRepairExpenseLabel").html("加工费");
            $("#processRepairDateLabel").html("加工时间");
            $("#saleExpenseDiv").show();
        } else if (this.value == <%=ErpConstant.stockInOut_type_repair%>) {
            $("#processRepairExpenseLabel").html("修补费");
            $("#processRepairDateLabel").html("修补时间");
            $("#saleExpenseDiv").hide();
        }
    });

    function setDisabled(inputs, enable){
        for (var i = 0; i < inputs.length; i++) {
            $(inputs[i]).attr("disabled", enable);
        }
    }

    $("#text1").focus(function(){
        if ($("#type").val() >= <%=ErpConstant.stockInOut_type_virtual_outWarehouse%> && $.trim(document.getElementById("warehouse[id]").value) == "") {
            alert("请选择出库出库");
            $("#text2").focus();
            return false;
        }
    });

    $("#text1").coolautosuggestm({
        url:"<%=request.getContextPath()%>/erp/entitiesSuggest/" + encodeURIComponent('purchase#purchaseDetail') + "/" +  encodeURIComponent('purchase#product'),
        marginTop: "margin-top:34px",
        width: 312,
        showProperty: "no#productNo",

        getQueryData: function(paramName){
            var queryJson = {};
            queryJson["purchase"] = {};
            queryJson["product"] = {};

            var suggestWord = $.trim(this.value);
            if (suggestWord != "") {
                queryJson["purchase"]["no"] = suggestWord;
                queryJson["product"]["no"] = suggestWord;
            }

            if ($("#type").val() < <%=ErpConstant.stockInOut_type_virtual_outWarehouse%>) {
                queryJson["purchase"]["state"] = <%=ErpConstant.purchase_state_close%>;
                queryJson["product"]["state"] = " in (" + <%=ErpConstant.product_state_purchase_close%> + "," + <%=ErpConstant.product_state_stockOut%> +
                        "," + <%=ErpConstant.product_state_stockOut_part%> + "," + <%=ErpConstant.product_state_returnedProduct%> +
                        "," + <%=ErpConstant.product_state_returnedProduct_part%> + ")";
            } else {
                queryJson["purchase"]["state"] = -1;      //不能出库采购单里的商品，由于出库单没有 -1 状态，所以设置为 -1，使得查询不到采购单里的商品
                queryJson["product"]["state"] = " in (" + <%=ErpConstant.product_state_stockIn%> + "," + <%=ErpConstant.product_state_stockIn_part%> +
                        "," + <%=ErpConstant.product_state_sold%> + "," + <%=ErpConstant.product_state_sold_part%> + ")";
                queryJson["product"]["stockInOut"] = '{"warehouse":{"id":' + parseInt(document.getElementById("warehouse[id]").value) + '}}';
            }

            return queryJson;
        },

        onSelected:function(result){
            if(result!=null){
                var tbody = $("#tbody");

                /**
                 * item is purchase
                 */
                if (result.details != undefined) {
                    for (var i = 0; i < result.details.length; i++) {
                        addItem(tbody, result.details[i], "products");
                    }

                    $("#purchaseId").val(result.id);
                    $("#purchaseNo").val(result.no);
                    $("#amount").val(result.amount);
                }

                /**
                 * item is purchaseDetail
                 */
                if (result.price != undefined) {
                    addItem(tbody, result, "product");

                    $("#purchaseId").val("");
                    $("#purchaseNo").val("");
                    $("#amount").val("");
                }
            }
        }
    });

    function addItem(tbody, item, productFlag) {
        if (document.getElementById("tr" + item["product"]["no"] + item["product"]["id"]) == null) {
            if ($("#type").val() != <%=ErpConstant.stockInOut_type_changeWarehouse%>) {
                setStockInOut(tbody, item, productFlag);

            } else {
                /**
                 * 调仓入库，自动设置入库仓库
                 */
                $("#type").ajaxPost("<%=request.getContextPath()%>/erp/privateQuery/<%=StockInOut.class.getSimpleName()%>",
                    '{"product":{"id":' + item["product"]["id"] + '},"type":"<%=ErpConstant.stockOut%>"}',
                    function(stockOut){
                        if (stockOut.type == <%=ErpConstant.stockInOut_type_changeWarehouse_outWarehouse%>) {

                            $("#text2").val(stockOut["changeWarehouse"]["targetWarehouse"]["name"]);
                            document.getElementById("warehouse[id]").value = stockOut["changeWarehouse"]["targetWarehouse"]["id"];

                            setStockInOut(tbody, item, productFlag);
                        }
                    }
                );
            }
        }
    }

    function setStockInOut(tbody, item, productFlag){
        var detailProducts = "[";

        if (productFlag == "product") {
            var setProducts = document.getElementsByName("details[][stockInOutDetailProducts]:array");
            var isCurrentProductSet = false;
            for (var i = 0; i < item["purchaseDetailProducts"].length; i++) {

                for (var j = 0; j < setProducts.length; j++) {
                    if (setProducts[j].value.indexOf('{"id":' + item["purchaseDetailProducts"][i]["product"]["id"] + "}") != -1) {
                        isCurrentProductSet = true;
                        break;
                    }
                }

                if (!isCurrentProductSet) {
                    detailProducts += '{"product":{"id":' + item["purchaseDetailProducts"][i]["product"]["id"] + "}}";
                    break;
                }

                isCurrentProductSet = false;
            }

        } else if (productFlag == "products") {
            for (var i = 0; i < item["purchaseDetailProducts"].length; i++) {
                detailProducts += '{"product":{"id":' + item["purchaseDetailProducts"][i]["product"]["id"] + "}}";
                if (i < item["purchaseDetailProducts"].length-1) {
                    detailProducts += ",";
                }
            }
        }


        detailProducts += "]";

        var quantity = item.quantity;
        if (productFlag == "product") {
            quantity = 1;
        }

        tbody.append("<tr id='tr" + item["product"]["no"] + item["product"]["id"] + "'>" +
            "<td>" + item.productName + "<input type='hidden' name='details[][stockInOutDetailProducts]:array' value='" + detailProducts + "'>" + "</td>" +
            "<td><input type='text' name='details[][productNo]:string' value='" + item["product"]["no"] + "' readonly></td>" +
            "<td>" + dataList.entityStateNames["product"]["state"][item["product"]["state"]] + "</td>" +
            "<td>" + item["product"]["type"]["name"] + "</td>" +
            "<td><input type='text' name='details[][quantity]:number' value='" + quantity + "'></td>" +
            "<td><input type='text' name='details[][unit]:string' value='" + item.unit + "'></td>" +
            "<td>" + item.price + "</td><td>" +
            "<a id='" + item["product"]["no"] + item["product"]["id"] + "' href='<%=FileServerInfo.imageServerUrl%>/" + item["product"]["describe"]["imageParentDirPath"] + "/snapshoot.jpg'>图片</a></td></tr>");

        $(document.getElementById(item["product"]["no"] + item["product"]["id"])).lightbox({
            fitToScreen: true,
            imageClickClose: false
        });
    }


    $("#text2").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/<%=Warehouse.class.getSimpleName().toLowerCase()%>/name/",
        marginTop: "margin-top:4px",
        width: 312,
        showProperty: "name",
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("warehouse[id]")).val(result.id);

                var stockWarehouses = document.getElementsByName("stocks[][warehouse[id]]:number");
                for (var i = 0; i < stockWarehouses.length; i++) {
                    stockWarehouses[i].value = result.id;
                }
            }
        }
    });

    $("#text3").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/<%=Warehouse.class.getSimpleName().toLowerCase()%>/name/",
        marginTop: "margin-top:4px",
        width: 312,
        showProperty: "name",
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("changeWarehouse[targetWarehouse[id]]")).val(result.id);
            }
        }
    });

    $("#expressReceiverInfoDiv").dialog({
        title: "请输入收件人及其他信息",
        autoOpen: false,
        width: 370,
        height:640,
        buttons: {
            "确定": function () {
                var expressReceiverInfoForm = $("#expressReceiverInfoForm");
                if (!validator.checkAll(expressReceiverInfoForm)) {
                    return false;
                }

                var receiverCity = $.trim($("#receiverCity").val()), receiverProvince = $.trim($("#receiverProvince").val());
                if (receiverCity.substring(receiverCity.length-1) != "市") {
                    $("#receiverCity").val(receiverCity + "市");
                }

                if (receiverProvince == "北京" || receiverProvince == "上海" || receiverProvince == "天津" || receiverProvince == "重庆") {
                    $("#receiverProvince").val(receiverProvince + "市")
                } else {
                    if (receiverProvince != "北京市" && receiverProvince != "上海市" && receiverProvince != "天津市" && receiverProvince != "重庆市") {
                        if (receiverProvince.substring(receiverProvince.length-1) != "省") {
                            $("#receiverProvince").val(receiverProvince + "省");
                        }
                    }
                }

                $("#expressReceiverInfo").val(JSON.stringify(expressReceiverInfoForm.serializeJSON()));
                /**
                 * 设置随机数使得 actionForm 可以随机提交
                 */
                $("#random").val(Math.random());

                /**
                 * 人工正常出库打印顺丰快递单，先生成顺丰快递单，再下载快递单图片
                 */
                var json = JSON.stringify($("#actionForm").serializeJSON());
                var isSuccess = false;
                $("#printForm").ajaxPost("<%=request.getContextPath()%>/erp/doBusiness/<%=ErpConstant.product_action_name_generateSfExpressOrderByReceiverAndStockOut%>", json,
                function(result){
                    if (result.result.indexOf("success") != -1) {
                        isSuccess = true;
                    }
                });

                var printExpressWaybillTimeout = setTimeout(function(){
                    if (isSuccess) {
                        $("#expressReceiverInfoDiv").dialog("close");

                        $("#jsonn").val(json);
                        $("#printForm").attr("action", "<%=request.getContextPath()%><%=ErpConstant.privilege_resource_uri_print_expressWaybill%>").submit();

                        clearTimeout(printExpressWaybillTimeout);
                    }
                }, 5000);
            },

            "取消": function () {
                $(this).dialog("close");
            }
        }
    });

    $('#expressDate').daterangepicker({
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

    $("#delItem").click(function(){
        var lastTr = $("#stockInOutProductList tbody tr:last-child");

        if (lastTr.html().indexOf('<td>') != -1) {
            var productNo = lastTr.attr("id").substring(2);

            document.getElementById("tbody").removeChild(document.getElementById("tr"+productNo));
            document.getElementById("form").removeChild(document.getElementById("div"+productNo));
        }
    });

    $("#delete").click(function(){
        if (confirm("确定作废该入库单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/delete/<%=StockInOut.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":<%=ErpConstant.stockInOut_state_cancel%>}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该入库单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/recover/<%=StockInOut.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":<%=ErpConstant.stockInOut_state_apply%>}');
        }
    });

    $("#printBarcodeBtn").click(function(){
        $("#jsonn").val(JSON.stringify($("#actionForm").serializeJSON()));
        $("#printForm").attr("action", "<%=request.getContextPath()%>/erp/print/<%=ErpConstant.stockInOut_action_name_print_barcode%>").submit();
    });

    $("#printStockOutBtn").click(function(){
        $('[type=text], textarea').each(function(){ this.defaultValue = this.value; });
        $('[type=checkbox], [type=radio]').each(function(){ this.defaultChecked = this.checked; });
        $('select option').each(function(){ this.defaultSelected = this.selected; });

        $("#printContent").val($("#main").html()
            .replace('id="tail"', 'style="display:none"')
            .replace('id="search"', 'style="display:none"')
            .replace(/\r/ig,"").replace(/\n/ig,""));

        $("#jsonn").val(JSON.stringify($("#actionForm").serializeJSON()));
        $("#printForm").attr("action", "<%=request.getContextPath()%>/erp/print/<%=ErpConstant.stockInOut_action_name_print_stockOutBills%>").submit();
    });

    $("#printExpressBtn").click(function(){
        $("#jsonn").val(JSON.stringify($("#actionForm").serializeJSON()));

        if ($("#type").val() == "<%=ErpConstant.stockInOut_type_normal_outWarehouse_manual%>") {
            $('#expressReceiverInfoDiv').dialog('open');
        } else if($("#type").val() == "<%=ErpConstant.stockInOut_type_normal_outWarehouse%>") {
            $("#printForm").attr("action", "<%=request.getContextPath()%><%=ErpConstant.privilege_resource_uri_print_expressWaybill%>").submit();
        }
    });

    $("#send").bind("click", function(){
        saveOrUpdateStockInOut();
    });

    $("#doBusiness").click(function(){
        if (confirm("入库后，入库单信息将不再可以编辑，确定入库吗？")) {
            if ($.trim($("#entityId").val()) == "") {
                saveOrUpdateStockInOut(function(){stockIn();}, false);
            } else {
                stockIn(true);
            }
        }
    });

    $("#doBusinessOut").click(function(){
        if (confirm("确定出库吗？")) {
            saveOrUpdateStockInOut(function(){stockOut();}, false);
        }
    });

    function saveOrUpdateStockInOut(callBack, isShowResult) {
        if(checkStockIn()){
            $("#form").submitForm('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=StockInOut.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+StockInOut.class.getSimpleName().substring(1)%>',
                function(result) {
                    if (result.result.indexOf("success") != -1) {
                        $("#entityId").val(result.id);
                        console.log($("#entityId").val());

                        if (callBack != undefined) {
                            callBack();
                        }
                    }
                }
            , isShowResult);
        }
    }

    function stockIn() {
        $('#actionForm').submitForm('<%=request.getContextPath()%>/erp/doBusiness/<%=ErpConstant.stockInOut_action_name_inProduct%>', function(result){
            if (result.result.indexOf("success") != -1 && parseInt(result.state) == <%=ErpConstant.stockInOut_state_finished%>) {
                if (parseInt(result.type) < <%=ErpConstant.stockInOut_type_virtual_outWarehouse%>) {
                    $("#printBarcodeBtn").show();
                }
            }
        });
    }

    function stockOut() {
        $('#actionForm').submitForm('<%=request.getContextPath()%>/erp/doBusiness/<%=ErpConstant.stockInOut_action_name_outProduct%>', function(result){
            if (result.result.indexOf("success") != -1 && parseInt(result.state) == <%=ErpConstant.stockInOut_state_finished%>) {
                if (parseInt(result.type) >= <%=ErpConstant.stockInOut_type_virtual_outWarehouse%>) {
                    $("#printStockOutBtn").show();

                    if (parseInt(result.type) == <%=ErpConstant.stockInOut_type_normal_outWarehouse_manual%>) {
                        $("#printExpressBtn").show();
                    }
                }
            }
        });
    }

    function checkStockIn() {
        if ($("#tbody").find("td").length == 0) {
            alert("请选择入库的商品");
            return false;
        }

        if (parseInt($("#type").val()) == <%=ErpConstant.stockInOut_type_deposit%>) {
            if (($("#purchaseNo").val() != "" && $("#purchaseNo").val().indexOf("<%=ErpConstant.no_purchase_perfix%>") != 0) || $("#purchaseNo").val() == "") {
                alert("押金入库只可以对采购单做入库");
                return false;
            }
        }
        if (parseInt($("#type").val()) == <%=ErpConstant.stockInOut_type_repair%>) {
            var stockInOutDetailProducts = document.getElementsByName("details[][stockInOutDetailProducts]:array");
            if (stockInOutDetailProducts.length > 1) {
                alert("修补入库是对单件商品做入库");
                return false;

            } else if (stockInOutDetailProducts.length == 1) {
                if (stockInOutDetailProducts[0].value.indexOf(",") != -1) {
                    alert("修补入库是对单件商品做入库");
                    return false;
                }
            }
        }

        var date = $("#date").val();
        var stockDates = document.getElementsByName("stocks[][date]");
        for (var i = 0; i < stockDates.length; i++) {
            stockDates[i].value = date;
        }

        return true;
    }

    document.title = <c:if test="${entity != null && entity.type < 10}">"入库单"</c:if><c:if test="${entity != null && entity.type >= 10}">"出库单"</c:if><c:if test="${entity == null}">"入库单/出库单填写"</c:if>;
</script>