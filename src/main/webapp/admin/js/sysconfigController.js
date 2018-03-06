(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('sysconfigController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../sysconfig/list",{
				  title:$scope.search.title,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
			//显示添加管理员
		   $scope.showModal = function(id){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'sysconfigAdd.html',
					      controller: 'sysconfigAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		 $scope.find();
	});
	   
	
	app.controller('sysconfigAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../sysconfig/"+id).success(function(resp){
				if(resp.success){
					$scope.item = resp.data;
				}
			})
		}else{
			$scope.item = {};
		}
        
	      //添加或更新管理员
		 $scope.save=function(item){
			 $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  $http.post("../sysconfig/saveOrUpdate",item).success(function(data, status, headers, config) {
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


