(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('transferController', function($scope,$rootScope,$uibModal, $http,appData) {
		document.title="转会记录";
		$scope.search={};
		$scope.items = {pageSize:10};
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../transferLog/list",{
				  name:$scope.search.name,
				  teamTitle:$scope.search.teamTitle,
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


