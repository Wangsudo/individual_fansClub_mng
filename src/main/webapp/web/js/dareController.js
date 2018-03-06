(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('dareController', function($scope, $http,$rootScope,appData, $routeParams,$location) {
		var parent = $scope.$parent;
	    $scope.curGame ={title:'友谊赛',isPublic:1,isEnabled:1};
	    $scope.hideB = $routeParams.id;
	  
	   //获取默认的环赛列表
	   $http.get("/app/getPublicCups").success(function(resp){
	   		if(resp.success){
	   			$scope.cups = resp.data;
	   		}else{
	   			console.error(resp.error);
	   		}
	   	})
	   	
	/*   $scope.$watch('user',function(newValue,oldValue, scope){
	        if(newValue && newValue.isCaptain ==1){
	        	$scope.curGame.teamA=newValue.team;
	        	$scope.curGame.teamType=newValue.team.teamType;
	        }
	    });*/
	   
	   $rootScope.userDefer.then(function(data){
		    if(data.success&&data.user.auditStatus!=2){
		    	var user = $rootScope.user = data.user;
		    	if(user && user.isCaptain ==1){
		        	$scope.curGame.teamA=user.team;
		        	$scope.curGame.teamType=user.team.teamType;
		        	if($routeParams.id){
			 			appData.findTeamById($routeParams.id,function(data){
			 				$scope.curGame.teamB = data;
			 				$scope.curGame.isPublic = 2;
			 			    });
			 		}else{
			 			 $http.get('/app/team/search', {
			 			      params: {
			 			        teamType:$scope.user.team.teamType,
			 			        exId:$scope.user.team.id,
			 			        pageSize: 1000
			 			      }
			 			    }).success(function(resp){
			 			      $scope.teamBList = resp;
			 			    });
			 		}
		        }
		    }}
	   )
	   
	   $scope.save = function(form){
		   form.submited = true;
		   if(form.$invalid){
			   return;
		   }
		    $http.get("/app/getTodayDareNum",{params:{teamId:$scope.user.team.id}}).success(function(num){
		    	if($scope.sysconfig.dare_max>num){
		    		saveGame(form);
		    	}else{
		    		appData.alert("您一天之内只有"+$scope.sysconfig.dare_max+"次约战次数！");
		    	}
		    })
		}
	   
	   function saveGame(form){
			if($scope.curGame.isPublic ==1){
				delete $scope.curGame.teamB;
			}
			$http.post("/app/game/saveOrUpdate",$scope.curGame).success(function(data, status, headers, config) {
					//after successfully uploaded, clear the form
					if(data.success){
						$location.url("/notice?type=约战");
					}else{
						appData.alert(data.error);
					}
			}).error(function(data, status, headers, config) {
  				appData.alert("添加比赛失败!");
			});	
	   }
	});
	
	//挑战详情页面控制器
	app.controller('dareDetailController', function($scope, $http,$routeParams,appData) {
		console.log("detail:"+$routeParams.id);
		
		$routeParams.id && appData.getGame($routeParams.id,function(game){
				$scope.game = game;
			})
			
		$scope.items = {};
		var getComments = function(pageNo){
			appData.getComments({
				  "type":4,//招人
				  "id":$routeParams.id,//
				  "currentPage":pageNo||$scope.items.curPage||1
			},function(data){ 
				$scope.items.list = data.list;
				$scope.items.total = data.totalRecord;
				$scope.items.curPage = data.currentPage;
			})
		}
		
		getComments(1) ;
		$scope.pageChanged = getComments;
		$scope.bean = {type:4,itemId:$routeParams.id};
		$scope.addComment = function(bean){
			if(!$scope.getPermission($scope.user,'comment')){
    	    	return;
    	    }
			  appData.addComment(bean,$scope.user,function(){
				  getComments(1) ;
			  })
		}
		
	});
	
})();