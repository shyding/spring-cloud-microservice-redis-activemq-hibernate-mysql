<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.erp.ProductType" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>商品类型<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>添加</c:otherwise></c:choose></h3>
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
                        <h2>ERP <small>商品类型</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">商品类型信息</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">名称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="name" class="form-control col-md-7 col-xs-12" value="${entity.name}" data-validate-length-range="2,20" data-validate-words="1" name="name"  required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="abbreviate" class="control-label col-md-3">缩写</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="abbreviate"  name="abbreviate" class="form-control col-md-7 col-xs-12" type="text" value="${entity.abbreviate}"data-validate-words="1" data-validate-length="2,10">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="title" class="control-label col-md-3">优化标题</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="title"  name="title" class="form-control col-md-7 col-xs-12" type="text" value="${entity.title}"data-validate-words="1" data-validate-length="2,50">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="keyword" class="control-label col-md-3">优化关键字 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="keyword"  name="keyword" class="form-control col-md-7 col-xs-12" type="text" value="${entity.keyword}"data-validate-words="1" data-validate-length="2,50" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="describes" class="control-label col-md-3">优化描述</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="describes"  name="describes" class="form-control col-md-7 col-xs-12" type="text" value="${entity.describes}"data-validate-words="1" data-validate-length="2,100">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">所属父类</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" value="${entity.parent.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入商品类型名称" />
                                    <input type="hidden" id="parent[id]" name="parent[id]" value="${entity.parent.id}" data-value-type="number" data-skip-falsy="true">
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <button id="send" type="button" class="btn btn-success"><c:choose><c:when test="${entity != null}">更新</c:when><c:otherwise>保存</c:otherwise></c:choose></button>
                                    <c:if test="${entity != null}"><button id="edit" type="button" class="btn btn-primary">编辑</button></c:if>
                                </div>
                            </div>
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=ProductType.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+ProductType.class.getSimpleName().substring(1)%>');});

    $("#text1").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/productType/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("parent[id]")).val(result.id);
            }
        }
    });

    <c:choose><c:when test="${entity != null}">document.title = "商品类型修改";</c:when><c:otherwise> document.title = "商品类型添加";</c:otherwise></c:choose>
</script>