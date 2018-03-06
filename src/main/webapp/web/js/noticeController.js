(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('noticeController', function($scope, $http, $rootScope,$routeParams,appData) {
		var parent = $scope.$parent;
     
		$scope.pageChanged = getNotice;
		
		if($routeParams.tab){
			var items = $scope.itemsList.find(function(v){return v.name==$routeParams.tab});
			items && (items.active = true);
		}
		/**
		 * 通知页签: 获取未读取消息数目
		 */
		function getNotice(user,tab){
			var tabList = (tab === undefined)?$scope.itemsList:[$scope.itemsList.find(function(v){return v.name==tab})];
			//上次阅读通知日期
			
			$.each(tabList,function(i,v){
				 var args ={
						  "isEnabled":1, 
						  "currentPage":v.curPage||1,
						  "pageSize":10
				  }
				  if(v.name=="球队招人"){
					  args.teamId = user.team.id;
					  $http.post("/app/listRecruit",args).success(function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				  }else if(v.name=="入队申请"){
					  args.teamId = user.team.id;
					  $http.post("/app/listApplyTank",args).success(function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				  }else if(v.name == "约战"){
					  if(!user.team) return;
					  args["teamId"] = user.team.id;
					  args["fromDate"] =new Date().getTime();
				      user.team&&user.team.id&& appData.getGames(args,function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				  }else if(v.name == '站内信'){
					    args.playerId = user.id;
					    args.type = 1;//站内信
					    $http.post("/app/listMessageTank",args).success(function(data){
						    v.list = data.list;
							v.total = data.totalRecord;
							v.curPage = data.currentPage;
					   })
				}else if(v.name == '签到'){
					  args.playerId = user.id;
					  args.type = 2;//签到信
					  $http.post("/app/listMessageTank",args).success(function(data){
						    v.list = data.list;
							v.total = data.totalRecord;
							v.curPage = data.currentPage;
					  })
				}else if(v.name == '我的申请'){
					  args.playerId = user.id;
					  $http.post("/app/listApplyTank",args).success(function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				}else if(v.name == '邀我入队'){
					args.playerId = user.id;
					 $http.post("/app/listRecruit",args).success(function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				}else if(v.name == '球员变动'){
					args.teamId = user.team.id;
					 $http.post("/app/listInOut",args).success(function(data){
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
					  })
				}
		   });
			if($routeParams.type){
        		$scope.itemsList.each(function(n){
        			if(n.name==$routeParams.type ){
        				n.active = true;
        			}
        		})	
        	}
			store.set(user.id+"_readDate",new Date().getTime());
		}
		
		
		// 确认或者放弃入队
		$scope.confirmApply = function(tankList,tank,confirmStatus){
			var tip = "";
			var tanks=[];
			if(confirmStatus ==2){
				tip ="您确认放弃加入球队"+tank.team.teamTitle+"吗？";
			}else{
				if(tankList.some(function(n){return !n.confirmStatus && n.id!=tank.id;})){
					tip ="您确认加入球队"+tank.team.teamTitle+"吗？其它申请将自动关闭！";
				}else{
					tip ="您确认加入球队"+tank.team.teamTitle+"吗？";
				}
				if(parent.user.team){
					tip+="<br/>您将自动退出当前球队"+parent.user.team.teamTitle+"!";
			   	}
			}
		    layer.confirm(tip, function(index){
				$http.get("/app/confirmApply",{params:{tankId:tank.id,confirmStatus:confirmStatus}}).success(function(data, status, headers, config){
					if(data.success){
						getNotice(parent.user,"我的申请");
					}
				});
				layer.close(index);
		    });
		};
	
		// 确认或者放弃入队
		$scope.confirmRecruit = function(recruitList,recruit,confirmStatus){
			var tip = "";
			var tanks=[];
			if(confirmStatus ==2){
				tip ="您确认放弃加入球队"+recruit.team.teamTitle+"吗？";
			}else{
				if(recruitList.some(function(n){return !n.confirmStatus && n.id!=recruit.id;})){
					tip ="您确认加入球队"+recruit.team.teamTitle+"吗？其它邀请将自动关闭！";
				}else{
					tip ="您确认加入球队"+recruit.team.teamTitle+"吗？";
				}
				if(parent.user.team){
					tip+="<br/>您将自动退出当前球队"+parent.user.team.teamTitle+"!";
			   	}
			}
		    layer.confirm(tip, function(index){
				$http.get("/app/confirmRecruit",{params:{recruitId:recruit.id,confirmStatus:confirmStatus}}).success(function(data, status, headers, config){
					if(data.success){
							if(confirmStatus ==1){
								parent.user.team=recruit.team;
							}else{
								recruit.confirmStatus =confirmStatus;
							}
					}
				});
				layer.close(index);
		    });
		};
		
		 $rootScope.userDefer.then(function(data){
		    if(data.success&&data.user.auditStatus!=2){
			    getNotice(data.user);
				//初始化通知页签结构信息
		}})
			   
	});
})();