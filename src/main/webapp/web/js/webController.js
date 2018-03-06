(function() {
	"use strict";
	var app = angular.module("football");
	// 路由
	app.config(function($routeProvider) {
		$routeProvider.when('/:module', {
			templateUrl : function($routeParams) {
                return $routeParams.module+".html";
            }
		}).when('/:module/:detail', {
			templateUrl : function($routeParams) {
                return $routeParams.detail+".html";
            }
		}).otherwise({
			redirectTo : '/chief'
		});
	});
	
	// 控制器
	app.controller('webController', function($rootScope,$scope,$http,$uibModal, $location,$cookies,$q,appData,$routeParams) {

		// 默认模块
		//$location.url("index");
		//$scope.module = $location.$$path.substr(1);
		$rootScope.$on('$locationChangeSuccess', function(event, newUrl){
			console.log(arguments);
			$scope.subpage ={};
			var regExp = /#(\/)?(\w+)(\/)?(\w+)?(\/)?(\w+)?/;
			var matches = newUrl.match(regExp);
			if(matches){
				switch(matches[2]){
				case "chief":$scope.module="chief";$scope.subpage = {hash:"#chief",moduleName:"首页"};break;
				case "square":$scope.module="square";$scope.subpage = {hash:"#square?tab="+$routeParams.tab,moduleName:"广场"};break;
				case "play":$scope.module="play";$scope.subpage ={hash:"#play",moduleName:"赛事"};break;
				case "transfers":$scope.module="transfers";$scope.subpage = {hash:"#transfers",moduleName:"球员转会"};break;
				case "team":$scope.module="team";$scope.subpage = {hash:"#team",moduleName:"球队"};break;
				case "player":$scope.module="player";$scope.subpage = {hash:"#player",moduleName:"球员"};break;
				case "account":$scope.module="account";$scope.subpage = {hash:"#account",moduleName:"用户中心"};break;
				case "notice":$scope.module="notice";$scope.subpage = {hash:"#notice",moduleName:"通知"};break;
				};
				if(!matches[4]) return ;
				switch(matches[4]){
				case "myPlay" :$scope.subpage.name = "我的比赛";break;
				case "myInfo" :$scope.subpage.name = "个人资料";break;
				case "teamInfo":$scope.subpage.name = "球队档案";break;
				case "myPlayer":$scope.subpage.name = "我的球队";break;
				case "findTeamList":$scope.subpage.name = "加入球队";break;
				case "createTeam":$scope.subpage.name = "新建球队";break;
				case "video":$scope.subpage.name = "我的视频";break;
				case "attendanceDetail":$scope.subpage.name = "签到情况";break;
				case "newsDetail":$scope.subpage.name = "站内信";break;
				case "img":$scope.subpage.name = "我的图片";break;
				case "release":$scope.subpage.name = "发布";break;
				case "findDetail":$scope.subpage.name = "招人";break;
				case "playDetail":$scope.subpage.name = "比赛情况";break;
				case "applyDetail":$scope.subpage.name = "找队";break;
				case "playerDetail" :$scope.subpage.name = "球员详情";break;
				default:$scope.subpage.name = "查看";break;
				};
			}
		})
		
		var staticDictPromis = appData.getStaticDicts().then(function(data){
			$.extend($rootScope,data);
		})
		
		$rootScope.preparePromise = $q.all([staticDictPromis])
		
		$rootScope.postConfig = {
				  transformRequest:function(obj){
						var str=[];
						if(typeof obj === 'object'&&!obj.length){
							for(var p in obj){
								str.push(encodeURIComponent(p) +"="+encodeURIComponent(obj[p]));
							}
						}else if(typeof obj === 'object'&&obj.length>0){
							for(var p in obj){
								str.push(encodeURIComponent(obj[p].name) +"="+encodeURIComponent(obj[p].value));
							}
						}
						return str.join("&");
					}, 
				headers:{'Content-Type':'application/x-www-form-urlencoded'}
			  };
		// 强制刷新视图
		$scope.synData = function(){
			$scope.$$phase || $scope.$apply();
		};
		$scope.gotoPage = function(hash){
			$location.url(hash);
		}
		
		//获取player 对code对应的操作有没有权限
	    $scope.getPermission = function(player,code){
	    	if(!player){
	    		appData.alert("请先登陆！");
	    		return;
	    	}
	    	var name = $rootScope.teamPermissions[code].name;
	    	var message ="您被限制使用<"+name+">功能";
	    	var allowed = true;
	    	//首先获取是否球队禁掉了相应要权限
	    	if(player.team && player.team.locks){
	    		var locks = player.team.locks;
	    		if(/^\{.+\}$/.test(locks)){
					try{
						var json =JSON.parse(locks)||{};
						var access = json[code];
						if(access && access.v==2 && (!access.toDate||new Date(access.toDate)<new Date() )){
							allowed = false;
							message = "因球队权限被控制，"+message;
						}
					}catch(e){
					}
				}
	    	}
	    	if(allowed && player.locks){
	    		var locks = player.locks;
	    		if(/^\{.+\}$/.test(locks)){
					try{
						var json =JSON.parse(locks)||{};
						var access = json[code];
						if(access && access.v==2 && (!access.toDate||new Date(access.toDate)<new Date() )){
							allowed = false;
							message = "因您权限被控制，"+message;
						}
					}catch(e){
					}
				}
	    	}
	    	if(!allowed){
	    		appData.alert(message);
	    	}
	    	return allowed;
		}
		
		
		$rootScope.getImgUrl = function(paths,size){
			size = size||1;
			var defaultImg = "/admin/images/ic_photo_grey600_48dp.png";
			try{
				var pathObj =JSON.parse(paths)||{};
				if(pathObj[size]){
					return pathObj[size];
				}
			}catch(e){
			}
			return defaultImg;
		}

		$rootScope.delItem = function(pics,pic){
			pics.remove(function(n){return n == pic})
			if(pic.seq != undefined){
				pics.each(function(n){
					if(n.seq > pic.seq){
						n.seq--;
					}
				})
			}
		}

		$scope.getLocation = function(val) {
			  return $http.get('/app/field/search', {
			      params: {
			        name: val,
			        pageSize: 10
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 };
		 
		$rootScope.getTeam = function(val,teamType,exId,cupId){
			 return $http.get('/app/team/search', {
			      params: {
			        name: val,
			        teamType:teamType,
			        exId:exId,
			        cupId:cupId,
			        pageSize: 10
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 }
	
		$rootScope.getPlayer = function(val,isCaptain){
			 return $http.get('/app/player/search', {
			      params: {
			        name: val,
			        pageSize: 10,
			        isCaptain:isCaptain
			      }
			    }).then(function(resp){
			      return resp.data;
			    });
		 }
		$rootScope.getNow = function(){
			return new Date().getTime();
		}
		
		//富文本编辑器的config
		$rootScope.config = {};
		
		$scope.showPhoto = function(picUrl){
			layer.open({
				  type: 1,
				  title: false,
				  closeBtn: 0,
				  area: '660px',
				  skin: 'layui-layer-nobg', //没有背景色
				  shadeClose: true,
				  content: $('#photoModal')
				});
		};
		
		
		
		$scope.getTeamTypeIcon = function(teamType){
			  switch (teamType){
			  case 11:return "./img/icon_teamgroup_11person.png";
			  case 7:return "./img/icon_teamgroup_7person.png";
			  case 5:return "./img/icon_teamgroup_5person.png";
			  case 3:return "./img/icon_teamgroup_3person.png";
			  }
		  }
	   $scope.getScore = function(game){
		   //比赛已结束
		   if(game && game.teamBOperation == 1 && new Date(game.beginTime) <= new Date() && game.auditStatus==1 &&game.scoreA!=undefined && game.scoreB!=undefined){
		    	return game.scoreA+":"+game.scoreB
		    }else{
		    	return "--:--";
		    }
       }
	   $scope.getWin = function(game){
		   //比赛已结束
		    if(game.scoreA!=undefined && game.scoreB!=undefined ){
		    	if(game.scoreA > game.scoreB) return 1;
		    	if(game.scoreA == game.scoreB) return 0;
		    	if(game.scoreA < game.scoreB) return -1;
		    }
		    return "";
       }
	   
	   $scope.getType = function(game){
		   if(!game) return;
		   if(game.teamBOperation == 2){
			   return "已拒战";
		   }else if(!game.teamBOperation && new Date(game.beginTime) > new Date()){
			   return "约战中";
		   }else if(!game.teamBOperation && new Date(game.beginTime) <= new Date()){
			   return "已过期";
		   }else if(game.teamBOperation ==1 && new Date(game.beginTime) > new Date()){
			   return "未开赛";
		   }else if(game.teamBOperation == 1 && new Date(game.beginTime) <= new Date()){
			      if(game.auditStatus==2){
			   		  return '审核不通过';
			   	   }else if(game.auditStatus==1){
			   		  return "已结束";
			   	   }else if(game.scoreA1 === undefined || game.scoreA2 === undefined){
			   	      return "待录入";
			   	   }else if(game.scoreA1!=game.scoreA2 || game.scoreB1!=game.scoreB2 ){
			   		  return "待审核";
			   	   }
		   }
	   }
		$scope.pastTime = function(time){
			return new Date(time) > new Date();
		}
		$scope.getGameConfirmation = function(game){
			if(!game||!game.teamB||!$scope.user||!$scope.user.team){
				return "";
			}
			if(game.teamB.id == $scope.user.team.id && $scope.user.isCaptain!=2 && !game.teamBOperation){
				return ""
			}
			switch(game.teamBOperation||0){
				case 1:return "已应战";
				case 2:return "已拒绝";
				default:return "等待应战中...";
			}
		}
		   
		$scope.getWidth = function(v,obj){
			if(typeof v!='number'||!obj) return;
			var radio =v/(obj.win+obj.lost+obj.even);
			return {"width":radio*100+"%"};
		}
		$scope.subpage ={};
		
		$scope.respondDare = function(game,teamBOperation,teamB){
			if(!$scope.getPermission($scope.user,'accept')){
    	    	return;
    	    }
		    $http.get("/app/respondDare",{params:{gameId:game.id,teamBId:teamB && teamB.id,operation:teamBOperation}}).success(function(data){
			  if(data.success){
				  game.teamBOperation = teamBOperation;
				  game.teamB = $scope.user.team;
			  }
		   })
		}
		
		$scope.existTeamLike = function(team){
			if(!$scope.likes||!$scope.likes.teamLikes||!team) return false; 
			return $scope.likes.teamLikes.some(function(n){
				return n.team.id== team.id;
			})
		}
		
		$scope.existPlayerLike = function(player){
			if(!$scope.likes||!$scope.likes.playerLikes||!player) return false; 
			return $scope.likes.playerLikes.some(function(n){
				return n.player.id== player.id;
			})
		}
		
		
		//给球队点赞
		$scope.saveTeamLike = function(team){
			if(!$scope.user){
				layer.alert("请先登陆！", {
			        skin: 'layui-layer-lan'
			        ,closeBtn: 0
			        ,shift: 4 //动画类型
		         });
				return;
			}
			
			//看今日点赞次数有无超过上限
			if($scope.sysconfig.like_team_max <= $scope.likes.teamLikes.length){
			    layer.alert("您一天之内只有"+$scope.sysconfig.like_team_max+"次给球队点赞的机会！", {
				        skin: 'layui-layer-lan'
				        ,closeBtn: 0
				        ,shift: 4 //动画类型
			    });
			}else if($scope.existTeamLike(team)){
				return;
			}else{
				var item ={
					team:team,giver:$scope.user,status:1	
				}
				appData.saveTeamLike($scope.user.id,team.id,function(data){
					if(data.success){
						$scope.likes.teamLikes.push(item);
						team.likeNum = team.likeNum||0;
						team.likeNum++;
					}
				})
			}
		}
		//给球员点赞
		$scope.savePlayerLike = function(player){
			if(!$scope.user){
				layer.alert("请先登陆！", {
			        skin: 'layui-layer-lan'
			        ,closeBtn: 0
			        ,shift: 4 //动画类型
		         });
				return;
			}
			//看今日点赞次数有无超过上限
			if($scope.sysconfig.like_player_max <= $scope.likes.playerLikes.length){
			    layer.alert("您一天之内只有"+$scope.sysconfig.like_player_max+"次给球员点赞的机会！", {
				        skin: 'layui-layer-lan'
				        ,closeBtn: 0
				        ,shift: 4 //动画类型
			    });
			}else{
				var item ={
					player:player,giver:$scope.user,status:1	
				}
				appData.savePlayerLike($scope.user.id,player.id,function(data){
					if(data.success){
						$scope.likes.playerLikes.push(item);
						player.likeNum = player.likeNum||0;
						player.likeNum++;
					}
				})
			}
		}
		
		$scope.getTotalUnread = function(){
			var total = 0;
			$scope.itemsList && $scope.itemsList.each(function(n){
				total += (n.unReadNum||0);
    		})	
    		return total;
		}
		
		function setUnreadCnt(user){
			var itemsListWhole = [{name:"球队招人"},{name:"入队申请"}, {name:"约战"}, {name:"签到"}, {name:"站内信"}, {name:"我的申请"}, {name:"邀我入队"}, {name:"球员变动"}];
			if(user.isCaptain ==1){
        		//用户为队长时
        		$scope.itemsList =  [itemsListWhole[0],itemsListWhole[1],itemsListWhole[2]];
        	}else if(user.isCaptain !=1 ){
        		//用户为普通球员时 , 没有权限审核新球员
        		$scope.itemsList = [itemsListWhole[2],itemsListWhole[3],itemsListWhole[4],itemsListWhole[5],itemsListWhole[6]]
        	}
			//当球员属于某球队时,查看球员变动
			if(user.team && user.team.id){
				$scope.itemsList.push(itemsListWhole[7]);
			}
			appData.getUnreadCnt(user.id,function(data){
				itemsListWhole[0].unReadNum = data.unReadCnt1;//球队招人
				itemsListWhole[1].unReadNum = data.unReadCnt2;//入队申请
				itemsListWhole[2].unReadNum = data.unReadCnt3;//约战
				itemsListWhole[3].unReadNum = data.unReadCnt4;//签到
				itemsListWhole[4].unReadNum = data.unReadCnt5;//站内信
				itemsListWhole[5].unReadNum = data.unReadCnt6;//我的申请
				itemsListWhole[6].unReadNum = data.unReadCnt7;//邀我入队
				itemsListWhole[7].unReadNum = data.unReadCnt8;//球员变动
			})
			
			appData.getLikes(user.id,function(data){
        		$scope.likes = data;
        	})
        	appData.getSysconfig(function(sysconfig){
        		$scope.sysconfig  = sysconfig;
        	})
        	
		}
		
		 function autoLogin(){
			var username = $cookies.get("username");
			var password = $cookies.get("password");
			 if(!username||!password){
			    	if($location.$$path.substr(1).startsWith("account")){
						$location.url("chief");
					}
			    	return;
		    }
			$rootScope.userDefer = appData.login(username,password);
			$rootScope.userDefer.then(function(data){
			    if(data.success&&data.user.auditStatus!=2){
				    $rootScope.user = data.user;
					//初始化通知页签结构信息
				    setUnreadCnt(data.user);
				}})
		}
		 
	
			
		
		$scope.showLoginModal = function(){
			var modalInstance = $uibModal.open({
			      templateUrl: 'loginDiv.html',
			      controller: 'loginController',
			      size:'sm',
			      resolve: {hideRegister:function(){return false;}
			      }
			    });
			    modalInstance.result.then(function (args) {
			    	$rootScope.user = args.user;
			    	setUnreadCnt(args.user);
			    }, function () {
			        console.info('Modal dismissed at: ' + new Date());
			    });
		};
		
		$scope.logout = function(){
			$cookies.password = '';
			$http.get("/app/logout").success(function(data, status, headers, config) {
				if(data.success){
					delete $rootScope.user ;
					if(/^account|^myInfo/.test($location.$$path.substr(1))){
						$location.url("chief");
					}
				}
			})
		} 
		
		 /**
		  * 获取广告
		  */
		  $http.get("/app/findAds") .success(function(resp, status, headers, config) {
			  	if(resp.success){
			  		$scope.slides = resp.data.findAll(function(n){return n.position == 1});//幻灯片
			  		$scope.centerAd = resp.data.find(function(n){return n.position == 2});//横条广告
			  		$scope.leftAd = resp.data.find(function(n){return n.position == 3});
			  		$scope.rightAd = resp.data.find(function(n){return n.position == 4});
			  	}
		   })
		
		 autoLogin();
		
	});
	
   $(".nav-menu li").on("mouseover",function(){
        $(this).addClass("cur");
    }).on("mouseout",function(){
        $(this).removeClass("cur");
    })
    
    var left = (window.innerWidth - 980)/2 -160;
    left = (left>=0?left:0)+"px";
    $(".left-ad").css({"top":"200px","left":left});
    $(".right-ad").css({"top":"200px","right":left});
		 
})();