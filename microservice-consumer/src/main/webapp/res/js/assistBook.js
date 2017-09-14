var assistBook = (function ($) {
    "use strict";

    var contextPath = "";
    var accChooseHref;

    function init(tableId, rowCount, rootPath) {
        contextPath = rootPath;

        var trs = $("#" + tableId + " tbody tr");
        var trHtml = "<tr>" + $(trs[trs.length - 1]).html() + "</tr>";

        var tbodyHtml = "";
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            tbodyHtml += trHtml;
        }
        $("#" + tableId + " tbody").append(tbodyHtml);

        suggestBookUser(contextPath);
        suggestProduct(null, '[data-property-name="productNo"]', contextPath);
        suggestPriceChange(null, '[data-property-name="priceChangeNo"]', contextPath);
        calculateAmountByQuantity(null, '[data-property-name="quantity"]');
        initAccs();
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
            url:contextPath + "/customerManagement/suggest/user/username/",
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
                        var productId = $(this).parent().parent().find('[data-property-name="productId"]')[0].value;
                        if ($.trim(productId) != "") {
                            queryJson["product"] = {};
                            queryJson["product"]["id"] = parseInt(productId);
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
                                    if (name == "details[][priceChange[product[id]]]:number") {
                                        inputs[x].value = result.product.id;
                                    }

                                    if (name == "details[][priceChange[product[no]]]:string") {
                                        inputs[x].value = result.product.no;
                                    }

                                    if (name == "details[][priceChange[price]]:number") {
                                        inputs[x].value = result.price;
                                        priceChangePrice = inputs[x];
                                    }

                                    if (name == "details[][priceChange[id]]:number") {
                                        inputs[x].value = result.id
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
                                calculateOrderAmount();
                            }
                        }
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
                                    if (name == "details[][priceChange[product[no]]]:string") {
                                        if (inputs[x].value != "" && inputs[x].value != result.no) {
                                            alert("选择的商品和价格浮动码对应商品:" + inputs[x].value +"不匹配！");
                                            $(this).focus();

                                            return false;
                                        }
                                    }

                                    if (name == "details[][product[id]]:number") {
                                        inputs[x].value = result.id;
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

                                calculateOrderAmount();
                            }
                        }
                    }
                });
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    function calculateAmountByQuantity(item, target){
        var quantityInputs = null;

        if (item != null) {
            quantityInputs = item;
        } else {
            quantityInputs = $(target);
        }

        quantityInputs.blur(function(){
            var inputs = $(this).parent().parent().find(":input");
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

            calculateOrderAmount();
        });
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
                    amount = Math.formatFloat(amount + parseFloat(price) * parseFloat(quantity), 2)
                }

                if ($.trim(priceChangePrice) != "") {
                    payAmount = Math.formatFloat(payAmount + parseFloat(priceChangePrice) * parseFloat(quantity), 2)
                } else if ($.trim(fatePrice) != "") {
                    payAmount = Math.formatFloat(payAmount + parseFloat(fatePrice) * parseFloat(quantity), 2)
                }
            }
        });

        $("#amount").val(amount);
        $("#payAmount").val(payAmount);
    }

    function initAccs() {
        var trs = $("#accList tbody tr");
        var trHtml = "<tr>" + $(trs[trs.length - 1]).html() + "</tr>";

        var tbodyHtml = "";
        for (var rowIndex = 0; rowIndex < 10; rowIndex++) {
            tbodyHtml += trHtml;
        }
        $("#accList tbody").append(tbodyHtml);

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
                                var accId = $(trs[i]).find('[name="accId"]')[0].value;
                                var accQuantity = $(trs[i]).find('[name="accQuantity"]')[0].value;
                                var accUnit = $(trs[i]).find('[name="accUnit"]')[0].value;

                                $(itemsInfo).append('<div data-accs-info="itemInfo" style="display: none"><input type="hidden" value="' + accUnit + '" name="details[][orderPrivate[accs[][unit]]]:string">' +
                                    '<input type="hidden" value="' + accQuantity + '" name="details[][orderPrivate[accs[][quantity]]]:number">' +
                                    '<input type="hidden" data-acc-info="id" value="' + accId + '" name="details[][orderPrivate[accs[][product[id]]]]:number">' +
                                    '<input type="hidden" data-acc-info="name" value="' + accName + '" name="details[][orderPrivate[accs[][product[name]]]]:string"></div>');


                                if ($.trim(accsInfo.value) == "") {
                                    accsInfo.value = accName + " " + accQuantity + " " + accUnit;
                                } else {
                                    accsInfo.value += ";" + accName + " " + accQuantity + " " + accUnit;
                                }
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
        var $form = $("#form");
        if (!validator.checkAll($form)) {
            return;
        }

        var json = JSON.stringify($form.serializeJSON());
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
                            } else if (type == 2) {
                                alert("请输入私人订制描述信息");
                            }
                            $(textInputs[j]).focus();

                            return false;
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

    return {
        init: init,
        addRow: addRow,
        saveOrder: saveOrder
    }
})(jQuery);
