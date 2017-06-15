<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.sys.*" %>
<%@ page import="com.hzg.erp.Purchase" %>
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
                        <span class="section"><c:choose><c:when test="${entity != null}">查看</c:when><c:otherwise>填写</c:otherwise></c:choose>采购单</span>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购标题 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="name" name="name" value="${entity.name}" data-validate-length-range="5,30" data-validate-words="1" class="form-control col-md-7 col-xs-12" required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="type">采购类型 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <select id="type" name="type:number" class="form-control col-md-7 col-xs-12" required>
                                    <option value="">请选择类型</option>
                                    <option value="0">正常采购</option>
                                    <option value="1">临时采购</option>
                                    <option value="2">应急采购</option>
                                    <option value="3">现金采购</option>
                                    <option value="4">押金采购</option>
                                </select>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="date">采购时间 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <div class="input-prepend input-group" style="margin-bottom:0">
                                    <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                    <input type="text" name="date" id="date" class="form-control" value="${entity.date}">
                                </div>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购金额 <span class="required">*</span>
                            </label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input id="amount" name="amount:number" value="${entity.amount}" class="form-control col-md-7 col-xs-12" required type="number">
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">采购人</label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text1" name="text1" value="${entity.charger.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                <input type="hidden" id="charger[id]" name="charger[id]" value="${entity.charger.id}">
                            </div>
                        </div>
                        <c:set var="supplierName" />
                        <c:set var="supplierId" />
                        <c:forEach items="${entity.details}" var="detail" end="0">
                            <c:set var="supplierName" value="${detail.supplier.name}" />
                            <c:set var="supplierId" value="${detail.supplier.id}" />
                        </c:forEach>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text2">供应商</label>
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <input type="text" id="text2" name="text2" value="${supplierName}" placeholder="供应商" class="form-control col-md-7 col-xs-12" style="width:40%" required />
                            </div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="amount">采购描述 <span class="required">*</span>
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

                    <form id="form1">
                    <div class="x_content" style="overflow: auto">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品名称</th><th>商品编号</th><th>种类</th><th>数量</th><th>计量单位</th><th>制作</th>
                                <th data-property-name="th-mountMaterial">镶嵌材质</th><th data-property-name="th-quality">特性</th>
                                <th data-property-name="th-color">颜色</th><th data-property-name="th-type">种类</th>
                                <th data-property-name="th-theme">题材</th><th data-property-name="th-style">款式</th>
                                <th data-property-name="th-transparency">透明度</th><th data-property-name="th-flaw">瑕疵</th>
                                <th data-property-name="th-size">尺寸</th><th data-property-name="th-weight">重量</th>
                                <th data-property-name="th-shape">形状</th><th data-property-name="th-carver">雕工</th>
                                <th data-property-name="th-originPlace">产地</th><th>证书</th><th>采购价</th>
                                <th>采购单价</th><th>市场价</th><th>结缘价</th><th>图片</th></tr>
                            </thead>
                            <tbody>
                            <tr>
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
                                    <input type="text" name="details[][quantity]:number" value="1" required>
                                    <input type="hidden" name="details[][product[supplier[id]]]:number" required>
                                </td>
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
                                <td>
                                    <select name="details[][product[feature]]:string" required>
                                        <option value="定制">定制</option>
                                        <option value="加工">加工</option>
                                    </select>
                                </td>
                                <td>
                                    <input type="text" data-property-name="mountMaterial" value="" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="quality" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="color" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="type" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="theme" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="style" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="transparency" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="flaw" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="size" name="propertyValue" required>
                                    <input type="hidden" name="details[][product[properties[]]:object" required>
                                </td>
                                <td>
                                    <input type="text" data-property-name="weight" name="propertyValue" required>
                                    <input type="hidden" name="details[][product[properties[]]:object" required>
                                </td>
                                <td>
                                    <input type="text" data-property-name="shape" data-input-type="multiple" name="propertyValue" required>
                                    <input type="hidden" name="details[][product[properties[]]:object" required>
                                </td>
                                <td>
                                    <input type="text" data-property-name="carver" name="propertyValue">
                                    <input type="hidden" name="details[][product[properties[]]:object">
                                </td>
                                <td>
                                    <input type="text" data-property-name="originPlace" name="propertyValue" required>
                                    <input type="hidden" name="details[][product[properties[]]:object" required>
                                </td>
                                <td><input type="text" name="details[][product[certificate]]:string"></td>
                                <td><input type="text" name="details[][product[costPrice]]:number" required></td>
                                <td><input type="text" name="details[][product[unitPrice]]:number" required></td>
                                <td><input type="text" name="details[][product[price]]:number"></td>
                                <td><input type="text" name="details[][product[fatePrice]]:number"></td>
                                <td><input type="file" name="details[][product[describe[imageUrl]]]:String" ></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    </form>

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
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:if test="${entity.state == 0}">
                                            <button id="send" type="button" class="btn btn-success">更新</button>
                                            <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                            <button id="delete" type="button" class="btn btn-danger">作废</button>
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

    <c:if test="${entity != null}">
    setSelect(document.getElementById("type"), "${entity.type}");
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
                var suppliers = document.getElementsByName("details[][product[supplier[id]]]:number");
                for (var i = 0; i < suppliers.length; i++) {
                    suppliers[i].value = result.id;
                }
            }
        }
    });

    $("#delete").click(function(){
        if (confirm("确定作废该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":2}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Purchase.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":0}');
        }
    });

    $(document).unbind("keydown");

    tableSheet.init("productList", 15, "<%=request.getContextPath()%>");
    $('#addItem').click(function(){tableSheet.addRow("productList");});

    $("#send").bind("click", function(){tableSheet.addPurchase('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Purchase.class.getSimpleName().toLowerCase()%>');});

    <c:choose><c:when test="${entity != null}">document.title = "采购单查看";</c:when><c:otherwise> document.title = "采购单填写";</c:otherwise></c:choose>
</script>