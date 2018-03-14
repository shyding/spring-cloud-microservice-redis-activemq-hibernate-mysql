<%@ page import="com.hzg.sys.User" %>
<%@ page import="com.hzg.sys.Audit" %>
<%@ page import="com.hzg.erp.Product" %>
<%@ page import="com.hzg.pay.Pay" %>
<%@ page import="com.hzg.finance.Voucher" %>
<%@ page import="com.hzg.finance.Subject" %>
<%@ page import="com.hzg.pay.Refund" %>
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
                    <li><a id="auditA" href="#/sys/list/<%=Audit.class.getSimpleName().toLowerCase()%>/%7B%22state%22:%220%22%7D" onclick="render('/sys/list/<%=Audit.class.getSimpleName().toLowerCase()%>/%7B%22state%22:%220%22%7D');"><i class="fa fa-home"></i> 待办事宜 <span class="fa fa-chevron-right"></span></a></li>

                    <c:if test="${resources != null}">
                        <c:if test="${fn:contains(resources, '/erp/')}">
                            <li><a><i class="fa fa-edit"></i> ERP <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/erp/list/<%=Product.class.getSimpleName().toLowerCase()%>/%7B%22product%22:%7B%7D%7D">管理</a></li>
                                    <c:if test="${fn:contains(resources, '/erp/save/purchase')}">
                                        <li><a href="#/erp/view/purchase/-1">录入采购单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/save/stockInOut')}">
                                        <li><a href="#/erp/view/stockInOut/-1">入库出库</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/business/uploadMediaFiles')}">
                                        <li><a href="#/erp/business/uploadMediaFiles">上传商品多媒体文件</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/update/productDescribe')}">
                                        <li><a href="#/erp/view/productDescribe/-1">商品编辑</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/doBusiness/upShelfProduct')}">
                                        <li><a href="#/erp/view/productUpDownShelf/-1">商品上架下架</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/save/productPriceChange')}">
                                        <li><a href="#/erp/view/productPriceChange/-1">商品调价</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/erp/save/productCheck')}">
                                        <li><a href="#/erp/view/productCheckInput/-1">商品盘点</a></li>
                                    </c:if>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/orderManagement/')}">
                            <li><a><i class="fa fa-edit"></i> 销售订单 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/orderManagement/list/order/{}">管理</a></li>
                                    <c:if test="${fn:contains(resources, '/orderManagement/business/assistBook')}">
                                        <li><a href="#/orderManagement/business/assistBook">代下单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/orderManagement/list/orderPrivate')}">
                                        <li><a href="#/orderManagement/list/orderPrivate/{}">加工费、私人订制费用核定</a></li>
                                    </c:if>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/afterSaleService/')}">
                            <li><a><i class="fa fa-edit"></i> 售后服务 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/afterSaleService/list/returnProduct/{}">管理</a></li>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductSaleAudit')}">
                                        <li><a href="#/afterSaleService/list/returnProduct/%7B%22state%22:0%7D">销售审核退货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductDirectorAudit')}">
                                        <li><a href="#/afterSaleService/list/returnProduct/%7B%22state%22:3%7D">销售总监审核退货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductWarehousingAudit')}">
                                        <li><a href="#/afterSaleService/list/returnProduct/%7B%22state%22:%22%20in%20(4,7)%22%7D">仓储审核退货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/purchaseReturnProductSupplierReceived')}">
                                        <li><a href="#/afterSaleService/list/returnProduct/%7B%22state%22:8%7D">供应商确认收货</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/returnProductRefund')}">
                                        <li><a href="#/afterSaleService/list/returnProduct/%7B%22state%22:%22%20in%20(5,9)%22%7D">财务审核退货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductSaleAudit')}">
                                        <li><a href="#/afterSaleService/list/changeProduct/%7B%22state%22:0%7D">销售审核换货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductDirectorAudit')}">
                                        <li><a href="#/afterSaleService/list/changeProduct/%7B%22state%22:3%7D">销售总监审核换货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductWarehousingAudit')}">
                                        <li><a href="#/afterSaleService/list/changeProduct/%7B%22state%22:4%7D">仓储审核换货单</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/afterSaleService/doBusiness/changeProductComplete')}">
                                        <li><a href="#/afterSaleService/list/changeProduct/%7B%22state%22:5%7D">财务审核换货单</a></li>
                                    </c:if>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/customerManagement/')}">
                            <li><a><i class="fa fa-edit"></i> 客户服务 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/customerManagement/list/customer/{}">管理</a></li>
                                    <c:if test="${fn:contains(resources, '/customerManagement/save/customer')}">
                                        <li><a href="#/customerManagement/view/customer/-1">添加客户</a></li>
                                    </c:if>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/pay/')}">
                            <li><a><i class="fa fa-edit"></i> 支付 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/pay/list/<%=Pay.class.getSimpleName().toLowerCase()%>/{}">管理</a></li>
                                    <c:if test="${fn:contains(resources, '/refund')}">
                                        <li><a href="#/pay/list/<%=Refund.class.getSimpleName().toLowerCase()%>/{}">退款记录</a></li>
                                    </c:if>
                                    <c:if test="${fn:contains(resources, '/pay/save/account')}">
                                        <li><a href="#/pay/view/account/-1">添加账户</a></li>
                                    </c:if>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/finance/')}">
                            <li><a><i class="fa fa-edit"></i> 财务 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a><i class="fa fa-edit"></i>凭证<span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li><a href="#/finance/list/<%=Voucher.class.getSimpleName().toLowerCase()%>/%7b%22voucher%22%3a%7b%7d%2c%22chartMaker%22%3a%7b%22name%22%3a%22%22%7d%7d">凭证管理</a></li>
                                            <c:if test="${fn:contains(resources, '/finance/save/voucher')}">
                                                <li><a href="#/finance/view/voucher/-1">填制凭证</a></li>
                                            </c:if>
                                            <c:if test="${fn:contains(resources, '/finance/save/voucher')}">
                                                <li><a href="#/finance/view/createVoucher/-1">生成凭证</a></li>
                                            </c:if>
                                        </ul>
                                    </li>
                                    <li><a><i class="fa fa-edit"></i>科目<span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                                <li><a href="#/finance/list/<%=Subject.class.getSimpleName().toLowerCase()%>/{}">科目管理</a></li>
                                            <c:if test="${fn:contains(resources, '/finance/save/subject')}">
                                                <li><a href="#/finance/view/subject/-1">新增科目</a></li>
                                            </c:if>
                                            <c:if test="${fn:contains(resources, '/finance/save/subject')}">
                                                <li><a href="#/finance/view/subjectSet/-1">科目设置</a></li>
                                            </c:if>
                                        </ul>
                                    </li>
                                    <li><a href="#level1_3">报表</a></li>
                                </ul>
                            </li>
                        </c:if>

                        <c:if test="${fn:contains(resources, '/sys/save')}">
                            <li><a><i class="fa fa-edit"></i> 后台 <span class="fa fa-chevron-down"></span></a>
                                <ul class="nav child_menu">
                                    <li><a href="#/sys/list/<%=User.class.getSimpleName().toLowerCase()%>/{}">管理</a></li>
                                <c:if test="${fn:contains(resources, '/sys/save/user')}">
                                    <li><a href="#/sys/view/user/-1">注册用户</a></li>
                                </c:if>
                                <c:if test="${fn:contains(resources, '/sys/save/auditFlow')}">
                                    <li><a href="#/sys/view/auditFlow/-1">添加流程</a></li>
                                </c:if>
                                <c:if test="${fn:contains(resources, '/sys/save/article')}">
                                    <li><a href="#/sys/view/article/-1">添加文章</a></li>
                                </c:if>
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
                            <input type="hidden" id="json" name="json" value='{"sessionId":"${sessionId}"}' />
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