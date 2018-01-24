<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.hzg.erp.Supplier" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>供应商<c:choose><c:when test="${entity != null}">修改</c:when><c:otherwise>添加</c:otherwise></c:choose></h3>
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
                        <h2>ERP <small>供应商</small></h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">供应商信息</span>

                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="name">名称 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="name" class="form-control col-md-7 col-xs-12" value="${entity.name}" data-validate-length-range="2,30" data-validate-words="1" name="name"  required type="text">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="text1">主要供货类型</label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input type="text" id="text1" name="text1" value="${entity.mainProductType.name}" class="form-control col-md-7 col-xs-12" style="width:40%" placeholder="输入姓名" required />
                                    <input type="hidden" id="mainProductType[id]" name="mainProductType[id]" value="${entity.mainProductType.id}">
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="level">等级 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="level" name="level" class="form-control col-md-7 col-xs-12" required>
                                        <option value="A">A级  90－100分  请保持</option>
                                        <option value="B">B级  80－ 89分  正常抽样，请努力</option>
                                        <option value="C">C级  70－ 79分  加严抽样，请改善</option>
                                        <option value="D">D级  70分以下  列入考察</option>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="charger" class="control-label col-md-3">负责人 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="charger"  name="charger" class="form-control col-md-7 col-xs-12" type="text" value="${entity.charger}"data-validate-words="1" data-validate-length="2,20" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="address" class="control-label col-md-3">地址 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="address" class="form-control col-md-7 col-xs-12" type="text" value="${entity.address}" name="address" data-validate-words="1" data-validate-length="2,60" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="phone" class="control-label col-md-3">电话 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="phone" name="phone" class="form-control col-md-7 col-xs-12" type="text" value="${entity.phone}" data-validate-words="1" data-validate-length="5,16" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label class="control-label col-md-3 col-sm-3 col-xs-12"  for="cooperateDate">合作日期 <span class="required">*</span>
                                </label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="input-prepend input-group" style="margin-bottom:0">
                                        <span class="add-on input-group-addon"><i class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
                                        <input type="text" name="cooperateDate" id="cooperateDate" class="form-control" value="${entity.cooperateDate}">
                                    </div>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="account" class="control-label col-md-3">付款账号 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="account" name="account" class="form-control col-md-7 col-xs-12" type="text" value="${entity.account}" data-validate-words="1" data-validate-length="15,20" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="branch" class="control-label col-md-3">开户行 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="branch" name="branch" class="form-control col-md-7 col-xs-12" type="text" value="${entity.branch}" data-validate-words="1" data-validate-length="4,30" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="bank" class="control-label col-md-3">银行 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <input id="bank" name="bank" class="form-control col-md-7 col-xs-12" type="text" value="${entity.bank}" data-validate-words="1" data-validate-length="4,30" required>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="payTypes[]" class="control-label col-md-3">结款方式 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="payTypes[]" name="payTypes[]:number" class="form-control col-md-7 col-xs-12" multiple required>
                                        <option value="0">现金</option>
                                        <option value="1">转账</option>
                                        <option value="2">支付宝</option>
                                        <option value="3">微信</option>
                                        <option value="4">银联</option>
                                        <option value="5">支票</option>
                                        <option value="6">其他</option>
                                    </select>
                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="types[]" class="control-label col-md-3">商家类型 <span class="required">*</span></label>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <select id="types[]" name="types[]:number" class="form-control col-md-7 col-xs-12" multiple>
                                        <option value="0">供货商</option>
                                        <option value="1">加工商</option>
                                    </select>
                                </div>
                            </div>
                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">取消</button>
                                    <c:if test="${entity == null}">
                                        <button id="send" type="button" class="btn btn-success">保存</button>
                                    </c:if>
                                    <c:if test="${entity != null}">
                                        <c:choose>
                                            <c:when test="${entity.state == 0}">
                                                <button id="send" type="button" class="btn btn-success">修改</button>
                                                <button id="edit" type="button" class="btn btn-primary">编辑</button>
                                                <button id="delete" type="button" class="btn btn-danger">注销</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button id="editState" type="button" class="btn btn-primary">编辑</button>
                                                <button id="recover" type="button" class="btn btn-success">置为可用</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </div>
                            </div>
                            <c:if test="${entity != null}"><input type="hidden" id="id" name="id" value="${entity.id}"></c:if>
                            <input type="hidden" id="inputer[id]" name="inputer[id]" value="${userId}">
                            <input type="hidden" id="state" name="state:number" value="<c:choose><c:when test="${entity != null}">${entity.state}</c:when><c:otherwise>0</c:otherwise></c:choose>">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /page content -->
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    <c:if test="${entity != null}">
    setMutilSelect(document.getElementById("payTypes[]"), ${entity.payTypesStr});
    setMutilSelect(document.getElementById("types[]"), ${entity.typesStr});
    </c:if>

    $('#cooperateDate').daterangepicker({
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

    $("#account").accountInput();

    $("#send").click(function(){$('#form').submitForm('<%=request.getContextPath()%>/erp/<c:choose><c:when test="${entity != null}">update</c:when><c:otherwise>save</c:otherwise></c:choose>/<%=Supplier.class.getSimpleName().toLowerCase()%>');});

    $("#delete").click(function(){
        if (confirm("确定作废该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Supplier.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":2}');
        }
    });

    $("#recover").click(function(){
        if (confirm("确定恢复该采购单吗？")) {
            $("#form").sendData('<%=request.getContextPath()%>/erp/update/<%=Supplier.class.getSimpleName().toLowerCase()%>',
                '{"id":${entity.id},"state":0}');
        }
    });

    $("#text1").coolautosuggest({
        url:"<%=request.getContextPath()%>/erp/suggest/productType/name/",
        showProperty: 'name',
        onSelected:function(result){
            if(result!=null){
                $(document.getElementById("mainProductType[id]")).val(result.id);
            }
        }
    });

    <c:choose><c:when test="${entity != null}">document.title = "供货商修改";</c:when><c:otherwise> document.title = "供货商添加";</c:otherwise></c:choose>
</script>