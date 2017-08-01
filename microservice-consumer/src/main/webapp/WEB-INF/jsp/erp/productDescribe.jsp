<%@ page import="com.hzg.sys.Company" %>
<%@ page import="com.hzg.erp.Warehouse" %>
<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.ProductDescribe" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>商品上架</h3>
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
                        <h2>ERP <small>商品上架</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品编辑信息</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="seoTitle">商品优化标题 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoTitle" type="text" name="seoTitle" value="${entity.seoTitle}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoKeyword">商品优化关键词 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoKeyword" type="text" name="seoKeyword" value="${entity.seoKeyword}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoDesc">商品优化描述 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoDesc" type="text" name="seoDesc" value="${entity.seoDesc}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">软文描述 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="describes" class="form-control col-md-7 col-xs-12" rows="4"  name="describes" required>${entity.name}</textarea>
                                </div>
                            </div>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">商品编号<span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择编号添加商品条目" />
                                </div>
                            </div>

                            <c:forEach items="${products}" var="products">
                                <div id="div${product.describe.id}" style="display: none">
                                    <input type='hidden' name='id:number' value='${product.describe.id}'>
                                </div>
                            </c:forEach>
                        </form>
                    </div>

                    <div class="x_content" style="overflow: auto;margin-top: 30px">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品名称</th><th>商品编号</th><th>种类</th><th>采购价</th><th>图片</th></tr></thead>
                            <tbody id="tbody">
                            <c:forEach items="${products}" var="product">
                                <tr id="tr${product.id}"><td>${product.name}</td><td>${product.no}</td><td>${product.type.name}</td>
                                    <td>${product.unitPrice}</td>
                                    <td><a id="${detail.product.id}" href="<%=FileServerInfo.imageServerUrl%>/${product.describe.imageParentDirPath}/snapshoot.jpg" class="lightbox">查看图片</a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div id="delDiv" class="form-group">
                                <div class="col-md-6 col-md-offset-0" style="margin-top: 10px">
                                    <button id="delItem" type="button" class="btn btn-success">减少条目</button>
                                </div>
                            </div>

                            <div class="ln_solid"></div>
                            <div class="form-group" id="submitDiv">
                                <div class="col-md-6 col-md-offset-10">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                        <button id="doBusiness" type="button" class="btn btn-success">上架商品</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:if test="${entity.state == 7}">
                                            <button id="send" type="button" class="btn btn-success">更新</button>
                                            <button id="doBusiness" type="button" class="btn btn-success">上架商品</button>
                                            <button id="edit" type="button" class="btn btn-primary">编辑</button>
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

    $("#send").click(function(){
        $('#form').submitForm('<%=request.getContextPath()%>/erp/update/<%=ProductDescribe.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+ProductDescribe.class.getSimpleName().substring(1)%>');
    });

    $("#doBusiness").click(function(){
        if (confirm("商品上架后，商品信息将不再可以编辑，确定上架商品吗？")) {
            $('#form').append("<input type='hidden' name='state:number' value='<%=ErpConstant.product_state_onSale%>'>");
            $("#send").click();
        }
    });

    $("#text1").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/product/no/",
        showProperty: "no",
        onSelected:function(result){
            if(result!=null){
                var form = $("#form");
                var tbody = $("#tbody");

                addItem(form, tbody, result);
            }
        }
    });

    function addItem(form, tbody, item) {
        if (document.getElementById(item.describe.id) == null) {
            form.append("<div id='div" + item.describe.id +"' style='display: none'>" +
                "<input type='hidden' name='id:number' value='" + item.describe.id + "'></div>");

            tbody.append("<tr id='tr" + item.describe.id + "'><td>" + item.name + "</td><td>" + item.no + "</td><td>" + item.type.name + "</td><td>" + item.unitPrice + "</td><td>" +
                "<a id='" + item.id + "' href='<%=FileServerInfo.imageServerUrl%>/" + item.describe.imageParentDirPath + "/snapshoot.jpg'>图片</a></td></tr>");

            $(document.getElementById(item.id)).lightbox({
                fitToScreen: true,
                imageClickClose: false
            });
        }
    }

    $("#delItem").click(function(){
        var lastTr = $("#productList tbody tr:last-child");

        if (lastTr.html().indexOf('<td>') != -1) {
            var idSuffix = lastTr.attr("id").substring(2);

            document.getElementById("tbody").removeChild(document.getElementById("tr"+idSuffix));
            document.getElementById("form").removeChild(document.getElementById("div"+idSuffix));
        }
    });

    document.title = "商品上架";
</script>