<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.Product" %>
<%@ page import="com.hzg.erp.ProductOwnProperty" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>查看商品</h3>
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
                        <h2>商品 <small>信息</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">名称 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="name" type="text" value="${entity.name}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">编号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input id="no" name="no" type="text" value="${entity.no}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">证书信息 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="certificate" type="text" value="${entity.certificate}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">市场价 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="price:number" type="text" value="${entity.price}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">结缘价 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="fatePrice:number" type="text" value="${entity.fatePrice}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">成本价 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="costPrice:number" type="text" value="${entity.costPrice}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">单价（成本价/库存量） <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="unitPrice:number" type="text" value="${entity.unitPrice}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>
                             <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">供应商 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text2" name="text2" value="${entity.supplier.name}" placeholder="供应商" class="form-control col-md-7 col-xs-12" required />
                                    <input name="supplier[id]" type="hidden" value="${entity.supplier.id}" class="form-control col-md-7 col-xs-12">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">状态 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12"><input name="stateName" type="text" value="${entity.stateName}" class="form-control col-md-7 col-xs-12" required></div>
                            </div>

                            <%
                                Product product = (Product)request.getAttribute("entity");
                            %>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">缩列图 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/snapshoot.jpg" class="lightbox">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/snapshoot.jpg" width="60%">
                                    </a>
                                </div>
                            </div>
                            <c:if test="${entity.state == 6}">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">上传缩列图 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="file" type="file" name="file">
                                    <input type="hidden" name="describe[id]" value="${entity.describe.id}">
                                    <input type="hidden" id="imageParentDirPath" name="describe[imageParentDirPath]:string" value='${entity.describe.imageParentDirPath}' required>
                                    <input type="hidden" id="imageTopDirPath" name="imageTopDirPath" value='<%=product.getDescribe().getImageParentDirPath().replace("/"+product.getNo(), "")%>'>
                                </div>
                            </div>
                            </c:if>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="type" name="type[id]:number" class="form-control col-md-7 col-xs-12" required>
                                        <c:forEach items="${productTypes}" var="productType">
                                            <option value="${productType.id}">${productType.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="item form-group" style="margin-top: 30px">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">属性条目</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <table class="table-sheet product-property-input">
                                        <thead><th>属性名</th><th>属性值</th></thead>
                                        <tbody>
                                        <c:forEach items="${entity.properties}" var="productProperty">
                                            <%
                                                ProductOwnProperty productProperty = (ProductOwnProperty)pageContext.getAttribute("productProperty");
                                                if (!productProperty.getName().equals("形状")) {
                                                    String propertyValue = "";

                                                    if (productProperty.getProperty() != null) {
                                                        propertyValue = "{\"property\":{\"id\":" + productProperty.getProperty().getId() + "},\"name\":\"" + productProperty.getName() + "\",\"value\":\"" + productProperty.getValue() + "\"}";
                                                    } else {
                                                        propertyValue = "{\"name\":\"" + productProperty.getName() + "\",\"value\":\"" + productProperty.getValue() + "\"}";
                                                    }
                                            %>
                                            <tr>
                                                <td><input type="text" name="propertyName" value="${productProperty.name}" required></td>
                                                <td>
                                                    <input type="text" data-property-name="<%=product.getPropertyCode(product.getType().getName(), productProperty.getName())%>" data-input-type="<%=product.getPropertyQuantityType(productProperty.getName())%>" name="propertyValue" value='${productProperty.value}'>
                                                    <input type="hidden" name="properties[]:object" value='<%=propertyValue%>'>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </c:forEach>

                                        <tr>
                                            <%
                                                String values = product.getMutilPropertyValues("形状");
                                            %>
                                            <td><input type="text" name="propertyName"  value="形状" required></td>
                                            <td><input type="text" data-property-name="shape" data-input-type="multiple" name="propertyValue"  value="<%=values%>" required>
                                            <%
                                                String[] valuesArr = values.split("#");
                                                for (String value : valuesArr) {
                                            %>
                                            <input type="hidden" name="properties[]:object" value='<%=product.getPropertyJson("形状", value)%>'>
                                            <%
                                                }
                                            %>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                            <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>0</c:otherwise></c:choose>">
                        </form>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <div class="col-md-6 col-md-offset-3">
                                <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                <c:if test="${entity == null}">
                                    <button id="send" type="button" class="btn btn-success">保存</button>
                                </c:if>
                                <c:if test="${entity != null}">
                                    <c:choose>
                                        <c:when test="${entity.state == 6}">
                                            <button id="send" type="button" class="btn btn-success">修改</button>
                                            <button id="editSheet" type="button" class="btn btn-primary">编辑</button>
                                            <%--<button id="delete" type="button" class="btn btn-danger">作废</button>--%>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${entity.state == 5}">
                         <%--                   <button id="editState" type="button" class="btn btn-primary">编辑</button>
                                            <button id="recover" type="button" class="btn btn-success">置为可用</button>--%>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
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
        $('.table-sheet tr :input').css("border", "0px solid #ccc");
        $('.readonly').attr("readonly",true);
        $('#send, #delete, #recover').attr("disabled", false);
        $("#editSheet").attr("disabled", "disabled");
    });

    $(".lightbox").lightbox({
        fitToScreen: true,
        imageClickClose: false
    });

    <c:if test="${entity != null}">
    setSelect(document.getElementById("type"), ${entity.type.id});
    </c:if>

    $("#text2").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/supplier/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                document.getElementById("suppler[id]").value = result.id;
            }
        }
    });

    <c:forEach items="${entity.properties}" var="property">
        <%
            ProductOwnProperty property = (ProductOwnProperty)pageContext.getAttribute("property");
            String propertyCode = product.getPropertyCode(product.getType().getName(), property.getName());
            String contextPath = request.getContextPath();
            if (!propertyCode.equals("shape")) {
        %>
        tableSheet.suggests(null, "<%=propertyCode%>", "<%=contextPath%>");
        <%
            }
        %>
    </c:forEach>
    tableSheet.suggests(null, "shape", "<%=request.getContextPath()%>");


    $("#send").click(function(){
        if (!validator.checkAll($('#form'))) {
            return;
        }

        $("#imageParentDirPath").val($("#imageTopDirPath").val() + "/" + $("#no").val());

        $(this).sendData('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Product.class.getSimpleName().toLowerCase()%>',
            JSON.stringify($('#form').find(":input").not('[value=""]').not('[name="propertyName"]').not('[name="propertyValue"]').serializeJSON()),
            function (result) {
                if (result.result.indexOf("success") != -1) {
                    var file = document.getElementById("file");
                    if ($.trim(file.value) != "") {
                        sendFormData("snapshoot", $("#imageTopDirPath").val() +"/"+$("#no").val(), file, '<%=FileServerInfo.uploadFilesUrl%>', '<%=FileServerInfo.imageServerUrl%>');
                    }
                }
            });
    });

    function sendFormData(name, dir, file, uploadFilesUrl, imageServerUrl){
        var fd = new FormData();
        fd.append("name", name);
        fd.append("dir", dir);
        fd.append("file", file.files[0]);

        $("#form").sendFormData(uploadFilesUrl, fd, function(result){

            var resultTd = $(file).parent();
            if (result.result.indexOf("success") == -1) {
                resultTd.append("&nbsp;&nbsp;&nbsp;&nbsp;" + result.result + ',请选择文件后，点击<a href="#uploadFile" onclick="sendFormData(\'snapshoot\', $(\'#imageTopDirPath\').val() +\'/\'+$(\'#no\').val(), $(\'#file\'), \'<%=FileServerInfo.uploadFilesUrl%>\', \'<%=FileServerInfo.imageServerUrl%>\')">上传</a>');

            } else {
                resultTd.append('&nbsp;&nbsp;&nbsp;&nbsp;<a id="' + dir + '" href="' + imageServerUrl + '/' + result.filePath + '" class="lightbox">查看图片</a>');
                $(document.getElementById(dir)).lightbox({
                    fitToScreen: true,
                    imageClickClose: false
                });

            }
        });
    }

    $("#delete").click(function(){
        if (confirm("确定作废该商品吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/delete/<%=Product.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":5}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该商品为入库状态吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/recover/<%=Product.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":1}');
        }
    });
</script>