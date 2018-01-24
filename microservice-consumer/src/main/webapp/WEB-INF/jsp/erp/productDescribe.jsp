<%--
**
* Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
* 文件名: productDescribe.jsp
*
* @author smjie
* @Date  2017/7/10
* @version 1.00
*
--%>
<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.ProductDescribe" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page import="com.hzg.erp.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>商品描述信息编辑</h3>
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
                        <h2>商品 <small>描述信息编辑</small></h2>
                        <div class="clearfix"></div>
                    </div>

                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品描述信息</span>

                            <div id="productDescribe">
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="seoTitle">商品优化标题 <span class="required">*</span>
                                    </label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input id="seoTitle" type="text" name="describe[seoTitle]" value="${entity.describe.seoTitle}" class="form-control col-md-7 col-xs-12" required>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoKeyword">商品优化关键词 <span class="required">*</span></label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input id="seoKeyword" type="text" name="describe[seoKeyword]" value="${entity.describe.seoKeyword}" class="form-control col-md-7 col-xs-12" required>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoDesc">商品优化描述 <span class="required">*</span></label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input id="seoDesc" type="text" name="describe[seoDesc]" value="${entity.describe.seoDesc}" class="form-control col-md-7 col-xs-12" required>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">软文描述 <span class="required">*</span>
                                    </label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <textarea id="describes" class="form-control col-md-7 col-xs-12" rows="4"  name="describe[describes]" required>${entity.describe.describes}</textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">商品编号<span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择编号添加商品条目" />
                                </div>
                            </div>

                            <input type='hidden' id="describeId" name='describe[id]:number' value='${entity.describe.id}'>
                            <input type="hidden" name="describe[editor[id]]:number" value="${userId}">
                        </form>
                    </div>

                    <div class="x_content" style="overflow: auto;margin-top: 30px">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品名称</th><th>编号</th><th>状态</th><th>种类</th><th>采购价</th><th>图片</th></tr></thead>
                            <tbody id="tbody">
                            <c:if test="${entity != null}">
                                <tr><td>${entity.name}</td><td>${entity.no}</td><td>${entity.stateName}</td><td>${entity.type.name}</td><td>${entity.unitPrice}</td>
                                    <td><a id="${entity.id}" href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/snapshoot.jpg" class="lightbox">查看图片</a></td>
                                </tr>
                            </c:if>
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
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <button id="send" type="button" class="btn btn-success">更新</button>
                                        <button id="edit" type="button" class="btn btn-primary">编辑</button>
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
    init(<c:out value="${entity.describe.editor == null}"/>);

    <c:if test="${entity != null}">
        $("#${entity.id}").lightbox({
            fitToScreen: true,
            imageClickClose: false
        });
    </c:if>

    $("#send").click(function(){
        if ($.trim($("#describeId").val()) != "") {
            $('#form').submitForm('<%=request.getContextPath()%>/erp/update/<%=ProductDescribe.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+ProductDescribe.class.getSimpleName().substring(1)%>');
        } else {
            alert("请选择要编辑的商品");
            $("#text1").focus();
            return false;
        }
    });

    $("#text1").coolautosuggestm({
        url:"<%=request.getContextPath()%>/erp/privateQuery/<%=Product.class.getSimpleName().toLowerCase()%>",
        width : 309,
        marginTop : "margin-top:34px",
        showProperty: "no",

        getQueryData: function(paramName){
            var queryJson = {};

            var suggestWord = $.trim(this.value);
            if (suggestWord != "") {
                queryJson["no"] = suggestWord;
            }
            queryJson["state"] = " in (" + <%=ErpConstant.product_state_mediaFiles_uploaded%> + "," + <%=ErpConstant.product_state_edit%> + ")";

            return queryJson;
        },

        onSelected:function(result){
            if(result!=null){
                var tbody = $("#tbody");

                if (tbody.html().indexOf('<td>') == -1) {
                    addItem(tbody, result);
                } else if (tbody.html().indexOf('<td>' + result.no + "</td>") == -1) {
                    alert("只能对同一类商品编辑");
                }
            }
        }
    });

    function addItem(tbody, item) {
        $("#describeId").val(item.describe.id);
        tbody.append("<tr><td>" + item.name + "</td><td>" + item.no + "</td><td>" +
            dataList.entityStateNames["product"]["state"][item.state] + "</td><td>" + item.type.name + "</td><td>" + item.unitPrice + "</td><td>" +
            "<a id='" + item.id + "' href='<%=FileServerInfo.imageServerUrl%>/" + item.describe.imageParentDirPath + "/snapshoot.jpg'>图片</a></td></tr>");

        $(document.getElementById(item.id)).lightbox({
            fitToScreen: true,
            imageClickClose: false
        });
    }

    $("#delItem").click(function(){
        var lastTr = $("#productList tbody tr:last-child");

        if (lastTr.html().indexOf('<td>') != -1) {
            lastTr.remove();
            $("#describeId").val("");
        }
    });

    document.title = "商品描述信息编辑";
</script>