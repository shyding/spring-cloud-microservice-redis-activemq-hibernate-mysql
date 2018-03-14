<%@ page import="com.hzg.customer.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>用户<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></h3>
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
                        <h2>客户服务 <small>用户</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">用户信息</span>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="username">用户名 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="username" name="username" value="${entity.username}" class="form-control col-md-7 col-xs-12" data-validate-length-range="6，20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="nickname">昵称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="nickname" name="nickname" value="${entity.nickname}" class="form-control col-md-7 col-xs-12" data-validate-length-range="6，20" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="email">Email <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="email" id="email" name="email" value="${entity.email}" class="form-control col-md-7 col-xs-12" data-validate-length-range="5,64" data-validate-words="1" required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="describes">描述 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <textarea id="describes" name="describes" class="form-control col-md-7 col-xs-12" data-validate-length-range="6,256" data-validate-words="1"required>${entity.describes}</textarea>
                                </div>
                            </div>
                            <c:if test="${entity == null}">
                            <div class="item form-group">
                                <label for="password1" class="control-label col-md-3">密码 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="password1" type="password" name="password1" data-validate-length="6,32" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="password2" class="control-label col-md-3 col-sm-3 col-xs-12">确认密码 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="password2" type="password" name="password2" data-validate-linked="password1" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <input type="hidden" id="password" name="users[][password]">
                            </c:if>
                            <c:if test="${entity != null}">
                                <div class="item form-group">
                                    <label for="password3" class="control-label col-md-3">密码 <span class="required">*</span></label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <input id="password3" type="password" name="password3" value="*********" data-validate-length="6,32" class="form-control col-md-7 col-xs-12" disabled readonly>
                                        <a id="modifyPasswordA" style="cursor:pointer">修改密码</a>&nbsp;&nbsp;&nbsp;&nbsp;<a id="resetPasswordA" style="cursor:pointer">重置密码</a>
                                    </div>
                                </div>
                            </c:if>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="stateName">状态 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="stateName" name="stateName" value="${entity.stateName}" class="form-control col-md-7 col-xs-12" data-validate-length-range="6，20" data-validate-words="1" readonly type="text">
                                </div>
                            </div>

                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">返回</button>
                                    <c:if test="${entity == null}">
                                    <button id="send" type="button" class="btn btn-success">保存</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <button id="send" type="button" class="btn btn-success">修改</button>
                                        <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                        <button id="delete" type="button" class="btn btn-danger">注销</button>
                                    </c:if>
                                </div>
                            </div>
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                        </form>
                    </div>

                    <c:if test="${entity != null}">
                        <div id="modifyPasswordDiv">
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="oldPassword1">旧密码 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="oldPassword1" type="password"  data-validate-length="6,32" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label for="newPassword1" class="control-label col-md-3">新密码 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="newPassword1" type="password" name="newPassword1" data-validate-length="6,32" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <div class="clearfix" style="margin-bottom: 15px"></div>
                            <div class="item form-group">
                                <label for="confirmNewPassword1" class="control-label col-md-3 col-sm-3 col-xs-12">确认密码 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="confirmNewPassword1" type="password"  data-validate-linked="newPassword1" class="form-control col-md-7 col-xs-12" required>
                                </div>
                            </div>
                            <form id="modifyPasswordForm">
                                <input type="hidden" name="entityId" value="${entity.id}">
                                <input type="hidden" id="oldPassword" name="oldPassword">
                                <input type="hidden" id="newPassword" name="newPassword">
                                <input type="hidden" name="sessionId" value="${sessionId}">
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->

<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    $('#birthday').daterangepicker({
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

    $("#send").click(function(){
        <c:if test="${entity == null}">
        if ($("#form").isFullSet()) {
            var password1 = $("#password1");
            $("#password").val(faultylabs.MD5(jQuery.trim(password1.val())));
            password1.attr("disabled","disabled");
            $("#password2").attr("disabled","disabled");
        }
        </c:if>

        $("#form").submitForm('<%=request.getContextPath()%>/customerManagement/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=User.class.getSimpleName().toLowerCase()%>');
    });

    <c:if test="${entity != null}">
        $("#modifyPasswordA").click(function(){
            $('#modifyPasswordDiv').dialog('open');
            return false;
        });

        $("#modifyPasswordDiv").dialog({
            title: "修改",
            autoOpen: false,
            width: 400,
            height:310,
            buttons: {
                "确定": function () {
                    var modifyPasswordForm = $("#modifyPasswordForm");
                    if (!modifyPasswordForm.isFullSet()) {
                        return false;
                    }

                    var oldPassword1 = $("#oldPassword1");
                    $("#oldPassword").val(faultylabs.MD5(jQuery.trim(oldPassword1.val())));
                    var newPassword1 = $("#newPassword1");
                    $("#newPassword").val(faultylabs.MD5(jQuery.trim(newPassword1.val())));

                    modifyPasswordForm.submitForm('<%=request.getContextPath()%>/customerManagement/doBusiness/modifyPassword');
                },

                "取消": function () {
                    $(this).dialog("close");
                }
            }
        });

        $("#resetPasswordA").click(function(){
            $("#modifyPasswordForm").sendData('<%=request.getContextPath()%>/customerManagement/doBusiness/resetPassword',
                '{"entityId":"${entity.id}","sessionId":"${sessionId}", "random":"' + Math.random() + '"}',
                function(result){
                    if (result.newPassword != undefined) {
                       alert("重置成功，新密码：" + result.newPassword);
                    }
                },
                false);
        });
    </c:if>

   <c:choose><c:when test="${entity != null}">document.title = "用户修改";</c:when><c:otherwise> document.title = "用户注册";</c:otherwise></c:choose>
</script>