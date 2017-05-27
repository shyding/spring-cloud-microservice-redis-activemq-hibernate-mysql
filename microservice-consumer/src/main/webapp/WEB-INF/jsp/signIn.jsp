<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Gentelella Alela! | </title>

    <!-- Bootstrap -->
    <link href="../../res/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../../res/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../../res/gentelella/vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- Animate.css -->
    <link href="../../res/gentelella/vendors/animate.css/animate.min.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="../../res/gentelella/build/css/custom.min.css" rel="stylesheet">
</head>

<body class="login">
<div>
    <a class="hiddenanchor" id="signup"></a>
    <a class="hiddenanchor" id="signin"></a>

    <div class="login_wrapper">
        <div class="animate form login_form">
            <section class="login_content">
                <form id="form">
                    <h1>后台用户登录</h1>
                    <c:if test="${result !=null}">
                        <div>${result}</div>
                        <c:if test="${fn:contains(result, '已经登录')}">
                            <div><a id="againSignIn" href="#">注销已登录用户</a></div>
                            <input type="hidden" name="dealType" value="againSignIn" />
                        </c:if>
                    </c:if>
                    <div>
                        <input type="text" id="username" name="username" class="form-control" value="" placeholder="用户名" required>
                    </div>
                    <div>
                        <input type="password" id="password" name="password" class="form-control" value="" placeholder="密码" required>
                    </div>
                    <div>
                        <a class="btn btn-default submit" id="send" href="#">登录</a>
                        <a class="reset_pass" href="#">忘记密码?</a>
                    </div>

                    <div class="clearfix"></div>

                    <div class="separator">
                        <p class="change_link">打开新页面?
                            <a href="#signup" class="to_register"> 创建一个账号 </a>
                        </p>

                        <div class="clearfix"></div>
                        <br />

                        <div>
                            <h1><i class="fa fa-paw"></i> Gentelella Alela!</h1>
                            <p>©2016 All Rights Reserved. Gentelella Alela! is a Bootstrap 3 template. Privacy and Terms</p>
                        </div>
                    </div>
                    <input type="hidden" id="encryptPassword" name="encryptPassword" />
                    <input type="hidden" name="sessionId" value="<%=session.getId()%>" />
                </form>
                <form action="<%=request.getContextPath()%>/sys/user/signIn" id="signInForm" method="post">
                    <input type="hidden" id="json" name="json" />
                </form>
            </section>
        </div>

        <div id="register" class="animate form registration_form">
            <section class="login_content">
                <form>
                    <h1>Create Account</h1>
                    <div>
                        <input type="text" class="form-control" placeholder="Username" required />
                    </div>
                    <div>
                        <input type="email" class="form-control" placeholder="Email" required />
                    </div>
                    <div>
                        <input type="password" class="form-control" placeholder="Password" required />
                    </div>
                    <div>
                        <a class="btn btn-default submit" href="index.html">Submit</a>
                    </div>

                    <div class="clearfix"></div>

                    <div class="separator">
                        <p class="change_link">Already a member ?
                            <a href="#signin" class="to_register"> 登录 </a>
                        </p>

                        <div class="clearfix"></div>
                        <br />

                        <div>
                            <h1><i class="fa fa-paw"></i> Gentelella Alela!</h1>
                            <p>©2016 All Rights Reserved. Gentelella Alela! is a Bootstrap 3 template. Privacy and Terms</p>
                        </div>
                    </div>
                </form>
            </section>
        </div>
    </div>
</div>
</body>
<script src="../../res/gentelella/vendors/jquery/dist/jquery.min.js"></script>
<script src="../../res/js/jquery.cookie.js"></script>
<!-- form submit -->
<script src="../../res/js/jquery.serializejson.js"></script>
<script src="../../res/js/submitForm.js"></script>
<script src="../../res/js/md5.js"></script>
<script type="text/javascript">
    $("#username").val("");
    $("#password").val("");
    $("#send").click(function(){
        if($.trim($("#username").val()) == "") {
            alert("请输入用户名");
            return false;
        }
        if($.trim($("#password").val()) == "") {
            alert("请输入密码");
            return false;
        }

        /**
         * pin 的值不能和在网络上传输的加密的密码值一样
         */
        $.cookie("pin", faultylabs.MD5(${salt}+faultylabs.MD5(jQuery.trim($("#password").val()))), {path:'/'});

        $("#encryptPassword").val(faultylabs.MD5(faultylabs.MD5(jQuery.trim($("#password").val()))+${salt}));
        $("#password").attr("disabled","disabled");

        $("#json").val(JSON.stringify($("#form").serializeJSON()));
        $("#signInForm").submit();
    });

    $(document).keydown(function(event){
        if(event.keyCode == 13 && $.trim($("#username").val()) != "" && $.trim($("#password").val()) != ""){ //绑定回车
            $('#send').click();
        }
    });

    $("#againSignIn").click(function(){
        <c:if test="${result !=null}">
        <c:if test="${fn:contains(result, '已经登录')}">
            $("#username").val("${fn:substringBefore(result, '已经登录')}");
        </c:if>
        </c:if>
        $("#signInForm").attr("action", "<%=request.getContextPath()%>/sys/user/hasLoginDeal");
        $("#json").val(JSON.stringify($("#form").serializeJSON()));
        $("#signInForm").submit();
    });
</script>
</html>
