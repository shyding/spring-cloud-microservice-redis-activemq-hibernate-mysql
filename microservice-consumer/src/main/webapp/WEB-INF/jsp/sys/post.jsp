<%@ page import="com.hzg.sys.Post" %>
<%@ page import="com.hzg.sys.Company" %>
<%@ page import="com.hzg.sys.Dept" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>岗位<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></h3>
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
                        <h2>后台管理 <small>岗位</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form" onsubmit="return false;">
                            <span class="section">岗位信息</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">岗位名称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="name" class="form-control col-md-7 col-xs-12" value="${entity.name}" data-validate-length-range="6,30" data-validate-words="1" name="name"  required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="company[id]">公司 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="company[id]" name="company[id]" class="form-control col-md-7 col-xs-12" required>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="dept[id]">部门 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="dept[id]" name="dept[id]" class="form-control col-md-7 col-xs-12" required>
                                    </select>
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <button id="send" type="button" class="btn btn-success"><c:choose><c:when test="${entity != null}">更新</c:when><c:otherwise>保存</c:otherwise></c:choose></button>
                                    <c:if test="${entity != null}"><button id="assignPrivilege" type="button" class="btn btn-success">分配权限</button></c:if>
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

    <c:if test="${entity != null}">
    $("#assignPrivilege").click(function(){render("<%=request.getContextPath()%>/sys/business/assignPrivilege?json=" + encodeURI("{\"id\":${entity.id}}"));});</c:if>
    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/sys/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Post.class.getSimpleName().toLowerCase()%>');});

    var companyId = "${entity.dept.company.id}", deptId="${entity.dept.id}";
    if (deptId != "") {
        selector.setSelect(['company[id]', 'dept[id]'], ['name', 'name'], ['id', 'id'],
            [companyId, deptId],
            ['<%=request.getContextPath()%>/sys/query/<%=Company.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Dept.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Post.class.getSimpleName().toLowerCase()%>'],
            '{}', ['company[id]'], 0);

    } else {
        selector.setSelect(['company[id]', 'dept[id]'], ['name', 'name'], ['id', 'id'], [],
            ['<%=request.getContextPath()%>/sys/query/<%=Company.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Dept.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Post.class.getSimpleName().toLowerCase()%>'],
            '{}', ['company[id]'], 0);
    }

    <c:choose><c:when test="${entity != null}">document.title = "岗位修改";</c:when><c:otherwise> document.title = "岗位注册";</c:otherwise></c:choose>
</script>