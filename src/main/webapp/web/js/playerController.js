(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('playerController', function($scope,$rootScope, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		
		$scope.itemsList = [{name:"全部",teamType:0}, {name:"3人制",teamType:3}, {name:"5人制",teamType:5}, {name:"7人制",teamType:7},{name:"11人制",teamType:11}]

      //获取球员信息
		$scope.getPlayers = function(teamType,pageNo){
				var typeList = (teamType === undefined)?$scope.itemsList:$scope.itemsList.findAll(function(n){return n.teamType==teamType});

				$.each(typeList,function(i,v){
					appData.getPlayers({
						  "teamType":v.teamType, 
						  "orderby":"createTime desc",
						  "snippet":$scope.search.snippet||"",
						  "currentPage":pageNo||v.curPage||1,
						  "pageSize":20
					  },function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
			});
	   };
		   $scope.pageChanged = function(teamType){
				$scope.getPlayers(teamType);
			}
		   
		   $scope.find = function(event){
				if(event&&event.keyCode!=13){
					return;
				}
				$scope.getPlayers(undefined,1);
			}
		   
		   $scope.getPlayers();
	});
	
	app.controller('myPlayerController', function($scope, $http,$routeParams, $location,appData) {
		var parent = $scope.$parent;
		$scope.dismissTeam = function(user){
			 layer.confirm("您确定解散球队"+user.team.teamTitle+"吗?", function(index){
	    		    appData.dismissTeam(user.id,user.team.id,function(resp){
	    		    	if(resp.success){
	    		    		user.isCaptain = 2;
	    		    		delete user.team;
	    		    		$location.url("/account");
	    		    	}else{
	    		    		appData.alert("解散球队失败："+data.error)
	    		    	}
	    		    })
					layer.close(index);
		    });
		}
		
		if($routeParams.id){
			  appData.getPlayers({"teamId":$routeParams.id,
				                  "pageResult.pageSize":1000},function(data){
		  			$scope.players = data.list;
		  		});
		}
	});
	
	
	app.controller('myInfoController', function($rootScope,$scope, $http,$location,$uibModal, appData) {
		var parent = $scope.$parent;
		if($rootScope.userDefer){
			$rootScope.userDefer.then(function(data){
				$scope.player = $.extend(true,{},data.user);
			})
		}else{
			$location.url("chief");
		} 
		
		$scope.uploadImage = function(){
			appData.uploadImage({params:{original:true}},function(data){
				 var modalInstance = $uibModal.open({
				      templateUrl: 'jcrop.html',
				      controller: 'jcropCtrl',
				      resolve: {
				        data: function(){return data}
				      }
				    });
				    modalInstance.result.then(function (args) {
				    	$scope.player.iconUrl = args.picPath;
				    	$scope.synData();
				    }, function () {
				        console.info('Modal dismissed at: ' + new Date());
				    });
			})
		};
		
			
		$scope.update = function(form){
			form.submited = true;
			if(form.$invalid){
				return;
			}
			appData.updatePlayer($scope.player,function(data){
				if(data.success){
					$.extend(true,parent.user,$scope.player);
					$location.url("/account");
				}
			});
		}  
	});
	
})();