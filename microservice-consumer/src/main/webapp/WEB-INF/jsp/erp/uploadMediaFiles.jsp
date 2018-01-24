<%@ page import="com.hzg.sys.Company" %>
<%@ page import="com.hzg.erp.Warehouse" %>
<%@ page import="com.hzg.tools.FileServerInfo" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- page content -->
<div class="right_col" role="main">
    <div class="">
        <div class="page-title">
            <div class="title_left">
                <h3>上传商品多媒体文件</h3>
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
                        <h2>ERP <small>上传商品多媒体文件</small></h2>
                        <div class="clearfix"></div>
                    </div>

                    <div class="x_content">
                        <form class="form-horizontal form-label-left" novalidate id="form">
                            <span class="section">上传商品多媒体文件</span>
                            <div id="iframeContainer" class="item form-group" style="width: 50%;height: 860px;"></div>

                            <div class="ln_solid"></div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-3">
                                    <button id="cancel" type="button" class="btn btn-primary">返回</button>
                                </div>
                            </div>
                            <input type="hidden" id="sessionId" name="sessionId" value="${sessionId}" />
                            <input type="hidden" id="userId" name="userId" value="${userId}" />
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    init(<c:out value="${entity == null}"/>);

    var iframe = document.createElement("iframe");
    iframe.src = "<%=FileServerInfo.uploadDirOrFilesUrl%>?" + Math.random();
    iframe.id = "uploadFrame";
    iframe.style.width = '100%';
    iframe.style.height = '100%';
    iframe.style.border = '0px';

    if (iframe.attachEvent){
        iframe.attachEvent("onload", function(){ // IE
            document.getElementById('uploadFrame').contentWindow.postMessage(JSON.stringify({userId:"${userId}", sessionId:"${sessionId}"}), '*');
        });
    } else {
        iframe.onload = function(){ // 非IE
            document.getElementById('uploadFrame').contentWindow.postMessage(JSON.stringify({userId:"${userId}", sessionId:"${sessionId}"}), '*');
        };
    }

    document.getElementById("iframeContainer").appendChild(iframe);

</script>