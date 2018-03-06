(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('cupController', function($scope,$rootScope,$uibModal, $http,appData) {
		document.title="赛事列表";
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			var page = { currentPage:pageNo||$scope.items.currentPage||1,
					  pageSize:$scope.items.pageSize}
			$.extend(page,$scope.search);
			if(page.teamId){
				page.teamId = page.teamId.id;
			}
			appData.getPageResult("../cup/list",page,function(data){
				  $scope.items = data;
			  });
	   };
	   $scope.del = function(id){
			  var url = "../cup/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(resp){
				  if(resp.success){
					  $scope.find();
				  }
			  });
		};
		   
		//编辑
		   $scope.showModal = function(cupId){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'cupAdd.html',
					      controller: 'cupAddController',
					      resolve: {cupId:cupId }
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
		   
		 //显示复制至其它国家的modal
		    $scope.copyModal = function(cup){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'copyModal.html',
				      controller: 'copyModalController',
				      size: '',
				      resolve: {cup: cup}
			     });
			      modal.result.then(function (args) {
						//复制商品至其它国家
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		     }
		   
		$scope.find();
	});
	   
	
	app.controller('cupAddController', function($scope,$http,$uibModalInstance,$rootScope,cupId,$uibModal,appData) {
		 if(cupId){
			 $http.get("../cup/"+cupId).success(function(resp){
					if(resp.success){
						$scope.cup = resp.data;
					}
			 })
		 }else{
			 $scope.cup = {isPublic:2,phases:[]};
		 }
		    /**
	         * 添加商品说明图片
	         */
	        $scope.uploadImage = function(cup){
	        	appData.uploadImage({},function(data){
	        		cup.iconUrl = data.picPath;
	        		$scope.synData();
				})
	        }
	        
	        $scope.clearImg = function(cup){
	        	cup.iconUrl = null;
	        }
	        
	        //添加比赛阶段
	        $scope.addPhase = function(phases,temp){
	        	if(/\S+/.test(temp.name)){
	        		phases.push({name:temp.name});
	        		temp.isOpen = false;
	        		temp.name = "";
	        		temp.error = false;
	        	}else{
	        		temp.error = true;
	        	}
	        }
	        
	        //编辑比赛阶段
			  $scope.editPhase = function(phase){
				  if(!phase.nameCopy || $scope.cup.phases.some(function(n){return n.name == phase.nameCopy.trim() && n !=phase})){
					  phase.error = true;
					  return;
				  }
				  phase.name = phase.nameCopy;
				  phase.isOpen = false;
			  }
			  //添加小组赛
			  $scope.addGroup = function(phase){
				  phase.groups = phase.groups||[];
				  phase.groups.push({});
			  }  
	        
	      //添加或更新管理员
		 $scope.save=function(cup){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  appData.updateTime(cup,$scope.admin);
			
			$http.post("../cup/saveOrUpdate",cup).success(function(data, status, headers, config) {
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


