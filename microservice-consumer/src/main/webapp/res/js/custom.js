/**
 * Resize function without multiple trigger
 *
 * Usage:
 * $(window).smartresize(function(){
 *     // code here
 * });
 */
(function($,sr){
    // debouncing function from John Hann
    // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
    var debounce = function (func, threshold, execAsap) {
        var timeout;

        return function debounced () {
            var obj = this, args = arguments;
            function delayed () {
                if (!execAsap)
                    func.apply(obj, args);
                timeout = null;
            }

            if (timeout)
                clearTimeout(timeout);
            else if (execAsap)
                func.apply(obj, args);

            timeout = setTimeout(delayed, threshold || 100);
        };
    };

    // smartresize
    jQuery.fn[sr] = function(fn){  return fn ? this.bind('resize', debounce(fn)) : this.trigger(sr); };

})(jQuery,'smartresize');
/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var CURRENT_URL = window.location.href.split('#')[0].split('?')[0],
    $BODY = $('body'),
    $MENU_TOGGLE = $('#menu_toggle'),
    $SIDEBAR_MENU = $('#sidebar-menu'),
    $SIDEBAR_FOOTER = $('.sidebar-footer'),
    $LEFT_COL = $('.left_col'),
    $RIGHT_COL = $('.right_col'),
    $NAV_MENU = $('.nav_menu'),
    $FOOTER = $('footer');



// Sidebar
function init_sidebar() {
// TODO: This is some kind of easy fix, maybe we can improve this
    var setContentHeight = function () {
        // reset height
        $RIGHT_COL.css('min-height', $(window).height());

        var bodyHeight = $BODY.outerHeight(),
            footerHeight = $BODY.hasClass('footer_fixed') ? -10 : $FOOTER.height(),
            leftColHeight = $LEFT_COL.eq(1).height() + $SIDEBAR_FOOTER.height(),
            contentHeight = bodyHeight < leftColHeight ? leftColHeight : bodyHeight;

        // normalize content
        contentHeight -= $NAV_MENU.height() + footerHeight;

        $RIGHT_COL.css('min-height', contentHeight);
    };

    $SIDEBAR_MENU.find('a').on('click', function(ev) {
        console.log('clicked - sidebar_menu');
        var $li = $(this).parent();

        if ($li.is('.active')) {
            $li.removeClass('active active-sm');
            $('ul:first', $li).slideUp(function() {
                setContentHeight();
            });
        } else {
            // prevent closing menu if we are on child menu
            if (!$li.parent().is('.child_menu')) {
                $SIDEBAR_MENU.find('li').removeClass('active active-sm');
                $SIDEBAR_MENU.find('li ul').slideUp();
            }else
            {
                if ( $BODY.is( ".nav-sm" ) )
                {
                    $SIDEBAR_MENU.find( "li" ).removeClass( "active active-sm" );
                    $SIDEBAR_MENU.find( "li ul" ).slideUp();
                }
            }
            $li.addClass('active');

            $('ul:first', $li).slideDown(function() {
                setContentHeight();
            });
        }
    });

// toggle small or large menu
    $MENU_TOGGLE.on('click', function() {
        console.log('clicked - menu toggle');

        if ($BODY.hasClass('nav-md')) {
            $SIDEBAR_MENU.find('li.active ul').hide();
            $SIDEBAR_MENU.find('li.active').addClass('active-sm').removeClass('active');
        } else {
            $SIDEBAR_MENU.find('li.active-sm ul').show();
            $SIDEBAR_MENU.find('li.active-sm').addClass('active').removeClass('active-sm');
        }

        $BODY.toggleClass('nav-md nav-sm');

        setContentHeight();
    });

    // check active menu
    $SIDEBAR_MENU.find('a[href="' + CURRENT_URL + '"]').parent('li').addClass('current-page');

    $SIDEBAR_MENU.find('a').filter(function () {
        return this.href == CURRENT_URL;
    }).parent('li').addClass('current-page').parents('ul').slideDown(function() {
        setContentHeight();
    }).parent().addClass('active');

    // recompute content when resizing
    $(window).smartresize(function(){
        setContentHeight();
    });

    setContentHeight();

    // fixed sidebar
    if ($.fn.mCustomScrollbar) {
        $('.menu_fixed').mCustomScrollbar({
            autoHideScrollbar: true,
            theme: 'minimal',
            mouseWheel:{ preventDefault: true }
        });
    }
}

