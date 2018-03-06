(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('roleController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {pageSize:10};
			
		$scope.find = function(pageNo){
				appData.getPageResult("../role/list",{
					  currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize
				  },function(data){
					  $scope.items = data;
				  });
		   };
		   $scope.del = function(id){
				  var url = "../role/del/"+id;
				  $scope.confirm(url,'DEL_MSG',function(data){
						if (data.success==false) {
							$scope.tips('DEL_ADMIN', '#delRole'+role.id+'');
						}else {
							$scope.find();
							appData.getDynaDicts().then(function(data){
								$rootScope.roleList = data.roleList||[];
							})
						}
				  });
		   };
		 //显示添加角色
		   $rootScope.showModal = function(roleId){
				   var modal = $uibModal.open({
					      animation: true,
					      size: 'lg',
					      templateUrl: 'roleAdd.html',
					      controller: 'roleAddController',
					      resolve: {roleId:roleId}
					    });

				   modal.result.then(function () {
					   $scope.find();
					   
					  }, function () {
						  $scope.find();
					      console.info('Modal dismissed at: ' + new Date());
					  });
				   
			   }
			 
			 
			   $scope.find();
	});
	   
	//添加管理员
	app.controller('roleAddController', function($scope,$http,$uibModalInstance,$rootScope,roleId,appData) {
		$scope.curMenus = $.extend(true,[],$scope.menus);
		if(roleId){
			$http.get("../role/"+roleId).success(function(role){
				$scope.role = role;
				//将当前角色的菜单权限赋予菜单, 用于显示在页面中
				 $scope.curMenus.each(function(menu){
					 //获取当前role 在一级菜单上的权限
					 var ace = role.menus.find(function(n){
						 return menu.id == n.menuId;
					 })
					 if(ace){
						 menu.access = ace.access & menu.power;
					 }else{
						 menu.access = 0;
					 }
					 
					 menu.items.each(function(submenu){
						 ace = role.menus.find(function(n){
							 return submenu.id == n.menuId;
						 })
						 if(ace){
							 submenu.access = ace.access& submenu.power;;
						 }else{
							 submenu.access = 0;
						 }
					 })	
				 })
			})
		}else{
			$scope.role = {menus:[]};
		}
		
		 $scope.getSubmenuLength = function(items){
			 return items.findAll(function(n){return n.power!=-1}).length;
		 }
		 
		 $scope.save=function(role){
			//点击后 校验不通过的会变红
			   $scope.form.$setSubmitted(true);
			   if(!$scope.form.$valid){
				  return;
			   }
			 $scope.curMenus.each(function(menu){
				 var ace = role.menus.find(function(n){
					 return menu.id == n.menuId;
				 })
				  //添加role 在一级菜单上的权限
				 if(ace){
					 ace.access = menu.access;
				 }else{
					 role.menus.push({menuId:menu.id,access:menu.access});
				 }
				
				 menu.items.each(function(submenu){
					 //添加role 在二级菜单上的权限
					 var ace = role.menus.find(function(n){
						 return submenu.id == n.menuId;
					 })
					  //添加role 在一级菜单上的权限
					 if(ace){
						 ace.access = submenu.access;
					 }else{
						 role.menus.push({menuId:submenu.id,access:submenu.access});
					 }
				 })	
			 })
				$http.post("../role/saveOrUpdate",role).success(function(resp, status, headers, config) {
					if(resp.success){
						appData.getDynaDicts().then(function(data){
							$rootScope.roleList = data.roleList||[];
							$rootScope.adminList = data.adminList||[];
							$uibModalInstance.dismiss('ok');
						});
					}
				})
		};
	
		$scope.toggleAccess = function(menu,digi){
			menu.access = menu.access ^digi;
		}
		$scope.checkAll = function(menu){
			if(menu.all==1){
				menu.access = menu.access&0;
				menu.all = 0;
			}else{
				menu.access = menu.access|31;
				menu.all = 1;
			}
		}
		
		//有相应权限时显示编辑项	
		$scope.getPower = function(menu,digi){
			if(!(menu.power&digi)){
				return {"visibility":"hidden"};
			}
		}
		$scope.cancel = function () {
	       $uibModalInstance.dismiss('cancel');
	    };
	});
	
})();