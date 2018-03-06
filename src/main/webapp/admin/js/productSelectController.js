-(function() {
	"use strict";
	var app = angular.module("admin.mainframe");

	//选择商品公共控制器
	app.controller('productSelectController', function($scope,$rootScope,$http,appData,$uibModalInstance,productId,exdIds,$uibModal) {
		$scope.items = {};
		$scope.search = {};
 		if(productId){
		  $http.get("../product/findById/"+productId).success(function(data){
			  $scope.curProduct = data.product||{};
		  });
	    }
		
		 $scope.find = function(pageNo){
			 var condition = (exdIds && exdIds.length>0)? (" and u.id not in ("+exdIds+")"):null;
			 var orderby = " u.modifyTime desc";
			 if(productId != undefined){
				 orderby =" case u.id when '"+productId+"' then 1 else 0 end desc, u.modifyTime desc";
			 }
				$http.post("../product/list",{
					  condition:condition,
					  orderby:orderby,
					  fromDate:$scope.search.fromDate && $scope.search.fromDate.format("{yyyy}-{MM}-{dd}"),//商品有效日期(处于上架与下架时间之间)
					  status:$scope.search.status,//发表状态
					  currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize||12
				  }).success(function(data, status, headers, config){
					  $scope.items= data;
				  }).error(function(data, status, headers, config) {
					  console.log("获取活动信息异常");
				  });
			};
			
			$scope.pageChanged = $scope.find;
			$scope.find();
		
			 $scope.selectProduct = function(product){
				 $scope.curProduct = product;
			  };
			  
		  
		  /*选择商品*/
		  $scope.add = function(){
			$uibModalInstance.close({
				productId:$scope.curProduct.id,
				price:$scope.curProduct.price,
				name:$scope.curProduct.name,
				coverPic:$scope.curProduct.coverPic
			})
		  }
		   
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	});
	
})();