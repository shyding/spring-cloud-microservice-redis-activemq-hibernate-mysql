<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.hzg.erp.Purchase" %>
<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.PurchaseDetail" %>
<%@ page import="com.hzg.erp.ProductType" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hzg.pay.Account" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page import="com.hzg.tools.PayConstants" %>
<%@ page import="com.hzg.pay.Pay" %>
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
                        <span class="section">采购单信息</span>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="no">采购单号 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="no" name="no" value="<c:choose><c:when test="${entity != null}">${entity.no}</c:when><c:otherwise>${no}</c:otherwise></c:choose>" data-validate-length-range="5,30" data-validate-words="1" class="form-control col-md-7 col-xs-12" readonly required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">采购标题 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="name" name="name" value="${entity.name}" data-validate-length-range="5,30" data-validate-words="1" class="form-control col-md-7 col-xs-12" required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">采购类型 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <select id="type" name="type:number" class="form-control col-md-7 col-xs-12" required>
                        <%
                            Purchase purchase = (Purchase) request.getAttribute("entity");
                            if (purchase == null) {
                        %>
                                    <option value="">请选择类型</option>
                                    <option value="<%=ErpConstant.purchase_type_normal%>">正常采购</option>
                                    <option value="<%=ErpConstant.purchase_type_temp%>">临时采购</option>
                                    <option value="<%=ErpConstant.purchase_type_emergency%>">应急采购</option>
                                    <option value="<%=ErpConstant.purchase_type_cash%>">现金采购</option>
                                    <option value="<%=ErpConstant.purchase_type_deposit%>">押金采购</option>
                        <%
                            } else {
                                String purchaseTypeOptions =
                                        ("<option value='" + ErpConstant.purchase_type_normal + "'>正常采购</option>" +
                                                "<option value='" + ErpConstant.purchase_type_temp + "'>临时采购</option>" +
                                                "<option value='" + ErpConstant.purchase_type_emergency + "'>应急采购</option>" +
                                                "<option value='" + ErpConstant.purchase_type_cash + "'>现金采购</option>" +
                                                "<option value='" + ErpConstant.purchase_type_deposit + "'>押金采购</option>")
                                                .replace("'" + purchase.getType() + "'", "'" + purchase.getType() + "' selected");
                        %>
                            <%=purchaseTypeOptions%>
                        <%
                            }
                        %>
                                </select>
                            </div>
                        </div>
                        <%
                            String totalPaymentSelected = "", depositSelected = "";
                            if (purchase != null && purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0) {
                                totalPaymentSelected = "selected";

                                if (purchase.getPurchaseBookPaid() != null && purchase.getPurchaseBookPaid()) {
                                    totalPaymentSelected = "";
                                    depositSelected = "selected";
                                }
                            }
                        %>
                            <div id="temporaryPurchasePayKindDiv" <c:if test="${entity.type != 1}">style="display: none"</c:if>>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="temporaryPurchasePayKind">付款类型</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="temporaryPurchasePayKind" name="temporaryPurchasePayKind" class="form-control col-md-7 col-xs-12">
                                        <option value="<%=ErpConstant.purchase_type_temp_payKind_totalPayment%>" <%=totalPaymentSelected%>>全款</option>
                                        <option value="<%=ErpConstant.purchase_type_temp_payKind_deposit%>" <%=depositSelected%>>订金</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div id="purchaseBookDiv"  <c:if test="${entity.purchaseBookPaid == null}">style="display: none"</c:if>>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="purchaseBookDeposit">订金</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="number" id="purchaseBookDeposit" name="purchaseBook[deposit]:number" value="${entity.purchaseBook.deposit}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入预定订金" data-skip-falsy="true">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="purchaseBookPayDate">订金付款时间</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="purchaseBookPayDate" name="purchaseBook[payDate]:string" value="${entity.purchaseBook.payDate}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入订金付款时间" data-skip-falsy="true">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">采购时间 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <div class="input-prepend input-group" style="margin-bottom:0">
                                    <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                    <input type="text" name="date" id="date" class="form-control" style="width:37%" value="${entity.date}">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">采购人</label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text1" name="text1" value="${entity.charger.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                <input type="hidden" id="charger[id]" name="charger[id]" value="${entity.charger.id}">
                            </div>
                        </div>

                        <c:set var="supplierId" />
                        <c:set var="supplierName" />
                        <c:set var="supplierAccount" />
                        <c:set var="supplierBranch" />
                        <c:set var="supplierBank" />
                        <c:forEach items="${entity.details}" var="detail" end="0">
                            <c:set var="supplierId" value="${detail.product.supplier.id}" />
                            <c:set var="supplierName" value="${detail.product.supplier.name}" />
                            <c:set var="supplierAccount" value="${detail.product.supplier.account}" />
                            <c:set var="supplierBranch" value="${detail.product.supplier.branch}" />
                            <c:set var="supplierBank" value="${detail.product.supplier.bank}" />
                        </c:forEach>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text2">供应商</label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text2" name="text2" value="${supplierName}" placeholder="供应商" class="form-control col-md-7 col-xs-12" style="width:40%" required />
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" id="amountLabel" for="amount">采购金额 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="amount" name="amount:number" value="${entity.amount}" style="width:40%" class="form-control col-md-7 col-xs-12" required type="number">
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">支付明细 <span class="required">*</span></label>
                            <div class="col-md-9 col-sm-6 col-xs-12">
                                <div style="padding-top:8px;padding-bottom:8px">支付方式&nbsp;/&nbsp;支付金额&nbsp;/&nbsp;支付账号&nbsp;/&nbsp;收款账号<c:if test="${entity.pays != null}">&nbsp;/&nbsp;状态</c:if></div>
                                <table id="payList">
                                    <tbody>
                                    <c:if test="${entity.pays == null}">
                                        <tr>
                                            <td>
                                                <select name="pays[][payType]:number" class="form-control col-md-7 col-xs-12" style="width:140px" required>
                                                    <option value="">请选择支付方式</option>
                                                    <option value="<%=PayConstants.pay_type_transfer_accounts_alipay%>">支付宝转账</option>
                                                    <option value="<%=PayConstants.pay_type_transfer_accounts_weixin%>">微信转账</option>
                                                    <option value="<%=PayConstants.pay_type_transfer_accounts%>">转账</option>
                                                    <option value="<%=PayConstants.pay_type_remit%>">汇款</option>
                                                    <option value="<%=PayConstants.pay_type_other%>">其他</option>
                                                </select>
                                            </td>
                                            <td><input type="number" class="form-control col-md-7 col-xs-12" name="pays[][amount]:number" style="width:140px" required></td>
                                            <td>
                                                <select name="payAccountInfo" class="form-control col-md-7 col-xs-12" style="width:280px" required>
                                                    <option value="">请选择支付账号</option>
                                                    <c:forEach items="${accounts}" var="account">
                                                    <option value="${account.account}/${account.branch}/${account.bank}">${account.account}/${account.branch}/${account.bank}</option>
                                                    </c:forEach>
                                                </select>
                                                <input type="hidden" name="pays[][payAccount]:string">
                                                <input type="hidden" name="pays[][payBranch]:string">
                                                <input type="hidden" name="pays[][payBank]:string">
                                            </td>
                                            <td>
                                                <div name="receiptAccountInfo"></div>
                                                <input type="hidden" name="pays[][receiptAccount]:string">
                                                <input type="hidden" name="pays[][receiptBranch]:string">
                                                <input type="hidden" name="pays[][receiptBank]:string">
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${entity.pays != null}">
                                        <c:forEach items="${entity.pays}" var="pay">
                                            <%
                                                Pay pay = (Pay) pageContext.getAttribute("pay");

                                                String payTypeOptions =
                                                        ("<option value='" + PayConstants.pay_type_transfer_accounts + "'>转账</option>" +
                                                        "<option value='" + PayConstants.pay_type_remit + "'>汇款</option>" +
                                                        "<option value='" + PayConstants.pay_type_transfer_accounts_alipay + "'>支付宝转账</option>" +
                                                        "<option value='" + PayConstants.pay_type_transfer_accounts_weixin + "'>微信转账</option>" +
                                                        "<option value='" + PayConstants.pay_type_other + "'>其他</option>")
                                                        .replace("'" + pay.getPayType() + "'", "'" + pay.getPayType() + "' selected");

                                                String payAccountOptions = "";
                                                List<Account> accounts = (List<Account>)request.getAttribute("accounts");
                                                for (Account account : accounts) {
                                                    String accountInfo = account.getAccount() + "/" + account.getBranch() + "/" + account.getBank();
                                                    payAccountOptions += "<option value='" + accountInfo + "'>" + accountInfo + "</option>";
                                                }
                                                String payAccountInfo = pay.getPayAccount() + "/" + pay.getPayBranch() + "/" + pay.getPayBank();
                                                payAccountOptions = payAccountOptions.replace("'" + payAccountInfo + "'", "'" + payAccountInfo + "' selected");

                                            %>
                                            <tr>
                                                <td><select name="pays[][payType]:number" class="form-control col-md-7 col-xs-12" style="width:140px" required><%=payTypeOptions%></select></td>
                                                <td><input type="number" class="form-control col-md-7 col-xs-12" name="pays[][amount]:number" value="${pay.amount}" style="width:140px" required></td>
                                                <td>
                                                    <select name="payAccountInfo" class="form-control col-md-7 col-xs-12" style="width:280px" required><%=payAccountOptions%></select>
                                                    <input type="hidden" name="pays[][payAccount]:string" value="${pay.payAccount}">
                                                    <input type="hidden" name="pays[][payBranch]:string" value="${pay.payBranch}">
                                                    <input type="hidden" name="pays[][payBank]:string" value="${pay.payBank}">
                                                </td>
                                                <td>
                                                    <div name="receiptAccountInfo">${supplierAccount}/${supplierBranch}/${supplierBank}</div>
                                                    <input type="hidden" name="pays[][receiptAccount]:string" value="${supplierAccount}">
                                                    <input type="hidden" name="pays[][receiptBranch]:string" value="${supplierBranch}">
                                                    <input type="hidden" name="pays[][receiptBank]:string" value="${supplierBank}">
                                                </td>
                                                <td>${pay.stateName}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:if>
                                    </tbody>
                                </table>
                                <div style="padding-top: 8px"><a href="javascript:void(0)" id="addPayItem">添加支付记录</a></div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">采购描述 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <textarea id="describes" name="describes" class="form-control col-md-7 col-xs-12" data-validate-length-range="6,256" data-validate-words="1"required>${entity.describes}</textarea>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">采购条目</label>
                        </div>

                        <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>0</c:otherwise></c:choose>">
                        <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                        <input type="hidden" id="inputer[id]" name="inputer[id]" value="${userId}">
                        </form>
                    </div>

                    <div class="x_content" style="overflow: auto">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><c:if test="${entity.details != null}"><th>状态</th></c:if><th>商品名称</th><th>商品编号</th><th>种类</th><th>用途</th><th>数量</th><th>计量单位</th><th>制作</th>
                                <th data-property-name="th-mountMaterial">镶嵌材质</th><th data-property-name="th-quality">特性</th>
                                <th data-property-name="th-color">颜色</th><th data-property-name="th-type">种类</th>
                                <th data-property-name="th-theme">题材</th><th data-property-name="th-style">款式</th>
                                <th data-property-name="th-transparency">透明度</th><th data-property-name="th-flaw">瑕疵</th>
                                <th data-property-name="th-size">尺寸</th><th data-property-name="th-weight">重量</th>
                                <th data-property-name="th-shape">形状</th><th data-property-name="th-carver">雕工</th>
                                <th data-property-name="th-originPlace">产地</th><th>证书</th><th>采购价</th>
                                <th>采购单价</th><th>市场价</th><th>结缘价</th><th>图片</th><th>图片上传情况</th></tr>
                            </thead>
                            <tbody>
                            <%
                                int detailsCount = 0;
                                List<ProductType> types = (List<ProductType>)request.getAttribute("productTypes");
                            %>
                            <c:if test="${entity.details != null}">
                            <c:forEach items="${entity.details}" var="detail">
                                <%
                                    PurchaseDetail detail=(PurchaseDetail)pageContext.getAttribute("detail");
                                    detailsCount++;

                                    String typeOptions = "";
                                    for (ProductType type : types) {
                                        typeOptions += "<option value='" + type.getId() + "'" + (detail.getProduct().getType().getId().compareTo(type.getId()) == 0 ? " selected" : "") + ">" + type.getName() + "</option>";
                                    }

                                    String useType = "<option value='product'>商品</option><option value='acc'>配饰</option><option value='materials'>加工材料</option>"
                                                     .replace("'" + detail.getProduct().getUseType() + "'", "'" + detail.getProduct().getUseType() + "' selected");

                                    String unitOptions =
                                            ("<option value='件'>件</option>" +
                                            "<option value='克'>克</option>" +
                                            "<option value='克拉'>克拉</option>" +
                                            "<option value='只'>只</option>" +
                                            "<option value='双'>双</option>" +
                                            "<option value='条'>条</option>" +
                                            "<option value='枚'>枚</option>" +
                                            "<option value='副'>副</option>" +
                                            "<option value='千克'>千克</option>" +
                                            "<option value='盎司'>克拉</option>" +
                                            "<option value='其他'>其他</option>")
                                            .replace("'" + detail.getUnit() + "'", "'" + detail.getUnit() + "' selected");

                                    String featureOptions =
                                            "<option value='定制'>定制</option>" +
                                            "<option value='加工'>加工</option>"
                                            .replace("'" + detail.getProduct().getFeature() + "'", "'" + detail.getProduct().getFeature() + "' selected");
                                %>
                                <tr class="state${detail.product.state}">
                                    <td><input type="text" value="${detail.product.stateName}" class="readonly" readonly></td>
                                    <td><input type="text" name="details[][product[name]]:string" value="${detail.product.name}" required></td>
                                    <td><input type="text" name="details[][product[no]]:string" value="${detail.product.no}" required></td>
                                    <td><select name="details[][product[type[id]]]:number" required><%=typeOptions%></select></td>
                                    <td><select name="details[][product[useType]]:string" required><%=useType%></select></td>
                                    <td><input type="text" name="details[][quantity]:number" value="${detail.quantity}" required></td>
                                    <td><select name="details[][unit]:string" required><%=unitOptions%></select></td>
                                    <td><select name="details[][product[feature]]:string" required><%=featureOptions%></select></td>
                                    <td>
                                        <input type="text" data-property-name="mountMaterial" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("镶嵌材质")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("镶嵌材质")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="quality" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("特性")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("特性")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="color" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("颜色")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("颜色")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="type" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("种类")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("种类")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="theme" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("题材")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("题材")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="style" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("款式")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("款式")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="transparency" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("透明度")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("透明度")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="flaw" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("瑕疵")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("瑕疵")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="size" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("尺寸")%>' required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("尺寸")%>' required>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="weight" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("重量")%>' required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("重量")%>' required>
                                    </td>
                                    <td>
                                        <%
                                            String values = detail.getProduct().getMutilPropertyValues("形状");
                                        %>
                                        <input type="text" data-property-name="shape" data-input-type="multiple" name="propertyValue" value='<%=values%>' required>
                                        <%
                                            String[] valuesArr = values.split("#");
                                            for (String value : valuesArr) {
                                        %>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("形状", value)%>' required>
                                        <%
                                            }
                                        %>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="carver" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("雕工")%>'>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("雕工")%>'>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="originPlace" name="propertyValue" value='<%=detail.getProduct().getPropertyValue("产地")%>' required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" value='<%=detail.getProduct().getPropertyJson("产地")%>' required>
                                    </td>
                                    <td><input type="text" name="details[][product[certificate]]:string" value="${detail.product.certificate}"></td>
                                    <td><input type="text" name="details[][amount]:number" value="${detail.amount}" required></td>
                                    <td><input type="text" name="details[][product[unitPrice]]:number" value="${detail.product.unitPrice}" required></td>
                                    <td><input type="text" name="details[][product[price]]:number" value="${detail.product.price}"></td>
                                    <td><input type="text" name="details[][product[fatePrice]]:number" value="${detail.product.fatePrice}"></td>
                                    <td>
                                        <input type="file" name="file">
                                        <input type="hidden" name="details[][product[describe[imageParentDirPath]]]:string" value='<%=detail.getProduct().getDescribe().getImageParentDirPath()%>'>
                                        <input type="hidden" name="imageTopDirPath" value='<%=detail.getProduct().getDescribe().getImageParentDirPath().replace("/"+detail.getProduct().getNo(), "")%>' required>
                                        <input type="hidden" name="details[][product[state]]:number" value="${detail.product.state}" required>
                                        <input type="hidden" name="details[][product[supplier[id]]]:number" value="${detail.product.supplier.id}" required>
                                        <input type="hidden" name="details[][purchase[id]]:number" value="${detail.purchase.id}" required>
                                        <input type="hidden" name="details[][supplier[id]]:number" value="${detail.product.supplier.id}" required>
                                    </td>
                                    <td><a id="${detail.product.no}" href="<%=FileServerInfo.imageServerUrl%>/${detail.product.describe.imageParentDirPath}/snapshoot.jpg" class="lightbox">查看图片</a></td>
                                </tr>
                            </c:forEach>
                            </c:if>
                                <tr>
                                    <c:if test="${entity.details != null}"><td><input type="text" class="readonly" readonly></td></c:if>
                                    <td><input type="text" name="details[][product[name]]:string" required></td>
                                    <td><input type="text" name="details[][product[no]]:string" required></td>
                                    <td>
                                        <select name="details[][product[type[id]]]:number" required>
                                            <c:forEach items="${productTypes}" var="productType">
                                                <option value="${productType.id}">${productType.name}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td>
                                        <select name="details[][product[useType]]:string" required>
                                            <option value="product">商品</option><option value="acc">配饰</option><option value="materials">加工材料</option>
                                        </select>
                                    </td>
                                    <td><input type="text" name="details[][quantity]:number" value="1" required></td>
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
                                            <option value="千克">千克</option>
                                            <option value="盎司">盎司</option>
                                            <option value="其他">其他</option>
                                        </select>
                                    </td>
                                    <td>
                                        <select name="details[][product[feature]]:string" required>
                                            <option value="定制">定制</option>
                                            <option value="加工">加工</option>
                                        </select>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="mountMaterial" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="quality" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="color" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="type" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="theme" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="style" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="transparency" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="flaw" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="size" name="propertyValue" required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" required>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="weight" name="propertyValue" required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" required>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="shape" data-input-type="multiple" name="propertyValue" required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" required>
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="carver" name="propertyValue">
                                        <input type="hidden" name="details[][product[properties[]]]:object">
                                    </td>
                                    <td>
                                        <input type="text" data-property-name="originPlace" name="propertyValue" required>
                                        <input type="hidden" name="details[][product[properties[]]]:object" required>
                                    </td>
                                    <td><input type="text" name="details[][product[certificate]]:string"></td>
                                    <td><input type="text" name="details[][amount]:number" required></td>
                                    <td><input type="text" name="details[][product[unitPrice]]:number" required></td>
                                    <td><input type="text" name="details[][product[price]]:number"></td>
                                    <td><input type="text" name="details[][product[fatePrice]]:number"></td>
                                    <td>
                                        <input type="file" name="file" >
                                        <input type="hidden" name="details[][product[describe[imageParentDirPath]]]:string" value="">
                                        <input type="hidden" name="imageTopDirPath" value="${currentDay}">
                                        <input type="hidden" name="details[][product[state]]:number" value="0" required>
                                        <input type="hidden" name="details[][product[supplier[id]]]:number" required>
                                        <input type="hidden" name="details[][supplier[id]]:number" required>
                                    </td>
                                    <td><input type="text" value="未上传" class="readonly" readonly></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-0" style="margin-top: 10px">
                                    <button id="addItem" type="button" class="btn btn-success">添加采购条目</button>
                                </div>
                            </div>

                            <div class="ln_solid"></div>
                            <div class="form-group" id="submitDiv">
                                <div class="col-md-6 col-md-offset-10">
                                    <button id="cancel" type="button" class="btn btn-primary">返回</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:if test="${entity.state == 0}">
                                            <button id="send" type="button" class="btn btn-success">更新</button>
                                            <button id="editSheet" type="button" class="btn btn-primary">编辑</button>
                                            <button id="delete" type="button" class="btn btn-danger">作废</button>
                                        </c:if>
                                        <c:if test="${fn:contains(resources, '/erp/doBusiness/purchaseBookPaid')}">
                                            <c:if test="${entity.state == 1 && entity.purchaseBookPaid == true && entity.toPayBalance == true}">
                                                <button id="payBalance" type="button" class="btn btn-success">确认余款已付</button>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${fn:contains(resources, '/afterSaleService/business/purchaseReturnProduct')}">
                                            <c:if test="${entity.state == 1}">
                                                <button id="purchaseReturnProduct" type="button" class="btn btn-danger">申请退货</button>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${entity.state == 2}">
                                            <button id="editState" type="button" class="btn btn-primary">编辑</button>
                                            <button id="recover" type="button" class="btn btn-success">恢复</button>
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
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $("#editSheet").unbind("click").click(function(){
        editable = true;

        $('#form :input').attr("readonly",false).css("border", "1px solid #ccc");
        $('#productList tr :input').attr("readonly",false);
        $('.state1, .state10, .state2, .state3, .state4').find(":input").attr("readonly",true);
        $('.readonly').attr("readonly",true);
        $('#send, #delete, #recover').attr("disabled", false);
        $("#editSheet").attr("disabled", "disabled");
    });

    $('.state1, .state10, .state2, .state3, .state4').find(":input").attr("readonly",true);
    $("#accountDiv").hide();

    $("#editState").unbind("click").click(function(){
        $('#delete, #recover').attr("disabled", false);
        $("#editState").attr("disabled", "disabled");
    });

    $("#type").change(function(){
        var typeValue = parseInt($("#type").val());

        if (typeValue == 4) {
            $("#amountLabel").html("押金金额");
        } else {
            $("#amountLabel").html("采购金额");
        }

        if (typeValue == 1) {
            $("#temporaryPurchasePayKindDiv").show();
        } else {
            $("#temporaryPurchasePayKindDiv").hide();
            $("#purchaseBookDiv").hide();
        }
    });

    $("#temporaryPurchasePayKind").change(function(){
        var typeValue = parseInt($(this).val());

        if (typeValue == 0) {
            $("#purchaseBookDiv").hide();
        } else {
            $("#purchaseBookDiv").show();
        }
    });

    <c:if test="${entity != null}">
    setSelect(document.getElementById("type"), "${entity.type}");

        <c:if test="${entity.details != null}">
            $(".lightbox").lightbox({
                fitToScreen: true,
                imageClickClose: false
            });
        </c:if>
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

    $('#purchaseBookPayDate').daterangepicker({
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
                setSupplierId(result.id);
                setSupplierAccount(result);
            }
        }
    });

    setPayAccountInfo();
    <c:if test="${supplierId != null}">
    setSupplierId(${supplierId});
    </c:if>

    function setSupplierId(id) {
        var detailSuppliers = document.getElementsByName("details[][supplier[id]]:number");
        for (var i = 0; i < detailSuppliers.length; i++) {
            detailSuppliers[i].value = id;
        }

        var suppliers = document.getElementsByName("details[][product[supplier[id]]]:number");
        for (var i = 0; i < suppliers.length; i++) {
            suppliers[i].value = id;
        }
    }

    function setSupplierAccount(supplier) {
        var receiptAccountInfos =  document.getElementsByName("receiptAccountInfo");
        for (var i = 0; i < receiptAccountInfos.length; i++) {
            var receiptAccountInfo = $(receiptAccountInfos[i]);
            receiptAccountInfo.html(supplier.account + "/" + supplier.branch + "/" + supplier.bank);

            var receiptAccountInfoInputs = receiptAccountInfo.parent().find(":input");
            for (var j = 0; j < receiptAccountInfoInputs.length; j++) {
                var receiptAccountInfoInput = $(receiptAccountInfoInputs[j]);

                if (receiptAccountInfoInput.attr("name") == "pays[][receiptAccount]:string") {
                    receiptAccountInfoInput.val(supplier.account);
                }

                if (receiptAccountInfoInput.attr("name") == "pays[][receiptBranch]:string") {
                    receiptAccountInfoInput.val(supplier.branch);
                }

                if (receiptAccountInfoInput.attr("name") == "pays[][receiptBank]:string") {
                    receiptAccountInfoInput.val(supplier.bank);
                }
            }
        }
    }

    $('#addPayItem').click(function(){addPay()});

    function addPay() {
        var trs = $("#payList tbody tr");
        $("#payList tbody").append("<tr>" + $(trs[trs.length - 1]).html() + "</tr>");
        setPayAccountInfo();
    }

    function setPayAccountInfo() {
        var trs = $("#payList tbody tr");

        $.each($(trs[trs.length-1]).find(":input"), function (ci, item) {
            var name = item.name;
            if (name != undefined && name == "payAccountInfo") {

                $(item).click(function(){
                    var payAccountInfo = $(this).val().split("/");

                    $.each($(this).parent().find(":input"), function (ci, item) {
                        var name = item.name;
                        if (name != undefined) {
                            if (name == "pays[][payAccount]:string") {
                                $(item).val(payAccountInfo[0]);
                            }

                            if (name == "pays[][payBranch]:string") {
                                $(item).val(payAccountInfo[1]);
                            }

                            if (name == "pays[][payBank]:string") {
                                $(item).val(payAccountInfo[2]);
                            }
                        }
                    });
                });
            }
        });
    }

    $("#delete").click(function(){
        if (confirm("确定作废该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/delete/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":2}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/recover/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":0}');
        }
    });

    $(document).unbind("keydown");

    $("#edit").click(function(){
        $(".table-sheet tbody tr td input").css("border", "1px solid #ccc");
    });

    <c:if test="${fn:contains(resources, '/erp/doBusiness/purchaseBookPaid')}">
        <c:if test="${entity.state == 1 && entity.purchaseBookPaid == true && entity.toPayBalance == true}">
            $("#payBalance").click(function(){
                if (confirm("确认临时采购预定单余款已支付吗？")) {
                    $("#form").sendData('<%=request.getContextPath()%>/erp/doBusiness/purchaseBookPaid', '{"entityId":"${entity.id}","sessionId":"${sessionId}"}');
                }
            });
        </c:if>
    </c:if>

    <c:if test="${entity.state == 1 && fn:contains(resources, '/afterSaleService/business/purchaseReturnProduct')}">
        $("#purchaseReturnProduct").click(function(){render("<%=request.getContextPath()%>/afterSaleService/business/purchaseReturnProduct?json=" + encodeURI('{"entityId":${entity.id}, "entity":"purchase","sessionId":"${sessionId}"}'));});
    </c:if>


    tableSheet.init("productList", 15-<%=detailsCount%>, "<%=request.getContextPath()%>");
    $('#addItem').click(function(){tableSheet.addRow("productList");});

    $("#send").bind("click", function(){
        var typeValue = parseInt($("#type").val());
        if (typeValue == 2 || typeValue == 3 || typeValue == 4) {
            if($(document.getElementById("account[id]")).val() == ""){
                alert("请选择支付账号");
                return false;
            }
        } else {
            $(document.getElementById("account[id]")).val("");
        }

        tableSheet.addPurchase('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Purchase.class.getSimpleName().toLowerCase()%>', '<%=FileServerInfo.uploadFilesUrl%>', '<%=FileServerInfo.imageServerUrl%>');
    });

    <c:choose><c:when test="${entity != null}">document.title = "采购单查看";</c:when><c:otherwise> document.title = "采购单填写";</c:otherwise></c:choose>
</script>