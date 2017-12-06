<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap -->
    <link href="../../res/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../../res/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../../res/gentelella/vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="../../res/gentelella/build/css/custom.min.css" rel="stylesheet">
    <%--jquery auto complete--%>
    <link type="text/css" href="../../res/css/jquery.coolautosuggest.css" rel="stylesheet">
    <!-- bootstrap-daterangepicker -->
    <link href="../../res/gentelella/vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">
    <%--jquery lightbox--%>
    <link type="text/css" href="../../res/css/lightbox.css" rel="stylesheet">
    <!-- iCheck -->
    <link href="../../res/gentelella/vendors/iCheck/skins/flat/green.css" rel="stylesheet">
    <!-- Datatables -->
    <link href="../../res/gentelella/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="../../res/gentelella/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="../../res/gentelella/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="../../res/gentelella/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="../../res/gentelella/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">
    <!-- custom -->
    <link href="../../res/css/custom.css" rel="stylesheet">
</head>
<body class="nav-md">
<div class="container body">
    <div class="main_container">
        <!-- print content -->
        <div>${printContent}</div>
    </div>
</div>
</body>
<script type="text/javascript">
    window.onload = function () {
        setTimeout(function(){
            window.print(); // blocking - so close will not
            //   window.close(); // execute until this is done
        }, 250);
    };
</script>
</html>
