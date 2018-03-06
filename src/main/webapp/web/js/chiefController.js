(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('chiefController', function($rootScope,$scope, $http,$uibModal, appData) {
    
		 $scope.parent = $scope.$parent;
		
		//幻灯片自动切换间格
		 $scope.myInterval = 3000;
		 
		 //公告:
		  $http.post("/app/listNews",
				  {
			  "isEnabled":1,
			  "status":2,
			  "orderby":" u.sort desc, u.startTime desc",
			  "pageSize":5
		  }).success(function(data, status, headers, config){
			  $scope.newsList = data.list;
		  })
		//招人:
		  appData.getRecruits({
			  "isPublic":1,
			  "pageSize":5
		  },function(data){
			  $scope.recruitList = data.list;
		  });
		  //找队:
		  appData.getApplys({
			  "isPublic":1,//广场消息
			  "pageSize":5
		  },function(data){
			  $scope.applyList  = data.list;
		  });
		  
		appData.getGames({
		      "isPublic":1,//shi == 1时 , public
			  "gameStatus":1,//约赛中
			  "pageSize":5
		},function(data){
			 $scope.dareList = data.list;
		})
		
	    
		  /**
		   * 球队
		   */
		  
		  $scope.teamtabList = [{name:"全部",teamType:0,list:[]},{name:"3人制",teamType:3,list:[]},
		                        {name:"5人制",teamType:5,list:[]},{name:"7人制",teamType:7,list:[]},{name:"11人制",teamType:11,list:[]}];
		  $scope.teamtabList.each(function(n){
			  var args ={'teamType':n.teamType,"orderby":"likeNumLastWeek desc","pageSize":6};
			  appData.getTeams(args,function(data){
				  n.list.insert(data.list,0);
			  })
			  appData.getTeams($.extend(args,{"orderby":"createTime desc","pageSize":2}),function(data){
				  n.list.add(data.list);
			  })
		  })
		  
		  /**
		   * 球员
		   */
		   $scope.playertabList = [{name:"全部",teamType:0,list:[]},{name:"3人制",teamType:3,list:[]},
		                        {name:"5人制",teamType:5,list:[]},{name:"7人制",teamType:7,list:[]},{name:"11人制",teamType:11,list:[]}];
		  $scope.playertabList.each(function(n){
			  var args ={'teamType':n.teamType,"orderby":"likeNumLastWeek desc","pageSize":6};
			  appData.getPlayers(args,function(data){
				  n.list.insert(data.list,0);
			  })
			  appData.getPlayers($.extend(args,{"orderby":"createTime desc","pageSize":2}),function(data){
				  n.list.add(data.list);
			  })
		  })
		  /**
		   * 比赛场地
		   */
		  appData.getFields({"pageSize":8},function(data){
			  $scope.fields = data.list;
		  });
		  $scope.upOrDown = function(v){
			  if(parseFloat(v)>0){
				  return './img/icon_list_arrow_up.png';
			  }else if(parseFloat(v)<0){
				  return './img/icon_list_arrow_down.png';
			  }else{
				  return "";
			  }
		  }
		  
	  /**
	   * 最新赛事
	   */	  
	  $scope.futureGames = [];
		$.each([11,7,5,3],function(i,v){
					appData.getGames({
						  "teamType":v,
						  "gameStatus":5,//比赛预告
						  "orderby":"beginTime asc",
						  "pageSize":5
					  },function(data){
						 $scope.futureGames[i] = data.list;
					})
		})
		/**
		 * 历史赛事
		 */
		 $scope.pastGames = [];
		$.each([11,7,5,3],function(i,v){
					appData.getGames({
						  "teamType":v,
						  "gameStatus":10,//历史赛事
						  "orderby":"beginTime desc",
						  "pageSize":5
					  },function(data){
						 $scope.pastGames[i] = data.list;
					})
		})
	  
	  /**
	   * 转会记录
	   */
	  $scope.transfersList = [{teamType:11,list:[]},{teamType:7,list:[]},{teamType:5,list:[]},{teamType:3,list:[]}];
		$scope.transfersList.each(function(v){
			appData.getTransfers({teamType:v.teamType},function(data){
				 v.list = data.list;
			})
		})
	});
})();