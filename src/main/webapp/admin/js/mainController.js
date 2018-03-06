(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	window.tips = function(message,id){
		layer.tips(message, id, {
				tips: [4, '#ff784e'],
				time: 1000
			}
		);
	}

	// 路由
	app.config(function($routeProvider) {
		$routeProvider.when('/:module', {
			templateUrl : function($routeParams) {
                return $routeParams.module+".html";
            }
		}).when('/:module/:detail', {
			templateUrl : function($routeParams) {
                return $routeParams.detail+".html";
            }
		}).otherwise({
			redirectTo : '/welcome'
		});
	});
	//国际化
	app.config(['$translateProvider',function($translateProvider){
        $translateProvider.useStaticFilesLoader(
        		{
        			prefix: 'i18n/',
        			suffix: '.json'
        		}
    		 );
        var lang = window.localStorage.lang||'zh';
        window.localStorage.lang = lang;
    	$translateProvider.preferredLanguage(lang);
    }]);
	
	//弹框
	app.config(['$uibModalProvider',function($uibModalProvider){
		$uibModalProvider.options = {backdrop:'static',keyboard:true};
    }]);

	// 控制器
	app.controller('mainController', function($scope,$rootScope,$location, $q,$http,$translate,$uibModal,appData) {
		//$scope.menus = appData.menus;
		appData.getMenus(function(data){
			$rootScope.menus = data.list;
			
		    var menuPlayer = $rootScope.menus.find(function(n){return n.code == "a" });
		    var itemPlayer = menuPlayer.items.find(function(n){return n.page == "player"});
			//获取未注册球员数目
		    itemPlayer && $http.get("../player/statics").success(function(resp){
		    	itemPlayer.unAuditNo = resp.unAuditNo; 
			})
			
		})
		// 默认模块
		$rootScope.postConfig = {
				  transformRequest:function(obj){
						var str=[];
						if(typeof obj === 'object'&&!obj.length){
							for(var p in obj){
								str.push(encodeURIComponent(p) +"="+encodeURIComponent(obj[p]));
							}
						}else if(typeof obj === 'object'&&obj.length>0){
							for(var p in obj){
								str.push(encodeURIComponent(obj[p].name) +"="+encodeURIComponent(obj[p].value));
							}
						}
						return str.join("&");
					}, 
				headers:{'Content-Type':'application/x-www-form-urlencoded'}
	    };
		
		$rootScope.$on('$locationChangeSuccess', function(event, newUrl){
			console.log(arguments);
			$scope.subpage ={};
			var regExp = /#(\/)?(\w+)(\/)?(\w+)?(\/)?(\w+)?/;
			var matches = newUrl.match(regExp);
			if(matches && matches[2]){
				$scope.page=matches[2];
				
				var o = $scope.menus.find(function(n){return n.page == $scope.page || n.items.some(function(v){return v.page == $scope.page}) });
				if(o && o.items.length == 0){
					//一级菜单选中
					o.active = true;
					$rootScope.curMenu = o;
				}else if(o && o.items.some(function(v){return v.page == $scope.page})){
					//一级菜单展开
					o.isCollapsed = true;
					var item = o.items.find(function(v){return v.page == $scope.page})
					//二级菜单选中
					item.active = true;
					$rootScope.curMenu = item;
				}
			}
			
		})
		//菜单
		
		$scope.showstatus = function(){
			$scope.status = true;
		}
		// 强制刷新视图
		$rootScope.synData = function(){
			$scope.$$phase || $scope.$apply();
		};
		
		$rootScope.getCheckClass = function(access,digi){
			 return	{'green':(access& digi),'glyphicon-check':(access& digi),'red':!(access& digi),'glyphicon-unchecked':!(access& digi)}; 
		} 
		$rootScope.getTabError = function(form,field){
			if(form.$submitted&&!form[field].$valid){
				return {'border':'1px solid rgba(255, 0, 0, 0.59)',
					'border-bottom': 'none',
					'-moz-box-shadow':'0 -1px 1px rgba(255, 0, 0, 0.59)',
				    '-webkit-box-shadow':'0 -1px 1px rgba(255, 0, 0, 0.59)',
				    'box-shadow':'0 -1px 1px rgba(255, 0, 0, 0.59)'}
			}
		}
		
		$rootScope.openTip = function(content){
			layer.open({
				  type: 1,
				  skin: 'layui-layer-rim', //加上边框
				  area: ['620px', '440px'], //宽高
				  content: content
				});
		}
		$scope.pop = function(url){
			var img = event.target;
			var rect = img.getBoundingClientRect();
			var popImg = document.getElementById("popImg");
			popImg.style.width=rect.width*1.2+"px";
			popImg.style.height=rect.height*1.2+"px";
			popImg.style.top = rect.top-rect.height*0.1+ document.body.scrollTop+"px";
			popImg.style.left = rect.left-rect.width*0.1+ document.body.scrollLeft+"px";
			$scope.curPicUrl = url;
			$scope.mouseOn = true;
		}
		
		$rootScope.delItem = function(pics,pic){
        	 pics.remove(function(n){return n == pic})
        }
		//排序
		$rootScope.moveItem = function(items,item,dct){
			//dct 为1 时右移, -1时左移
			var curIndex = items.indexOf(item);
			var nextSeq = (curIndex+dct)%(items.length);
			items.remove(item);
			items.add(item,nextSeq)
			items.each(function(v,i){
				v.seq = i;
			})
		 } 

		//比较两个对象是否完全相等
		$rootScope.isEqual = function(a,b){
			return Object.equal(a,b);
		 } 
		
		$rootScope.main = {size:1,
			defaultImg :"/admin/images/ic_photo_grey600_48dp.png",
			banner:[1440,800]
		};
		
		$rootScope.resolutionDemand = function(label){
			var dimens = $rootScope.main[label];
			if(Array.isArray(dimens) && dimens.length==2){
				var width = dimens[0] ;
				var height = dimens[1] ;
				return width +"X"+height;
			}
		}
		 //获取不同尺寸的图片
		$rootScope.getImgUrl = function(paths,size){
			size = size||1;
			if(/{.+}/.test(paths)){
				try{
					var pathObj =JSON.parse(paths)||{};
					if(pathObj[size]){
						return pathObj[size];
					}
				}catch(e){
				}
			}else if(/\/.+/.test(paths)){
				return paths;
			}
			return $rootScope.main.defaultImg;
		}
		
		$rootScope.checkDimens = function(picData,label,fn){
			var dimens = $rootScope.main[label];
			if(Array.isArray(dimens) && dimens.length==2){
				var width = dimens[0] ;
				var height = dimens[1] ;
				if(width!=picData.width || height!=picData.height  ){
					  fn && fn();
					/*$translate('RESOLUTION_WRONG').then(function (msg) {
						msg = msg.replace("{0}",width +"X"+height).replace("{1}",picData.width +"X"+picData.height);
		        	    layer.alert(msg,{btn: ['ok'],title:false});
		        	    fn && fn();
		        	});*/
				}else{
					fn && fn();
				}
			}else{
				fn && fn();
			}
		}
	
		//获取缺少的img
		$rootScope.getMissing = function(imgList){
			if(!imgList){
				return;
			}
			var cnt = 0;
			imgList.each(function(n){
				if(n == null){
					cnt++;
				}
			})
			return cnt;
		}
		
		$rootScope.getTeam = function(val,teamType,cupId){
			 return $http.get('/team/search', {
			      params: {
			        name: val,
			        teamType:teamType,
			        cupId:cupId,
			        pageSize: 10
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 }
	
		$rootScope.getPlayer = function(val,isCaptain){
			 return $http.get('/player/search', {
			      params: {
			        name: val,
			        pageSize: 10,
			        isCaptain:isCaptain
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 }
		$rootScope.getCup = function(val,isPublic){
			 return $http.get('/cup/search', {
			      params: {
			        name: val,
			        pageSize: 10,
			        isPublic:isPublic
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 }
		
		
		  $scope.getLocation = function(val) {
			  return $http.get('/field/search', {
			      params: {
			        name: val,
			        pageSize: 10
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 };
		 
		//自定义layer.confirm弹窗
        $rootScope.confirm = function(url,MSG,fn){
        	$translate(MSG).then(function (msg) {
        		 layer.confirm(msg,{btn: ['ok','cancel'],title:false},function(index){
   				  $http.get(url).success(function(data){
   					  fn&& fn(data);
   					  layer.close(index);
   					});
   		       });
        	});
		}
        
        //自定义layer.alert弹窗
        $rootScope.alert = function(MSG){
        	$translate(MSG).then(function (msg) {
        		 layer.alert(msg,{btn: ['ok'],title:false});
        	});
		}
        
        //自定义layer.tips弹窗
        $rootScope.tips= function(MSG,id){
        	$translate(MSG).then(function (msg) {
        		 layer.tips(msg,id);
        	});
		}
        
        //切换语言
		$scope.changeLocale = function(){
			var lang = window.localStorage.lang;
			if( lang =='en'){
				lang = "zh";
			}else{
				lang ="en";
			}
		        $translate.use(lang);
		        window.localStorage.lang = lang;
	     };
	 	  
		$scope.gotoPage = function(hash){
			$location.url(hash);
		}
		

		// 获取SESSIONID
		var sessionKeeper = function() {
			$http({
				method : "GET",
				url : "../adminLogin/keepSession?jssessionid=" + $scope.JSSESSIONID
			}).success(function(data, status, headers, config) {
				$scope.JSSESSIONID = data.JSSESSIONID;
			});
		};
		
		//显示大图
		$rootScope.showPhoto = function(picUrl){
			var src = event.srcElement.getAttribute("src");
			if(picUrl ==$rootScope.main.defaultImg ||src == $rootScope.main.defaultImg){
			return;
	    	}
			$rootScope.curPic = {url:picUrl,on:true};
			var width = window.innerWidth*0.9;
			var height = window.innerHeight*0.9;
		};
		
		$rootScope.showYoukuPlayer = function(id){
		   var modal = $uibModal.open({
			      animation: true,
			      templateUrl: 'youkuPlayer.html',
			      controller: 'videoAddController',
			      resolve:{id:id}
		   });
		   modal.result.then(function (args) {
			  }, function () {
			  });
        }
		 
		$rootScope.wheel = function(imgUrl){
        	if(imgUrl!=$rootScope.main.defaultImg){
        		wheelzoom(event.target);
        	}else{
        		event.target.dispatchEvent(new CustomEvent('wheelzoom.destroy'));
        	}
        }
		 
		$scope.clickModule = function(menu){
			var o = $scope.menus.find(function(n){return n.isCollapsed && n.name!= menu.name});
			if(o){
				o.isCollapsed = false;
				if(o.items.length == 0){
					o.active = false;
				}else{
					var item = o.items.find(function(n){return n.active});
					item && (item.active = false);
				}
			}
			menu.isCollapsed = !menu.isCollapsed;
			if(menu.items.length == 0){
				menu.active = true;
				menu.page && $scope.gotoPage(menu.page);
			}
		}
		$scope.clickItem = function(item){
			var o = $scope.menus.find(function(n){return n.active});
			o && (o.active = false);
			o = $scope.menus.find(function(n){return n.isCollapsed});
			var activeItem = o.items.find(function(n){return n.active && n.name != item.name});
			activeItem && (activeItem.active = false);
			item.active = true;
			item.page && $scope.gotoPage(item.page);
		}
		
		//获取相功能当前用户是否有权限
		$rootScope.getAccess = function(menu,digi,field){
			if($scope.admin && $scope.admin.role.id ==1){
				return true;
			}
			//球场权限
			if(field && field.adminId ==$scope.admin.id ){
				return true;
			}
			//父菜单
			if($.isEmptyObject(menu.items)){
				return menu.access&digi;
			}else if(menu.items.find(function(n){return n.access &1})){
				return true;
			}else{
				return false;
			}
		}
		
		//获取球员或者球队的权限
		$scope.getPermission = function(entity,isPlayer){
			var locks = entity.locks;
			var accessJson;
			if(isPlayer){
				accessJson = $.extend(true,{},$rootScope.playerPermissions);
			}else{
			   accessJson = $.extend(true,{},$rootScope.teamPermissions);
			}
		
			if(/^\{.+\}$/.test(locks)){
				try{
					var json =JSON.parse(locks)||{};
					 $.extend(true,accessJson,json);
				}catch(e){
				}
			}
			var list = [];
			for(var i in accessJson){
				var n = accessJson[i];
				n.v = n.v==2?2:1;
 				if(n.toDate){
 					var now = new Date().getTime();
 					var deadLine = new Date(n.toDate).getTime();
 					if(deadLine < now ){
 						n.v =1;
 					}
 				}
 				n.code = i;
 				list.push(n);
			}
			return list;
		}
		
		
		 /**
			 * 打开权限编辑框
			 */
			$scope.permissionModal = function(entity,url,playerOrTeam){
				var access = $scope.getPermission(entity,playerOrTeam =="player")
				 var modal = $uibModal.open({
				      animation: true,
				      size:'lg',
				      templateUrl: 'permissionModal.html',
				      controller: 'permissionModalController',
				      resolve: {obj:function(){return {url:url,access:access,entity:entity}} }
				    });
			     modal.result.then(function () {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
			}; 
			
		//获取 banner等 当前状态
		$rootScope.getState = function(status,startTime,stopTime){
			if(status != 2){
				return "DRAFT";
			}else if(status == 2){
				var now = new Date();
				if(now < new Date(startTime)){
					return 'SCHEDULED';
				}else if(!!stopTime && now >= new Date(stopTime)){
					return 'OFF_LINE'
				}else{
					return "ON_LINE"
				}
			}
		}
		
		$scope.logout = function(){
			$http.get("/adminLogin/clearSession").success(function(resp){
				if(resp.success){
					window.location.href='/admin/login.html';
				}
			})
		}	
		function getLoginUser(){
			var deferred = $q.defer(); // 声明延后执行，表示要去监控后面的执行  
			$http.get("../adminLogin/getUserbySession?t="+new Date().getTime()).success(function(admin){
//				debugger;
				if(admin.role && admin.role.menus){
					 $scope.menus.each(function(menu){
						 //获取当前role 在一级菜单上的权限
						 var ace = admin.role.menus.find(function(n){
							 return menu.id == n.menuId;
						 })
						 if(ace){
							 menu.access = ace.access;
						 }else{
							 menu.access = 0;
						 }
						 menu.items.each(function(submenu){
							 ace = admin.role.menus.find(function(n){
								 return submenu.id == n.menuId;
							 })
							 if(ace){
								 submenu.access = ace.access;
							 }else{
								 submenu.access = 0;
							 }
						 })	
					 })
					$rootScope.admin = admin;
					deferred.resolve(admin);
				}else{
					$scope.logout();
				}
			}).error(function(data, status, headers, config) {  
		          deferred.reject(data);   // 声明执行失败，即服务器返回错误  
		    });  
		    return deferred.promise;   // 返回承诺，这里并不是最终数据，而是访问最终数据的API   
		}
		
		var dynaDictPromise = appData.getDynaDicts().then(function(data){
			$rootScope.roleList = data.roleList||[];
			$rootScope.adminList = data.adminList||[];
		});
		var staticDictPromis = appData.getStaticDicts().then(function(data){
			$.extend($rootScope,data);
		})
		var loginPromise = getLoginUser();
		
		$rootScope.preparePromise = $q.all([dynaDictPromise,staticDictPromis,loginPromise])
	
//		sessionKeeper();
//		setInterval(sessionKeeper, 600000);
		//修改管理员信息
		 $scope.showinfo = function(adminUser){
			 $scope.status = false;
			   var modal = $uibModal.open({
				      animation: true,
				      size:'min',
				      templateUrl: 'adminInfo.html',
				      controller: 'adminAddController',
				      resolve: {adminUser: function(){	
				    	  adminUser = $.extend({},adminUser);
					      return adminUser;
				    	  }
				    }});
		      modal.result.then(function () {
			  }, function () {
			  });
       }

	});
})();