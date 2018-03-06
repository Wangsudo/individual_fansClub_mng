(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('userListController', function($scope,$rootScope, $http,appData,$uibModal) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {};
		
      //获取信息
		$scope.find = function(pageNo){
		  $http.post("../user/list",
				  {
			  name:$scope.search.name,
			  account:$scope.search.account,
			  email:$scope.search.email,
			  phone:$scope.search.phone,
			  currentPage:pageNo||$scope.items.currentPage||1,
			  pageSize:$scope.items.pageSize||10
		  }).success(function(data, status, headers, config){
			  $scope.items = data;
		  }).error(function(data, status, headers, config) {
			  console.log("获取球员列表异常");
		  }); 
	   };
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
		   $scope.pageChanged = $scope.find;
		   $scope.find();
		 
	});
	
	
	app.controller('userInfoController', function($scope,$uibModalInstance,user) {
		 $scope.user = user; 
		$scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	      
	});

	
})();