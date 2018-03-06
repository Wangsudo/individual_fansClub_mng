(function() {
	"use strict";
	var app = angular.module("football");
	// 控制器
	app.controller('loginController', function($scope, $http,$rootScope,$uibModalInstance,appData,$location,hideRegister) {
		//	$("body").css("overflow","hidden");
		    $scope.hideRegister = hideRegister;
			$scope.iuser = {};
			document.onkeydown  = function(){
			  if(event.keyCode  ==  13)   { 
					$scope.login($scope.iuser.account,$scope.iuser.password)
					// event.keyCode=9;
			  } 
			}; 
			
			$scope.login = function(username,password){
			    if(!username||!password){
			    	$scope.message = "请输入用户名与密码！"
			    	return;
			    }
			    $rootScope.userDefer = appData.login(username,password);
			    $rootScope.userDefer.then(function(data){
			    	if(data.success&&data.user.auditStatus!=2){
						$uibModalInstance.close(data);
					}else if(data.success&&data.user.auditStatus==2){
						$scope.message = "此帐号审核不通过!";
					}else if(!data.success){
						$scope.message = data.error;
					}
			   });
			} 
			
			$scope.cancel = function () {
			    $uibModalInstance.dismiss('cancel');
		  };
	});
	
	app.controller('jcropCtrl',function($http,$scope,data,$uibModalInstance){
		$scope.data = data;
		$scope.coord= {path:data.picPath};
		 var jcrop_api;
		 function updateCoords(c){
            $.extend($scope.coord,c);
        }
	
		 $scope.initJcrop = function(){
			 $(event.target).Jcrop({aspectRatio: 1,
	         	minSize: [ 160, 160 ],
	         	boxWidth: 570, boxHeight: 600,
		        onSelect: updateCoords},function(){
		          jcrop_api = this;
		          jcrop_api.animateTo([0,0,400,400],function(){$.extend($scope.coord,jcrop_api.tellSelect()) });
		        });
		 }
		 
		 $scope.ok = function () {
			 $http.post("/common/cropImage",$scope.coord).success(function(resp){
        		 $uibModalInstance.close(resp);
        	 });
		  };

		  $scope.cancel = function () {
		    $uibModalInstance.dismiss('cancel');
		  };
	});
	
})();