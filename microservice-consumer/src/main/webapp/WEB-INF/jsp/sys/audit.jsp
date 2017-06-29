<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.sys.Audit" %>
<%@ page import="com.hzg.tools.AuditFlowConstant" %>
<%@ page import="com.hzg.sys.AuditFlowNode" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3><c:choose><c:when test="${entity.state == 1}">办理事宜</c:when><c:otherwise>查看事宜</c:otherwise></c:choose></h3>
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
                        <h2><c:choose><c:when test="${entity.state == 1}">办理事宜</c:when><c:otherwise>查看事宜</c:otherwise></c:choose></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <div class="right_col">
                            <h2 class="h2-margin-bottom">名称: &nbsp;&nbsp;${entity.name}</h2>
                            <h2 class="h2-margin-bottom">内容: &nbsp;&nbsp;${entity.content}</h2>
                            <h2 class="h2-margin-bottom">流转时间: &nbsp;&nbsp;${entity.inputDate}</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="entityDiv"></div>

        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                    <div class="x_content">
                        <div>
                            <h2 class="h2-margin-bottom">节点处理情况</h2>
                            <c:forEach items="${entities}" var="entity" varStatus="status">
                                <c:if test="${entity.state == 1}">
                                    <div style="margin-top: 5px">${status.count}.&nbsp;${entity.dealDate}&nbsp;&nbsp;${entity.post.name}&nbsp;&nbsp;${entity.user.name}&nbsp;&nbsp;${entity.resultName}&nbsp;&nbsp;${entity.remark}</div>
                                </c:if>
                            </c:forEach>
                        </div>

                       <div class="ln_solid" style="margin-top: 40px; margin-bottom: 60px"></div>

                        <form class="form-horizontal form-label-left" novalidate id="dealForm">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="result">类型<span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="result" name="result" class="form-control col-md-7 col-xs-12" required>
                                        <option value="">请选择</option>
                                        <option value="<%=AuditFlowConstant.audit_do%>">已办</option>
                                        <option value="<%=AuditFlowConstant.audit_pass%>">审核通过</option>
                                        <c:if test="${refusePostOptions != null}">
                                        <option value="<%=AuditFlowConstant.audit_deny%>">审核未通过</option>
                                        </c:if>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group" id="refusePosts" style="display:none">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="result">返回节点<span class="required"></span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select name="toRefusePost[id]" class="form-control col-md-7 col-xs-12">
                                        ${prePostOptions}
                                        ${refusePostOptions}
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="remark">批语 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="remark" name="remark" value="${entity.remark}"  class="form-control col-md-7 col-xs-12" data-validate-length-range="6,30" data-validate-words="1"required></textarea>
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <button id="deal" type="button" class="btn btn-success">办理</button>
                                </div>
                            </div>
                            <input type="hidden" id="id" name="id" value="${entity.id}">
                            <input type="hidden" name="sessionId" value="${sessionId}">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->
<script type="text/javascript">
    var entity = "${entity.entity}";
    renderAudit($("#entityDiv"), "<%=request.getContextPath()%>" + dataList.modules[entity] + dataList.viewActions[entity] + "/" + entity + "/${entity.entityId}");

    if (${entity.state == 0}) {
        $("#deal").click(function(){
            $('#dealForm').submitForm('<%=request.getContextPath()%>/sys/<%=Audit.class.getSimpleName().toLowerCase()%>');
            $("#deal").attr("disabled", "disabled");
        });
    } else {
        $("#deal").attr("disabled", "disabled");
    }

    $("#result").change(function(){
        if (this.value == "N") {
            $("#refusePosts").show();
        } else {
            $("#refusePosts").hide();
        }
    });

    <c:choose><c:when test="${entity.state == 1}">document.title = "办理事宜";</c:when><c:otherwise> document.title = "查看事宜";</c:otherwise></c:choose>
</script>