(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('copyModalController', function($scope,$http,$uibModalInstance,$rootScope,cup) {
		$scope.cup= cup;
		$scope.rightList =cup.teams;
		$http.get("/team/getAllTeam").success(function(resp){
			if(resp.length){
				$scope.leftList = [];
				resp.each(function(n){
					if(!cup.teams.some(function(v){return v.teamId == n.id})){
						$scope.leftList.push({teamId:n.id,teamTitle:n.teamTitle,cupId:cup.id})
					}
				})
			}
		})
		
		//选中的向右移
		$scope.moveRight = function(item){
			$scope.rightList.push(item);
			$scope.leftList.remove(item);
		}
		//全部的向右移
		$scope.moveAllRight = function(){
			$scope.rightList.add($scope.leftList);
			$scope.leftList = [];
		}
	
		//选中的向左移
		$scope.moveLeft = function(item){
			$scope.leftList.push(item);
			$scope.rightList = $scope.rightList.remove(item);
		}
		//全部的向左移
		$scope.moveAllLeft = function(){
			$scope.leftList.add($scope.rightList);
			$scope.rightList = [];		
		}
		
//		function deActive(list){
//			list.each(function(n){
//				n.active = false;
//			})
//		}
		
		$scope.ok = function () {
			$http.post("/cup/saveOrUpdate",cup).success(function(resp){
				if(resp.success){
					$uibModalInstance.close();
				}
			})
	    };

        $scope.cancel = function (){
            $uibModalInstance.dismiss('cancel');
        };
		
	});

	app.controller('honorModalController', function($scope,$http,$uibModalInstance,$rootScope,item) {
		var playerId;
		var teamId;
		$scope.leftList = [];
		if(item.type == 1){
			playerId = item.player.id;
			$scope.rightList = $.extend([],item.player.honors);
		}else{
			teamId = item.team.id;
			$scope.rightList = $.extend([],item.team.honors);
		}
		
		$http.get("/honor/getOthers",{params:{teamId:teamId,playerId:playerId,type:item.type}}).success(function(resp){
			if(resp && resp.length){
				resp.each(function(n){
					$scope.leftList.push({teamId:teamId,playerId:playerId,honor:n})
				})
			}
		})
		
		//选中的向右移
		$scope.moveRight = function(item){
			$scope.rightList.push(item);
			$scope.leftList.remove(item);
		}
		//全部的向右移
		$scope.moveAllRight = function(){
			$scope.rightList.add($scope.leftList);
			$scope.leftList = [];
		}
	
		//选中的向左移
		$scope.moveLeft = function(item){
			$scope.leftList.push(item);
			$scope.rightList = $scope.rightList.remove(item);
		}
		//全部的向左移
		$scope.moveAllLeft = function(){
			$scope.leftList.add($scope.rightList);
			$scope.rightList = [];		
		}
		
//		function deActive(list){
//			list.each(function(n){
//				n.active = false;
//			})
//		}
		
		$scope.ok = function () {
			if(item.type == 1){
				item.player.honors = $scope.rightList;
				$http.post("/player/saveOrUpdate",item.player).success(function(resp){
					if(resp.success){
						$uibModalInstance.close();
					}
				})
			}else{
				item.team.honors = $scope.rightList;
				$http.post("/team/saveOrUpdate",item.team).success(function(resp){
					if(resp.success){
						$uibModalInstance.close();
					}
				})
			}
	    };

        $scope.cancel = function (){
            $uibModalInstance.dismiss('cancel');
        };
		
	});
	
	app.controller('permissionModalController', function($scope,$http,$uibModalInstance,obj) {
		$scope.obj = obj;
		var access = $scope.access = obj.access;
		
		/**
		 * 当设置权限为 锁定时, 若截止时间小于当前时间, 则清空截止时间
		 */
		$scope.checkToDate = function(item){
			if(item.v ==2 && item.toDate){
				var now = new Date().getTime();
				var deadLine = new Date(item.toDate).getTime();
				if(now > deadLine){
					item.toDate = "";
				}	
			}
		};
		
		$scope.save = function(form){
			var lockObj = {};
			access.each(function(n){
				lockObj[n.code] = {v:n.v,toDate:n.toDate,reason:n.reason}
			})
		    var data = {id:obj.entity.id,locks:JSON.stringify(lockObj)};
		    $http.post(obj.url,data).success(function(resp){
		    	if(resp.success){
		    		$.extend(obj.entity,data);
		    		$uibModalInstance.close();
		    	}
		    })
		}
		
		 $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	        
	});
	
	app.controller('colorModalController', function($scope,$rootScope,$http,$uibModalInstance,appData,color) {
		$scope.color = color;
		$scope.textureUrl = color.textureUrl
		$scope.$on('colorPicked', function(event, color) {
            $scope.color.rgb = color;
            $scope.color.textureUrl = false;
        });
		
		//选择材质,
		$scope.chooseTexture = function(){
			$scope.color.textureUrl = $scope.textureUrl;
			$scope.color.rgb = "";
		}
		
		
		$scope.clearColor = function(){
			$scope.color.rgb = "";; 
		}
		
		
        $scope.uploadImage = function(){
			appData.uploadImage({},function(data){
			    $rootScope.checkDimens(data,'texture',function(){
			    	$scope.color.textureUrl = $scope.textureUrl =data.picPath;
			    	$scope.color.rgb = "";
					$scope.synData();
			    })
			})
		};
		
		$scope.confirm = function(){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			if(!$scope.color.rgb &&!$scope.textureUrl){
				return;
			}
			if(!$scope.color.rgb){
				$scope.color.textureUrl = $scope.textureUrl;
			}
		     $uibModalInstance.close(color);
		}

        $scope.cancel = function (){
            $uibModalInstance.dismiss('cancel');
        };
        
        
//        $http.get("../product/texture/all").success(function(data){
//        	if(data.success){
//        		$scope.textures = data.list;
//        		if(color.texture && color.texture.id){
//        			var activeTexture = $scope.textures.find(function(n){return n.id == color.texture.id});
//        			activeTexture && (activeTexture.active = true);
//        		}
//        	}
//        })
		
	});
	
})();