/* AUTOSIZE */

function init_autosize() {

    if(typeof $.fn.autosize !== 'undefined'){

        autosize($('.resizable_textarea'));

    }

}

var hisUrls = [], hisIndex = 0, hisLength = 10;
var dataListQueryJson = "{}", dataListQueryEntity = "";
var returnPage = false;
var username = "";

function render(url){
    $.ajax({
        type: "get",
        url:  url,

        success: function (pageContent) {
            if (isValid(pageContent, url.substring(0, url.indexOf("/")))) {
                setHisUrls(url);
                $("#pageContent").empty().html(pageContent);
            }
        }
    });
}

function renderAudit(element, url){
    $.ajax({
        type: "get",
        url:  url,
        success: function (pageContent) {
            if (isValid(pageContent, url.substring(0, url.indexOf("/")))) {
                element.empty().html(pageContent);

                /**
                 * 蕴藏部分元素
                 */
                element.find(".page-title").css("display", "none");
                element.find(".clearfix").css("display", "none");
                element.find(".x_title").css("display", "none");
                var children = document.getElementById("form").children;
                var index = children.length - 1;
                for (; index >= 0; index--) {
                    if (children[index].tagName.toLowerCase() == "div") {
                        children[index].innerHTML = "";
                        break;
                    }
                }
                $('#result').attr("readonly", false).css("border", "1px solid #ccc");
            }
        }
    });
}

function isValid(pageContent, contextPath) {
    if (typeof pageContent == 'object' && pageContent.result == "对不起，你访问的页面不存在，或者会话已经过期") {
        $("#pageContent").empty().html("<div class='right_col' style='height: 100%'>" + pageContent.result +
            "<br/><br/><a href='" + contextPath + "/sys/user/signIn'>请重新登录</a></div>");

        return false;
    }

    return true;
}

function setHisUrls(url) {
    if (url != hisUrls[hisIndex]) {
        if (hisUrls.length == hisLength) {
            hisIndex = 0;
        } else {
            ++hisIndex;
        }

        hisUrls[hisIndex] = url;
    }
}

function getPreUrls() {
    if (hisIndex == 0 && hisUrls.length == hisLength) {
        hisIndex = hisUrls.length - 1;
    } else {
        --hisIndex;
    }

    return hisUrls[hisIndex];
}

function setSelect(src, value) {
    var options = src.options;
    for (var i = 0; i < options.length; i++) {
        if (options[i].value == value) {
            options[i].selected = true;
        }
    }
}

function setMutilSelect(src, values) {
    for (var vi in values) {
        setSelect(src, values[vi]);
    }
}

function init(editable) {
    $("#edit").unbind("click").click(function(){
        editable = true;

        $('input, select').attr("readonly",false).css("border", "1px solid #ccc");
        $('#send, #delete, #recover').attr("disabled", false);
        $("#edit").attr("disabled", "disabled");
    });

    $("#editState").unbind("click").click(function(){
        $('#delete, #recover').attr("disabled", false);
        $("#editState").attr("disabled", "disabled");
    });

    if (!editable) {
        $('input, select').attr("readonly","readonly").css("border", "0");
        $('#send, #delete, #recover').attr("disabled","disabled");
    }

    $("#cancel, #return").unbind("click").click(function(){
        render(getPreUrls());
        returnPage = true;
    });
}

$(document).ready(function() {
    init_sidebar();
    init_autosize();

    $("#auditA").click();

    $("#signOut").click(function(){
        $("#signOutForm").submit();
    });

    if ($("input.flat")[0]) {
        $(document).ready(function () {
            $('input.flat').iCheck({
                checkboxClass: 'icheckbox_flat-green',
                radioClass: 'iradio_flat-green'
            });
        });
    }

    $("#send").keydown(function(event){
        if(event.keyCode == 13){ //绑定回车
            this.click();
            // event.preventDefault();
            return false;
        }
    });

    $("#sidebar-menu ul li ul li a").click(function(){render($(this).attr("href").toString().substring(1));});
});
