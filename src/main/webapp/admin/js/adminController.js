(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('adminController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../adminUser/list",{
				  name:$scope.search.name,
				  account:$scope.search.account,
				  dictId:$scope.search.dictId,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../adminUser/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(data){
				  if(data){
					  $scope.find();
				  }
			  });
		};
	
			//显示添加管理员
		   $scope.showModal = function(adminId){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'adminAdd.html',
					      controller: 'adminAddController',
					      resolve: {adminId: adminId}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		 $scope.find();
	});
	   
	
	app.controller('adminDetailController', function($scope,$uibModalInstance,adminUser) {
		 $scope.adminUser = adminUser; 
		$scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	      
	});
	
	app.controller('adminAddController', function($scope,$http,$uibModalInstance,$rootScope,adminId,$uibModal,appData) {
		var parent = $scope.$parent;
		if(adminId){
			$http.get("../adminUser/"+adminId).success(function(resp){
				if(resp.success){
					$scope.adminUser = resp.data;
				}
			})
		}else{
			$scope.adminUser = {};
		}
		   /**
	         * 添加商品说明图片
	         */
	        $scope.addAdminImg= function(){
	        	appData.uploadImage({},function(data){
	        		$scope.adminUser.headpic = data.picPath;
	        		$scope.synData();
				})
	        }
	        
	      //添加或更新管理员
		 $scope.saveAdmin=function(adminUser){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  $http.post("../adminUser/check",adminUser).success(function(data, status, headers, config) {
					if(!data.account){
						$scope.tips('NOACCOUNT', '#account');
						return false;
					}
					if(!data.email){
						$scope.tips('NOEMAIL', '#email1');
						return false;
					}
					else if(!data.teleNum){
						$scope.tips('NOTEL', '#teleNum');
						return false;
					}else{
					     appData.setTimeStamp(adminUser,$scope.admin);
						$http.post("../adminUser/saveOrUpdate",adminUser).success(function(data, status, headers, config) {
							if(data.success){
								$uibModalInstance.close();
							}
						})
					}
				})
			}; 
			
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	      //显示修改密码
	 	   $scope.passWord = function(adminUser){
	 			   var modal = $uibModal.open({
	 				      animation: true,
	 				      templateUrl: 'password.html',
	 				      controller: 'passWordController',
	 				      resolve: {adminUser: function(){	
	 				    	 adminUser = $.extend(true,{},adminUser);
	 					    	  return adminUser;
	 				    	  }
	 				    }});
	 			   	
	 			  modal.result.then(function () {
	 				  // $scope.find();
	 				  }, function () {
	 				      console.info('Modal dismissed at: ' + new Date());
	 				     
	 				  });
	 		   }
	});
	
	app.controller('passWordController', function($scope,$http,$uibModalInstance,$rootScope,$uibModal,appData,adminUser) {
		$http.get("../getUserbySession").success(function(admin){
			$scope.adminUser=admin;
		}) 
		
		$scope.savePassWord=function(){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  if($scope.passwordold!=$scope.adminUser.password){
					$scope.tips("WRONG", '#pold');
						return false;
					}
			  else if($scope.passwordnew==$scope.passwordold){
						$scope.tips("COMMON", '#pnew');
							return false;
						}
				  else if($scope.passwordtwo!=$scope.passwordnew){
						$scope.tips("DIFFERENT", '#ptwo');
						return false;
					}
					else{
					 
					  $http.get("../adminUser/changePassword/"+$scope.adminUser.id+"&"+$scope.passwordnew).success(function(data, status, headers, config){
						  $scope.alert('SUCCESS');
						 
					  }).error(function(data, status, headers, config) {
						  console.log("获取球员列表异常");
					  }); 
				      $uibModalInstance.close();
					}
				
				}; 
				
		 $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	            
	           
	        };
	})

	
})();


