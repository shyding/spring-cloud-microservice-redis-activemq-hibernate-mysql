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

    <title>列表</title>

    <!-- Bootstrap -->
    <link href="../../../res/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../../../res/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../../../res/gentelella/vendors/nprogress/nprogress.css" rel="stylesheet">
    <%--jquery auto complete--%>
    <link type="text/css" href="../../../res/css/jquery.coolautosuggest.css" rel="stylesheet">
    <!-- bootstrap-daterangepicker -->
    <link href="../../../res/gentelella/vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">
    <!-- iCheck -->
    <link href="../../../res/gentelella/vendors/iCheck/skins/flat/green.css" rel="stylesheet">
    <!-- Datatables -->
    <link href="../../../res/gentelella/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="../../../res/gentelella/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="../../../res/gentelella/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="../../../res/gentelella/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="../../../res/gentelella/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="../../../res/gentelella/build/css/custom.min.css" rel="stylesheet">
    <style type="text/css">
        table {
           font-size: 12px;
        }
        .daterangepicker .calendar th, .daterangepicker .calendar td{
            min-width: 22px;
        }
    </style>
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
                        <h3 id="htitle">列表</h3>
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
                                <h2>查询 <small id="stitle"></small></h2>
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
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="entity">类别<span class="required">*</span></label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <select id="entity" name="entity" class="form-control col-md-7 col-xs-12" required>
                                                <option value="">请选择类别</option>
                                                <option value="product">商品</option>
                                                <option value="stock">库存</option>
                                                <option value="purchase">采购</option>
                                                <option value="order">订单</option>
                                                <option value="pay">退货</option>
                                                <option value="pay">支付</option>
                                                <option value="customer">客户</option>
                                                <option value="user">后台用户</option>
                                                <option value="privilege">权限</option>
                                                <option value="post">岗位</option>
                                                <option value="dept">部门</option>
                                                <option value="company">公司</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div id="dateItems">
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="inputDate" id="timeLabel">时间</label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <div class="input-prepend input-group" style="margin-bottom:0">
                                                <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                                <input type="text" name="inputDate" id="inputDate" class="form-control" value="" />
                                            </div>
                                        </div>
                                    </div>
                                    </div>
                                    <div id="inputItems">
                                    <div class="item form-group">
                                        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">名称</label>
                                        <div class="col-md-6 col-sm-6 col-xs-12">
                                            <input type="text" id="name" name="name" class="form-control col-md-7 col-xs-12" placeholder="输入名称" />
                                        </div>
                                    </div>
                                    </div>
                                    <div class="item form-group">
                                        <div class="col-md-6 col-md-offset-3">
                                            <button id="send" type="button" class="btn btn-success">查询</button>
                                        </div>
                                    </div>
                                </form>
                                <div class="ln_solid"></div>
                            </div>
                            <div class="x_content">
                                <table id="dataList" class="table table-striped table-bordered jambo_table bulk_action"></table>
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
<!-- bootstrap-daterangepicker -->
<script src="../../../res/gentelella/vendors/moment/min/moment.min.js"></script>
<script src="../../../res/gentelella/vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
<!-- iCheck -->
<script src="../../../res/gentelella/vendors/iCheck/icheck.min.js"></script>
<!-- Datatables -->
<script src="../../../res/gentelella/vendors/datatables.net/js/jquery.dataTables.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-buttons-bs/js/buttons.bootstrap.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-buttons/js/buttons.flash.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-buttons/js/buttons.html5.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-buttons/js/buttons.print.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-fixedheader/js/dataTables.fixedHeader.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-keytable/js/dataTables.keyTable.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-responsive-bs/js/responsive.bootstrap.js"></script>
<script src="../../../res/gentelella/vendors/datatables.net-scroller/js/dataTables.scroller.min.js"></script>
<script src="../../../res/gentelella/vendors/jszip/dist/jszip.min.js"></script>
<script src="../../../res/gentelella/vendors/pdfmake/build/pdfmake.min.js"></script>
<script src="../../../res/gentelella/vendors/pdfmake/build/vfs_fonts.js"></script>
<!-- Custom Theme Scripts -->
<script src="../../../res/js/custom.js"></script>
<!-- form submit -->
<script src="../../../res/js/jquery.serializejson.js"></script>
<script src="../../../res/js/submitForm.js"></script>
<script src="../../../res/js/dataList.js"></script>
<script type="text/javascript">
    $('#form').preventEnterSubmit();
    $('#inputDate').daterangepicker({locale: {format: 'YYYY/MM/DD'}}, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });
    $("#entity").change(function(){dataList.setQuery();});
    $("#send").click(function(){dataList.query("<%=request.getContextPath()%>");});
    $(document).keydown(function(event){
        if(event.keyCode == 13){ //绑定回车
            $('#send').click();
        }
    });
</script>
</body>
</html>
