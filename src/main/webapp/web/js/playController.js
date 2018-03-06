(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('playController', function($scope,$rootScope,$routeParams, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.itemsList = [{name:"全部",teamType:0}, {name:"3人制",teamType:3}, {name:"5人制",teamType:5}, {name:"7人制",teamType:7},{name:"11人制",teamType:11}]
        
    //获取比赛信息 (目前获取已完赛且上传比分一致的比赛)
		$scope.getGames = function(teamType,pageNo){
				var typeList = (teamType === undefined)?$scope.itemsList:$scope.itemsList.findAll(function(n){return n.teamType==teamType});
				$.each(typeList,function(i,v){
					appData.getGames({
						  "teamType":v.teamType, 
						  // "condition":" and u.teamBOperation =1",// 未应战的球赛 也显示
						  "fromDate":v.fromDate,
						  "toDate":v.toDate,
						  "gameStatus":$routeParams.gameStatus,
						  "snippet":v.snippet||"",
						  "currentPage":pageNo||v.curPage||1
					  },function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
			});
	   };
	   $scope.find = function(event){
			if(event&&event.keyCode!=13){
				return;
			}
			$scope.getGames(undefined,1);
		}
	   
		   $scope.pageChanged = function(teamType){
				$scope.getGames(teamType);
			}
		   $scope.search = function(item){
			   if(!$scope.str){
				   return true;
			   }
			   if(item&&item.teamA&&item.teamA.teamTitle&&item.teamA.teamTitle.indexOf($scope.str)!=-1){
				   return true;
			   }
			   if(item&&item.teamB&&item.teamB.teamTitle&&item.teamB.teamTitle.indexOf($scope.str)!=-1){
				   return true;
			   }
			   return false;
		   }
		   $scope.getGames();
		});
	
	app.controller('playDetailController', function($scope, $http,$routeParams) {
		console.log("detail:"+$routeParams.id);
		$http.get("/app/game/"+$routeParams.id).success(function(data, status, headers, config) {
			if(data.success){
				$scope.game = data.data;
			}
		})
	});

	// 控制器
	app.controller('myPlayController', function($scope, $http,$rootScope,appData,$routeParams) {
		
		console.log("detail:"+$routeParams.id);

		$scope.itemsList = [{name:"全部",gameStatus:0}, {name:"未开赛",gameStatus:5}, {name:"已结束",gameStatus:10}, {name:"约战中",gameStatus:1},
			{name:"已拒战",gameStatus:4}, {name:"已过期",gameStatus:2}, {name:"比分待录入",gameStatus:7}, {name:"待审核",gameStatus:8}, {name:"审核不通过",gameStatus:9}];
	    //获取比赛信息
			$scope.getMyGames = function(items){
					var gameList = (items === undefined)?$scope.itemsList:[items];
					$.each(gameList,function(i,v){
						appData.getGames({
							  "teamId":$routeParams.id,
							  "gameStatus":v.gameStatus,
							  "currentPage":v.curPage
						},function(data){
							v.list = data.list;
							v.total = data.totalRecord;
							v.curPage = data.currentPage;
						});
				});
		   };
		   $scope.pageChanged = function(items){
				$scope.getMyGames(items);
			}
		   $scope.getOri = function(item){
			   if(item.isPublic !=1 && item.teamA.id == $routeParams.id){
				   return "我发布到球队";
			   }else if(item.isPublic !=1 && item.teamB.id == $routeParams.id){
				   return "我接受挑战";
			   }
			   else if(item.isPublic ==1 && item.teamA.id == $routeParams.id){
				   return "来自约战";
			   }else if(item.isPublic ==1 && item.teamB.id == $routeParams.id){
				   return "来自应战";
			   }
			   return "";
		   }
	
		   $scope.getMyGames();
	});
	
	app.controller('attendanceDetailController', function($scope,$rootScope, $routeParams,$http,appData) {
		var parent = $scope.$parent;
		console.log("detail:"+$routeParams.id);
		$scope.getConfirms = function(tanks){
			if(!tanks) return 0;
			var showups = tanks.findAll(function(v){return v.confirmStatus == 1});
			return showups.length;	
		}
		$scope.pageChanged = function(){
			 $scope.tanks = $scope.message.tanks.slice($scope.message.curPage*10-10,$scope.message.curPage*10);
		}
		  $http.get("/app/message/"+$routeParams.id).success(function(resp, status, headers, config){
			  if(resp.success){
				  resp.data.tanks.remove(function(n){return n.player.isCaptain == 1});
				  $scope.message = resp.data;
				  $scope.tanks = resp.data.tanks.slice(0,10);
				  $scope.message.total = resp.data.tanks.length;
				  $scope.message.curPage = 1;
			  }
		  })
		  
		  $scope.signIn = function(tank,confirmStatus){
				var tip = "";
				if(confirmStatus == 1){
					tip = "确认签到吗?"
				}else if(confirmStatus == 2){
					//原先已签到， 现要取消签到（在活动的一天以前, 也可取消签到）
					if(tank.confirmStatus == 1){
						var beginDate = new Date($scope.message.beginTime).beginningOfDay();
						var now = new Date();
						if( now.isAfter(beginDate.addDays(-1))){
							appData.alert("在活动开始前24小时内不可取消签到！");
							return;
						}
					}
					tip = "确认放弃参加活动吗?";
				}
			    layer.confirm(tip, function(index){
					$http.get("/app/signIn",{params:{tankId:tank.id,confirmStatus:confirmStatus}}).success(function(resp, status, headers, config){
						if(resp.success){
							tank.confirmStatus = confirmStatus;
						}
					});
					layer.close(index);
			    });
			}
		  
		$scope.audit = function(tank,auditStatus){
			var tip = "";
			if(auditStatus == 1){
				tip = "确认通过吗?"
			}else{
				tip = "确认拒绝吗?"
			}
		    layer.confirm(tip, function(index){
				$http.get("/app/signInAudit",{params:{tankId:tank.id,auditStatus:auditStatus}}).success(function(data, status, headers, config){
					if(data.success){
						tank.auditStatus = auditStatus;
						tank.auditTime = new Date().format("{yyyy}-{MM}-{dd} {HH}:{mm}");
					}
				});
				layer.close(index);
		    });
		}
		
		
	});
	
	
	
})();