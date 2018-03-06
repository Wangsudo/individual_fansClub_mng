(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('newsController', function($scope,$rootScope,$uibModal, $http,$sce,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../news/list",{
				  title:$scope.search.title,
				  status:$scope.search.status,
				  fromDate:$scope.search.fromDate,
				  orderby:" u.sort desc, u.modifyTime desc",
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../news/del/"+id;
			  $scope.confirm(url,'DEL_MSG',function(data){
				  if(data){
					  $scope.find();
				  }
			  });
		};
		//将新闻置顶
		$scope.setTop = function(id){
			confirm('确定置顶吗?',function(){
				$http.get("../news/setTop/"+id).success(function(resp){
					if(resp.success){
						$scope.find();
					}
				})
			});
		};
			//显示添加管理员
		   $scope.showModal = function(id){
				   var modal = $uibModal.open({
					      animation: true,
					      size:'lg',
					      templateUrl: 'newsAdd.html',
					      controller: 'newsAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		 $scope.find();
	});
	   
	
	app.controller('newsAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../news/"+id).success(function(resp){
				if(resp.success){
					$scope.news = resp.data;
				}
			})
		}else{
			$scope.news = {};
		}
        
	      //添加或更新管理员
		 $scope.save=function(news){
			  news.status = 1;
			  appData.setTimeStamp(news,$scope.admin);
			  $http.post("../news/saveOrUpdate",news).success(function(data, status, headers, config) {
					if(data.success){
						$uibModalInstance.close();
					}
			  })
		}; 
		
		//显示预览对话框
		   $scope.release = function(news){
		       //点击后 校验不通过的会变红
			   $scope.form.$setSubmitted(true);
			   if(!$scope.form.$valid){
				  return;
			   }
			   if(news.stopTime && news.stopTime < new Date().getTime()){
				   $scope.alert("STOPTIME_WRONG");
				   return;
			   }
			   news.status = 2;
			   appData.setTimeStamp(news,$scope.admin);
			   $http.post("../news/saveOrUpdate",news).success(function(data, status, headers, config) {
					if(data.success){
						$uibModalInstance.close();
					}
			  })
		   }
			
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	      };
	});
	
})();


