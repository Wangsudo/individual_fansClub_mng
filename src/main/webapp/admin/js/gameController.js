(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('gameController', function($scope,$rootScope,$uibModal,$sce, $http,appData) {
		document.title="球员列表";
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../game/list",{
				teamTitle:$scope.search.teamTitle,
				teamType:$scope.search.teamType,//是否队长
				scores: $scope.search.scores,
			    status:$scope.search.status,
			    gameStatus:$scope.search.gameStatus,
			    fromDate:$scope.search.fromDate,
			    toDate:$scope.search.toDate,
			    orderby:$scope.search.orderby,
				currentPage:pageNo||$scope.items.currentPage||1,
				pageSize:$scope.items.pageSize
			  },function(data){
				  data.list.each(function(n){
					  n.trustedContent =  $sce.trustAsHtml(n.content);
					  n.scorehtml = $scope.showScoreTip(n);
				  })
				  $scope.items = data;
			  });
	   };
	   
	   $scope.sortRet = function(orderby){
		   $scope.search.orderby = orderby;
		   $scope.find(1);
	   }
	   
	   $scope.showScoreTip = function(row){
			if(!row.teamB){
				return "";
			}
			var ret = "<table><thead><tr><td colspan='2'>上报分数</td></tr></thead>";
			var v1 = row.scoreA1>=0?(row.scoreA1+"&nbsp;:&nbsp;"+row.scoreB1):"未上报";
			var v2 = row.scoreB1>=0?(row.scoreA2+"&nbsp;:&nbsp;"+row.scoreB2):"未上报";
				ret +="<tr><td class='text-left'>"+row.teamA.teamTitle+"</td>"+"<td>"+v1+"</td></tr>"
				ret +="<tr><td class='text-left'>"+row.teamB.teamTitle+"</td>"+"<td>"+v2+"</td></tr>"
			ret+="</table>";
		    var html = $sce.trustAsHtml(ret);
			return  html;
		}
	   
	   $scope.getType = function(game){
		   var now = new Date().getTime();
		   if(!game){
			   return;
		   }else if(!game.teamA){
			   return "信息不全";
		   }else if(game.teamBOperation == 2){
			   return "已拒战";
		   }else if(!game.teamBOperation && game.beginTime > now){
			   return "约战中";
		   }else if(!game.teamBOperation && game.beginTime <= now){
			   return "已过期";
		   }else if(game.teamBOperation ==1 && game.beginTime >now){
			   return "未开赛";
		   }else if(game.teamBOperation == 1 && game.beginTime <= now){
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
	   $scope.del = function(id){
			  var url = "../game/del/"+id;
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
					      templateUrl: 'gameAdd.html',
					      controller: 'gameAddController',
					      resolve: {id:id }
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

		$scope.toggleEnable =function(item,id){
			$http.get("/game/toggleLock?id="+item.id).success(function(resp){
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
	   
	
	app.controller('gameAddController', function($scope,$http,$uibModalInstance,$rootScope,id,$uibModal,appData) {
		function setCurPhase(){
			$scope.phases && $scope.phases.each(function(n){
				if(n.groups.some(function(m){return $scope.game.group && m.id == $scope.game.group.id})){
					$scope.curPhase = n;
				}
			 })
		} 
		if(id){
			 $http.get("../game/"+id).success(function(resp){
					if(resp.success){
						$scope.game = resp.data;
						$scope.game.cup && ( $scope.phases = $scope.game.cup.phases);
						setCurPhase();
					}
			 })
		 }else{
			 $scope.game = {};
		 }
		 
		$scope.beforeNow = function(game){
			return game.teamBOperation == 1 && game.beginTime <new Date().getTime();
		}
		 $scope.getPhases = function($item, $model, $label, $event){
			 $http.get("/cup/getPhases",{params:{cupId:$item.id}}).success(function(resp){
				 if(resp){
					 $scope.phases = resp;
					 setCurPhase();
				 }
			 })
		 }
	      //添加或更新管理员
		 $scope.save=function(game){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  if(game.teamA && game.teamB && game.teamA.id == game.teamB.id){
				  layer.alert("参赛球队不能为同一只!")
			  }
			  appData.setTimeStamp(game,$scope.admin);
			  $http.post("../game/saveOrUpdate",game).success(function(data, status, headers, config) {
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


