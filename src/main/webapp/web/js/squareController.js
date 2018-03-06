(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('squareController', function($rootScope,$scope,$location, $http,$uibModal, appData,$routeParams) {
		var parent = $scope.$parent;
		console.log($routeParams.no);
		
		
		var itemsList = [{name:"全部",teamType:0}, {name:"3人制",teamType:3}, {name:"5人制",teamType:5}, {name:"7人制",teamType:7},{name:"11人制",teamType:11}]
		 
        var itemstab0 = {name:"视频",list:$.extend(true,[],itemsList)}
        var itemstab1 = {name:"图片",list:$.extend(true,[],itemsList)}
        var itemstab2 = {name:"找队",list:$.extend(true,[],itemsList)}
        var itemstab3 = {name:"招人",list:$.extend(true,[],itemsList)}
        var itemstab4 = {name:"约战",list:$.extend(true,[],itemsList)}
        var itemstab5 = {name:"动态",list: [{name:"全部",teamType:0}]};
        $scope.itemstabList = [itemstab0,itemstab1,itemstab2,itemstab3,itemstab4,itemstab5]
	
		//获取公告
		$scope.getNews = function(pageNo){
			  appData.getNews( {
				  "snippet":$scope.itemstabList[5].snippet||"",
				  "currentPage":pageNo||$scope.itemstabList[5].list[0].curPage||1,
			  },function(data, status, headers, config){
				  $scope.itemstabList[5].list[0].list = data.list;
				  $scope.itemstabList[5].list[0].total = data.totalRecord;
				  $scope.itemstabList[5].list[0].curPage = data.currentPage;
			  })
		}
		
		//获取招人信息:
		$scope.getRecruits = function(teamType,pageNo){
			var typeList = (teamType === undefined)?$scope.itemstabList[3].list:$scope.itemstabList[3].list.findAll(function(n){return n.teamType==teamType});
			$.each(typeList,function(i,v){
				appData.getRecruits({
					"teamType":v.teamType,
					"snippet":$scope.itemstabList[3].snippet||"",
					"isPublic":1,//广场消息
					"currentPage":pageNo||v.curPage||1,
				},function(data){
					  v.list = data.list;
					  v.total = data.totalRecord;
					  v.curPage = data.currentPage;
				})
			})
		}
		  
		//获取找队信息:
		$scope.getApply = function(teamType,pageNo){
			var typeList = (teamType === undefined)?$scope.itemstabList[2].list:$scope.itemstabList[2].list.findAll(function(n){return n.teamType==teamType});
			$.each(typeList,function(i,v){
				appData.getApplys({
					"currentPage":pageNo||v.curPage||1,
					 "teamType":v.teamType,
					 "snippet":$scope.itemstabList[2].snippet||"",
					 "isPublic":1,//广场消息
					 "isOpen":1//是否保持有效
				},function(data){
					 v.list = data.list;
					  v.total = data.totalRecord;
					  v.curPage = data.currentPage;
				})
			})
		}  
		//获取照片信息:
		$scope.getPics = function(teamType,pageNo){
			var typeList = (teamType === undefined)?$scope.itemstabList[1].list:$scope.itemstabList[1].list.findAll(function(n){return n.teamType==teamType});
			$.each(typeList,function(i,v){
				appData.getPhotos({
					  "isEnabled":1,
					  "teamType":v.teamType,
					  "status":1,//审核通过
					  "viewType":1,//发布到广场 
					  "orderby":"createTime desc",
					  "currentPage":pageNo||v.curPage||1,
					  "snippet":$scope.itemstabList[1].snippet||""
				  },function(data){
						  $.each(data.list,function(j,va){
							  va.cover = va.pics.find(function(c){return c.status == 1});
						  })
						  v.list = data.list;
						  v.total = data.totalRecord;
						  v.curPage = data.currentPage;
				  });
			})
		}  
		//获取视频信息:
		$scope.getVideos = function(teamType,pageNo){
			var typeList = (teamType === undefined)?$scope.itemstabList[0].list:$scope.itemstabList[0].list.findAll(function(n){return n.teamType==teamType});
			$.each(typeList,function(i,v){
				appData.getVideos( {
					  "teamType":v.teamType,
					  "status":1,
					  "viewType":1,//发布到广场 
					  "orderby":"createTime desc",
					  "currentPage":pageNo||v.curPage||1,
					  "snippet":$scope.itemstabList[0].snippet||""
				  },function(data){
					  v.list = data.list;
					  v.total = data.totalRecord;
					  v.curPage = data.currentPage;
				})
			})
		}  
		  
		//获取约战
		$scope.getChanllege = function(teamType,pageNo){
			var typeList = (teamType === undefined)?$scope.itemstabList[4].list:$scope.itemstabList[4].list.findAll(function(n){return n.teamType==teamType});
			$.each(typeList,function(i,v){
				appData.getGames({
				      "isPublic":1,//shi == 1时 , public
					  "teamType":v.teamType,
					  "gameStatus":1,//约赛中
					  "snippet":$scope.itemstabList[4].snippet||"",
					  "currentPage":pageNo||v.curPage||1
				},function(data){ 
					$.each(data.list,function(j,va){
						  va.date = new Date(va.beginTime).format("{yyyy}-{MM}-{dd}",'zh-CN');
						  va.time = new Date(va.beginTime).format("{HH}:{mm}",'zh-CN');
					  })
					  v.list = data.list;
					  v.total = data.totalRecord;
					  v.curPage = data.currentPage;
				})
			})
		}
		//接收约战
		$scope.acceptDare = function(game){
			if(!$scope.getPermission($scope.user,'accept')){
    	    	return;
    	    }
            $http.get("/app/respondDare",{params:{gameId:game.id,operation:1,teamBId:$scope.user.team.id}}).success(function(data){
                if(data.success){
                    $location.url("/notice?type=约战");
                }else{
                    layer.alert(data.error, {
                        skin: 'layui-layer-lan'
                        ,closeBtn: 0
                        ,shift: 4 //动画类型
                    });
				}
            })
		};
		
		$scope.pageChanged = function(tabname,teamType){
			switch(tabname){
			case "视频":$scope.getVideos(teamType);break;
			case "图片":$scope.getPics(teamType);break;
			case "找队":$scope.getApply(teamType);break;
			case "招人":$scope.getRecruits(teamType);break;
			case "约战":$scope.getChanllege(teamType);break;
			case "动态":$scope.getNews();break;
			}
		}
		$scope.find = function(tabname,event){
			if(event&&event.keyCode!=13){
				return;
			}
			switch(tabname){
			case "视频":$scope.getVideos(undefined,1);break;
			case "图片":$scope.getPics(undefined,1);break;
			case "找队":$scope.getApply(undefined,1);break;
			case "招人":$scope.getRecruits(undefined,1);break;
			case "约战":$scope.getChanllege(undefined,1);break;
			case "动态":$scope.getNews(1);break;
			}
		}
	
		switch($routeParams.tab){
		 case "0": $scope.itemstabList[0].active = true;$scope.getVideos();break;
		 case "1": $scope.itemstabList[1].active = true;$scope.getPics();break;
		 case "2": $scope.itemstabList[2].active = true;$scope.getApply();break;
		 case "3": $scope.itemstabList[3].active = true;$scope.getRecruits();break;
		 case "4": $scope.itemstabList[4].active = true;$scope.getChanllege();break;
		 case "5": $scope.itemstabList[5].active = true;$scope.getNews();break;
		}
		 
	});
	//视频详情页面控制器
	app.controller('videoController', function($scope, $http,$routeParams,appData) {
		var parent = $scope.$parent;
		console.log("detail:"+$routeParams.id);
		
		$routeParams.id && $http.get("/app/video/"+$routeParams.id).success(function(resp){
			if(resp.success){
				var data = resp.data;
				if(data.isEnabled!=2 && data.auditStatus!=2){
					$scope.curVideo = data;
					$scope.likeList = data.likes?data.likes.split(','):[];
				}
			}
		})
		$scope.items = {};
		
		var findVideoComments = function(pageNo){
			appData.getComments({
				  "type":2,//视频
				  "id":$routeParams.id,//
				  "currentPage":pageNo||$scope.items.curPage||1
			},function(data){ 
				$scope.items.list = data.list;
				$scope.items.total = data.totalRecord;
				$scope.items.curPage = data.currentPage;
			})
		}
		
		
		$scope.existLike = function(user){
			if(!$scope.likeList || !user) return false ;
			if($scope.likeList.some(function(n){return n == user.id})){
				return true;
			}
			return false;
		}
		$scope.like = function(curVideo){
			if(!parent.user) return ;
			if($scope.existLike(parent.user)) return ;
			appData.addVideoLike(curVideo.id,parent.user.id,function(data){
				$scope.likeList = data.likes?data.likes.split(','):[];
			});
		}
		findVideoComments(1) ;
		$scope.pageChanged = findVideoComments;
		$scope.bean = {type:2,itemId:$routeParams.id};
		$scope.addComment = function(bean){
			if(!$scope.getPermission($scope.user,'comment')){
    	    	return;
    	    }
			  appData.addComment(bean,$scope.user,function(){
				  findVideoComments(1) ;
			  })
		   }
		
		$scope.parseVideo = function(videoLink){
			if(!videoLink){return "";}
			var TEMPLETE = '<iframe height=450 width=100% src="http://player.youku.com/embed/{0}" frameborder=0 allowfullscreen></iframe>'
			var mat =	videoLink.match(/http:\/\/player\.youku\.com\/\S*(sid|embed)\/([^/]+)("|\/v.swf)/);
			var url = mat && mat.length == 4? mat[2]:"";
			return TEMPLETE.replace('{0}',url);
		}
		
	});
	
	//图片详情页面控制器
	app.controller('imgController', function($scope, $http,$routeParams,appData) {
		var parent = $scope.$parent;
		console.log("this is tab"+$routeParams.tab);
		console.log("detail:"+$routeParams.id);
		$routeParams.id && $http.get("/app/photo/"+$routeParams.id).success(function(resp){
			if(resp.success){
				resp.data.pics.remove(function(v){
					return v.status == 2;
				})
				$scope.curPhoto = resp.data;
				$scope.likeList = resp.data.likes?resp.data.likes.split(','):[];
			}
		})
		
		$scope.existLike = function(user){
			if(!$scope.likeList || !user) return false ;
			if($scope.likeList.some(function(n){return n == user.id})){
				return true;
			}
			return false;
		}
		$scope.like = function(curPhoto){
			if(!parent.user) return ;
			if($scope.existLike(parent.user)) return ;
			appData.addPhotoLike(curPhoto.id,parent.user.id,function(data){
				$scope.likeList = data.likes?data.likes.split(','):[];
			});
		}
		
		$scope.items = {};
		var findPhotoComments = function(pageNo){
			appData.getComments({
				  "type":1,//图片
				  "id":$routeParams.id,//
				  "currentPage":pageNo||$scope.items.curPage||1
			},function(data){ 
				$scope.items.list = data.list;
				$scope.items.total = data.totalRecord;
				$scope.items.curPage = data.currentPage;
			})
		}
		findPhotoComments(1) ;
		$scope.pageChanged = findPhotoComments;
		$scope.bean = {type:1,itemId:$routeParams.id};
		 $scope.addComment = function(bean){
			 if(!$scope.getPermission($scope.user,'comment')){
	    	    	return;
	    	    }
			  appData.addComment(bean,$scope.user,function(){
				  findPhotoComments(1) ;
			  })
		 }
	});
	
	//公告详情页面控制器
	app.controller('newsDetailController', function($scope, $http,$routeParams,appData) {
		console.log("detail:"+$routeParams.id);
		if($routeParams.id){
			appData.findNewsById($routeParams.id,function(data){
				$scope.curNews = data;
			}) ;
		}
		
		$scope.items = {};
		var getComments = function(pageNo){
			appData.getComments({
				  "type":3,//公告
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
		$scope.bean = {type:3,itemId:$routeParams.id};
		$scope.addComment = function(bean){
			if(!$scope.getPermission($scope.user,'comment')){
    	    	return;
    	    }
			  appData.addComment(bean,$scope.user,function(){
				  getComments(1) ;
			  })
		}
		
	});
	
	//站内信
	app.controller('bulletinDetailController', function($scope, $http,$routeParams,appData) {
		console.log("detail:"+$routeParams.id);
		if($routeParams.id){
			appData.findMessageById($routeParams.id,function(data){
				$scope.curMessage = data;
			}) ;
		}
	});
	
})();