(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('playerDetailController', function($scope, $http,$rootScope, $routeParams,$location,appData) {
		var parent = $scope.$parent;
	
	   //转让队长
	   $scope.setCaptain = function(player){
			 layer.confirm("您确定转让长给球员"+player.name+"吗?(未处理事务将转交新队长处理!)", function(index){
				   appData.setCaptain(player.id,player.team.id,function(data){
						if(data){
							appData.getPlayer(parent.user.id,function(data){
								$.extend(parent.user,data);
								$location.url("account");
							})
						}
					});
					layer.close(index);
		    });
	   }
	   //开除某球员
	   $scope.dismiss = function(player){
		   layer.confirm("您确定开除球员"+player.name+"吗?", function(index){
			    appData.dismissPlayer(player.id,function(data){
				    delete player.team;
				});
				layer.close(index);
	      });
	   }
	   
	   /**
	    * 获取球员发布的有效的找队信息
	    */
	   function getApplyMes(playerId){
		   	$http.get("/app/activeApply",{params:{"playerId":playerId}}).success(function(resp){
		   		if(resp.success){
		   			var apply = resp.data;
		   			if(apply && apply.tanks && apply.tanks.length){
		   				$scope.applyTank = apply.tanks.find(function(n){return n.team.id == $scope.user.team.id && n.confirmStatus !=2});
		   			}else{
		   				$scope.applyTank = null;
		   			}
		   			if(!$scope.applyTank){
		   				$http.get("/app/activeRecruit",{params:{"playerId":playerId,"teamId":$scope.user.team.id}}).success(function(resp2){
		   					if(resp2.success){
		   						$scope.recruitTank = resp2.data;
		   					}
		   				});
		   			}
		   		}
		   	});
	   };
	   //邀请入队
	   $scope.recruitHe = function(player,team){
		  if(!$scope.getPermission($scope.user,'recruit')){
   	    	return;
   	      }
		   $http.get("/app/recruitPlayer",{params:{playerId:player.id,teamId:team.id}}).success(function(resp){
			   if(resp.success){
				   getApplyMes($routeParams.id);
			   }
		   })
	   }
	   
	   //发送入队通知 或 拒绝加入
	   $scope.giveOffer = function(applyTank,accept){
		   $http.get("/app/audit", {params:{tankId:applyTank.id,auditStatus:accept,captainId:$scope.user.id}}).success(function(data, status, headers, config){
	  			 if(data.success){
					 getApplyMes($routeParams.id);
					 if(data.message){
						 $scope.player.team = $scope.user.team;
						 appData.alert(data.message);
		    		}
				 }
		   })
       }
	   
	   $scope.pageChanged = function(tab){
		   var viewType = 1;
		   if( parent.user && parent.user.team && $scope.player.team && $scope.player.team.id == parent.user.team.id){
			   viewType = undefined;
		   }
		       if(tab =="图片"){
		    	   var v = $scope.itemsList[0];
		    	   appData.getPhotos({"playerId":$routeParams.id,"currentPage":v.curPage||1,"pageSize":8,"viewType":viewType},function(data){
					   v.list = data.list;
					   v.total = data.totalRecord;
					   v.curPage = data.currentPage;
				   })
		       }else{
		    	   var v = $scope.itemsList[1];
		    	   appData.getVideos({"playerId":$routeParams.id,"currentPage":v.curPage||1,"pageSize":8,"viewType":viewType},function(data){
					   v.list = data.list;
					   v.total = data.totalRecord;
					   v.curPage = data.currentPage;
				   })
		       }
	     }
		   
	     $scope.$watch('user',function(newValue,oldValue, scope){
	    	 if(newValue){
	    		  if(newValue.isCaptain ==1 && $routeParams.id){
		        		//用户为队长时
		        	 getApplyMes($routeParams.id);
		          }
	    	 }
	    });
	     
	    
	     $scope.itemsList = [{name:"图片",active:true},{name:"视频"}];
	      if($routeParams.id){
		       appData.getPlayer($routeParams.id,function(data){ 
		    	    $scope.player = data;
		    	    appData.caluPlayer($scope.player);
		       		$scope.pageChanged("图片");
		       		$scope.pageChanged("视频");
		       });
			   appData.getGameLogs({"playerId":$routeParams.id},function(data){
				   $scope.gameLogs = data.list;
			   })
			   appData.findHistory($routeParams.id,function(data){
				   $scope.history = data;
			   });
		}
		  
	});
	
	app.controller('playerInfoController', function($scope,appData, $routeParams) {
		var parent = $scope.$parent;
		if($routeParams.id){
			appData.getPlayer($routeParams.id,function(data){ $scope.player = data;});
			appData.findHistory($routeParams.id,function(data){
				   $scope.history = data;
		     });
		}
	});
})();