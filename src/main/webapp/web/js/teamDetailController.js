(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('teamDetailController', function($scope, $http,$rootScope,appData, $routeParams,$location) {
		var parent = $scope.$parent;
		$scope.items = {};
		
		 //获取已审核通过的球赛
	    $scope.pageChanged = function(){
	    	 var args = {"teamId":$routeParams.id,
		   				"status":1,
		   				currentPage:$scope.items.curPage||1
		   				};
			   appData.getGames(args,function(data){
					$scope.items = {list:data.list,total:data.totalRecord,curPage:data.currentPage};
				});
	    }
	    
	    $scope.dareTeam = function(){
	    	if(!$scope.getPermission($scope.user,'dare')){
    	    	return;
    	    }
	    	appData.findTeamById(parent.user.team.id,function(data){
	    		var teamA = data;
		    	var teamB = $scope.team;
		    	if(teamA.teamType > teamA.playerNum){
					 layer.alert('当前球队人数不够'+teamA.teamType+"人,请先招募!", {
					        skin: 'layui-layer-lan'
					        ,closeBtn: 0
					        ,shift: 4 //动画类型
				    });
				}else if(teamB.teamType > teamB.playerNum){
					 layer.alert('目标球队人数不够'+teamA.teamType+"人,请选择其它球队!", {
					        skin: 'layui-layer-lan'
					        ,closeBtn: 0
					        ,shift: 4 //动画类型
				    });
				}else{
					$location.url("/team/dare?id="+teamB.id);
				}
	    	})
		}
	    
	   if($routeParams.id){
			appData.findTeamById($routeParams.id,function(data){
				   $scope.team = data;
				   appData.caluTeamGrades($scope.team);
				   $scope.$watch('user',function(newValue,oldValue, scope){
				        if(newValue && newValue.isCaptain !=1 && $routeParams.id){
				        	//当前player 不是队长时， 获取 球队招人信息
				        	activeApply($scope.team,newValue);
				        }
				    });
		    });
		   appData.getPlayers({"teamId":$routeParams.id},function(data){
			   $scope.players = data.list;
			   $scope.captain = data.list.find(function(n){return n.isCaptain == 1});
			  
		   });
		   $scope.pageChanged();
		}
	   
	    function activeApply(team,player){
		    $http.get("/app/activeApply",{params:{"playerId":player.id}}).success(function(resp){
		   		if(resp.success){
		   			var apply = resp.data;
		   			if(apply && apply.tanks){
		   				$scope.tank = apply.tanks.find(function(n){return n.team.id == team.id});
		   			}
		   		}
		   	})
	    }
	    
	    $scope.apply = function(team,player){
	    	if(!$scope.getPermission($scope.user,'apply')){
    	    	return;
    	    }
 		     $http.get("/app/applyTeam", {params:{teamId:team.id,playerId:player.id}}
 		    		 ).success(function(data, status, headers, config){
 	    			 if(data.success){
 	    				activeApply(team,player);
 	    			 }
 			  })
	    }

		$scope.getConfirms = function(tanks){
			var showups = tanks.findAll(function(v){return v.confirmStatus == 1});
			return showups.length;	
		}
		//恢复球队
		$scope.restoreTeam = function(user,team){
			 layer.confirm("您确定恢复球队"+team.teamTitle+"吗?", function(index){
	    		    appData.restoreTeam(user.id,team.id,function(resp){
	    		    	if(resp.success){
	    		    		$rootScope.user = resp.data;
	    		    		$location.url("/account");
	    		    	}
	    		    })
					layer.close(index);
		    });
		}
	});
	
})();