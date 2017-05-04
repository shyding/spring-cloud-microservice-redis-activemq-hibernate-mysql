<%@ page import="com.hzg.sys.User" %>
<%@ page import="com.hzg.sys.Company" %>
<%@ page import="com.hzg.sys.Dept" %>
<%@ page import="com.hzg.sys.Post" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>后台用户<c:choose><c:when test="${user != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></title>

    <!-- Bootstrap -->
    <link href="../../../res/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../../../res/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../../../res/gentelella/vendors/nprogress/nprogress.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="../../../res/gentelella/build/css/custom.min.css" rel="stylesheet">
</head>
<body class="nav-md">
<div class="container body">
    <div class="main_container">
        <%@ include file="../common/header.jsp"%>

        <!-- page content -->
        <div class="right_col" role="main">
            <div class="">
                <div class="page-title">
                    <div class="title_left">
                        <h3>后台用户<c:choose><c:when test="${user != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></h3>
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
                                <h2>后台管理 <small>用户</small></h2>
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
                                    <span class="section">用户信息</span>

                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">姓名 <span class="required">*</span>
                                        </label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="name" class="form-control col-md-7 col-xs-12" value="${user.name}" data-validate-length-range="6，20" data-validate-words="1" name="name" required type="text">
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
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="posts[][id]">岗位 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <select id="posts[][id]" name="posts[][id]" class="form-control col-md-7 col-xs-12" required>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="item form-group">
                                        <label for="username" class="control-label col-md-3">用户名 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="username" type="text" name="username" value="${user.username}" data-validate-length="6,20" class="form-control col-md-7 col-xs-12" required>
                                        </div>
                                    </div>
                                    <c:if test="${user == null}">
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
                                    <input type="hidden" id="password" name="password">
                                    </c:if>
                                    <c:if test="${user != null}">
                                    <div class="item form-group">
                                        <label for="password1" class="control-label col-md-3">密码 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="password3" type="password" name="password3" value="*********" data-validate-length="6,32" class="form-control col-md-7 col-xs-12" disabled readonly>
                                            <a>修改密码</a>
                                        </div>
                                    </div>
                                    </c:if>
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="gender">性别 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <div id="gender" class="btn-group" data-toggle="buttons">
                                                <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                    <input type="radio" name="gender" value="male" <c:if test="${user != null && user.gender == 'male'}">checked</c:if>> &nbsp;男&nbsp;
                                                </label>
                                                <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                    <input type="radio" name="gender" value="female" <c:if test="${user != null && user.gender == 'female'}">checked</c:if>> &nbsp;女&nbsp;
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="email">Email <span class="required">*</span>
                                        </label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input type="email" id="email" name="email" value="${user.email}" data-validate-length="6,30" required class="form-control col-md-7 col-xs-12">
                                        </div>
                                    </div>
                                    <div class="ln_solid"></div>
                                    <div class="form-group">
                                        <div class="col-md-6 col-md-offset-3">
                                            <button type="submit" class="btn btn-primary">取消</button>
                                            <button id="send" type="button" class="btn btn-success"><c:choose><c:when test="${user != null}">更新</c:when><c:otherwise>保存</c:otherwise></c:choose></button>
                                        </div>
                                    </div>
                                    <c:if test="${user != null}"><input type="hidden" id="id" name="id" value="${user.id}"></c:if>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /page content -->

        <%@ include file="../common/footer.jsp"%>
    </div>
</div>

<!-- jQuery -->
<script src="../../../res/gentelella/vendors/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap -->
<script src="../../../res/gentelella/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="../../../res/gentelella/vendors/fastclick/lib/fastclick.js"></script>
<!-- NProgress -->
<script src="../../../res/gentelella/vendors/nprogress/nprogress.js"></script>
<!-- validator -->
<script src="../../../res/gentelella/vendors/validator/validator.js"></script>
<!-- Custom Theme Scripts -->
<script src="../../../res/gentelella/build/js/custom.min.js"></script>
<!-- form submit -->
<script src="../../../res/js/jquery.serializejson.js"></script>
<script src="../../../res/js/submitForm.js"></script>
<script src="../../../res/js/setSelect.js"></script>
<script src="../../../res/js/md5.js"></script>
<script type="text/javascript">
    $('#form').preventEnterSubmit();

    $("#send").click(function(){
        <c:if test="${user == null}">
        if ($('#form').isFullSet()) {
            var password1 = $("#password1");
            $("#password").val(faultylabs.MD5(jQuery.trim(password1.val())));
            password1.attr("disabled","disabled");
            $("#password2").attr("disabled","disabled");
        }
        </c:if>

        $('#form').submitForm('<%=request.getContextPath()%>/sys/<c:choose><c:when test="${user != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=User.class.getSimpleName().toLowerCase()%>');
    });
    $(document).keydown(function(event){
        if(event.keyCode == 13){ //绑定回车
            $('#send').click();
        }
    });

    <c:set var="postIds" value="" />
    <c:set var="deptId" value="" />
    <c:if test="${user != null}">
        <c:forEach var="post" items="${user.posts}" varStatus="status">
            <c:set var="postIds" value="${postIds + post.id}" />
            <c:set var="deptId" value="${post.dept.id}" />
        </c:forEach>
    </c:if>

    var companyId = "${companyId}", deptId = "${deptId}", postId = "${postIds}";
    if (postId != "") {
        $.ajax({
            type: "get",
            url: '<%=request.getContextPath()%>/sys/query/<%=Dept.class.getSimpleName().toLowerCase()%>',
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {json: '{"id":"' + deptId + '"}'},
            dataType: "json",

            success: function(items){
                companyId = items[0].company.id;

                selector.setSelect(['company[id]', 'dept[id]', 'posts[][id]'], ['name', 'name', 'name'], ['id', 'id', 'id'],
                    [companyId, deptId, postId],
                    ['<%=request.getContextPath()%>/sys/query/<%=Company.class.getSimpleName().toLowerCase()%>',
                        '<%=request.getContextPath()%>/sys/query/<%=Dept.class.getSimpleName().toLowerCase()%>',
                        '<%=request.getContextPath()%>/sys/query/<%=Post.class.getSimpleName().toLowerCase()%>'],
                    '{}', ['company[id]', 'dept[id]'], 0);
            }
        });
    } else {
        selector.setSelect(['company[id]', 'dept[id]', 'posts[][id]'], ['name', 'name', 'name'], ['id', 'id', 'id'], [],
            ['<%=request.getContextPath()%>/sys/query/<%=Company.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Dept.class.getSimpleName().toLowerCase()%>',
                '<%=request.getContextPath()%>/sys/query/<%=Post.class.getSimpleName().toLowerCase()%>'],
            '{}', ['company[id]', 'dept[id]'], 0);
    }

</script>
</body>
</html>