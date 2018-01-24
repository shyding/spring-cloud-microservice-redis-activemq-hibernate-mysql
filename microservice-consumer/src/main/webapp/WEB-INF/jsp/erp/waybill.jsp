<%  response.setHeader("Access-Control-Allow-Origin", "*"); %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>顺丰打印单</title>
    <link rel="icon" href="">
    <link rel="stylesheet" href="../../../res/css/common.css">
    <link rel="stylesheet" href="../../../res/css/style.css">
</head>
<body>
<c:forEach items="${details}" var="detail">
<div class="page">
    <div class="m1 b">
        <img src="../../../res/img/logo.jpg" class="img1">
        <img src="../../../res/img/tel.png" class="img2">
        <p><span class="s1">E</span><span class="s2"></span></p>
    </div>
    <div class="m2 b">
        <div class="left">
            <img src='data:image/png;base64,${detail.mailNoBarcode}'/>
            <p><span>${detail.qzoneMailNo}</span></p>
        </div>
        <div class="right">
            <div class="r1 b">
                <p>电商特惠</p>
            </div>
            <div class="r2">
            </div>
        </div>
    </div>
    <div class="m3 b">
        <div class="left">
            <p>目的地</p>
        </div>
        <div class="right">
            <p>${detail.dest}</p>
        </div>
    </div>
    <div class="m4 b">
        <div class="left">
            <p>收件人</p>
        </div>
        <div class="right">
            <p>${detail.expressDeliver.receiver} ${detail.expressDeliver.receiverTel} ${detail.expressDeliver.receiverMobile} ${detail.expressDeliver.receiverCompany}</p>
            <p><c:if test="${detail.expressDeliver.receiverProvince != detail.expressDeliver.receiverCity}">${detail.expressDeliver.receiverProvince}</c:if>${detail.expressDeliver.receiverCity}${detail.expressDeliver.receiverAddress}</p>
        </div>
    </div>
    <div class="m5 b">
        <div class="left">
            <p>寄件人</p>
        </div>
        <div class="right-1">
            <p>${detail.expressDeliver.sender} ${detail.expressDeliver.senderTel}</p>
            <p><c:if test="${detail.expressDeliver.senderProvince != detail.expressDeliver.senderCity}">${detail.expressDeliver.senderProvince}</c:if>${detail.expressDeliver.senderCity}${detail.expressDeliver.senderAddress}</p>
        </div>
        <div class="right-2">
            <p class="p1"></p>
            <p class="p2"></p>
        </div>
    </div>
    <div class="m6 b cl">
        <div class="left">
            <div class="l1 b">
                <ul>
                    <li>付款方式：${detail.payType}</li>
                    <li>月结账号：${detail.custId}</li>
                    <li>第三方地区：</li>
                    <li>实际重量：${detail.totalWeight}<c:if test="${detail.totalWeight != null}">KG</c:if></li>
                </ul>
                <ul>
                    <li>计费重量：</li>
                    <li>声明价值：</li>
                    <li>保价费用：<c:if test="${detail.insure != null}">${detail.insure}元</c:if></li>
                    <li>定时派送：</li>
                </ul>
                <ul>
                    <li>包装费用：</li>
                    <li>运费：</li>
                    <li>费用合计：</li>
                </ul>
                <p></p>
            </div>
            <div class="l2">
                <div class="l2-a">
                    <p>托寄物</p>
                </div>
                <%--<div class="l2-b">${detail.productNo} ${detail.quantity} ${detail.unit}</div>--%>
                <div class="l2-b">贵重物品</div>
                <div class="l2-c">
                    <ul>
                        <li>收件员：</li>
                        <li>收件日期：${detail.expressDeliver.cnDate}</li>
                        <li>派件员：</li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="right">
            <p>签名：</p>
            <p class="p2">月<span>日</span></p>
        </div>
    </div>
    <div class="item b item-last">
        <div class="m7 b cl">
            <div class="left">
                <img class="img1" src="../../../res/img/logo.jpg">
                <img src="../../../res/img/tel.png">
            </div>
            <div class="right">
                <img src='data:image/png;base64,${detail.mailNoBarcode}'/>
                <p><span>${detail.qzoneMailNo}</span></p>
            </div>
        </div>
        <div class="m8 b">
            <div class="left">
                <p>寄件人</p>
            </div>
            <div class="right">
                <p>${detail.expressDeliver.sender} ${detail.expressDeliver.senderTel}</p>
                <p><c:if test="${detail.expressDeliver.senderProvince != detail.expressDeliver.senderCity}">${detail.expressDeliver.senderProvince}</c:if>${detail.expressDeliver.senderCity}${detail.expressDeliver.senderAddress}</p>
            </div>
        </div>
        <div class="m8 b">
            <div class="left">
                <p>收件人</p>
            </div>
            <div class="right">
                <p>${detail.expressDeliver.receiver} ${detail.expressDeliver.receiverTel} ${detail.expressDeliver.receiverMobile} ${detail.expressDeliver.receiverCompany}</p>
                <p><c:if test="${detail.expressDeliver.receiverProvince != detail.expressDeliver.receiverCity}">${detail.expressDeliver.receiverProvince}</c:if>${detail.expressDeliver.receiverCity}${detail.expressDeliver.receiverAddress}</p>
            </div>
        </div>
        <div class="m10"></div>
    </div>
</div>
</c:forEach>
</body>
<script type="text/javascript">
    window.onload = function () {
        setTimeout(function(){
            window.print();
        }, 50);
    };
</script>
</html>