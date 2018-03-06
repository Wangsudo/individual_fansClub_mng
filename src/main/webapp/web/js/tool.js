//获取url地址参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURIComponent(r[2]); return null;
}

$(function(){
    //文本框焦点样式
    $("body").on("focus",".search input,.search textarea",function(){
        $(this).parent().addClass("focus");
    }).on("blur",".search input,.search textarea",function(){
        $(this).parent().removeClass("focus");
    })


//    $("body").on("click",".checkbox",function(){
//        var $this=$(this);
//        if($this.hasClass("all-checkbox")){
//            if($this.hasClass("selected")){
//                $(".checkbox").removeClass("selected");
//            }else{
//                $(".checkbox").addClass("selected");
//            }
//            $(".checkbox")
//        }else{
//            if($this.hasClass("selected")){
//                $this.removeClass("selected");
//            }else{
//                $this.addClass("selected");
//            }
//        }
//
//    })
})