(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('teamController', function($scope, $rootScope,$http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.itemsList = [{name:"全部",teamType:0}, {name:"3人制",teamType:3}, {name:"5人制",teamType:5}, {name:"7人制",teamType:7},{name:"11人制",teamType:11}]

    //获取球队信息
		$scope.getTeams = function(teamType,pageNo){
				var typeList = (teamType === undefined)?$scope.itemsList:$scope.itemsList.findAll(function(n){return n.teamType==teamType});
				$.each(typeList,function(i,v){
					appData.getTeams({
						  "teamType":v.teamType, 
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
				$scope.getTeams(teamType);
			}
		   
		    $scope.find = function(event){
				if(event&&event.keyCode!=13){
					return;
				}
				$scope.getTeams(undefined,1);
			}
		   
		   $scope.search = function(item){
			   if(!$scope.search.str){
				   return true;
			   }
			   if(item.teamTitle&&item.teamTitle.indexOf($scope.search.str)!=-1){
				   return true;
			   }
			   return false;
		   }
		   $scope.getTeams();
		  
	});
	
	
	app.controller('teamInfoController', function($scope,appData, $http,$rootScope,$routeParams) {
		console.log("teamId:"+$routeParams.id);
		appData.findTeamById($routeParams.id,function(data){
		   $scope.curTeam = data;
	    });
	    appData.getPlayers({ "teamId":$routeParams.id,
					 		"isCaptain":1,//是队长
							"pageSize":1	},function(data){
								 if(data.list.length>0){
									  $scope.curCaptain = data.list[0];
								  }
								})		
	 });
	
	app.controller('findTeamListController', function($scope, $http,$rootScope,$routeParams) {
		console.log("teamId:"+$routeParams.id);
		
	});
	
	app.controller('createTeamController', function($scope, $http,$rootScope,$routeParams,$uibModal,appData,$location) {
		console.log("userId:"+$routeParams.id);
		var parent = $scope.$parent;
		
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
				    	$scope.curTeam.iconUrl = args.picPath;
				    	$scope.synData();
				    }, function () {
				        console.info('Modal dismissed at: ' + new Date());
				    });
			})
		};
		
		$scope.add = function(form){
			form.submited = true;
			if(form.$invalid){
				return;
			}
			var playerId = $scope.user.id;
			$scope.curTeam.register = playerId;
			$http.get("/app/canRegTeam?playerId="+playerId).success(function(resp){
				if(resp.success){
					regteam($scope.curTeam)
				}else{
					appData.alert(resp.error);
				}
			})
		}
		
		function regteam(curTeam){
			$http.post("/app/team/register",curTeam).success(function(resp, status, headers, config) {
				//after successfully uploaded, clear the form
				if(resp.success && resp.data.id){
					layer.confirm("创建球队成功,恭喜你成为队长!", {btn: ['确定'],title:false},function(index){
						 //	 $location.url("/account");
						 //location.assign("/web/main.html#account");
						 $scope.$apply(function(){
							 $scope.user.team = resp.data;
							 $scope.user.isCaptain = 1;
							 $location.url("account");
								layer.close(index);
						 })
	   		        });
				}
			})
		}
		
		$scope.cancel = function(){
			$location.url("account");
		}
		$scope.curTeam = {acRate:1};
		if($routeParams.id){
			 $http.post("/app/listTeam",{"dismisser":$routeParams.id,
					"isDismissed":1,
					"orderby":"dismissTime desc"}).success(function(data, status, headers, config){
						if(data.list.length){
							$scope.items = {};
							$scope.items.list = data.list;
							$scope.items.total = data.totalRecord;
							$scope.items.curPage = data.currentPage;
						}
			  })
		}
		//获取默认的球队队徽
		$http.get("/app/getLogos").success(function(resp){
			if(resp.success){
				$scope.logos = resp.data;
			}
		})
		
	});
	app.controller('teamInfoEditController', function($scope, $http,$rootScope,$routeParams,appData,$location) {
		console.log("teamId:"+$routeParams.id);
		var parent = $scope.$parent;
		
		$scope.uploadImage = function(){
				appData.uploadImage({},function(data){
				$scope.curTeam.coverUrl = data.picPath;
				$scope.synData();
			})
		};
		
		$scope.save = function(form){
			form.submited = true;
			if(form.$invalid){
				return;
			}
			var curTeam = $scope.curTeam;
			$http.post("/app/team/saveOrUpdate",curTeam).success(function(data, status, headers, config) {
				 if(data.success){
					$rootScope.user.team = curTeam;
					$location.url("account");
				 }
			}).error(function(data, status, headers, config) {
    			 $scope.modifyTeamMessage = "修改球队信息失败!";
			});		
		}
		
		$scope.cancel = function(){
			$location.url("account");
		}
		if($routeParams.id){
			appData.findTeamById($routeParams.id,function(data){
				   $scope.curTeam = data;
			    });
		}
		
	});
	
})();