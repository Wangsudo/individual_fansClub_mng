(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('userLoginLogController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			 $http.post("../userLoginLog/list",
					  
				  {
				 account:$scope.search.account,
				 ip:$scope.search.ip,
			 currentPage:pageNo||$scope.items.currentPage||1,
			  pageSize:$scope.items.pageSize||10
		  }).success(function(data, status, headers, config){
			  $scope.items = data;
		  }).error(function(data, status, headers, config) {
			  console.log("获取球员列表异常");
		  }); 
	   };
	
		   $scope.pageChanged = $scope.find;
		   
		   $scope.find();
		   
		   $scope.showinfo = function(user){
			   var modal = $uibModal.open({
				      animation: true,
				      size:'min',
				      templateUrl: 'userInfo.html',
				      controller: 'userInfoController',
				      resolve: {user: function(){	
				    	  user = $.extend({},user);
					      return user;
				    	  },
				    }});

			   modal.result.then(function () {
				   $scope.find();
				   
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
       }
		 
	});

	
})();