<%@ page import="com.hzg.sys.Company" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>公司<c:choose><c:when test="${company != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></title>

    <!-- Bootstrap -->
    <link href="../../../res/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../../../res/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../../../res/gentelella/vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="../../../res/gentelella/build/css/custom.min.css" rel="stylesheet">
    <%--jquery auto complete--%>
    <link rel="stylesheet" type="text/css" href="../../../res/css/jquery.coolautosuggest.css" />
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
                        <h3>公司<c:choose><c:when test="${company != null}">修改</c:when><c:otherwise>注册</c:otherwise></c:choose></h3>
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
                                <h2>后台管理 <small>公司</small></h2>
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
                                    <span class="section">公司信息</span>

                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">公司名称 <span class="required">*</span>
                                        </label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="name" class="form-control col-md-7 col-xs-12" value="${company.name}" data-validate-length-range="6,30" data-validate-words="1" name="name"  required type="text">
                                        </div>
                                    </div>
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="phone">联系电话 <span class="required">*</span>
                                        </label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="phone" class="form-control col-md-7 col-xs-12" value="${company.phone}" data-validate-length-range="5,16" data-validate-words="1" name="phone" required type="text">
                                        </div>
                                    </div>
                                    <div class="item form-group">
                                        <label for="address" class="control-label col-md-3">地址 <span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input id="address" class="form-control col-md-7 col-xs-12" type="text" value="${company.address}" name="address" data-validate-words="1" data-validate-length="6,60" required>
                                        </div>
                                    </div>
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">负责人</label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input type="text" id="text1" name="text1" <c:if test="${company.charger != null}">value="${company.charger.name}"</c:if> class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" />
                                            <input type="hidden" id="charger[id]" name="charger[id]" <c:if test="${company.charger != null}">value="${company.charger.id}"</c:if>>
                                        </div>
                                    </div>
                                    <div class="ln_solid"></div>
                                    <div class="form-group">
                                        <div class="col-md-6 col-md-offset-3">
                                            <button type="submit" class="btn btn-primary">取消</button>
                                            <button id="send" type="button" class="btn btn-success"><c:choose><c:when test="${company != null}">更新</c:when><c:otherwise>保存</c:otherwise></c:choose></button>
                                        </div>
                                    </div>
                                    <c:if test="${company != null}"><input type="hidden" id="id" name="id" value="${company.id}"></c:if>
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
<%--jquery suggest--%>
<script language="javascript" type="text/javascript" src="../../../res/js/jquery.coolautosuggest.js"></script>
<!-- form submit -->
<script src="../../../res/js/jquery.serializejson.js"></script>
<script src="../../../res/js/submitForm.js"></script>
<script type="text/javascript">
    $('#form').preventEnterSubmit();
    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/sys/<c:choose><c:when test="${company != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Company.class.getSimpleName().toLowerCase()%>');});
    $(document).keydown(function(event){
        if(event.keyCode == 13){ //绑定回车
            $('#send').click();
        }
    });

    $("#text1").coolautosuggest({
        url:"<%=request.getContextPath()%>/sys/suggest/user/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("charger[id]")).val(result.id);
            }
        }
    });
</script>
</body>
</html>