(function() {
	"use strict";
	window.confirm = function(message,fn){
		layer.confirm(message, {btn: ['确定','取消'],title:false },function(index){ fn && fn();  layer.close(index);},function(){});
	}
	window.msg = function(tip){layer.msg(tip||"操作成功！", {time: 2000, icon:1});}
	window.alert = function(tip){layer.msg(tip||'操作失败！', {time: 2000, icon:5});}

	var app = angular.module("admin.mainframe", ["ngRoute",'ui.bootstrap',"dataService","pascalprecht.translate","ngAnimate","ng.ueditor"]);
	app.filter("formatDate",function(){
		return function(input,fmtStr){
			if(!input) return "";
			return new Date(input).format(fmtStr,'zh-CN')
		}
		//	  va.date = new Date(va.beginTime).format("{MM}-{dd} {weekday}");
		//	  va.time = new Date(va.beginTime).format("{HH}:{mm}",'zh-CN');
	});
	//取消html转译
	app.filter('trustHtml', function ($sce) {
        return function (input) {
            return $sce.trustAsHtml(input);
        }
    });
	 app.filter("getRadio",function(){
		return function(list){
			var out = "";
			if(typeof list ==='object' && list.length > 0){
				var passed = list.findAll(function(v){return v.status == 1});
				out ="("+ passed.length+"/"+list.length+")";
			}
			return out;
		}
  	});
		
	//用于状态由key 转为对应名称: 如1 指 草稿.
	app.filter("keyToValue",function(){
		return function(input,dictList){
			if(!input&& input!='0') return "";
		   var pair = dictList && dictList.find(function(n) {return n[0] == input});
		   return pair && pair[1];
		}
	});
	
	//获取管理员account
	app.filter("idToValue",function(){
		return function(input,dictList){
			if(!input&& input!='0') return "";
		   var obj = dictList && dictList.find(function(n) {return n.id == input});
		   return obj && obj.account;
		}
	});
	
	//取消html转译
	app.filter('trustHtml', function ($sce) {
        return function (input) {
            return $sce.trustAsHtml(input);
        }
    });
	
	//用于状态由key 转为对应名称: 如1 指 草稿.
	app.filter("getNature",function(){
		return function(input,natureList){
			var names = [];
			natureList.each(function(n){
				if(n.code & input){
					names.push(n.name);
				}
			})
			return names.join();
		}
	});
	
	app.filter("toPercent",function(){
		return function(input){
			if(!input || input ==0){
				return 0;
			}else{
				return parseInt(input * 100)+"%";
			}
		}
	});
	//截取显示用户反馈内容
	app.filter('cut', function () {
        return function (value, wordwise, max, tail) {
            if (!value) return '';
            max = parseInt(max, 10);
            if (!max) return value;
            if (value.length <= max) return value;
            value = value.substr(0, max);
            if (wordwise) {
                var lastspace = value.lastIndexOf(' ');
                if (lastspace != -1) {
                    value = value.substr(0, lastspace);
                }
            }
            return value + (tail || ' …');
        };
    });		
	
	app.directive('compileText', function($compile) {
	      // directive factory creates a link function
	      return function(scope, element, attrs) {
	        scope.$watch(
	          function(scope) {
	             // watch the 'compile' expression for changes
	        	  
	            return scope.$eval(attrs.compileText);
	          },
	          function(value) {
	            // when the 'compile' expression changes
	            // assign it into the current DOM
	        	var div = document.createElement("div");
	        	div.innerHTML = value;
	        	var text = div.innerText;
	        	if(attrs.limit){
	        		text = text.substr(0,attrs.limit)
	        	}
	            element.html(text);
	            // compile the new DOM and link it to the current
	            // scope.
	            // NOTE: we only compile .childNodes so that
	            // we don't get into infinite loop compiling ourselves
	           // $compile(element.contents())(scope);
	          }
	        );
	      };
	    });
	
	app.directive('textDate', function() {
	    return {
	        require: 'ngModel',
	        link: function(scope, element, iAttrs, modelCtrl) {
	        	 
	            modelCtrl.$parsers.push(function(input) {
	            	return new Date(input).getTime();
                }); 
                modelCtrl.$formatters.push(function(paths) {
                	return paths && new Date(paths).format("{yyyy}-{MM}-{dd} {HH}:{mm}");
                });
	        }
	    };
	});
	
	app.directive('compile', function($compile) {
	      // directive factory creates a link function
	      return function(scope, element, attrs) {
	        scope.$watch(
	          function(scope) {
	             // watch the 'compile' expression for changes
	            return scope.$eval(attrs.compile);
	          },
	          function(value) {
	            // when the 'compile' expression changes
	            // assign it into the current DOM
	            element.html(value);
	            // compile the new DOM and link it to the current
	            // scope.
	            // NOTE: we only compile .childNodes so that
	            // we don't get into infinite loop compiling ourselves
	            $compile(element.contents())(scope);
	          }
	        );
	      };
	    });
	  app.directive('between',function() {
		  return {
		    require: 'ngModel',
		    link: function(scope, ele, attrs, c) {
		      scope.$watch(attrs.ngModel, function(v) {
		    	  if(v == null){
		    		  return;
		    	  }
		    	  var data = JSON.parse(attrs.between);
		    	  var lt = data.lt;
		    	  var gt = data.gt;
		    	  if((lt==undefined || v <= lt) && (gt ==undefined || v >=gt)){
		    		  c.$setValidity('between', true);
		    	  }else{
		    		  c.$setValidity('between', false);
		    	  }
		      });
		    }
		  }
		});
	
		app.directive('imageLoad', function () {
		    return {
		        restrict: 'A', link: function (scope, element, attrs) {
		            element.bind('load', function () { 
		                //call the function that was passed 
		                scope.$apply(attrs.imageLoad);
		            });
		        }
		    };
		})
	  app.directive('errSrc', function() {
		  return {
		    link: function(scope, element, attrs) {
		      element.bind('error', function() {
		        if (attrs.src != attrs.errSrc) {
		          attrs.$set('src', attrs.errSrc);
		        }
		      });
		    }
		  }
		});

})();
function clickDownload()  
{  
     var ss = "\ufeff";
		$("table.table tr").each(function(i,v){
			 var cols = $("td,th", v).length;
			$.each($("td,th", v),function(i,td){
				    if(i<cols && $(td).attr("excel")!="none") {
				    		//ss+=$(td).html().stripTags().remove(/\s/g).replace(/,/g,'，').replace(/&nbsp;/g,' ')+",";
				    	ss +=$(td)[0].innerText.remove(/[\f\n\r\t\v]/g).replace(/,/g,"，")+",";
				    }
				});
			ss+="\n";
		})
	 download(document.title+".csv",ss);
//	   str =  encodeURIComponent(ss);  
//     $("#download")[0].href = "data:text/csv;charset=utf-8,"+str; 
//     $("#download")[0].download=document.title+".csv";
//     $("#download")[0].click();  
}  