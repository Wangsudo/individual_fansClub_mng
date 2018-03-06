(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('playerController', function($scope,$rootScope,$uibModal,$sce, $http,appData) {
		document.title="球员列表";
		$scope.search={};
		$scope.items = {pageSize:10};
			
		$scope.find = function(pageNo){
			$.extend($scope.search, {
				currentPage:pageNo||$scope.items.currentPage||1,
				pageSize:$scope.items.pageSize||10
			});
			appData.getPageResult("../player/list",$scope.search,function(data){
				  $scope.items = data;
		    });
	   };
		$scope.setCoordinate = function(){
			var players = $scope.items.list.map(function(n){return {id: n.id,x:1,y:null}});
			$http.post("/app/player/updateCoordinate",players).succcess(function(resp){
				alert(resp);
			})
		}
	   $scope.del = function(id){
			  var url = "../player/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(resp){
				  if(resp.success){
					  $scope.find();
				  }
			  });
		};

		$scope.resetPassword = function(player){
			confirm('确定重置球员'+player.name+'的密码为111111吗?',function(){
				$http.get("../player/resetPassword/"+player.id).success(function(resp){
					if(resp.success){
						msg("重置成功！");
					}else{
						alert("重置失败！");
					}
				})
			});
		};

		//编辑
		   $scope.showModal = function(playerId){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'playerAdd.html',
					      controller: 'playerAddController',
					      resolve: {playerId:playerId }
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
				   modal.opened.then(function(){
					   console.log("Modal opened");
				   });
				    modal.rendered.then(function(){
					   console.log("Modal rendered");
				   });
	      }
		   
		   $http.get("/player/eleWeight").success(function(data){
				$rootScope.weight = {};
				$.each(["player_total_game","player_like","player_active","player_win_rate","beginer","total","win","even","lost"],function(i,v){
					$rootScope.weight[v] = parseFloat(data[v]);
				})
			});
		   
		   $scope.getActive = function(item){
			   item.win = item.win||0;
			   item.lost = item.lost||0;
			   item.even = item.even||0;
			   $http.get("/player/getActive",{params:{playerId:item.id}}).success(function(resp){
				   if(resp.success){
					   $scope.active = resp.data;
				   }
			   });
		   }
		   
		   $scope.showFormula = function(){
			   var modal = $uibModal.open({
				      animation: true,
				      size:'lg',
				      templateUrl: 'playerFormula.html',
				      controller: 'playerFormulaController'
				    });
			     modal.result.then(function () {
				   $scope.find();
				  }, function () {
				      console.info('showFormula dismissed at: ' + new Date());
				  });
		   }
		   
		   $scope.showValueFormula = function(){
			   var modal = $uibModal.open({
				      animation: true,
				      size:'lg',
				      templateUrl: 'playerValueFormula.html',
				      controller: 'playerValueFormulaController'
				    });
			     modal.result.then(function () {
				   $scope.find();
				  }, function () {
				      console.info('showFormula dismissed at: ' + new Date());
				  });
		   }
		   
		 //显示荣誉
		    $scope.honorModal = function(player){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'honorModal.html',
				      controller: 'honorModalController',
				      size: '',
				      resolve: {item: {player:player,type:1}}
			     });
			      modal.result.then(function (args) {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		     }
		    
			
		$scope.find();
	});
	
	app.controller('playerFormulaController', function($scope,$http,$uibModalInstance) {
		
		$scope.save = function(form){
			 form.$setSubmitted(true);
			if(!$scope.form.$valid){
				return;
			}
		/*	var sum = 0;
			$.each(["total_game_weight","player_grade_weight","active_index_weight","win_rate_weight"],function(i,v){
				sum +=parseFloat($scope.weight[v]);
			})
			if(sum!=1){
				$scope.formularError = "各因式权重之和不等于1,请检查!";
				return;
			}*/
			$http({
				method:"POST",
				url:"/player/saveEleWeight",
				data:$scope.weight
			}).success(function(data, status, headers, config){
				if(data.success){
					$uibModalInstance.close();
				}else{
					$scope.formularError  = "保存参数失败!";
				}
			});
		};
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	});
	
	app.controller('playerAddController', function($scope,$http,$uibModalInstance,$rootScope,playerId,$uibModal,appData) {
		 if(playerId){
			 $http.get("../player/"+playerId).success(function(resp){
					if(resp.success){
						$scope.player = resp.data;
						if($scope.player.birth){
							$scope.player.birthDay  = new Date($scope.player.birth).format('{yyyy}-{MM}-{dd}','zh-CN');
						}
					}
			 })
		 }else{
			 $scope.player = {};
		 }
		    /**
	         * 添加商品说明图片
	         */
	        $scope.uploadImage = function(player){
	        	appData.uploadImage({},function(data){
	        		player.iconUrl = data.picPath;
	        		$scope.synData();
				})
	        }
	        
	        $scope.clearImg = function(player){
	        	player.iconUrl = null;
	        	$("#playerIcon")[0].dispatchEvent(new CustomEvent('wheelzoom.destroy'));
	        }
	        
	        $scope.wheel = function(imgUrl){
	        	if(imgUrl!=$rootScope.main.defaultImg){
	        		wheelzoom($("#playerIcon")[0]);
	        	}
	        }
	        
	        
	      //添加或更新管理员
		 $scope.save=function(player){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  player.pointMinus = player.pointMinus||0;
			  if(player.pulish){
				  player.pointMinus = player.pointMinus + player.pulish;
			  }
			  
			  if($scope.player.birthDay){
				  player.birth = new Date(player.birthDay).getTime();
			  }
			  appData.updateTime(player,$scope.admin);
			
			$http.post("../player/saveOrUpdate",player).success(function(data, status, headers, config) {
				if(data.success){
					$uibModalInstance.close();
				}
			})
		}; 
			
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	      //显示修改密码
	 	   $scope.passWord = function(adminUser){
	 			   var modal = $uibModal.open({
	 				      animation: true,
	 				      templateUrl: 'password.html',
	 				      controller: 'passWordController',
	 				      resolve: {adminUser: function(){	
	 				    	 adminUser = $.extend(true,{},adminUser);
	 					    	  return adminUser;
	 				    	  }
	 				    }});
	 			   	
	 			  modal.result.then(function () {
	 				  // $scope.find();
	 				  }, function () {
	 				      console.info('Modal dismissed at: ' + new Date());
	 				     
	 				  });
	 		   }
	});
	

	
})();


