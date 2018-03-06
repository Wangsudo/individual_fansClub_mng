(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('applyController', function($scope,$rootScope,$uibModal, $http,$sce,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../apply/list",{
				  name:$scope.search.name,
				  title:$scope.search.title,
				  fromDate:$scope.search.fromDate,
				  toDate:$scope.search.toDate,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../apply/del/"+id;
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
					      templateUrl: 'applyAdd.html',
					      controller: 'applyAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		$scope.toggleEnable =function(item,id){
			$http.get("/apply/toggleLock?id="+item.id).success(function(resp){
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
	   
	
	app.controller('applyAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../apply/"+id).success(function(resp){
				if(resp.success){
					$scope.apply = resp.data;
				}
			})
		}else{
			$scope.apply = {};
		}
        
	      //保存
		 $scope.save=function(apply){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  appData.setTimeStamp(apply,$scope.admin);
			  $http.post("../apply/saveOrUpdate",apply).success(function(data, status, headers, config) {
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


