(function() {
	"use strict";
	var app = angular.module("football", ["ngRoute","dataService",'ui.bootstrap',"ngAnimate",'ngCookies',"meta.umeditor"]);
	//filter
	app.filter("keyToValue",function(){
		return function(input,dictList){
			if(!input&& input!='0') return "";
		   var pair = dictList && dictList.find(function(n) {return n[0] == input});
		   return pair && pair[1];
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
	app.filter("likeNum",function(){
		return function(input){
			var out = 0;
			if( input && input.length > 0){
				var list = input.split(",");
				out = list.length;
			}
			return out;
		}
	});
	app.filter("removeSec",function(){
		return function(input){
			var out = input;
			if(/\d{2}:\d{2}:\d{2}/.test(input)){
				out = input.substr(0,input.lastIndexOf(":"))
			}
			return out;
		}
	});
	
	app.filter("getDate",function(){
		return function(input){
			if(!input) return "";
			if(new Date(input) == 'Invalid Date') return input;
			return new Date(input).format("{yyyy}-{MM}-{dd}")
		}
	});
	app.filter("getTime",function(){
		return function(input){
			if(!input) return "";
			return new Date(input).format("{HH}:{mm}:{ss}")
		}
	});
	app.filter("formatDate",function(){
		return function(input,fmtStr){
			if(!input) return "";
			if(new Date(input) == 'Invalid Date') return input;
			return new Date(input).format(fmtStr,'zh-CN')
		}
	//	  va.date = new Date(va.beginTime).format("{MM}-{dd} {weekday}");
	//	  va.time = new Date(va.beginTime).format("{HH}:{mm}",'zh-CN');
	});
	app.filter("getAge",function(){
		return function(input){
			if(!input){
				return "";
			}
			return new Date().yearsSince(input);
		}
	});
	
	app.filter("toPercent",function(){
		return function(input){
			if(!input || input ==0){
				return 0;
			}else{
				return (input * 100).toFixed(1)+"%";
			}
		}
	});
	app.filter("activityRate",function(){
		return function(input,createTime){
			if(!input || !createTime){
				return 0;
			}else{
				var days = new Date(createTime).daysFromNow();
				if(days == 0){
					return 0;
				}else{
					return parseInt(input/days * -100)+"%";
				}
			}
		}
	});
	
	
	app.filter("getConfirms",function(){
		return function(tanks){
			if($.isEmptyObject(tanks)){
				return 0;
			}
			var showups = tanks.findAll(function(v){return v.confirmStatus == 1});
			return showups.length;	
		}
	});
	
	app.filter("splitLikes",function(){
		return function(likes){
			var likeList = likes?likes.split(','):[];
			return likeList.length;	
		}
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
	
	
})();