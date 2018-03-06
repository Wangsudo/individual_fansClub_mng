(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('photoController', function($scope,$rootScope,$uibModal, $http,appData) {
		$scope.search={};
		$scope.items = {pageSize:10};
			
      //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../photo/list",{
				  teamTitle:$scope.search.teamTitle,
				  name:$scope.search.name,
				  type:$scope.search.type,
				  status:$scope.search.status,
				  fromDate:$scope.search.fromDate,
				  toDate:$scope.search.toDate,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize,
				  orderby:"createTime desc"
			  },function(data){
				  $scope.items = data;
			  });
	   };
	   
	   $scope.del = function(id){
			  var url = "../photo/del/"+id;
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
					      templateUrl: 'photoAdd.html',
					      controller: 'photoAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
		$scope.toggleEnable =function(item,id){
			$http.get("/photo/toggleLock?id="+item.id).success(function(resp){
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
	   
	
	app.controller('photoAddController', function($scope,$http,$uibModalInstance,$rootScope,id,appData) {
		if(id){
			$http.get("../photo/"+id).success(function(resp){
				if(resp.success){
					$scope.photo = resp.data;
				}
			})
		}else{
			$scope.photo = {};
		}
		   /**
	         * 添加商品说明图片
	         */
        $scope.uploadImage= function(){
        	appData.uploadImage({},function(data){
        		$scope.photo.pics.push({url:data.picPath,name:data.name});
        		$scope.synData();
			})
        }
        
        $scope.changeStatus = function(pic){
        	pic.status = pic.status?pic.status^3:1;
		}
        
		 $scope.save=function(photo){
			  $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			  $http.post("../photo/saveOrUpdate",photo).success(function(data, status, headers, config) {
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


