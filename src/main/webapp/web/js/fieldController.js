(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('fieldController', function($scope,$rootScope, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {};
      //获取球员信息
		$scope.getFields = function(pageNo){
			var v = $scope.items;
			appData.getFields({
				"snippet":$scope.search.snippet||"",
				"currentPage":pageNo||v.curPage||1
			},function(data){
				  v.list = data.list;
				  v.total = data.totalRecord;
				  v.curPage = data.currentPage;
			})
	   };
	   $scope.pageChanged = $scope.getFields;
	   $scope.find = function(event){
			if(event&&event.keyCode!=13){
				return;
			}
			$scope.getFields(1);
		}
	   
	   $scope.getFields();
	});
	
	app.controller('fieldDetailController', function($scope, $http,$routeParams, $location,appData) {
		if($routeParams.id){
		    appData.getField($routeParams.id,function(data){
	  			$scope.field = data;
	  		});
		}
	});
	
})();