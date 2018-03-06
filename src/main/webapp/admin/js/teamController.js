(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('teamController', function($scope,$rootScope,$uibModal, $http,appData) {
		document.title="球队列表";
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			var page = {currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize}
			appData.getPageResult("../team/list",$.extend(page,$scope.search),function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../team/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(resp){
				  if(resp.success){
					  $scope.find();
				  }
			  });
		};
	
		 //编辑
		   $scope.showModal = function(id){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'teamAdd.html',
					      controller: 'teamAddController',
					      resolve: {id:id }
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		   
		   //显示荣誉
		    $scope.honorModal = function(team){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'honorModal.html',
				      controller: 'honorModalController',
				      size: '',
				      resolve: {item: {team:team,type:2}}
			     });
			      modal.result.then(function (args) {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		     }
		    
		    $http.get("/team/eleWeight").success(function(data){
				$rootScope.weight = {};
				$.each(["team_total_game","team_like","team_active","team_win_rate"],function(i,v){
					$rootScope.weight[v] = parseFloat(data[v]);
				})
			});
		    
		    //荣誉公式
		    $scope.showFormula = function(){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'teamFormula.html',
					      controller: 'teamFormulaController'
					    });
				     modal.result.then(function () {
					   $scope.find();
					  });
			   }
			   $scope.find();
//			   $scope.showModal();
			// 删除
			  
	});
	   
	app.controller('teamFormulaController', function($scope,$http,$uibModalInstance) {
		
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
				url:"/team/saveEleWeight",
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
	
	
	app.controller('teamAddController', function($scope,$http,$uibModalInstance,$rootScope,id,$uibModal,appData) {
		 if(id){
			 $http.get("../team/"+id).success(function(resp){
					if(resp.success){
						$scope.team = resp.data;
						$scope.team.dismissed = $scope.team.dismissed||2;
					}
			 })
		 }else{
			 $scope.team = {registDate:new Date(),acRate:1};
		 }
		 /**
	         * 添加商品说明图片
	         */
	        $scope.uploadIcon = function(team){
	        	appData.uploadImage({},function(data){
	        		team.iconUrl = data.picPath;
	        		$scope.synData();
				})
	        }
	        
	        $scope.uploadCover = function(team){
	        	appData.uploadImage({},function(data){
	        		team.coverUrl = data.picPath;
	        		$scope.synData();
				})
	        }
	        
		   	 //清除队徽
			 $scope.clearIcon = function(team){
				    team.iconUrl = null;
		        	$("#teamIcon")[0].dispatchEvent(new CustomEvent('wheelzoom.destroy'));
		     }
			 //清除合影
			 $scope.clearCover = function(team){
				    team.coverUrl = null;
		        	$("#teamCover")[0].dispatchEvent(new CustomEvent('wheelzoom.destroy'));
		     }
			 
	        $scope.wheelIcon = function(imgUrl){
	        	if(imgUrl!=$rootScope.main.defaultImg){
	        		wheelzoom($("#teamIcon")[0]);
	        	}
	        }
	        
	        $scope.wheelCover = function(imgUrl){
	        	if(imgUrl!=$rootScope.main.defaultImg){
	        		wheelzoom($("#teamCover")[0]);
	        	}
	        }
	
	        //添加或更新管理员
			 $scope.save=function(team){
				  $scope.form.$setSubmitted(true);
				  if(!$scope.form.$valid){
					  return;
				  }
				  
				  team.pointMinus = team.pointMinus||0;
				  if(team.pulish){
					  team.pointMinus = team.pointMinus + team.pulish;
				  }
				  
				  if($scope.team.registDate){
					  team.registTime = new Date(team.registDate).getTime();
				  }
				  
				  appData.setTimeStamp(team,$scope.admin);
				
				$http.post("../team/saveOrUpdate",team).success(function(data, status, headers, config) {
					if(data.success){
						$uibModalInstance.close();
					}
				})
			}; 
				
			  $scope.cancel = function () {
		            $uibModalInstance.dismiss('cancel');
		        };
		 
	});
})();


