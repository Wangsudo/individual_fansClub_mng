(function() {
	"use strict";
		var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('adminExecLogController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {pageSize:10};
		
	      //获取管理员信息
			$scope.find = function(pageNo){
				appData.getPageResult("../adminExecLog/list",{
					  account:$scope.search.account,
					  fromDate:$scope.search.fromDate,
					  toDate:$scope.search.toDate,
					  currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize
				  },function(data){
					  $scope.items = data;
				  });
		   };
			
		   $scope.find();
		   
		 //显示详细
		   $scope.showinfo = function(adminUser){
			   var modal = $uibModal.open({
				      animation: true,
				      size:'min',
				      templateUrl: 'adminDetail.html',
				      controller: 'adminDetailController',
				      resolve: {adminUser: function(){	
				    	  adminUser = $.extend(true,{},adminUser);
					      return adminUser;
				    	  },
				    }});

			   modal.result.then(function () {
				   $scope.find();
				   
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
       }
	});
	
	app.controller('playerExecLogController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
	      //获取管理员信息
			$scope.find = function(pageNo){
				appData.getPageResult("../playerExecLog/list",{
					  account:$scope.search.account,
					  fromDate:$scope.search.fromDate,
					  toDate:$scope.search.toDate,
					  currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
		   };
		 $scope.find();
	});
	
})();