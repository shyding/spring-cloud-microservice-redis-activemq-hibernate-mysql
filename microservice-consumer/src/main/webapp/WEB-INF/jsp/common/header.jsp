<%@ page import="com.hzg.sys.User" %>
<%@ page import="com.hzg.sys.Audit" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="col-md-3 left_col">
    <div class="left_col scroll-view">
        <div class="navbar nav_title" style="border: 0;">
            <a href="index.html" class="site_title"><i class="fa fa-paw"></i> <span>Gentelella Alela!</span></a>
        </div>

        <div class="clearfix"></div>

        <!-- menu profile quick info -->
        <div class="profile clearfix">
            <div class="profile_pic">
                <img src="../../../res/gentelella/production/images/img.jpg" alt="..." class="img-circle profile_img">
            </div>
            <div class="profile_info">
                <span>欢迎,</span>
                <h2>${username}</h2>
            </div>
        </div>
        <!-- /menu profile quick info -->

        <br />

        <!-- sidebar menu -->
        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
            <div class="menu_section">
                <h3>General</h3>
                <ul class="nav side-menu">
                    <li><a href="#/sys/list/<%=Audit.class.getSimpleName().toLowerCase()%>/%7B%22state%22:%221%22%7D" onclick="render('/sys/list/<%=Audit.class.getSimpleName().toLowerCase()%>/%7B%22state%22:%221%22%7D');"><i class="fa fa-home"></i> 待办事宜 <span class="fa fa-chevron-right"></span></a></li>

                    <c:if test="${resources != null}">
                    <c:if test="${fn:contains(resources, '/sys/')}">
                    <li><a><i class="fa fa-edit"></i> 后台 <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="#/sys/list/<%=User.class.getSimpleName().toLowerCase()%>/{}">管理</a></li>
                        <c:if test="${fn:contains(resources, '/sys/save/user')}">
                            <li><a href="#/sys/view/user/-1">注册用户</a></li>
                        </c:if>
                        <c:if test="${fn:contains(resources, '/sys/save/auditFlow')}">
                            <li><a href="#/sys/view/auditFlow/-1">添加流程</a></li>
                        </c:if>
                        </ul>
                    </li>
                    </c:if>

                    <c:if test="${fn:contains(resources, '/erp/')}">
                    <li><a><i class="fa fa-edit"></i> 商品 <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="#/product/list/product/{}">管理</a></li>
                        <c:if test="${fn:contains(resources, '/erp/save/product')}">
                            <li><a href="#/sys/view/user/26">录入商品</a></li>
                        </c:if>
                        <c:if test="${fn:contains(resources, '/erp/save/purchase')}">
                            <li><a href="#/sys/view/user/26">采购</a></li>
                        </c:if>
                        <c:if test="${fn:contains(resources, '/erp/save/stockInOut')}">
                            <li><a href="#/sys/view/user/26">入库出库</a></li>
                        </c:if>
                        </ul>
                    </li>
                    </c:if>

                    <c:if test="${fn:contains(resources, '/order/')}">
                    <li><a><i class="fa fa-edit"></i> 销售订单 <span class="fa fa-chevron-down"></span></a>
                        <ul class="nav child_menu">
                            <li><a href="#/sys/list/order">管理</a></li>
                        </ul>
                    </li>
                    </c:if>
                    </c:if>
                </ul>
            </div>

        </div>
        <!-- /sidebar menu -->

        <!-- /menu footer buttons -->
        <div class="sidebar-footer hidden-small">
            <a data-toggle="tooltip" data-placement="top" title="Settings">
                <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
            </a>
            <a data-toggle="tooltip" data-placement="top" title="FullScreen">
                <span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span>
            </a>
            <a data-toggle="tooltip" data-placement="top" title="Lock">
                <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
            </a>
            <a data-toggle="tooltip" data-placement="top" title="Logout" href="login.html">
                <span class="glyphicon glyphicon-off" aria-hidden="true"></span>
            </a>
        </div>
        <!-- /menu footer buttons -->
    </div>
</div>

<!-- top navigation -->
<div class="top_nav">
    <div class="nav_menu">
        <nav>
            <div class="nav toggle">
                <a id="menu_toggle"><i class="fa fa-bars"></i></a>
            </div>

            <ul class="nav navbar-nav navbar-right">
                <li class="">
                    <a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                        <img src="../../../res/gentelella/production/images/img.jpg" alt="">${username}
                        <span class=" fa fa-angle-down"></span>
                    </a>
                    <ul class="dropdown-menu dropdown-usermenu pull-right">
                        <li><a href="javascript:;"> Profile</a></li>
                        <li>
                            <a href="javascript:;">
                                <span class="badge bg-red pull-right">50%</span>
                                <span>Settings</span>
                            </a>
                        </li>
                        <li><a href="javascript:;">Help</a></li>
                        <li><a href="#" id="signOut"><i class="fa fa-sign-out pull-right"></i> 注销 </a></li>
                        <form action="<%=request.getContextPath()%>/sys/user/signOut" id="signOutForm" method="post">
                            <input type="hidden" id="json" name="json" value='{"sessionId":"<%=session.getId()%>"}' />
                        </form>
                    </ul>
                </li>

                <%--system notifitaion--%>
                <li role="presentation" class="dropdown">
                    <a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown" aria-expanded="false">
                        <i class="fa fa-envelope-o"></i>
                        <span class="badge bg-green">1</span>
                    </a>
                    <ul id="menu1" class="dropdown-menu list-unstyled msg_list" role="menu">
                        <li>
                            <a>
                                <span class="image"><img src="../../../res/gentelella/production/images/img.jpg" alt="Profile Image" /></span>
                                <span>
                                  <span>John Smith</span>
                                  <span class="time">3 mins ago</span>
                                </span>
                                <span class="message">
                                  Film festivals used to be do-or-die moments for movie makers. They were where...
                                </span>
                            </a>
                        </li>

                        <li>
                            <div class="text-center">
                                <a>
                                    <strong>See All Alerts</strong>
                                    <i class="fa fa-angle-right"></i>
                                </a>
                            </div>
                        </li>
                    </ul>
                </li>

            </ul>
        </nav>
    </div>
</div>
<!-- /top navigation -->