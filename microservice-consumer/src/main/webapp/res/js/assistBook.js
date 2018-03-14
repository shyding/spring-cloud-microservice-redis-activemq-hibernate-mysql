var assistBook = (function ($) {
    "use strict";

    var contextPath = "";
    var accChooseHref, giftChooseHref;

    function init(tableId, rowCount, rootPath) {
        contextPath = rootPath;

        var trs = $("#" + tableId + " tbody tr");
        var trHtml = "<tr>" + $(trs[trs.length - 1]).html() + "</tr>";

        var tbodyHtml = "";
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            tbodyHtml += trHtml;
        }
        $("#" + tableId + " tbody").append(tbodyHtml);

        $('[data-account="account"]').accountInput();

        setOrderBookShow();

        suggestBookUser(contextPath);
        setReceiptAccountInfo();

        suggestProduct(null, '[data-property-name="productNo"]', contextPath);
        suggestPriceChange(null, '[data-property-name="priceChangeNo"]', contextPath);
        // calculateAmountByPrice(null, '[data-property-name="fatePrice"]');
        calculateAmountByQuantity(null, '[data-property-name="quantity"]');

        initExpressDate();
        initAccs();
        initGifts();
    }

    function addRow() {
        var trs = $("#productList tbody tr");
        $("#productList tbody").append("<tr>" + $(trs[trs.length - 1]).html() + "</tr>");
        trs = $("#productList tbody tr");

        $.each($(trs[trs.length-1]).find("input,a"), function (ci, item) {
            var propertyName = item.dataset.propertyName;
            if (propertyName != undefined) {

                if (propertyName == "productNo") {
                    suggestProduct($(item), '[data-property-name="productNo"]', contextPath);
                }

                if (propertyName == "priceChangeNo") {
                    suggestPriceChange($(item), '[data-property-name="priceChangeNo"]', contextPath);
                }

                /*if (propertyName == "fatePrice") {
                    calculateAmountByPrice($(item), '[data-property-name="fatePrice"]');
                }*/

                if (propertyName == "quantity") {
                    calculateAmountByQuantity($(item), '[data-property-name="quantity"]');
                }

                if (propertyName == "chooseAccs") {
                    $(item).bind("click", function () {
                        accChooseHref = this;
                        $('#accChooseDiv').dialog('open');
                        return false;
                    });
                }

                if (propertyName == "accsInfo") {
                    $(item).blur(function () {
                        setAccs(this);
                    });
                }
            }
        });
    }

    function suggestBookUser(contextPath){
        $("#bookUser").coolautosuggest({
            url:contextPath + "/customerManagement/unlimitedSuggest/user/username/",
            showProperty: 'username',

            onSelected:function(result){
                if(result!=null){
                    $(document.getElementById("user[id]")).val(result.id);

                    var expressesHtml = "", expresses = result.customer.expresses;
                    for (var i = 0; i < expresses.length; i++) {
                        var checked = "";

                        if (expresses[i].defaultUse == "Y") {
                            var detailExpresses = document.getElementsByName("details[][express[id]]:number");
                            for (var j = 0; j < detailExpresses.length; j++) {
                                detailExpresses[j].value = expresses[i].id;
                            }

                            checked += "checked";
                        }

                        expressesHtml += '<div style="padding-bottom: 5px"><input type="radio" name="expressRadio" value="' + expresses[i].id + '" class="flat" ' + checked + ' />&nbsp;&nbsp;' + expresses[i].address + "&nbsp;/&nbsp;" + expresses[i].receiver + "&nbsp;/&nbsp;" + expresses[i].phone + "&nbsp;/&nbsp;" + expresses[i].postCode + "</div>";
                    }

                    $("#expressDiv").html('<div style="padding-top:8px;padding-bottom:8px">选择&nbsp;&nbsp;收货地址&nbsp;/&nbsp;收货人&nbsp;/&nbsp;收货电话&nbsp;/&nbsp;邮编</div>' +
                        expressesHtml);
                    $('input.flat').iCheck({
                        checkboxClass: 'icheckbox_flat-green',
                        radioClass: 'iradio_flat-green'
                    });

                    $('[name="expressCheckbox"]').unbind("click").bind("click", function(){
                        var detailExpresses = document.getElementsByName("details[][express[id]]:number");
                        for (var j = 0; j < detailExpresses.length; j++) {
                            detailExpresses[j].value = this.val();
                        }
                    });
                }
            }
        });
    }

    function suggestPriceChange(item, target, contextPath) {
        var suggestInputs = null;

        if (item != null) {
            suggestInputs = item;
        } else {
            suggestInputs = $(target);
        }

        try {
            if (suggestInputs != null) {
                suggestInputs.coolautosuggestm({
                    url: contextPath + "/erp/privateQuery/productPriceChange",
                    showProperty: 'no',

                    getQueryData: function(paramName){
                        var queryJson = {};

                        var suggestWord = $.trim(this.value);
                        if (suggestWord != "") {
                            queryJson["no"] = suggestWord;
                        }
                        var productNo = $(this).parent().parent().find('[data-property-name="productNo"]')[0].value;
                        if ($.trim(productNo) != "") {
                            queryJson["productNo"] = productNo;
                        }
                        queryJson["state"] = 1;

                        return queryJson;
                    },

                    onSelected: function (result) {
                        if (result != null) {
                            var inputs = this.parent().parent().find(":input");
                            var quantity, payAmount, priceChangePrice;

                            for (var x = 0; x < inputs.length; x++) {
                                var name = $(inputs[x]).attr("name");

                                if (name != undefined) {
                                    if (name == "details[][priceChange[productNo]]:string") {
                                        inputs[x].value = result.product.no;
                                    }

                                    if (name == "details[][priceChange[price]]:number") {
                                        inputs[x].value = result.price;
                                        priceChangePrice = inputs[x];
                                    }

                                    if (name == "details[][priceChange[id]]:number") {
                                        inputs[x].value = result.id;
                                    }

                                    if (name == "details[][quantity]:number") {
                                        quantity = inputs[x];
                                    }

                                    if (name == "details[][payAmount]:number") {
                                        payAmount = inputs[x];
                                    }
                                }
                            }

                            if ($.trim(quantity.value) != "") {
                                payAmount.value = Math.formatFloat(parseFloat(priceChangePrice.value) * parseFloat(quantity.value), 2);
                            }

                            calculateOrderAmount();
                        }
                    }
                });

                suggestInputs.blur(function(){
                    if ($(this).val() == "") {
                        var inputs = $(this).parent().parent().find(":input");
                        var price, fatePrice, quantity, payAmount, amount;

                        for (var x = 0; x < inputs.length; x++) {
                            var name = $(inputs[x]).attr("name");

                            if (name != undefined) {
                                if (name == "details[][priceChange[productNo]]:string") {
                                    inputs[x].value = "";
                                }

                                if (name == "details[][priceChange[price]]:number") {
                                    inputs[x].value = "";
                                }

                                if (name == "details[][priceChange[id]]:number") {
                                    inputs[x].value = "";
                                }

                                if (name == "details[][product[price]]:number") {
                                    price = inputs[x];
                                }

                                if (name == "details[][product[fatePrice]]:number") {
                                    fatePrice = inputs[x];
                                }

                                if (name == "details[][quantity]:number") {
                                    quantity = inputs[x];
                                }

                                if (name == "details[][amount]:number") {
                                    amount = inputs[x];
                                }

                                if (name == "details[][payAmount]:number") {
                                    payAmount = inputs[x];
                                }
                            }
                        }

                        if ($.trim(quantity.value) != "") {
                            if ($.trim(price.value) != "") {
                                amount.value = Math.formatFloat(parseFloat(price.value) * parseFloat(quantity.value), 2);
                            }

                            if ($.trim(price.value) != "") {
                                payAmount.value = Math.formatFloat(parseFloat(fatePrice.value) * parseFloat(quantity.value), 2);
                            }
                        }

                        calculateOrderAmount();
                    }
                });
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    function suggestProduct(item, target, contextPath) {
        var suggestInputs = null;

        if (item != null) {
            suggestInputs = item;
        } else {
            suggestInputs = $(target);
        }

        try {
            if (suggestInputs != null) {
                suggestInputs.coolautosuggestm({
                    url: contextPath + "/erp/privateQuery/product",
                    showProperty: "no",

                    getQueryData: function(paramName){
                        var queryJson = {};

                        var suggestWord = $.trim(this.value);
                        if (suggestWord != "") {
                            queryJson["no"] = suggestWord;
                        }
                        queryJson["state"] = 3;

                        return queryJson;
                    },

                    onSelected:function(result){
                        if(result!=null){
                            var inputs = this.parent().parent().find(":input");
                            var price, fatePrice, quantity, amount, payAmount, priceChangePrice;

                            for (var x = 0; x < inputs.length; x++) {
                                var name = $(inputs[x]).attr("name");

                                if (name != undefined) {
                                    if (name == "details[][priceChange[productNo]]:string") {
                                        if (inputs[x].value != "" && inputs[x].value != result.no) {
                                            alert("选择的商品和价格浮动码对应商品:" + inputs[x].value +"不匹配！");
                                            $(this).focus();

                                            return false;
                                        }
                                    }

                                    if (name == "details[][product[name]]:string") {
                                        inputs[x].value = result.name;
                                    }

                                    if (name == "details[][product[price]]:number") {
                                        inputs[x].value = result.price;
                                        price = inputs[x];
                                    }

                                    if (name == "details[][product[fatePrice]]:number") {
                                        inputs[x].value = result.fatePrice;
                                        fatePrice = inputs[x];
                                    }

                                    if (name == "details[][productPrice]:number") {
                                        inputs[x].value = result.fatePrice;
                                    }

                                    if (name == "details[][unit]:string") {
                                        inputs[x].value = result.soldUnit;
                                    }

                                    if (name == "details[][quantity]:number") {
                                        quantity = inputs[x];
                                    }

                                    if (name == "details[][amount]:number") {
                                        amount = inputs[x];
                                    }

                                    if (name == "details[][payAmount]:number") {
                                        payAmount = inputs[x];
                                    }

                                    if (name == "details[][priceChange[price]]:number") {
                                        priceChangePrice = inputs[x];
                                    }
                                }
                            }

                            if ($.trim(quantity.value) != "") {
                                amount.value = Math.formatFloat(parseFloat(price.value) * parseFloat(quantity.value), 2);

                                if ($.trim(priceChangePrice.value) == "") {
                                    payAmount.value = Math.formatFloat(parseFloat(fatePrice.value) * parseFloat(quantity.value), 2);
                                }
                            }

                            calculateOrderAmount();
                        }
                    }
                });
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    function calculateAmountByPrice(item, target){
        var fatePriceInputs = null;

        if (item != null) {
            fatePriceInputs = item;
        } else {
            fatePriceInputs = $(target);
        }

        fatePriceInputs.blur(function(){
            calculateOrderDetailAmount(this);
            calculateOrderAmount();
        });
    }

    function calculateAmountByQuantity(item, target){
        var quantityInputs = null;

        if (item != null) {
            quantityInputs = item;
        } else {
            quantityInputs = $(target);
        }

        quantityInputs.blur(function(){
            calculateOrderDetailAmount(this);
            calculateOrderAmount();
        });
    }

    function calculateOrderDetailAmount(item) {
        var inputs = $(item).parent().parent().find(":input");
        var price, fatePrice, quantity, amount, payAmount, priceChangePrice;

        for (var x = 0; x < inputs.length; x++) {
            var name = $(inputs[x]).attr("name");

            if (name != undefined) {
                if (name == "details[][product[price]]:number") {
                    price = inputs[x];
                }

                if (name == "details[][product[fatePrice]]:number") {
                    fatePrice = inputs[x];
                }

                if (name == "details[][priceChange[price]]:number") {
                    priceChangePrice = inputs[x];
                }

                if (name == "details[][quantity]:number") {
                    quantity = inputs[x];
                }

                if (name == "details[][amount]:number") {
                    amount = inputs[x];
                }

                if (name == "details[][payAmount]:number") {
                    payAmount = inputs[x];
                }
            }
        }

        if ($.trim(quantity.value) != "") {
            if ($.trim(price.value) != "") {
                amount.value = Math.formatFloat(parseFloat(price.value) * parseFloat(quantity.value), 2);
            }

            if ($.trim(priceChangePrice.value) != "") {
                payAmount.value = Math.formatFloat(parseFloat(priceChangePrice.value) * parseFloat(quantity.value), 2);
            } else if ($.trim(fatePrice.value) != "") {
                payAmount.value = Math.formatFloat(parseFloat(fatePrice.value) * parseFloat(quantity.value), 2);
            }

        } else {
            amount.value = 0;
            payAmount.value = 0;
        }
    }

    function calculateOrderAmount() {
        var amount = 0, payAmount = 0;

        var trs = $("#productList tbody tr");

        $.each(trs, function(ci, tr){
            var price = "", fatePrice = "", quantity = "", priceChangePrice = "";

            $.each($(tr).find(":input"), function(cii, item){
                var name = $(item).attr("name");

                if (name != undefined) {
                    if (name == "details[][product[price]]:number") {
                        price = item.value;
                    }

                    if (name == "details[][product[fatePrice]]:number") {
                        fatePrice = item.value;
                    }

                    if (name == "details[][quantity]:number") {
                        quantity = item.value;
                    }

                    if (name == "details[][priceChange[price]]:number") {
                        priceChangePrice = item.value;
                    }
                }
            });

            if ($.trim(quantity) != "") {
                if ($.trim(price) != "") {
                    amount = Math.formatFloat(amount + parseFloat(price) * parseFloat(quantity), 2);
                }

                if ($.trim(priceChangePrice) != "") {
                    payAmount = Math.formatFloat(payAmount + parseFloat(priceChangePrice) * parseFloat(quantity), 2);
                } else if ($.trim(fatePrice) != "") {
                    payAmount = Math.formatFloat(payAmount + parseFloat(fatePrice) * parseFloat(quantity), 2);
                }
            }
        });

        $("#amount").val(amount);
        $("#payAmount").val(payAmount);
    }

    function initExpressDate() {
        $('#expressDate').daterangepicker({
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
            startDate:moment().add(1, 'days'),
            singleDatePicker: true,
            singleClasses: "picker_3"
        }, function(start, end, label) {
            console.log(start.toISOString(), end.toISOString(), label);
        });

        setExpressDate();

        $('#expressDate').blur(function(){
            setExpressDate();
        });
    }

    function setExpressDate(){
        var detailExpresses = document.getElementsByName("details[][expressDate]:string");
        for (var j = 0; j < detailExpresses.length; j++) {
            detailExpresses[j].value = $('#expressDate').val();
        }
    }

    function setOrderBookShow(){
        $("#type").bind("click", function () {
            var type = $(this);
            if (parseInt(type.val()) == 3) {
                $("#orderBookDiv").show();
                $("#orderBookDeposit").attr("disabled", false);
                $("#orderBookPayDate").attr("disabled", false);
            } else {
                $("#orderBookDiv").hide();
                $("#orderBookDeposit").attr("disabled", true);
                $("#orderBookPayDate").attr("disabled", true);
            }
        });

        $('#orderBookPayDate').daterangepicker({
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
    }

    function initGifts() {
        $('[name="giftName"]').coolautosuggestm({
            url:contextPath + "/erp/privateQuery/product",
            showProperty: 'name',

            getQueryData: function(paramName){
                var queryJson = {};

                var suggestWord = $.trim(this.value);
                if (suggestWord != "") {
                    queryJson["name"] = suggestWord;
                }
                queryJson["state"] = 3;

                return queryJson;
            },

            onSelected:function(result){
                if(result!=null){
                    var tr = $(this).parent().parent();
                    tr.find('[name="giftNo"]')[0].value = result.no;
                    tr.find('[name="giftUnitPrice"]')[0].value = result.unitPrice;
                    tr.find('[name="giftFatePrice"]')[0].value = result.fatePrice;
                    tr.find('[name="giftUnit"]')[0].value = result.soldUnit;
                    tr.find('[name="giftId"]')[0].value = result.id;
                }
            }
        });

        $("#giftChooseDiv").dialog({
            title: "选择赠品",
            autoOpen: false,
            width: 1100,
            height:360,
            buttons: {
                "添加": function () {
                    var giftTd = $(giftChooseHref).parent();

                    var giftsInfo = giftTd.find('[data-property-name="giftsInfo"]')[0];
                    var itemsInfo = giftTd.find('[data-gift-info="itemsInfo"]')[0];

                    var trs = $("#giftList tbody tr");

                    for (var i = 0; i < trs.length; i++) {
                        var tds = $(trs[i]).find("td");

                        if (tds.length > 0) {
                            var giftName = $(trs[i]).find('[name="giftName"]')[0].value;

                            if ($.trim(giftName) != "") {
                                var giftUnit = $(trs[i]).find('[name="giftUnit"]')[0].value;
                                var giftQuantity = $(trs[i]).find('[name="giftQuantity"]')[0].value;
                                var giftNo = $(trs[i]).find('[name="giftNo"]')[0].value;
                                var fatePrice = $(trs[i]).find('[name="giftFatePrice"]')[0].value;

                                $(itemsInfo).append('<div data-accs-info="itemInfo" style="display: none"><input type="hidden" value="' + giftUnit + '" name="gifts[][unit]:string">' +
                                    '<input type="hidden" value="' + giftQuantity + '" name="gifts[][quantity]:number">' +
                                    '<input type="hidden" value="' + giftNo + '" name="gifts[][productNo]:string">' +
                                    '<input type="hidden" data-gift-info="name" value="' + giftName + '" name="gifts[][product[name]]:string"></div>');

                                if ($.trim(giftsInfo.value) == "") {
                                    giftsInfo.value = giftName + " " + giftQuantity + " " + giftUnit + " ¥" + Math.formatFloat(parseFloat(fatePrice) * parseFloat(giftQuantity), 2);
                                } else {
                                    giftsInfo.value += ";" + giftName + " " + giftQuantity + " " + giftUnit + " ¥" + Math.formatFloat(parseFloat(fatePrice) * parseFloat(giftQuantity), 2);
                                }

                                giftsInfo.readOnly = false;
                            }
                        }
                    }

                    for (var i = 0; i < trs.length; i++) {
                        var tds = $(trs[i]).find("td");

                        if (tds.length > 0) {
                            var giftInputs = $(trs[i]).find("input");
                            for (var j = 0; j < giftInputs.length; j++) {
                                var name = $(giftInputs[j]).attr("name");

                                if (name != undefined && name != "giftQuantity") {
                                    $(giftInputs[j]).val("");
                                } else {
                                    $(giftInputs[j]).val(1);
                                }
                            }
                        }
                    }

                    $(this).dialog("close");
                },

                "取消": function () {
                    $(this).dialog("close");
                }
            }
        });

        $('[data-property-name="chooseGifts"]').bind("click", function () {
            giftChooseHref = this;
            $("#giftChooseDiv").dialog("open");
            return false;
        });

        setGiftsProxy(null, '[data-property-name="giftsInfo"]');
    }

    function setGiftsProxy(item, target){
        var itemsInfoInput = null;

        if (item != null) {
            itemsInfoInput = item;
        } else {
            itemsInfoInput = $(target);
        }

        itemsInfoInput.blur(function(){
            setGifts(this);
        });
    }

    function setGifts(item){
        var giftsInfo = $(item).val();
        var itemsInfo = $($(item).parent().find('[data-gift-info="itemsInfo"]')[0]);

        if ($.trim(giftsInfo) == "") {
            itemsInfo.empty();

        } else {
            var accsInfoArr = giftsInfo.split(";");
            var unEmptyGiftsInfoArr = [];
            for (var i = 0; i < accsInfoArr.length; i++) {
                if ($.trim(accsInfoArr[i]) != "") {
                    unEmptyGiftsInfoArr.push(accsInfoArr[i]);
                }
            }

            var giftNameInputs = itemsInfo.find('[data-gift-info="name"]');
            var emptyGifts = [];

            for (var x = 0; x < giftNameInputs.length; x++) {
                var accName = $(giftNameInputs[x]).val();

                var isContain = false;
                for (var xx = 0; xx < unEmptyGiftsInfoArr.length; xx++) {

                    if (unEmptyGiftsInfoArr[xx].split(" ")[0] == accName) {
                        isContain = true;
                    }
                }

                if (!isContain) {
                    emptyGifts.push(giftNameInputs[x]);
                }
            }

            for (var k = 0; k < emptyGifts.length; k++) {
                $(emptyGifts[k]).parent().empty();
            }
        }
    }

    function initAccs() {
        $('[name="accName"]').coolautosuggestm({
            url:contextPath + "/erp/privateQuery/product",
            showProperty: 'name',

            getQueryData: function(paramName){
                var queryJson = {};

                var suggestWord = $.trim(this.value);
                if (suggestWord != "") {
                    queryJson["name"] = suggestWord;
                }
                queryJson["state"] = 1;
                queryJson["useType"] = "acc";

                return queryJson;
            },

            onSelected:function(result){
                if(result!=null){
                    var tr = $(this).parent().parent();
                    tr.find('[name="accNo"]')[0].value = result.no;
                    tr.find('[name="accId"]')[0].value = result.id;
                    tr.find('[name="accUnit"]')[0].value = result.soldUnit;
                }
            }
        });

        $("#accChooseDiv").dialog({
            title: "选择配饰",
            autoOpen: false,
            width: 900,
            height:510,
            buttons: {
                "添加": function () {
                    var accTd = $(accChooseHref).parent();

                    var accsInfo = accTd.find('[data-property-name="accsInfo"]')[0];
                    var itemsInfo = accTd.find('[data-acc-info="itemsInfo"]')[0];

                    var trs = $("#accList tbody tr");

                    for (var i = 0; i < trs.length; i++) {
                        var tds = $(trs[i]).find("td");

                        if (tds.length > 0) {
                            var accName = $(trs[i]).find('[name="accName"]')[0].value;

                            if ($.trim(accName) != "") {
                                var accNo = $(trs[i]).find('[name="accNo"]')[0].value;
                                var accQuantity = $(trs[i]).find('[name="accQuantity"]')[0].value;
                                var accUnit = $(trs[i]).find('[name="accUnit"]')[0].value;

                                $(itemsInfo).append('<div data-accs-info="itemInfo" style="display: none"><input type="hidden" value="' + accUnit + '" name="details[][orderPrivate[accs[][unit]]]:string">' +
                                    '<input type="hidden" value="' + accQuantity + '" name="details[][orderPrivate[accs[][quantity]]]:number">' +
                                    '<input type="hidden" value="' + accNo + '" name="details[][orderPrivate[accs[][productNo]]]:string">' +
                                    '<input type="hidden" data-acc-info="name" value="' + accName + '" name="details[][orderPrivate[accs[][product[name]]]]:string"></div>');


                                if ($.trim(accsInfo.value) == "") {
                                    accsInfo.value = accName + " " + accQuantity + " " + accUnit;
                                } else {
                                    accsInfo.value += ";" + accName + " " + accQuantity + " " + accUnit;
                                }

                                accsInfo.readOnly = false;
                            }
                        }
                    }

                    for (var i = 0; i < trs.length; i++) {
                        var tds = $(trs[i]).find("td");

                        if (tds.length > 0) {
                            var accInputs = $(trs[i]).find("input");
                            for (var j = 0; j < accInputs.length; j++) {
                                var name = $(accInputs[j]).attr("name");

                                if (name != undefined && name != "accQuantity") {
                                    $(accInputs[j]).val("");
                                } else {
                                    $(accInputs[j]).val(1);
                                }
                            }
                        }
                    }

                    $(this).dialog("close");
                },

                "取消": function () {
                    $(this).dialog("close");
                }
            }
        });

        $('[data-property-name="chooseAccs"]').bind("click", function () {
            accChooseHref = this;
            $("#accChooseDiv").dialog("open");
            return false;
        });

        setAccsProxy(null, '[data-property-name="accsInfo"]');
    }

    function setAccsProxy(item, target){
        var itemsInfoInput = null;

        if (item != null) {
            itemsInfoInput = item;
        } else {
            itemsInfoInput = $(target);
        }

        itemsInfoInput.blur(function(){
            setAccs(this);
        });
    }

    function setAccs(item){
        var accsInfo = $(item).val();
        var itemsInfo = $($(item).parent().find('[data-acc-info="itemsInfo"]')[0]);

        if ($.trim(accsInfo) == "") {
            itemsInfo.empty();

        } else {
            var accsInfoArr = accsInfo.split(";");
            var unEmptyAccsInfoArr = [];
            for (var i = 0; i < accsInfoArr.length; i++) {
                if ($.trim(accsInfoArr[i]) != "") {
                    unEmptyAccsInfoArr.push(accsInfoArr[i]);
                }
            }

            var accNameInputs = itemsInfo.find('[data-acc-info="name"]');
            var emptyAccs = [];

            for (var x = 0; x < accNameInputs.length; x++) {
                var accName = $(accNameInputs[x]).val();

                var isContain = false;
                for (var xx = 0; xx < unEmptyAccsInfoArr.length; xx++) {

                    if (unEmptyAccsInfoArr[xx].split(" ")[0] == accName) {
                        isContain = true;
                    }
                }

                if (!isContain) {
                    emptyAccs.push(accNameInputs[x]);
                }
            }

            for (var k = 0; k < emptyAccs.length; k++) {
                $(emptyAccs[k]).parent().empty();
            }
        }
    }

    function saveOrder(uri){
        if (parseInt($("#type").val()) == 3) {
            var orderBookDeposit = $("#orderBookDeposit");
            if ($.trim(orderBookDeposit.val()) == "") {
                alert("请输入预定订金");
                orderBookDeposit.focus();
                return false;
            }

            var orderBookPayDate = $("#orderBookPayDate");
            if ($.trim(orderBookPayDate.val()) == "") {
                alert("输入订金付款时间");
                orderBookPayDate.focus();
                return false;
            }
        }

        var payItemAmounts = document.getElementsByName("pays[][amount]:number");
        var totalPayItemAmount = 0;
        for (var i = 0; i < payItemAmounts.length; i++) {
            if ($.trim(payItemAmounts[i].value) != "") {
                totalPayItemAmount = Math.formatFloat(totalPayItemAmount + parseFloat(payItemAmounts[i].value), 2);
            }
        }

        if (totalPayItemAmount != Math.formatFloat(parseFloat($("#payAmount").val()), 2)) {
            alert("填写的支付金额与订单实际支付金额不一致");
            $(payItemAmounts[0]).focus();
            return false;
        }

        var $form = $("#form");
        if (!validator.checkAll($form)) {
            return;
        }

        var formData = $form.serializeJSON();
        var pays = formData.pays;
        var validPays = new Array(), k = 0;
        formData.pays = [];
        for (var i = 0; i < pays.length; i++) {
            if ($.trim(pays[i].amount) != "" && parseInt(pays[i].amount) != 0) {
                validPays[k++] = pays[i];
            }
        }
        formData.pays = validPays;

        var json = JSON.stringify(formData);
        json = json.substring(0, json.length-1) + ',"details":[';


        var trs = $("#productList tbody tr");

        for (var i = 0; i < trs.length; i++) {
            var textInputs = $(trs[i]).find("input");
            var tds = $(trs[i]).find("td");

            if (tds.length > 0) {
                if ($.trim($(trs[i]).find('[data-property-name="productNo"]')[0].value) != "") {

                    for (var j = 0; j < textInputs.length; j++) {
                        if ($.trim(textInputs[j].value) == "" && $(textInputs[j]).attr("required") != undefined) {
                            alert("请输入值");
                            $(textInputs[j]).focus();

                            return false;
                        }

                        var type = $("#type").val();
                        if ($.trim(textInputs[j].value) == "" && $(textInputs[j]).attr("name") == "details[][orderPrivate[describes]]:string") {
                            if (type == 4) {
                                alert("请输入商品加工描述信息");
                                $(textInputs[j]).focus();
                                return false;

                            } else if (type == 2) {
                                alert("请输入私人订制描述信息");
                                $(textInputs[j]).focus();
                                return false;
                            }


                        }

                        if ($.trim(textInputs[j].value) == "" && $(textInputs[j]).attr("name") == "accsQuantityUnit" && type == 2) {
                            alert("私人订制，请选择配饰");
                            return false;
                        }
                    }

                    json += JSON.stringify($(trs[i]).find(":input").not('[value=""]').serializeJSON()["details"][0]) + ",";
                }
            }
        }

        if (json.substring(json.length-1) == "[") {
            alert("请输入订购商品明细");
            return false;

        } else {
            json = json.substring(0, json.length-1) + ']}';
        }

        $form.sendData(uri, json);
    }

    function addPay() {
        var trs = $("#payList tbody tr");
        $("#payList tbody").append("<tr>" + $(trs[trs.length - 1]).html() + "</tr>");
        setReceiptAccountInfo();
    }

    function setReceiptAccountInfo() {
        var trs = $("#payList tbody tr");

        $.each($(trs[trs.length-1]).find(":input"), function (ci, item) {
            var name = item.name;
            if (name != undefined) {
                if (name == "receiptAccountInfo") {
                    $(item).click(function(){
                        var receiptAccountInfo = $(this).val().split("/");

                        $.each($(this).parent().find(":input"), function (ci, item) {
                            var name = item.name;
                            if (name != undefined) {
                                if (name == "pays[][receiptAccount]:string") {
                                    $(item).val(receiptAccountInfo[0]);
                                }

                                if (name == "pays[][receiptBranch]:string") {
                                    $(item).val(receiptAccountInfo[1]);
                                }

                                if (name == "pays[][receiptBank]:string") {
                                    $(item).val(receiptAccountInfo[2]);
                                }
                            }
                        });
                    });
                }

                if (name == "pays[][payAccount]:string") {
                    $(item).accountInput();
                }
            }
        });
    }

    return {
        init: init,
        addRow: addRow,
        saveOrder: saveOrder,
        addPay: addPay,
        setReceiptAccountInfo: setReceiptAccountInfo
    }
})(jQuery);
