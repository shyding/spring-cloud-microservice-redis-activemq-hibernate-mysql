<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.tools.ErpConstant" %>
<%@ page import="com.hzg.erp.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>商品上架/下架</h3>
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
                        <h2>商品 <small id="smallTitle">上架</small></h2>
                        <div class="clearfix"></div>
                    </div>

                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="operationType">类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="operationType" name="operationType" class="form-control col-md-7 col-xs-12" required>
                                        <option value="">请选择操作类型</option>
                                        <option value="<%=ErpConstant.product_action_upShelf%>">上架商品</option>
                                        <option value="<%=ErpConstant.product_action_downShelf%>">下架商品</option>
                                    </select>
                                </div>
                            </div>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">商品编号<span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="选择编号添加商品条目" />
                                </div>
                            </div>
                            <input type="hidden" name="sessionId:string" value="${sessionId}">
                        </form>
                    </div>

                    <div class="x_content" style="overflow: auto;margin-top: 30px">
                        <table id="productList" class="table-sheet" width="100%">
                            <thead><tr><th>商品名称</th><th>编号</th><th>状态</th><th>种类</th><th>采购价</th><th>图片</th></tr></thead>
                            <tbody id="tbody">
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
                                    <button id="doBusiness" type="button" class="btn btn-success">上架商品</button>
                                    <button id="doBusinessOut" type="button" class="btn btn-success" style="display:none">下架商品</button>
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
    init(true);

    $("#operationType").change(function(){
        if (this.value == <%=ErpConstant.product_action_upShelf%>) {
            $("#smallTitle").html("上架");
            $("#doBusiness").show();
            $("#doBusinessOut").hide();

        } else if (this.value == <%=ErpConstant.product_action_downShelf%>) {
            $("#smallTitle").html("下架");
            $("#doBusiness").hide();
            $("#doBusinessOut").show();
        }

        $("#tbody").empty();
    });

    $("#doBusiness").click(function(){
        if (confirm("商品上架后，商品信息将不再可以编辑，确定上架商品吗？")) {
            $('#form').submitForm('<%=request.getContextPath()%>/erp/doBusiness/<%=ErpConstant.product_action_name_upShelf%>');
        }
    });

    $("#doBusinessOut").click(function(){
        if (confirm("商品下架后，商品将不再可以销售，确定下架商品吗？")) {
            $('#form').submitForm('<%=request.getContextPath()%>/erp/doBusiness/<%=ErpConstant.product_action_name_downShelf%>');
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

            var operationType = $("#operationType").val();
            if (operationType == 0) {
                queryJson["state"] = <%=ErpConstant.product_state_edit%>;
            } else if (operationType == 1) {
                queryJson["state"] = <%=ErpConstant.product_state_onSale%>;
            }

            return queryJson;
        },

        onSelected:function(result){
            if(result!=null){
                addItem($("#form"), $("#tbody"), result);
            }
        }

    });

    function addItem(form, tbody, item) {
        if (document.getElementById("tr" + item.id) == null) {
            form.append("<input id='ih" + item.id +"' type='hidden' name='entityIds[]' value='" + item.id + "'>");
            tbody.append("<tr id='tr" + item.id + "'><td>" + item.name + "</td><td>" + item.no + "</td><td>" +
                dataList.entityStateNames["product"]["product[state]"][item.state] + "</td><td>" + item.type.name + "</td><td>" + item.unitPrice + "</td><td>" +
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
            document.getElementById("form").removeChild(document.getElementById("ih"+idSuffix));
        }
    });

    document.title = "商品上架";
</script>