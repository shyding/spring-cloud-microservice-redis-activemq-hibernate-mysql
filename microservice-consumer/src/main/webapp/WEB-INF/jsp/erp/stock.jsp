<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--jquery ui--%>
<link type="text/css" href="../../../res/css/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>查看库存</h3>
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
                        <h2>库存 <small>信息</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                        <span class="section">库存信息</span>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">库存单号 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.no}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">商品名称 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.product.name}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">商品编号 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.product.no}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">商品数量 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.quantity}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">商品单位 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.unit}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">入库时间 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.date}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="item form-group">
                            <label class="control-label col-md-3 col-sm-3 col-xs-12">仓库 <span class="required">*</span></label>
                            <div class="col-md-6 col-sm-6 col-xs-12"><input type="text" value="${entity.warehouse.name}" class="form-control col-md-7 col-xs-12" readonly></div>
                        </div>
                        <div class="ln_solid"></div>
                        <div class="form-group">
                            <div class="col-md-6 col-md-offset-3">
                                <button id="cancel" type="button" class="btn btn-primary">取消</button>
                            </div>
                        </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);
</script>