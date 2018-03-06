(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('messageController', function($scope,$rootScope,$uibModal, $http,$sce,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../message/list",{
				  name:$scope.search.name,
				  title:$scope.search.title,
				  type:$scope.search.type,//站内信类型
				  isEnabled:$scope.search.isEnabled,
				  fromDate:$scope.search.fromDate,
				  toDate:$scope.search.toDate,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../message/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(data){
				  if(data){
					  $scope.find();
				  }
			  });
		};
	
			//显示添加管理员
		   $scope.showModal = function(id){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'messageAdd.html',
					      controller: 'messageAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		$scope.toggleEnable =function(item,id){
			$http.get("/message/toggleLock?id="+item.id).success(function(resp){
				if(resp.success){
					tips("操作成功!",id)
				}else{
					tips("操作失败!",id)
					item.isEnabled = item.isEnabled^3;
				}
			})
		}
		 $scope.find();
	});
	   
	
	app.controller('messageAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../message/"+id).success(function(resp){
				if(resp.success){
					$scope.message = resp.data;
				}
			})
		}else{
			$scope.message = {};
		}
		//签到
		$scope.signIn = function(tank,status){
			tank.confirmStatus = status;
			tank.confirmTime = new Date().getTime();
		}
		//审核
		$scope.audit = function(tank,status){
			tank.auditStatus = status;
			tank.auditTime = new Date().getTime();
		}
	      //保存
		 $scope.save=function(message){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  appData.setTimeStamp(message,$scope.admin);
			  $http.post("../message/saveOrUpdate",message).success(function(data, status, headers, config) {
					if(data.success){
						$uibModalInstance.close();
					}
			  })
		}; 
			
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	      };
	});
	
	
})();


