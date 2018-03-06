(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('releaseController', function($scope,$rootScope, $http,$location,$routeParams,appData) {
		var parent = $scope.$parent;
		
		
		//通用的.
	    $scope.checkAll = function(list,mother){
	    	if(!mother.checkAll){
	    		mother.checkAll = true;
	    		list.each(function(v){v.checked = true;})
	    	}else{
	    		mother.checkAll = false;
	    		list.each(function(v){v.checked = false;})
	    	}
	    }
	    
		$scope.saveApply = function(form){
			if(!$scope.getPermission($scope.user,'apply')){
    	    	return;
    	    }
			  if(!form.$valid){
				  form.$setDirty(true);
				  return;
			  }
			$http.post("/app/apply/saveOrUpdate",$scope.apply).success(function(data, status, headers, config){
					if(data.success){
						$location.url("/account?type=7");
					}
			  })			
		}
		
		function findFinishedGames(teamId){
			$http.get("/app/game/toSocreGames",{params:{teamId:teamId}}).success(function(resp){
				if(resp.success){
					$scope.games =resp.data;
				}
			})
		}
		   $scope.$watch('user',function(user,oldValue, scope){
		        if(user ){
		        	//获取未关闭的找队信息
		        	appData.getApplys({"playerId":user.id,isOpen:1},function(data){
		        		if(data&&data.list&&data.list.length){
		        			$scope.apply = data.list[0];
		        		}else{
		        			$scope.apply = {player:{id:user.id},isPublic:2,isOpen:1,isEnabled:1,applyTime:new Date().getTime()};
		        		}
		        	})
		        	if(user.isCaptain ==1 && user.team.id){
		        		//用户参加比赛  , 且 赛已结束 ,且未审核
		        		findFinishedGames(user.team.id);
			        	//初始化球队找人
			        	 $scope.recruit = {title:user.team.teamTitle+'球队招人',isPublic:1,isEnabled:1,team:{id:user.team.id}};
		        	}
		        }
		  });
		   
			/** begin mehtod for input score             **/  
		   //A队上传比分
		   $scope.inputScoreA  = function(game){
			   game.inputA=!game.inputA;
			   game._scoreA1 = game.scoreA1;
			   game._scoreB1 = game.scoreB1;
		   }
		   $scope.confirmScoreA  = function(game,user){
			   if(/^[1-9]\d*|0$/.test(game._scoreA1) && /^[1-9]\d*|0$/.test(game._scoreB1)){
				  $http.get("/app/game/inputScore",
						  {params:{gameId:game.id,teamId:user.team.id,scoreA:game._scoreA1,scoreB:game._scoreB1}}
				   ).success(function(resp, status, headers, config){
					   if(resp.success){
						   findFinishedGames(user.team.id);
						   game.inputA=!game.inputA;
						   console.log("update success!")
					   }
				  })
			   }
		   }
		   
		   //B队上传比分
		   $scope.inputScoreB  = function(game){
			   game.inputB=!game.inputB;
			   game._scoreA2 = game.scoreA2;
			   game._scoreB2 = game.scoreB2;
		   }
		   $scope.confirmScoreB  = function(game,user){
			   if(/^[1-9]\d*|0$/.test(game._scoreA2) && /^[1-9]\d*|0$/.test(game._scoreB2)){
				   $http.get("/app/game/inputScore",
							  {params:{gameId:game.id,teamId:user.id,scoreA:game._scoreA2,scoreB:game._scoreB2}}
				   ).success(function(resp, status, headers, config){
					   if(resp.success){
						   findFinishedGames(user.team.id);
						   game.inputB=!game.inputB;
						   console.log("update success!")
					   }
				  })
			   }
		   }
		   
		/** end of  mehtod for input score     **/   
		   
		/**
		 * begin mehtod for recruit
		 */   
		  
		  $scope.releaseRecruit = function(form){
			  if(!$scope.getPermission($scope.user,'message')){
	    	    	return;
	    	    }
			    form.submited = true;
				if(form.$invalid){
					return;
				}
				$http.post("/app/recruit/saveOrUpdate",$scope.recruit).success(function(resp){
					if(resp.success){
						$location.url("/account?type=6");
					}
				})
		  }
		   
	   /**
		 * end of recruit
		 */   
		  
		  /** begin of  mehtod for upload images    */   
		  $scope.curGroup = {pics:[],viewType:1,isEnabled:1}
		  $scope.uploadImage = function(){
			    if( !$scope.getPermission($scope.user,'photo')){
	    	    	return;
	    	    }
			    appData.uploadImages({},function(data){
			    	data.each(function(n){
			    		$scope.curGroup.pics.push({url:n.picPath,seq:$scope.curGroup.pics.length});
						parent.synData();
			    	})
				})
		  };
			
	      $scope.savePhoto = function(form){
	    	    if( !$scope.getPermission($scope.user,'photo')){
	    	    	return;
	    	    }
				var curGroup = $scope.curGroup;
				if(curGroup.pics.length==0){
					appData.alert("请上传图片!");
				    return;
				}
				curGroup.player = parent.user;
				curGroup.createTime = new Date().getTime();
				curGroup.auditStatus = 1;
				curGroup.pics.each(function(n){
					n.status = 1;
				})
				$http.post("/app/photo/saveOrUpdate",curGroup).success(function(data, status, headers, config) {
						if(data.success){
							  $location.url("/account?type=2");
						}
				})	
			}
			
		/** end of  mdthods for upload Images     **/   
	      
	      /** begin of  mehtods for upload video     **/   
	      
	      $scope.curVideo = {viewType:1}
		  function parseVideoDiv(videoDiv){
			  var mat =	videoDiv.match(/http:\/\/player\.youku\.com\/\S*(sid|embed)\/([^/]+)('|"|\/v.swf)/);
			  return (mat && mat.length == 4? mat[2]:"");
			}
			
	      $scope.addVideo = function(form){
	    	    if(!$scope.getPermission($scope.user,'video')){
	    	    	return;
	    	    }
		    	if(!form.$valid){
					  form.$setDirty(true);
					  return;
				}
				var curVideo = $scope.curVideo;
				
				var vid = parseVideoDiv(curVideo.videoDiv);
				if(!vid){
					appData.alert("优酷URL输入解析错误，请检查！")
					return;
				}
				//默认为启用.
				curVideo.isEnabled = 1;
				curVideo.createTime = new Date().getTime();
				curVideo.player = parent.user;
				curVideo.auditStatus = 1;
				$http.jsonp('http://play.youku.com/play/get.json?vid='+vid+'&ct=10&ran=9083&callback=JSON_CALLBACK'
				).success(function(data, status, headers, config) {
					if(data&&data.data&&data.data.video&&data.data.video.logo){
						curVideo.screenshot = data.data.video.logo;
					}
					$http.post("/app/video/saveOrUpdate",curVideo).success(function(data, status, headers, config) {
						if(data.success){
							  $location.url("/account?type=3");
						}
				    })	
			    }).error(function(data, status, headers, config) {
			    	 appData.alert("优酷URL输入解析错误，请检查！")
				        // called asynchronously if an error occurs
				        // or server returns response with an error status.
		        });
			}
	      /** end of  mdthods for upload video     **/   
	      
	      
     /** begin of  mehtods for send messge     **/   
	      //1:普通信 ， 2:签到信
	      $scope.curMessage = {messageType:1,checkAll:true}
	      
	      $scope.showMembers = function(){
	    	  if(!$scope.teamMembers){
					 $http.get("/app/findPlayersByTeam",{params:{teamId:$rootScope.user.team.id,isCaptain:false}}).success(function(resp, status, headers, config){
						  if(resp.success){
							  $scope.teamMembers =resp.data;
							  $scope.teamMembers.each(function(n){
								  n.checked = true;
							  })
						  }
					  })
				}
	      }
	      
	
		$scope.addMessage = function(form){
			if(!$scope.getPermission($scope.user,'message')){
    	    	return;
    	    }
			form.submited = true;
			if(form.$invalid){
				return;
			}
			var mes = $scope.curMessage;
			if(!$scope.teamMembers.some(function(n){return n.checked})){
				$scope.messageError= "请至少勾选一个球员！";
				return false;
			}
			
			mes.tanks = [];
		    $scope.teamMembers.each(function(n){
				if(n.checked){
					mes.tanks.push({"player":{id:n.id}});
				}
			})
			if(mes.messageType==1){
				$scope.messageError= "";
				delete mes.beginTime;
				delete mes.address;
			}else{
				//签到信, 默认会发给队长
				mes.tanks.push({"player":{id:$scope.user.id}});
			}
			mes.team={id:$scope.user.team.id};
			mes.isEnabled = 1;
			$http.post("/app/message/saveOrUpdate",mes).success(function(data, status, headers, config){
					if(data.success){
						if(mes.messageType == 2){
							$location.url("/account?type=4");//签到信
						}else{
							$location.url("/account?type=5");//站内信
						}
					}
			  })
		}
		
	      /** end of  mdthods for send messge    **/   
	      
		$scope.tabs ={};
		if($routeParams.tab){
		
			$scope.tabs[$routeParams.tab]= true;
			if($routeParams.tab =='签到信'){
				$scope.tabs['站内信']= true;
				$scope.curMessage.messageType=2;
			}
			//switch ($routeParams.tab)=
		}
	});
	
	
})();