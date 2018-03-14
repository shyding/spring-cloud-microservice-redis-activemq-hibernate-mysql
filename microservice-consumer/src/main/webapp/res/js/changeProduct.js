var changeProduct = (function ($) {
    "use strict";

    var contextPath = "";
    var accounts = {};

    function init(isSave, tableSelector, rowCount, payInfo, rootPath) {
        contextPath = rootPath;
        accounts = payInfo;

        $('[data-entity-no-a="entityNoA"]').click(function(){
            if (this.dataset.entityNo.indexOf("FR") == 0) {
                render(contextPath + "/orderManagement/view/order/" + this.dataset.entityId);
            }
        });

        if (isSave) {
            setReceiptAccountInfo();
            $('[data-account-suggest="account"]').unbind("keyup");
            suggestAccount($('[data-account-suggest="account"]'), accounts);
            $('[data-account="account"]').accountInput();

            $('#addPayItem').click(function(){
                addPay()
            });


            $('input.flat').iCheck({
                checkboxClass: 'icheckbox_flat-green',
                radioClass: 'iradio_flat-green'

            }).on("ifClicked",function(event){
                var theICheck = $(this);
                if(event.target.checked){
                    theICheck.iCheck('uncheck');
                }else{
                    theICheck.iCheck('check');
                }

                calculateAmounts(theICheck.parent());
            });



            var trs = $(tableSelector).find("tr");
            var trHtml = "<tr>" + $(trs[trs.length - 1]).html() + "</tr>";

            var tbodyHtml = "";
            for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                tbodyHtml += trHtml;
            }
            $(tableSelector).find("tbody").append(tbodyHtml);

            $('[data-property-name="quantity"]').blur(function(){
                var quantityObj = $(this);
                calculateDetailAmount(quantityObj);
                setPayInfo();
            });

            suggestProduct($('[suggest-name="productNo"]'));
        }
    }

    function suggestAccount(suggestInputs, accounts) {
        try {
            suggestInputs.coolautosuggestm({
                staticData: accounts,
                showProperty: "payAccount",
                marginTop: "margin-top:34px",
                width: 118,
                onSelected:function(result){
                    if(result!=null){
                        $(this).val(result.payAccount);
                    }
                }
            });
        } catch(e) {
            console.log(e.message);
        }
    }

    function addPay() {
        var trs = $("#payList tbody tr");
        $("#payList tbody").append("<tr>" + $(trs[trs.length - 1]).html() + "</tr>");
        setReceiptAccountInfo();

        var newTrs = $("#payList tbody tr");
        $.each($(newTrs[newTrs.length-1]).find(":input"), function (ci, item) {
            var name = item.name;
            if (name != undefined) {
                if (name == "pays[][payAccount]:string") {
                    $(item).unbind("keyup");
                    suggestAccount($(item), accounts);
                    $(item).accountInput();
                }
            }
        });
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
            }
        });
    }

    function suggestProduct(suggestInputs) {
        try {
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
                        var price, quantity, amount;

                        for (var x = 0; x < inputs.length; x++) {
                            var name = $(inputs[x]).attr("name");

                            if (name != undefined) {
                                if (name == "details[][productNo]:string") {
                                    inputs[x].value = result.no;
                                }

                                if (name == "details[][product[name]]:string") {
                                    inputs[x].value = result.name;
                                }

                                if (name == "details[][price]:number") {
                                    inputs[x].value = result.fatePrice;
                                    price = inputs[x];
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
                            }
                        }

                        if ($.trim(quantity.value) != "") {
                            amount.value = Math.formatFloat(parseFloat(price.value) * parseFloat(quantity.value), 2);
                        }

                        setPayInfo();
                    }
                }
            });
        } catch(e) {
            console.log(e.message);
        }
    }

    function calculateAmounts(tdChild) {
        calculateDetailAmount(tdChild);
        setPayInfo();
    }

    function calculateDetailAmount(tdChild) {
        var amount, price, quantity;
        var inputs = tdChild.parent().parent().find(":input");

        for (var x = 0; x < inputs.length; x++) {
            var name = $(inputs[x]).attr("name");

            if (name != undefined) {
                if (name == "details[][amount]:number") {
                    amount = inputs[x];
                }

                if (name == "details[][price]:number") {
                    price = inputs[x];
                }

                if (name == "details[][quantity]:number") {
                    quantity = inputs[x];
                }
            }
        }

        if ($.trim(quantity.value) != "" && $.trim(price.value) != "") {
            amount.value = Math.formatFloat(parseFloat(price.value) * parseFloat(quantity.value), 2);
        }
    }

    function calculateChangeProductAmount() {
        var feeStr = $.trim($("#fee").val());
        var fee = 0;
        if (feeStr != "") {
            fee = parseFloat(feeStr);
        }

        var netAmount = calculateAmount($('[data-table-name="changeProductList"]').find("tbody tr"),
            -calculateAmount($('[data-table-name="returnProductList"]').find("tbody tr"), 0));

        $("#amount").val(Math.formatFloat(netAmount + fee, 2));
    }

    function setPayInfo() {
        calculateChangeProductAmount();

        if (parseFloat($("#amount").val()) >= 0) {
            $("#payDiv").show();
        } else {
            $("#payDiv").hide();
        }

        var trs = $("#payList tbody tr");
        for (var i = 1; i <= trs.length - 1; i++) {
            $(trs[i]).remove();
        }

        var payItemAmounts = document.getElementsByName("pays[][amount]:number");
        for (var i = 0; i < payItemAmounts.length; i++) {
            payItemAmounts[i].value = "";
        }
    }

    function calculateAmount(trs, amount) {
        $.each(trs, function(ci, tr){
            var quantity = "", price = "";

            $.each($(tr).find(":input"), function(cii, item){
                var name = $(item).attr("name");

                if (name != undefined) {
                    if (name == "details[][quantity]:number") {
                        quantity = item.value;
                    }

                    if (name == "details[][price]:number") {
                        price = item.value;
                    }
                }
            });

            if ($.trim(quantity) != "" && $.trim(price) != "") {
                amount = Math.formatFloat(amount + parseFloat(price) * parseFloat(quantity), 2);
            }
        });

        return amount;
    }

    function audit(auditResult, url) {
        $("#auditResult").val(auditResult);
        $("#actionForm").submitForm(url);
    }

    function save(url) {
        var amount = Math.formatFloat(parseFloat($("#amount").val()), 2);

        if (amount >= 0 ) {
            var payItemAmounts = document.getElementsByName("pays[][amount]:number");
            var totalPayItemAmount = 0;
            for (var i = 0; i < payItemAmounts.length; i++) {
                if ($.trim(payItemAmounts[i].value) != "") {
                    totalPayItemAmount = Math.formatFloat(totalPayItemAmount + parseFloat(payItemAmounts[i].value), 2);
                }
            }

            if (totalPayItemAmount != amount) {
                alert("填写的支付金额与换货差价不一致");
                $(payItemAmounts[0]).focus();
                return false;
            }
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

        formData.details = [];
        var json = JSON.stringify(formData);
        json = json.substring(0, json.length-2);

        var trs = $('[data-table-name="returnProductList"]').find("tbody tr");
        for (var i = 0; i < trs.length; i++) {
            var textInputs = $(trs[i]).find("input");
            var tds = $(trs[i]).find("td");

            if (tds.length > 0) {
                if ($.trim($(trs[i]).find('[data-property-name="productNo"]')[0].value) != "" &&
                    $(trs[i]).find('input.flat')[0].checked) {

                    for (var j = 0; j < textInputs.length; j++) {
                        if ($.trim(textInputs[j].value) == "" && $(textInputs[j]).attr("required") != undefined) {
                            alert("请输入值");
                            $(textInputs[j]).focus();
                            return false;
                        }
                    }

                    json += JSON.stringify($(trs[i]).find(":input").not('[value=""]').serializeJSON()["details"][0]) + ",";
                }
            }
        }

        if (json.indexOf('"type":1') == -1) {
            alert("请输要退货的商品");
            return false;
        }

        var trs = $('[data-table-name="changeProductList"]').find("tbody tr");
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
                    }

                    json += JSON.stringify($(trs[i]).find(":input").not('[value=""]').serializeJSON()["details"][0]) + ",";
                }
            }
        }

        if (json.indexOf('"type":0') == -1) {
            alert("请输要换货的商品");
            return false;

        } else {
            json = json.substring(0, json.length-1) + ']}';
        }

        $form.sendData(url, json, function(result){
            if (result.result.indexOf("success") != -1) {
                $("#entityId").val(result.id);
                audit('Y', contextPath + '/afterSaleService/doBusiness/changeProductSaleAudit');
            }
        }, false);
    }

    return {
        calculateAmounts: calculateAmounts,
        calculateReturnProductDetailAmount: calculateDetailAmount,
        calculateChangeProductAmount: calculateChangeProductAmount,
        save: save,
        saleAudit: audit,
        directorAudit: audit,
        warehousingAudit: audit,
        changeProductComplete: audit,
        init: init
    }
})(jQuery);
