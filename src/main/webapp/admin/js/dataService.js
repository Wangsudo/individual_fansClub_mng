(function() {
	"use strict";
	var dataService = angular.module('dataService', []);
	
	dataService.factory("httpInterceptor", ['$log','$q','$translate','$rootScope',function($log,$q,$translate,$rootScope) {
		var httpInterceptor = {   
				'responseError' : function(response) {
					layer.close($rootScope.waiting);
					if (response.status == 902) {
			        	$translate("SESSION_ERASED").then(function (msg) {
			        		 layer.confirm(msg, {btn: ['ok'],title:false},function(index){
			        			 window.location.href="login.html"
			   		       });
			        	});
						return $q.reject(response);        
					} else if (response.status == 901) {
						window.location.href="login.html"
						return $q.reject(response);        
					} else if (response.status === 404) {  
						alert("404!");      
						return $q.reject(response);  
					}      
			  	}, 
				'response' : function(response) {       
					layer.close($rootScope.waiting);
					return response;       },    
				'request' : function(config) {
					if(/\/checkUnique|\/search|\/getActive/.test(config.url)){
						return config;
					}
					$rootScope.waiting = layer.load(1, {
						  shade: [0.2,'#000'] //0.1透明度的白色背景
					 });   
					return config;       }      
			//	'requestError' : function(config){         ......         return $q.reject(config);       }     
				}   
		return httpInterceptor; 
	}]);
	
	dataService.config(['$httpProvider', function($httpProvider) { 
		$httpProvider.interceptors.push('httpInterceptor');
	}]);
	

	dataService.factory("appData", function($http,$filter,$q) {
		return {
		getMenus:function(fn){
			$.ajax({url:"../menu/all",async:false,success:function(data){
				fn&&fn(data);
			}});
		},
		getStaticDicts:function(){
			var deferred = $q.defer();
			$http.get("/admin/i18n/dictionary.json").success(function(data){
				deferred.resolve(data);
			})
			return deferred.promise;
		},
		getDynaDicts:function(){
			var deferred = $q.defer();
			$http.get("../common/getDicts").success(function(data, status, headers, config) {
				  deferred.resolve(data);
			})
			return deferred.promise;
		},
		
		setTimeStamp:function(item,admin){
			//若存在ID,即是修改,否则为新增
		      if(item.id){
		    	  item.modifier = admin.id;
		    	  item.modifyTime = new Date().getTime();
		      }else{
		    	  item.modifier = item.creater =admin.id;
		    	  item.modifyTime =  item.createTime = new Date().getTime();
		      }
		},
		
		//获取分页查询结果
		getPageResult:function(url,param,fn){
			$http.post(url,param).success(function(data, status, headers, config){
	    		/**
	    		 * 始终得有第一页和最后一页.当前页 ,前一页,后一页.
	    		 */
	    		var list =[1,data.currentPage-1,data.currentPage,data.currentPage+1,data.totalPage];
	    		var pages = [];
	    		var hash ={};
	    		list.forEach(function(v){
	    			if(!hash[v]&& v<=data.totalPage && v>0){
	    				hash[v] =true;
	    				pages.push(v);
	    			}
	    		})
	    		if(pages.length>2&&pages.indexOf(2)==-1){
	    			pages.splice(1,0,'······');
	    		}
	    		if(pages.length>2&&pages.indexOf(data.totalPage-1)==-1){
	    			pages.splice(pages.length-1,0,'······');
	    		}
	    		data.pages = pages;
				fn&&fn(data);
			  }).error(function(data, status, headers, config) {
				  console.log("获取列表异常");
			  }); 
		},
		updateTime:function(item,admin){
			 //若存在ID,即是修改,否则为新增
		      if(item.id){
		    	  item.modifier = admin.id;
		    	  item.modifyTime = new Date().getTime();
		      }else{
		    	  item.modifier = item.creater =admin.id;
		    	  item.modifyTime =  item.createTime = new Date().getTime();
		      }
		},
		// 本地导入
		uploadImage: function(options,fn) {
			if(typeof(options.params)!="object"){
				options.params = {};
			}
			if(!options.url){
				options.url = '../common/uploadImg';
			}
			var index;
			options.onSubmit = function(){
				 index = layer.load(1, {
					  shade: [0.2,'#000'] //0.1透明度的白色背景
				 });
			};
			options.onComplate = function(data) {
				layer.close(index);
				if(data.success){
					fn&&fn(data);
				}else{
					layer.alert(data.error,{btn: ['ok'],title:false});
				}
			};
			// 上传方法
			$.upload(options);
		},
		// 本地导入
		uploadImages: function(options,fn) {
			if(typeof(options.params)!="object"){
				options.params = {};
			}
			if(!options.url){
				options.url = '../common/uploadImgs';
			}
			var index;
			options.onSubmit = function(){
				 index = layer.load(1, {
					  shade: [0.2,'#000'] //0.1透明度的白色背景
				 });
			};
			options.onComplate = function(data) {
				layer.close(index);
				if(Array.isArray(data)){
					fn&&fn(data);
				}else if(Object.isObject(data)){
					if(data.success){
						fn&&fn(data);
					}else{
						layer.alert(data.error,{btn: ['ok'],title:false});
					}
				}else{
					layer.alert("发生错误:"+data,{btn: ['ok'],title:false});
				}
			};
			// 上传方法
			$.upload(options);
		}
		 
	}});
})();