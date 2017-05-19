<%--
**
* Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
* 文件名: PrivilegeResource.jsp
*
* @author smjie
* @Date  2017/4/12
* @version 1.00
*
--%>
<%@ page import="com.hzg.sys.PrivilegeResource" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>权限<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>添加</c:otherwise></c:choose></h3>
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
                        <h2>后台管理 <small>权限</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">权限信息</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">权限名称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="name" class="form-control col-md-7 col-xs-12" value="${entity.name}" data-validate-length-range="4,30" data-validate-words="1" name="name"  required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="uri" class="control-label col-md-3">URL <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="uri" class="form-control col-md-7 col-xs-12" type="text" value="${entity.uri}" name="uri" data-validate-words="1" data-validate-length="1,64" required>
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
<!-- Custom Theme Scripts -->
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);
    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/sys/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=PrivilegeResource.class.getSimpleName().toLowerCase().substring(0,1).toLowerCase()+PrivilegeResource.class.getSimpleName().substring(1)%>');});
    <c:choose><c:when test="${entity != null}">document.title = "权限修改";</c:when><c:otherwise> document.title = "权限注册";</c:otherwise></c:choose>
</script>