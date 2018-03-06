(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('videoController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../video/list",{
				  title:$scope.search.title,
				  teamTitle:$scope.search.teamTitle,
				  name:$scope.search.name,
				  isEnabled:$scope.search.isEnabled,
				  status:$scope.search.status,
				  fromDate:$scope.search.fromDate,
				  toDate:$scope.search.toDate,
				  orderby:"modifyTime desc",
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../video/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(data){
				  if(data){
					  $scope.find();
				  }
			  });
		};
	
			//显示添加管理员
		   $scope.showModal = function(id){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'videoAdd.html',
					      controller: 'videoAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		$scope.toggleEnable =function(item,id){
			$http.get("/video/toggleLock?id="+item.id).success(function(resp){
				if(resp.success){
					tips("操作成功!",id)
				}else{
					tips("操作失败!",id)
					item.isEnabled = item.isEnabled^3;
				}
			})
		}
		 $scope.find();
	});
	   
	app.controller('videoAddController', function($scope,$http,appData,$uibModalInstance,$rootScope,id,$uibModal) {
		if(id){
			$http.get("../video/"+id).success(function(resp){
				if(resp.success){
					$scope.video = resp.data;
				}
			})
		}else{
			$scope.video = {};
		}
		 $scope.parseVideo = function(videoLink){
				if(!videoLink){return "";}
				var TEMPLETE = '<iframe height=450 width=100% src="http://player.youku.com/embed/{0}" frameborder=0 allowfullscreen></iframe>'
				var mat =	videoLink.match(/http:\/\/player\.youku\.com\/\S*(sid|embed)\/([^/]+)('|"|\/v.swf)/);
				var url = mat && mat.length == 4? mat[2]:"";
				return TEMPLETE.replace('{0}',url);
			}
			
			function parseVideoDiv(videoDiv){
			   var mat =	videoDiv.match(/http:\/\/player\.youku\.com\/\S*(sid|embed)\/([^/]+)('|"|\/v.swf)/);
			  return (mat && mat.length == 4? mat[2]:"");
			}
			
		  $scope.save = function(video){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  appData.updateTime(video,$scope.admin);
              $http.post("../video/saveOrUpdate",video).success(function(data, status, headers, config) {
                  if(data.success){
                      $uibModalInstance.close();
                  }
              })

			 /* var vid = parseVideoDiv(video.videoDiv);*/

			/*	$http.jsonp('http://play.youku.com/play/get.json?vid='+vid+'&ct=10&ran=9083&callback=JSON_CALLBACK'
				).success(function(data, status, headers, config) {
					if(data&&data.data&&data.data.video&&data.data.video.logo){
						video.screenshot = data.data.video.logo;
						$http.post("../video/saveOrUpdate",video).success(function(data, status, headers, config) {
							if(data.success){
								$uibModalInstance.close();
							}
					  })
					}
			    }).error(function(data, status, headers, config) {
		        });*/

             /* $http.jsonp('https://api.youku.com/videos/show.json?video_id='+vid+'&client_id=937f9355701f8383&callback=JSON_CALLBACK'
              ).success(function(data, status, headers, config) {
                  if(data&&data.thumbnail){
                      video.screenshot = data.thumbnail;
                      $http.post("../video/saveOrUpdate",video).success(function(data, status, headers, config) {
                          if(data.success){
                              $uibModalInstance.close();
                          }
                      })
                  }
              }).error(function(data, status, headers, config) {
              });*/

		  };
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	      };
	});
	
	
})();


