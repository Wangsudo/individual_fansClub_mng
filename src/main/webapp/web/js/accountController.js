(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('accountController', function($scope, $http,$rootScope,appData, $routeParams,$location) {
		var parent = $scope.$parent;
		
		$scope.logg = function(user){
		 //	console.log(user);
		}
		$scope.dare = function(){
			if(!$scope.getPermission($scope.user,'dare')){
    	    	return;
    	    }
			if(parent.user.team.teamType > $scope.players.length){
				 layer.alert('当前队伍人数不够'+parent.user.team.teamType+"人,请先招募!", {
				        skin: 'layui-layer-lan'
				        ,closeBtn: 0
				        ,shift: 4 //动画类型
			    });
			}else{
				$location.url("/account/dare");
			}
		}
		
		//判断可否注册球队
		$scope.canRegTeam = function(playerId){
			$http.get("/app/canRegTeam?playerId="+playerId).success(function(resp){
				if(resp.success){
					$scope.gotoPage('/'+$scope.module+'/createTeam?id='+playerId)
				}else{
					appData.alert(resp.error);
				}
			})
		}
		
		$scope.release = function(){
			if(parent.user.auditStatus!=1){
				 layer.alert("您的帐号目前尚未通过审核！", {
				        skin: 'layui-layer-lan'
				        ,closeBtn: 0
				        ,shift: 4 //动画类型
			    });
			}else{
				$location.url("/account/release?tab="+parent.user.tab);
			}
		}
		
		 $scope.quitTeam = function(user){
	    	 layer.confirm("您确定退出当前球队"+user.team.teamTitle+"吗?", function(index){
					$http.get("/app/quitTeam",{params:{playerId:user.id}}).success(function(data, status, headers, config){
						if(data.success){
							  delete user.team;
						}
					});
					layer.close(index);
		    });
	    }
		
		//获取比赛信息 (目前获取已完赛且上传比分一致的比赛)
		$scope.getfutureGames = function(teamId){
			appData.getGames({teamId:teamId,"pageSize":4},function(data){
				$scope.futureGames = data.list;
			})
	   };
	   
	   
	   var itemsListWhole = [{name:"比分",type:1,url:"/app/listScore",delUrl:"/app/delScore"},
	                         {name:"图片",type:2,url:"/app/listPhoto",delUrl:"/app/delPhoto"},
	                         {name:"视频",type:3,url:"/app/listVideo",delUrl:"/app/delVideo"},
	                         {name:"签到信",type:4,url:"/app/listMessage",delUrl:"/app/delMessage"},
	                         {name:"站内信",type:5,url:"/app/listMessage",delUrl:"/app/delMessage"},
	                         {name:"招人",type:6,url:"/app/listRecruit",delUrl:"/app/delRecruit"},
	                         {name:"找队",type:7,url:"/app/listApply",delUrl:"/app/delApply"}];

	   $scope.scoreList = itemsListWhole[0];
	   $scope.photoList = itemsListWhole[1];
	   $scope.videoList = itemsListWhole[2];
	   $scope.signInList = itemsListWhole[3];
	   $scope.messageList = itemsListWhole[4];
	   $scope.recruitList =itemsListWhole[5];
	   $scope.applyList = itemsListWhole[6];
	   

	    //获取比赛信息
		function getMyRelease(user,type){
					var typeList = (type === undefined)?$scope.itemsList:[$scope.itemsList.find(function(v){return v.type==type})];
					$.each(typeList,function(i,v){
						  var args ={
								  "isEnabled":1, 
								  "currentPage":v.curPage||1,
								  "pageSize":10
						  }
						  switch(v.type){
							  case 1: args["teamId"] = user.team.id;  break;
							  case 2: args["playerId"] = user.id;
							          args["status"] = 1;//审核通过
							  		  args["orderby"] = "createTime desc" ;break;
							  case 3: args["playerId"] = user.id;
							          args["status"] = 1;//审核通过
							  		  args["orderby"] = "createTime desc" ;break;
							  case 4: args["teamId"] = user.team.id;
							  		  args["type"] = 2;
							  		  args["orderby"] = "opTime desc" ;break;
							  case 5: args["teamId"] = user.team.id;
							  		  args["type"] = 1;
							  		  args["orderby"] = "opTime desc" ;break;
							  case 6: args["teamId"] = user.team.id;
							  		  args["orderby"] = "opTime desc" ;break;	
							  case 7: args["playerId"] = user.id;//找队
							  		  args["orderby"] = "applyTime desc" ;break;	
						  }
						  $http.post(v.url,args
						  ).success(function(data, status, headers, config){
							  v.list = data.list;
							  v.total = data.totalRecord;
							  v.curPage = data.currentPage;
						  }).error(function(data, status, headers, config) {
						      console.log("服务器异常!");
						  }); 
				});
		   };
		
			
		   $scope.pageChanged = getMyRelease;
		   $scope.checkAll = function(items){
			   items.list.each(function(v){
				   if(items.name == "签到信" && v.beginTime <new Date().getTime()){

				   }else if(items.name == "比分" && $scope.cantDel(v)) {
				   }else  {
					   v.checked = true;
				   }
			   })
		   }
		   $scope.cantDel= function(game){
			   if($scope.user.isCaptain !=1){
				   return true;
			   }
			   if(isNaN(game.auditStatus) && game.auditStatus>0){
				   return true;
			   }
			   if(!isNaN(game.scoreA1) && !isNaN(game.scoreA2) ){
				   return true;
			   }
			   return false;
		   }
		   $scope.del = function(items){
			        var  params = {ids:[]};
			    	items.list.each(function(v){
						if(v.checked) {
							params.ids.push(v.id);
						}
				   })
			       if(items.name == "比分"){
					   params.teamId = $scope.user.team.id;
				   }
			       params.ids.length && $http.get(items.delUrl,{params:params}).success(function(data, status, headers, config){
					   if(data.success){
						   getMyRelease($scope.user,items.type);
					   }
				   })
		   }
		   $scope.getMyScore = function(game,user){
			   if(!user){
				   return;
			   }
			   if(game.teamA.id == user.team.id){
				   return (isNaN(game.scoreA1)?'--':game.scoreA1)+":"+(isNaN(game.scoreB1)?'--':game.scoreB1);
			   }else{
				   return (isNaN(game.scoreA2)?'--':game.scoreA2)+":"+(isNaN(game.scoreB2)?'--':game.scoreB2);
			   }
		   }
				   
		   $scope.$watch('user',function(newValue,oldValue, scope){
		        if(newValue){
		        	newValue.allTabActive = true;
		        	if(newValue.team){
		        		$scope.getfutureGames(newValue.team.id);
		        	}
		        	if(newValue.isCaptain ==1){
		        		//用户为队长时, 没有 找队页
		        		$scope.itemsList = [itemsListWhole[0],itemsListWhole[1],itemsListWhole[2],itemsListWhole[3],itemsListWhole[4],itemsListWhole[5]];
		        		//用户为队长时, 获取球员信息
		        		appData.getPlayers({"teamId":newValue.team.id,"pageSize":1000},function(data){
		        			$scope.players = data.list;
		        		});
		        		
		        	}else if(newValue.isCaptain !=1 ){
		        		//用户为普通球员时 , 没有权限发布 比分 , 招人,签到信,站内信, 但是可以查看比分

		        		$scope.itemsList = [itemsListWhole[1],itemsListWhole[2],itemsListWhole[6]];
						if(newValue.team){
							$scope.itemsList.unshift(itemsListWhole[0]);
						}
	        		   appData.findHistory(newValue.id,function(data){
						   $scope.history = data;
					   });
		        	}
		        	//计算等级
			    	appData.caluPlayer(newValue);
			    	newValue.team && appData.caluTeamGrades(newValue.team);
		        	getMyRelease(newValue);
		        	//查看找队记录
		        	$http.post("/app/listApplyTank",{"playerId":newValue.id,"isEnabled":1,"isOpen":1,"condition":' and u.confirmStatus is null '}).success(function(data){
		        		if(data && data.list && data.list.length){
		        			$scope.tank = data.list[0];
		        		}}
		        	);
		        	if($routeParams.type){
		        		$scope.itemsList.each(function(n){
		        			if(n.type==$routeParams.type ){
		        				n.active = true;
		        				newValue.tab=n.name;
		        			}
		        		})	
		        	}else if(newValue.tab){
		        		$scope.itemsList.each(function(n){
		        			if(n.name==newValue.tab ){
		        				n.active = true;
		        			}
		        		})
		        	}
		        }
		  });
	
	});
	
	app.controller('findDetailController', function($scope,$rootScope, $http,$routeParams,appData) {
		
		$routeParams.id && $http.get("/app/recruit/"+$routeParams.id).success(function(resp){
			if(resp.success){
				$scope.item = resp.data;
			}
		})
		
		$scope.items = {};
		var getComments = function(pageNo){
			appData.getComments({
				  "type":6,//招人
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
		$scope.bean = {type:6,itemId:$routeParams.id};
		$scope.addComment = function(bean){
			if(!$scope.getPermission($scope.user,'comment')){
    	    	return;
    	    }
			  appData.addComment(bean,$scope.user,function(){
				  getComments(1) ;
			  })
		}
		
	});
	
	app.controller('applyDetailController', function($scope,$rootScope, $http,$routeParams,appData) {
		var parent = $scope.$parent;
		$routeParams.id && $http.get("/app/apply/"+$routeParams.id).success(function(resp, status, headers, config){
			  $scope.apply = resp.data;
			  $scope.tanks = resp.data.tanks.slice(0,10);
			  $scope.apply.total = resp.data.tanks.length;
			  $scope.apply.curPage = 1;
		  })
		
		$scope.pageLocal = function(){
			 $scope.tanks = $scope.apply.tanks.slice($scope.apply.curPage*10-10,$scope.apply.curPage*10);
		}
		
		$scope.items = {};
		var getComments = function(pageNo){
			appData.getComments({
				  "type":5,//公告
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
		$scope.bean = {type:5,itemId:$routeParams.id};
		$scope.addComment = function(bean){
			if(!$scope.getPermission($scope.user,'comment')){
    	    	return;
    	    }
			  appData.addComment(bean,$scope.user,function(){
				  getComments(1) ;
			  })
		}
		 
//		$scope.$watch('message', function (mes) {
//			if(!$scope.message||!$scope.message.tanks) return;
//			
//			
//		  },true);
		$scope.checkRank = function(rank){
			if(rank.confirmStatus != 1){
				$scope.apply.tanks.each(function(n){
					if(n.confirmStatus==1 && n.id != rank.id){
						n.confirmStatus=2;
					}
			   })}
		}
		
		// 删除
		$scope.send = function(apply){
			//1，判断有无球队接收
			var tip = "";
			var tank;
			if($.isEmptyObject(apply.tanks)||!apply.tanks.some(function(n){return n.auditStatus == 1})){
				tip = "未有球队邀请，确定关闭此找队公告吗？";
			}
			//2.有球队接收， 但是没有选中一家
			else if(!apply.tanks.some(function(n){return n.auditStatus == 1 && n.confirmStatus == 1})){
				tip = "未选择加入任何一支球队，确定关闭此找队公告吗？";
			}else{
				tank = apply.tanks.find(function(n){return n.auditStatus == 1 && n.confirmStatus == 1});
				tip = "您已选择"+tank.player.team.teamTitle+",确认加入并关闭此找队公告吗？"
			}
		    layer.confirm(tip, function(index){
				$http.post("/app/applyAction!update.action",message).success(function(data, status, headers, config){
					if(data.success){
						$http.post("/app/playerAction!update.action",message.player).success(function(data){
				    		//球员入队成功
				    		if(data.success){
				    			parent.user=message.player;
				    			$scope.synData();
								layer.close(index);
				    		}
				    	})
		    		}
				});
				layer.close(index);
		    });
		};
	});
	
	
})();