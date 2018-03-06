(function() {
	"use strict";
	var dataService = angular.module('dataService', []);
	dataService.factory("httpInterceptor", ['$q','$rootScope',function($q,$rootScope) {
		var httpInterceptor = {   
				'responseError' : function(response) {
					layer.close($rootScope.waiting);
					return $q.reject(response);  
			  	}, 
				'response' : function(response) {       
					layer.close($rootScope.waiting);
					return response;       },    
				'request' : function(config) {
					if(/\/checkUnique|\/search/.test(config.url)){
						return config;
					}
					$rootScope.waiting = layer.load(1, {
						  shade: [0.2,'#000'] //0.1透明度的白色背景
					 });
					return config;       }      
			//	'requestError' : function(config){         ......         return $q.reject(config);       }     
				}   
		return httpInterceptor; 
	}]);
	
	dataService.config(['$httpProvider', function($httpProvider) { 
		$httpProvider.interceptors.push('httpInterceptor');
	}]);
	
	
	dataService.factory("appData", function($http,$filter,$cookies,$q) {
		var postConfig = {
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
		return {
			/*alert:function(message){
				layer.alert(message, {
					skin: 'layui-layer-lan'
					,closeBtn: 0
					,shift: 4 //动画类型
			    });
			},*/
			alert: function(tip){layer.msg(tip, {time: 2000, icon:0});},
			
			login:function(username,password){
				var deferred = $q.defer();
				$http.post("/app/login",{
					"account":username,
				    "password":password
				}).success(function(data, status, headers, config) {
					if(data.success&&data.user.auditStatus!=2){
						$cookies.put("username",username);
						$cookies.put("password",password);
					}
					deferred.resolve(data);
				}).error(function(data, status, headers, config) {
					console.error("登陆错误!");
				});
				return deferred.promise;
			},
			caluPlayer: function(player){
		    	 function pushItem(array,length){
		    		 for(var i=0;i<length;i++){
		    			 array.push(i);
		    		 }
		    	 }
		    	 player.grade ={crowns:[],shields:[],stars:[]};
		    	 player.win = player.win||0;
		    	 player.even = player.even||0;
		    	 player.lost = player.lost||0;
		    	 player.total = player.total||0;
		    	 /*
		    	  * 1星:1-100分
					2星:101-300分
					3星：301-600分
					4星：601-900分
					5星：901-1200分
		    	  */
		    	 player.point = player.point||0;
		    	 if(player.point >=1 && player.point <=100){
		    		 pushItem(player.grade.stars,1);
		    	 }else if(player.point >=101 && player.point <=300){
		    		 pushItem(player.grade.stars,2);
		    	 }else if(player.point >=301 && player.point <=600){
		    		 pushItem(player.grade.stars,3);
		    	 }else if(player.point >=601 && player.point <=900){
		    		 pushItem(player.grade.stars,4);
		    	 }else if(player.point >=901 && player.point <=1200){
		    		 pushItem(player.grade.stars,5);
		    	 }
		/*    	 var total = player.win + player.even + player.lost+5;
		    	 var crowns = parseInt(total/125);
		    	 var shields = parseInt(total%125/25);
		    	 var stars = parseInt(total%25/5);
		    	 player.grade.num = total/5;
		    	 pushItem(player.grade.crowns,crowns);
		    	 pushItem(player.grade.shields,shields);
		    	 pushItem(player.grade.stars,stars);*/
		     },
		     caluTeamGrades: function(team){
		    	 function pushItem(array,length){
		    		 for(var i=0;i<length;i++){
		    			 array.push(i);
		    		 }
		    	 }
		    	 /*
		    	  * 1星:1-100分
					2星:101-300分
					3星：301-600分
					4星：601-900分
					5星：901-1200分
		    	  */
		    	 team.point = team.point||0;
		    	 team.grade ={crowns:[],shields:[],stars:[]};
		    	 if(team.point >=1 && team.point <=100){
		    		 pushItem(team.grade.stars,1);
		    	 }else if(team.point >=101 && team.point <=300){
		    		 pushItem(team.grade.stars,2);
		    	 }else if(team.point >=301 && team.point <=600){
		    		 pushItem(team.grade.stars,3);
		    	 }else if(team.point >=601 && team.point <=900){
		    		 pushItem(team.grade.stars,4);
		    	 }else if(team.point >=901 && team.point <=1200){
		    		 pushItem(team.grade.stars,5);
		    	 }
		     },
			getPlayer : function(playerId,fn) {
				  $http.get("/app/player/"+playerId).success(function(resp, status, headers, config){
					  if(resp.success){
						  fn&&fn(resp.data);
					  }
				  }) 
			},
			updatePlayer:function(player,fn){
				$http.post("/app/player/saveOrUpdate",player).success(function(data, status, headers, config){
					  fn&&fn(data);
				})
			},
			getPlayers : function(data,fn) {
				var args = {"orderby":"isCaptain asc,u.createTime desc"};
				$http.post("/app/listPlayer",
						  $.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getTeams:function(data,fn){
				var args = {
						  "orderby":" u.point desc",
						  "condition":" and (u.dismissed is null or u.dismissed !=1)"};
				  $http.post("/app/listTeam",$.extend(args,data)).success(function(data, status, headers, config){
					  fn&&fn(data)
				  })
			},
			getField:function(fieldId,fn){
				 $http.get("/app/field/"+fieldId).success(function(resp, status, headers, config){
					  if(resp.success){
						  fn&&fn(resp.data);
					  }
				  }) 
			},
			getFields:function(data,fn){
				var args = {"orderby":" opTime desc"};
				  $http.post("/app/listField",$.extend(args,data)).success(function(data, status, headers, config){
							fn&&fn(data)
				  })
			},
			
			getGames : function(data,fn) {
				var args = {
						  "isEnabled":1,
						  "orderby":"beginTime desc"};
				$http.post("/app/listGame",$.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getComments : function(params,fn) {
				$http.post("/app/listComment",params).success(function(data, status, headers, config) {
					fn&&fn(data);
				})
			},
			
			getGame:function(gameId,fn){
				 $http.get("/app/game/"+gameId).success(function(resp, status, headers, config){
					  if(resp.success){
						  fn&&fn(resp.data);
					  }
				  }) 
			},
			getGameLogs : function(data,fn) {
				var args = { pageSize:4};
				$http.post("/app/listGamelog",$.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getTransfers:function(data,fn){
				var args = { "orderby":"toTime desc",pageSize:5};
				$http.post("/app/listTransfer",$.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getNews:function(data,fn){
				var args = {"status":2,
						  "orderby":"startTime desc"};
				$http.post("/app/listNews",$.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getUnreadCnt:function(playerId,fn){
				var lastReadTime=store.get(playerId+"_readDate")||0;
				$http.get("/app/getUnreadCnt",{params:{playerId:playerId,lastReadTime:lastReadTime}}).success(function(resp, status, headers, config) {
					fn&&fn(resp);
				})
			},
			findNewsById:function(id,fn){
				$http.get("/app/news/"+id).success(function(resp, status, headers, config) {
					if(resp.success){
						fn&&fn(resp.data);
					}
				})
			},
			findMessageById:function(id,fn){
				$http.get("/app/message/"+id).success(function(resp, status, headers, config) {
					if(resp.success){
						fn&&fn(resp.data);
					}
				})	
			},
			findHistory : function(playerId,fn) {
				$http.get("/app/transfer/history",{params:{"playerId":playerId}}).success(function(resp, status, headers, config) {
					 var history = [];
					   var sorted = resp.data.sortBy(function(n){return n.id;},false)
					   for(var i=0;i<sorted.length;i++){
						   var item = sorted[i];
						   var rec = {};
						   if(item.toTeam){
							   if(i!=sorted.length-1){
								   rec.teamTitle = item.toTeam.teamTitle;
								 //toTime为加入一个球队的起始时间
								   rec.beginTime = item.toTime;
								 //fromTime为退出一个球队的时间
								   rec.endTime = sorted[i+1].fromTime;
							   }else{
								   rec.teamTitle = item.toTeam.teamTitle;
								   rec.beginTime = item.toTime;
								   rec.endTime = '至今';
							   }
							   rec.teamTitle&& history.unshift(rec);
						   }
					   }
					fn&&fn(history);
				})
			},
			
			findPhotoComments : function(photoGroupId,fn) {
				$http.get("/app/photoComment",{params:{"photoGroupId":photoGroupId}}).success(function(resp, status, headers, config) {
					if(resp.success){
						fn&&fn(resp.data);
					}
				})
			},
			getPhotos : function(data,fn) {
				var args = {"isEnabled":1,
						  "orderby":"createTime desc"};
				$http.post("/app/listPhoto",$.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			getVideos : function(data,fn) {
				var args = {"isEnabled":1,
						  "orderby":"createTime desc",
						  "pageSize":10};
				$http.post("/app/listVideo", $.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
		 	addMessage : function(data,fn) {
				$http.post("/app/message/saveOrUpdate",data).success(function(data, status, headers, config){
						fn&&fn(data);
				 })
			},
			addComment : function(bean,user,fn) {
				  if(bean.comment && bean.comment.trim().length>1){
					  if(!user){
						  layer.alert("请先登陆!", {
								skin: 'layui-layer-lan'
								,closeBtn: 0
								,shift: 4 //动画类型
						    });
						   return;
					   }
					  bean.player = {id:user.id};
					 $http.post("/app/comment/saveOrUpdate", bean).success(function(data, status, headers, config){
						 if(data.success){
							  bean.comment = "";
							  fn&&fn(data);
						 }
					 })
				  }
			},
			getRecruits : function(data,fn) {
				var args = {"isEnabled":1,
						    "orderby":"opTime desc"};
				$http.post("/app/listRecruit", $.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data);
				})
			},
			
			getApplys : function(data,fn) {
				var args = {
						  "isEnabled":1,
						  "orderby":"applyTime desc"};
				$http.post("/app/listApply", $.extend(args,data)).success(function(data, status, headers, config) {
					fn&&fn(data);
				})
			},
			
			findTeamById:function(teamId,fn){
				$http.get("/app/team/"+teamId).success(function(resp, status, headers, config) {
					if(resp.success){
						fn&&fn(resp.data)
					}
				})
			},
			dismissTeam:function(captainId,teamId,fn){
				$http.get("/app/team/dismissTeam",{params:{"captainId":captainId,"teamId":teamId}}).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			restoreTeam:function(captainId,teamId,fn){
				$http.get("/app/team/restoreTeam",{params:{"captainId":captainId,"teamId":teamId}}).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			dismissPlayer:function(playerId,fn){
				$http.get("/app/dismissPlayer",{params:{"playerId":playerId}}).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			changePassword:function(mobile,password,fn){
				$http.get("/app/player/changePassword",{params:{"mobile":mobile,"password":password}}).success(function(data, status, headers, config) {
								fn&&fn(data)
					})
			},
			setCaptain:function(playerId,teamId,fn){
				$http.get("/app/player/setCaptain",{params:{"playerId":playerId,"teamId":teamId}}).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			//获取每日点赞,挑战次数配置
			getSysconfig:function(fn){
				$http.get("/app/sysconfig",{params:{code:["like_player_max","like_team_max","dare_max","creat_team_max"]}}).success(function(resp){
					if(resp.success ){
						var sysconfig = {};
						resp.data.each(function(n){
							sysconfig[n.code] = parseInt(n.value);
						})
						fn&&fn(sysconfig);
					}
				})
			},
			//获取当天已点赞的详细记录(给球员与球队点赞)
			getLikes:function(playerId,fn){
				$http.get("/app/findLikes",
						{params:{"playerId":playerId}}).success(function(data, status, headers, config) {
					fn&&fn(data)
				})
			},
			savePlayerLike : function(giverId,playerId,fn) {
				$http.get("/app/likePlayer",{params:{"giverId":giverId,"playerId":playerId}}).success(function(data, status, headers, config){
						fn&&fn(data);
				 })
			},
			
			saveTeamLike : function(giverId,teamId,fn) {
				$http.get("/app/likeTeam",{params:{"giverId":giverId,"teamId":teamId}}).success(function(data, status, headers, config){
						fn&&fn(data);
				 })
			},
			addPhotoLike : function(photoGroupId,playerId,fn) {
				$http.get("/app/likePhoto",{params:{"photoGroupId":photoGroupId,"playerId":playerId}}).success(function(resp, status, headers, config){
						if(resp.success){
							fn&&fn(resp.data);
						}
				 })
			},
			addVideoLike : function(videoId,playerId,fn) {
				$http.get("/app/likeVideo",{params:{"videoId":videoId,"playerId":playerId}}).success(function(resp, status, headers, config){
					if(resp.success){	
						fn&&fn(resp.data);
					}
				 })
			},
			/**
			 * 发送修改密码用短信
			 */
			sendSmsForPassword:function(mobile,fn){
				$http.get("/app/player/sendSms?mobile="+mobile)
				.success(function(data, status, headers, config) {
					  fn&&fn(data);
	    			})
			},
			verifyCode:function(mobile,verifyCode,fn){
				$http.get("/app/player/checkVerifyCode",{params:{mobile:mobile,verifyCode:verifyCode}})
				.success(function(data, status, headers, config) {
					 fn&&fn(data);
	    			})
			},
			getStaticDicts:function(){
				var deferred = $q.defer();
				$http.get("/admin/i18n/dictionary.json").success(function(data){
					deferred.resolve(data);
				})
				return deferred.promise;
			},
			// 本地导入
			// 本地导入
			uploadImage: function(options,fn) {
				if(typeof(options.params)!="object"){
					options.params = {};
				}
				if(!options.url){
					options.url = '../common/uploadImg';
				}
				var index;
				options.onSubmit = function(){
					 index = layer.load(1, {
						  shade: [0.2,'#000'] //0.1透明度的白色背景
					 });
				};
				options.onComplate = function(data) {
					layer.close(index);
					if(data.success){
						fn&&fn(data);
					}else{
						layer.alert(data.error,{btn: ['ok'],title:false});
					}
				};
				// 上传方法
				$.upload(options);
			},
			// 本地导入
			uploadImages: function(options,fn) {
				if(typeof(options.params)!="object"){
					options.params = {};
				}
				if(!options.url){
					options.url = '../common/uploadImgs';
				}
				var index;
				options.onSubmit = function(){
					 index = layer.load(1, {
						  shade: [0.2,'#000'] //0.1透明度的白色背景
					 });
				};
				options.onComplate = function(data) {
					layer.close(index);
					if(Array.isArray(data)){
						fn&&fn(data);
					}else if(Object.isObject(data)){
						if(data.success){
							fn&&fn(data);
						}else{
							layer.alert(data.error,{btn: ['ok'],title:false});
						}
					}else{
						layer.alert("发生错误:"+data,{btn: ['ok'],title:false});
					}
				};
				// 上传方法
				$.upload(options);
			}
		}});
	
})();