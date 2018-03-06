(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('logoController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			var page = {   currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize};
			appData.getPageResult("../logo/list",
					$.extend(page,$scope.search),function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../logo/del/"+id;
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
					      templateUrl: 'logoAdd.html',
					      controller: 'logoAddController',
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
	   
	
	app.controller('logoAddController', function($scope,$http,$uibModalInstance,$rootScope,id,appData) {
		if(id){
			$http.get("../logo/"+id).success(function(resp){
				if(resp.success){
					$scope.logo = resp.data;
				}
			})
		}else{
			$scope.logo = {};
		}
		   /**
	         * 添加商品说明图片
	         */
        $scope.uploadImage= function(){
        	appData.uploadImage({},function(data){
        		$scope.logo.url = data.picPath;
        		$scope.synData();
			})
        }
        
        $scope.clearImg = function(logo){
        	logo.url = null;
        }
        
	      //添加或更新管理员
		 $scope.save=function(logo){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  $http.post("../logo/saveOrUpdate",logo).success(function(data, status, headers, config) {
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


