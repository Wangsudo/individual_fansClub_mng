(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('fieldController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			var page = {   currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize};
			appData.getPageResult("../field/list",
					$.extend(page,$scope.search),function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../field/del/"+id;
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
					      templateUrl: 'fieldAdd.html',
					      controller: 'fieldAddController',
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
	   
	
	app.controller('fieldAddController', function($scope,$http,$uibModalInstance,$rootScope,id,appData) {
		if(id){
			$http.get("../field/"+id).success(function(resp){
				if(resp.success){
					$scope.field = resp.data;
				}
			})
		}else{
			$scope.field = {};
		}
		   /**
	         * 添加商品说明图片
	         */
        $scope.uploadImage= function(){
        	appData.uploadImage({},function(data){
        		$scope.field.url = data.picPath;
        		$scope.synData();
			})
        }
        
        $scope.clearImg = function(field){
        	field.url = null;
        }
        
	      //添加或更新管理员
		 $scope.save=function(field){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  $http.post("../field/saveOrUpdate",field).success(function(data, status, headers, config) {
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


