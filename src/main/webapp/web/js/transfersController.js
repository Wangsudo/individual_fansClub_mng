(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('transfersController', function($scope,$rootScope, $http,$uibModal,appData) {
		
		var parent = $scope.$parent;
		$scope.itemsList = [{name:"全部",teamType:0}, {name:"3人制",teamType:3}, {name:"5人制",teamType:5}, {name:"7人制",teamType:7},{name:"11人制",teamType:11}]
		
		//获取转会信息 
		$scope.getTranfers = function(seq,pageNo){
				var typeList = (seq === undefined)?$scope.itemsList:[$scope.itemsList[seq]];
				$.each(typeList,function(i,v){
					appData.getTransfers({
						  "teamType":v.teamType, 
						  "fromDate":v.fromDate,
						  "toDate":v.toDate,
						  "orderby":"toTime desc",
						  "snippet":v.snippet||"",
						  "currentPage":pageNo||v.curPage||1
					  },function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				  });
		};
		   $scope.pageChanged = function(teamType){
				$scope.getTranfers(teamType);
			}
		   
		   $scope.find = function(seq,event){
				if(event&&event.keyCode!=13){
					return;
				}
				$scope.getTranfers(seq,1);
			}
		   
		   $scope.search = function(item){
			   if(!$scope.search.str){
				   return true;
			   }
			   if(item&&item.fromTeam&&item.fromTeam.teamTitle&&item.fromTeam.teamTitle.indexOf($scope.search.str)!=-1){
				   return true;
			   }
			   if(item&&item.toTeam&&item.toTeam.teamTitle&&item.toTeam.teamTitle.indexOf($scope.search.str)!=-1){
				   return true;
			   }
			   if(item&&item.player&&item.player.name&&item.player.name.indexOf($scope.search.str)!=-1){
				   return true;
			   }
			   return false;
		   }
		   
			 $scope.getTranfers();

	});
})();