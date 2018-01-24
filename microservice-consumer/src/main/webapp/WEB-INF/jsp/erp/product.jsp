<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page import="com.hzg.erp.Product" %>
<%@ page import="com.hzg.erp.ProductOwnProperty" %>
<%--
**
* Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
* 文件名: product.jsp
*
* @author smjie
* @Date  2017/7/18
* @version 1.00
*
--%>
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
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">采购图 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <a id="imgA" href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/snapshoot.jpg?<%=Math.random()%>" class="lightbox">
                                        <img id="img" src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/snapshoot.jpg?<%=Math.random()%>" width="60%">
                                    </a>
                                </div>
                            </div>

                            <c:if test="${entity.state == 6}">
                                <div class="item form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12">上传采购图 <span class="required">*</span></label>
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
                                        <thead><tr><th>属性名</th><th>属性值</th></tr></thead>
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

                            <c:if test="${entity.describe.photographer != null}">
                            <span class="section">商品多媒体文件</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">摄影图 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/1.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/1.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/2.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/2.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/3.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/3.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/4.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/4.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/5.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/5.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/6.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/6.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/7.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/7.jpg" width="15%">
                                    </a>
                                    <a href="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/8.jpg"  class="lightbox" rel="roadtrip">
                                        <img src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/8.jpg" width="15%">
                                    </a>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12">视频 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <video width="320" height="240" controls>
                                        <source src="<%=FileServerInfo.imageServerUrl%>/${entity.describe.imageParentDirPath}/${entity.no}.mp4" type="video/mp4">
                                        您的浏览器不支持Video标签。
                                    </video>
                                </div>
                            </div>
                            </c:if>

                            <c:if test="${entity.describe.editor != null}">
                            <span class="section">商品描述</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="seoTitle">商品优化标题 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoTitle" type="text" name="seoTitle" value="${entity.describe.seoTitle}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoKeyword">商品优化关键词 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoKeyword" type="text" name="seoKeyword" value="${entity.describe.seoKeyword}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="seoDesc">商品优化描述 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="seoDesc" type="text" name="seoDesc" value="${entity.describe.seoDesc}" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="describes">软文描述 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="describes"  class="form-control col-md-7 col-xs-12" rows="4"  name="describes" required>${entity.describe.describes}</textarea>
                                </div>
                            </div>
                            </c:if>

                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                            <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>0</c:otherwise></c:choose>">
                        </form>
                    </div>

                    <div class="x_content">
                        <div class="form-horizontal form-label-left">
                            <div class="ln_solid"></div>
                            <div class="col-md-6 col-md-offset-3" id="submitDiv">
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
        $('#seoTitle, #seoKeyword, #seoDesc, #describes').attr("readonly",true);
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
            if (result.result.indexOf("success") != -1) {
                $("#imgA").attr("href", imageServerUrl + '/' + result.filePath + "?" + Math.random());
                $("#img").attr("src", imageServerUrl + '/' + result.filePath + "?" + Math.random());

                $(document.getElementById("imgA")).lightbox({
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