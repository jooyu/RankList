String.prototype.trim=function(){
    return this.replace(/(^\s*)|(\s*$)/g, "");
};
//JSON To String
function obj2str(o){
    var r = [];
    if(typeof o =="number") {
        return o;
    }
    if(typeof o =="string") {
        return "\""+o.replace(/([\'\"\\])/g,"\\$1").replace(/(\n)/g,"\\n").replace(/(\r)/g,"\\r").replace(/(\t)/g,"\\t")+"\"";
    }
    if(typeof o == "object"){
        if(o===null) return "null";
        if(!o.sort){
            for(var i in o){
                r.push('"' + i + '"' + ":" + obj2str(o[i]));
            }
            if(!!document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)){
                r.push("toString:"+o.toString.toString());
            }
            r="{"+r.join()+"}";
        }else{
            for(var i =0;i<o.length;i++){
                r.push(obj2str(o[i]));
            }
            r="["+r.join()+"]";
        }
        return r;
    }
    return r.toString();
};
function getValue(selector){
    var v = selector.val().replace(/^\s*|\s*$/g,"");
    v = htmlencode(v);
    return v;
};
function htmlencode(s){
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(s));
    return div.innerHTML;
}
function getByteLen(val) {
    var len = 0;
    for (var i = 0; i < val.length; i++){
        if (val[i].match(/[^\x00-\xff]/ig) != null){ //全角
            len += 2;
        }else{
            len += 1;
        }
    }
    return len;
 }
//公共弹窗组件
(function($){
    function dialogClass(opt){
        this.opt = opt;
        this.$mask = null;
        this.$popup = null;
    };
    dialogClass.prototype.show = function(){
        var self = this;
        if(this.opt.mask){
            this.$mask = this.setMask();
            $("body").append(self.$mask);
            this.$mask.height($(document).height());
        }
        this.$popup = this.drawPopup();
        $("body").append(this.$popup);
        this.setPosition();
        $(window).resize(function(){
            self.setPosition();
        });
    };
    dialogClass.prototype.close = function(fn){
        if(this.$mask){
            this.$mask.remove();
        };
        this.$popup.remove();
        fn && fn();
    };
    dialogClass.prototype.drawPopup = function(){
        var self = this;
        var $popupHtml = $('<div class="popup_container" style="background:#000;background:rgba(0,0,0,0.6);padding:8px;border-radius:3px;position:absolute;top:0;left:0;z-index:'+self.opt.zIndex+';">' +
                            '<div class="pop-up">'+
                                '<div id="dialogContent" class="dialogContent">' +
                                     '<p class="loading">数据加载中...</p>'+
                                '</div>' +
                            '</div>'+
                          '</div>');
        if(this.opt.dialogClass){
            $popupHtml.addClass(this.opt.dialogClass);
        }
        var pos = ($.browser.msie && parseInt($.browser.version,10) <= 6 ) ? 'absolute' : 'fixed'; // IE6 Fix
        $popupHtml.css({
            position: pos,
            zIndex: self.opt.zIndex,
            margin: 0,
            width:self.opt.width + "px",
            height:self.opt.height + "px"
        });
        $popupHtml.find(".pop-up").css({
            "height":self.opt.height,
            "zoom":"1",
            "background":"#fff"
        });
        if(this.opt.title){
            var title = '<div style="height:40px;line-height:40px;padding:0 20px 0 30px;border-bottom:1px solid #d0d0d0;background:#ececec;">'+
                        '<span>'+self.opt.title+'</span>';
            title += '</div>';
            $popupHtml.find(".pop-up").prepend(title);
            $popupHtml.find("#close").click(function(){
                self.close();
            });
        }
        if(this.opt.closeButtom){
            $close = $('<a id="close" href="javascript:void(0)">关闭</a>');
            $close.click(function(){
                self.close();
            });
            $popupHtml.append($close);
        }
        if(this.opt.button){
            var $btnContent = $('<div class="submit"></div>');
            $.each(self.opt.button,function(id,v){
                $('<a id="'+id+'" class="'+v.className+'" style="margin:0 5px;">'+v.name+'</a>').appendTo($btnContent).click(function(){
                    v.fn && v.fn(self.$popup,self);
                   self.close();
                });
            });
            $btnContent.css({"position":"absolute","left":"0","bottom":"20px","width":"100%","text-align":"center"});
            $popupHtml.find(".pop-up").append($btnContent);
        }
        $popupHtml.find("#dialogContent").html(self.opt.content);
        return $popupHtml;
    };
    dialogClass.prototype.setMask = function(){
        var self = this;
        var $mask = $('<div><iframe style="position:absolute;width:100%;height:100%;filter:alpha(opacity=0);opacity=0;border-style:none;"></iframe></div>')
        $mask.css({
            position: 'absolute',
            zIndex: self.opt.zIndex-1,
            top: '0',
            left: '0',
            width: '100%',
            height: '100%',
            background: self.opt.maskColor,
            opacity: self.opt.maskOpacity
        });
        return $mask;
    };
    dialogClass.prototype.setPosition = function(){
        var top = ($(window).height()/2) - ((this.$popup.outerHeight()|this.$popup.height())/2);
        var left = ($(window).width()/2) - ((this.$popup.outerWidth()|this.$popup.width())/2);
        if($.browser.msie && parseInt($.browser.version,10) <= 6 ) {top = top + $(window).scrollTop();}
        this.$popup.css({
            top: top + 'px',
            left: left + 'px'
        });
        this.$mask.css({height:$(document).height()});
    };
    $.dialogObj = {
        defaultOptions:{
            width:600,
            height:300,
            content:'',
            zIndex:1000,
            title:'',
            mask:true,
            dialogClass:'',
            maskColor:'#000',
            maskOpacity:'.15',
            closeButtom:true
        },
        getzIndex:function(){
            var num = $(".popup_container").size();
            return $.dialogObj.defaultOptions.zIndex + num*5;
        },
        dialog:function(opt){
            var options = $.extend({},$.dialogObj.defaultOptions,opt);
            options.zIndex = $.dialogObj.getzIndex();
            var obj = new dialogClass(options);
            obj.show();
            return obj;
        },
        popup:function(){
            var opt = {
                width:300,
                height:150,
                button:{'ok':{'name':'确定',"className":"btn"}},
                dialogClass:this.type
            };
            if(arguments[0]){
                opt.content = arguments[0];
            }
            switch(this.type){
                case "error": // continue
                case "prompt":
                case "warn":
                case "alert":
                    if(arguments[1]){
                        if(typeof arguments[1] == 'function'){
                            opt.button.ok.fn= arguments[1];
                        }else if(typeof arguments[1] == 'object'){
                             $.extend(opt, arguments[1]);
                        }
                    }
                    if(arguments[2] && typeof arguments[2] == 'object'){
                         $.extend(opt, arguments[2]);
                    }
                    break;
                case "confirm":
                    opt.button.cancel = {'name':'取消',"className":"cancel"};
                    if(arguments[1]){
                        if(typeof arguments[1] == 'function'){
                            opt.button.ok.fn= arguments[1];
                        }else if(typeof arguments[1] == 'object'){
                             $.extend(opt, arguments[1]);
                        }
                    }
                    if(arguments[2]){
                        if(typeof arguments[2] == 'function'){
                            opt.button.cancel.fn= arguments[2];
                        }else if(typeof arguments[2] == 'object'){
                             $.extend(opt, arguments[2]);
                        }
                    }
                    if(arguments[3] && typeof arguments[3] == 'object'){
                         $.extend(opt, arguments[3]);
                    }
                    break;
                case "mask":
                    opt.width = 300;
                    opt.height = 50;
                    opt.button = {};
                    opt.closeButtom = false;
                    break;
                default:
                    break;
            }
            var options = $.extend({},$.dialogObj.defaultOptions,opt);
            options.zIndex = $.dialogObj.getzIndex();
            var obj = new dialogClass(options);
            obj.show();
        }
    }
    jDialog = function(opt){
        return  $.dialogObj.dialog(opt);
    };
    jAlert = function(){
        this.type = "alert";
        $.dialogObj.popup.apply(this,arguments);//content,callback,option
    };
    jConfirm = function(){
        this.type = "confirm";
        $.dialogObj.popup.apply(this,arguments);//content,okCallback,cancelCallback,option
    };
    jError = function(){
        this.type = "error";
        $.dialogObj.popup.apply(this,arguments);//content,callback,option
    };
    jPrompt = function(){
        this.type = "prompt";
        $.dialogObj.popup.apply(this,arguments);//content,callback,option
    };
    jWarn = function(){
        this.type = "warn";
        $.dialogObj.popup.apply(this,arguments);//content,callback,option
    };
    setMask = function(){
        this.type = "mask";
        $.dialogObj.popup.apply(this,arguments);//content
    }
    removeMask = function(){
        $(".mask").prev("div").remove();
        $(".mask").remove();
    }
    showMsg = function(text,callback){
        var textInfo = $('<div class="textInfo">'+ text +'</div>');
        $("body").append(textInfo);
        var s = $(window).scrollTop();
        textInfo.animate({
            top:s+"px"
        });
        setTimeout(function(){
            textInfo.animate({
                top:"-30px"
            },function(){
                textInfo.remove();
                callback && callback();
            });
        },1000)
    }
})(jQuery);
$.cookie = function(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        var path = options.path ? '; path=' + options.path : '';
        var domain = options.domain ? '; domain=' + options.domain : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};
//设置左侧菜单高度
function setFrame(){
    var colMain = $(".colMain").height();
    var wh = $(window).height();
    var hh = $(".header").height()+1;
    var sh = $(".sidebar").height()+15;
    $(".colMain").css({
            "height":"auto!important",
            "min-height":(wh-hh)+"px"
    });
   /* if(colMain <= (wh - hh)){
         $(".colMain").css({
                 "height":"auto!important",
                 "min-height":(wh-hh)+"px"
         });
    }
    if((wh - hh) <= sh){
        $(".colMain").css({
            "height":sh+"px",
            "min-height":sh+"px!important"
        });
    }*/
}
$(function(){
    setFrame();
    window.DGC = {};
    $(window).resize(function(){
        setFrame();
    });
    if($.browser.msie && parseInt($.browser.version,10) <= 10 ){
        var browser = $.cookie("browser");
        if(!browser){
            jAlert("为了能得到更好的体验，请使用Firefox/Chrome/Safari/Opera浏览器");
            $.cookie("browser",1);
        }
    }
    //tabs
    $(".tabs").find("li").click(function(){
        var ele = $(this);
        var index = ele.index();
        if(window.DGC.tabsEventController){
            window.DGC.tabsEventController(ele,function(){
                ele.addClass("cur").siblings().removeClass("cur");
                ele.parents(".tabs").next(".tabContent").children(":eq("+index+")").show().siblings().hide();
            });
        }else{
            ele.addClass("cur").siblings().removeClass("cur");
            ele.parents(".tabs").next(".tabContent").children(":eq("+index+")").show().siblings().hide();
        }
        setFrame();
    });
  //表单校验
    $(".number").live("keydown",function(e){
        return isNumber(e,$(this));
    });
    $(".isInt").live("keydown",function(e){
        return isInt(e,$(this));
    });
    $(".isPrice").live("keyup",function(e){
         isPrice(e,$(this));
    });
    /*
    $(".sidebar").find(".title").click(function(){
        $(this).next().slideToggle("fast","linear");
        $(this).parent(".box").siblings().find("ul").slideUp("fast","linear");
    });
    */
});

//javascript错误监控
/*window.onerror = function(msg,url,linenumber,colnumber,obj){
    console.log("%cJS脚本报错：","font-size:16px;font-weight:700;color:#f00");
    console.log("错误信息：",msg);
    console.log("出错文件：",url);
    console.log("出错行数：",linenumber);
}*/
//表单数字校验
wk = function(e) {
    // input value like :123,456.800
    var _keyCode = getEventKeyCodeValue(e);
    if(e.shiftKey){return false;}//禁用shift键，防止输入其他字符
    if ((_keyCode > 47 && _keyCode < 58)|| (_keyCode > 95 && _keyCode < 106) ||(_keyCode > 36 && _keyCode < 41) || (_keyCode == 46)||  (_keyCode == 8) || (_keyCode == 9) || (_keyCode == 188) || (_keyCode == 190)|| (_keyCode == 110)) {
        return true;
    }
    return false;
};
english = function(e) { // 大小写E文字母
    var _keyCode = getEventKeyCodeValue(e);
    if ((_keyCode >= 65 && _keyCode <= 122) || (_keyCode == 8)|| (_keyCode == 9)) {
        return true;
    }
    return false;
};
isNumber = function(e) {// 只允许数字
    if (wk(e)) {
        var _keyCode = getEventKeyCodeValue(e);
        if (_keyCode == 44) {
            return false;
        }
        return true;
    } else {
        return false;
    }
};
var record= {
        num : ""
}
isPrice = function(e,n) {// 只允许价格格式0.00
    var decimalReg=/^\d{0,8}\.{0,1}(\d{1,2})?$/;//var decimalReg=/^[-\+]?\d{0,8}\.{0,1}(\d{1,2})?$/;
    if(n.val() != "" && decimalReg.test(n.val())){
        record.num =  n.val();
    }else{
        if(n.val() != ""){
            n.val(record.num);
        }
    }
};
isInteger = function(e) { // 只允许整数
    if (wk(e)){
        var _keyCode = getEventKeyCodeValue(e);
        if ((_keyCode == 44) || (_keyCode == 190)) {//190为句号键
            return false;
        }
        return true;
    }else{
        return false;
    }
};
isTel = function(e){ //电话号码，只允许整数和"-"
    var _keyCode = getEventKeyCodeValue(e);
    if(e.shiftKey){return false;}//禁用shift键，防止输入其他字符
    if ((_keyCode > 47 && _keyCode < 58)|| (_keyCode > 95 && _keyCode < 106) ||(_keyCode > 36 && _keyCode < 41) ||  (_keyCode == 8) || (_keyCode == 9) || (_keyCode == 188) || (_keyCode == 109)||(_keyCode == 189)) {
        return true;
    }else{
        return false;
    }
};
isInt = function(e,ele) { // 只允许整数,且不能以0开头，percent不能超过100
    var _keyCode = getEventKeyCodeValue(e);
    if(e.shiftKey){return false;}//禁用shift键，防止输入其他字符
    if ((_keyCode > 47 && _keyCode < 58)|| (_keyCode > 95 && _keyCode < 106) ||(_keyCode > 36 && _keyCode < 41) ||  (_keyCode == 8) || (_keyCode == 9) || (_keyCode == 188) ) {
        var self = ele;
        var oldv = self.val();
        setTimeout(function(){
            var newv = self.val();
            var newva = [];
            if(newv){
                if(newv == 0 && (e.keyCode == 96 || e.keyCode == 48)){
                    self.val("");
                }else{
                    self.val(+newv);
                }
            }
            if(self.hasClass("percent")){
                if(+newv > 100){
                    self.val(oldv);
                }
            }
            return true;
        },10);
    }else{
        return false;
    }
};
getEventKeyCodeValue = function(e) {
    e = e || event;
    return e.keyCode || e.which;
};
/*function getTextLength(str){
    return str.replace(/[^\x00-\xff]/g,"xx").length;
};
function chechMaxLength(ele){
    var ele = ele;
    var max = ele.attr("maxlength");
    var str = ele.val();
    var length = 0;
    $.each(str,function(i,v){
        length += getTextLength(v);
    });
    console.log(length);
    if(length > max){
        ele.val(str.slice(0,-1));
    }
}*/

//input值校验，当前值是否已经存在
/*
 * ele : 需要检测的元素，jquery对象
 * data ：参数
 * msg ：错误时提示信息
 * url : 校验地址
 *
 * */
function checkValue(option){
    var opt = $.extend({
            ele:"",
            data:{},
            msg:"该字符串已被使用",
            url:"/tokens/checkNameUnique"
    },option);
    opt.ele[0].onpropertychange = function(e){
        _change($(this));
    };
    opt.ele[0].oninput = function(e){
        _change($(this));
    };
    var timeId = null;
    function _change(ele,v){
        if(timeId){
            clearTimeout(timeId);
        }
        timeId = setTimeout(function(){
            var v = ele.val();
            if(v){
                opt.data.name = v;
                setParam(opt.url,opt.data,ele,opt.msg);
            }else{
                ele.siblings(".error").remove();
            }
        },300);
    }
    function setParam(url,data,ele,msg){
        $.ajax({
            type:"GET",
            url:url,
            dataType:"json",
            data:data,
            success:function(respon){
                if(respon.code == 0){
                    ele.siblings(".error").remove();
                }else{
                    if(ele.siblings(".error").size() <= 0){
                        ele.after('<span class="error">'+msg+'</span>');
                    }else{
                        ele.siblings(".error").text(msg);
                    }
                }
            }
        })
    }
}