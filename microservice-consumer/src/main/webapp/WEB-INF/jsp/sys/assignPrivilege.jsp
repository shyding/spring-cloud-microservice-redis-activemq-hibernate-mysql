<%--
**
* Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
* 文件名: assignPrivilege.jsp
*
* @author smjie
* @Date  2017/4/12
* @version 1.00
*
--%>
<%@ page import="com.hzg.sys.Post" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>权限分配</h3>
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
                        <ul class="nav navbar-right panel_toolbox">
                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                            </li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
                                <ul class="dropdown-menu" role="menu">
                                    <li><a href="#">Settings 1</a>
                                    </li>
                                    <li><a href="#">Settings 2</a>
                                    </li>
                                </ul>
                            </li>
                            <li><a class="close-link"><i class="fa fa-close"></i></a>
                            </li>
                        </ul>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">分配权限</span>

                            <div class="item form-group">
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" style="text-align: left; margin-bottom:6px" for="unAssignPrivileges[][id]">未分配权限
                                    </label>
                                    <select id="unAssignPrivileges[][id]" name="unAssignPrivileges[][id]" class="form-control col-md-7 col-xs-12" multiple size="20">
                                        <c:forEach items="${unAssignPrivileges}" var="privilege">
                                            <option value="${privilege.id}">${privilege.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <div class="col-md-6 col-sm-6 col-xs-12" style="text-align: center;margin-top:20px">
                                    <button id="assign" class="btn btn-success" style="width: 50px; height: 30px" type="button"><i class="fa fa-arrow-down"></i></button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <button id="remove" class="btn btn-warning" style="width: 50px; height: 30px" type="button"><i class="fa fa-arrow-up"></i></button>
                                </div>
                            </div>
                            <div class="item form-group">
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12" style="text-align: left;margin-bottom:6px" for="privilegeResources[][id]">已分配权限
                                    </label>
                                    <select id="assignPrivileges[][id]" name="assignPrivilegs[][id]" class="form-control col-md-7 col-xs-12" multiple size="8">
                                        <c:forEach items="${entity.privilegeResources}" var="privilege">
                                            <option value="${privilege.id}">${privilege.name}</option>
                                        </c:forEach>
                                    </select>
                                    <select id="privilegeResources[][id]" name="privilegeResources[][id]" class="form-control col-md-7 col-xs-12" style="display: none" multiple size="8">
                                        <c:forEach items="${entity.privilegeResources}" var="privilege">
                                            <option value="${privilege.id}">${privilege.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-0">
                                    <button id="return" type="button" class="btn btn-success">返回</button>
                                    <button id="send" type="button" class="btn btn-success">保存</button>
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
    $("#assign").click(function () {
        addRemoveOptions(document.getElementById("unAssignPrivileges[][id]"), document.getElementById("privilegeResources[][id]"));
        cloneOptions(document.getElementById("privilegeResources[][id]"), document.getElementById("assignPrivileges[][id]"));
    });

    $("#remove").click(function () {
        addRemoveOptions(document.getElementById("assignPrivileges[][id]"), document.getElementById("unAssignPrivileges[][id]"));
        cloneOptions(document.getElementById("assignPrivileges[][id]"), document.getElementById("privilegeResources[][id]"));
    });

    function addRemoveOptions(src, dst) {
        var options = src.options;
        for (var i = 0; i < options.length; i++) {
            if (options[i].selected) {
                $(dst).append(options[i]);
                --i;
            }
        }
    }

    function cloneOptions(src, dst){
        var options = src.options;
        $(dst).empty();
        for (var i = 0; i < options.length; i++) {
            dst.append(new Option(options[i].text, options[i].value));
        }
    }

    $("#send").click(function(){
        var options = document.getElementById("privilegeResources[][id]").options;
        for (var ii in options) {
            options[ii].selected = true;
        }

        $('#form').submitForm('<%=request.getContextPath()%>/sys/update/<%=Post.class.getSimpleName().toLowerCase()%>');
    });

    var editable = <c:out value="${entity == null}"/>;

    if (!editable) {
        $('select').attr("readonly","readonly").css("border", "0");
        $('#send, #assign, #remove').attr("disabled","disabled");
    }

    $(document).ready(function (){
        $("#edit").click(function(){
            editable = true;

            $('select').attr("readonly",false).css("border", "1px solid #ccc");
            $('#send, #assign, #remove').attr("disabled", false);
            $("#edit").attr("disabled", "disabled");
        });

        $("#return").click(function(){
            render(getPreUrls());
            returnPage = true;
        });
    });

    document.title = "权限分配";
</script>