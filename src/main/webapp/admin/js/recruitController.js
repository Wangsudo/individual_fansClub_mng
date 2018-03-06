(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('recruitController', function($scope,$rootScope,$uibModal, $http,$sce,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../recruit/list",{
				  teamTitle:$scope.search.teamTitle,
				  title:$scope.search.title,
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
			  var url = "../recruit/del/"+id;
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
					      templateUrl: 'recruitAdd.html',
					      controller: 'recruitAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		$scope.toggleEnable =function(item,id){
			$http.get("/recruit/toggleLock?id="+item.id).success(function(resp){
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
	   
	
	app.controller('recruitAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../recruit/"+id).success(function(resp){
				if(resp.success){
					$scope.recruit = resp.data;
				}
			})
		}else{
			$scope.recruit = {};
		}
        
	      //保存
		 $scope.save=function(recruit){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  appData.setTimeStamp(recruit,$scope.admin);
			  $http.post("../recruit/saveOrUpdate",recruit).success(function(data, status, headers, config) {
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


