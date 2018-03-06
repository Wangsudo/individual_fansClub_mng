(function() {
	"use strict";
	var app = angular.module("admin.login", ['ngCookies','ui.bootstrap']);

	// 控制器
	app.controller('loginController', function($scope,$http,$cookies, $location,$rootScope) {
		var parent = $scope.$parent; 
		$scope.admin ={account:$cookies.get("account"),password:$cookies.get("password")};
		$scope.login = function(){
	          if(!$scope.admin.account || !$scope.admin.password){
	            	$scope.admin.error='账号或密码不能为空！';
	                return ;
	          }
	            //先清空密码 防止下去自动登陆
			    $scope.admin.error = "";
			    
	            $scope.admin.loginMessage = "";
	            $scope.waiting = true;
	            $http({
	                url:"../adminLogin/login",
	                data:$scope.admin,
	                method:"POST"
	            }).success(function(data){
	            	  $scope.waiting = false;
	                if(data.success){
	                    $cookies.put("account",$scope.admin.account);
	                    $cookies.put("password",$scope.admin.password);
	                    location.assign("../admin/main.html");
	                }else{
	                    $scope.admin.error = data.error;
	                }
	            }).error(function(error){
	            })
	        }
		document.onkeydown  = function(event){
		  if(event.keyCode  ==  13)   { 
			  $scope.login();
		  } 
		}; 
		
		var emailPattern = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		$scope.sendMail = function(){
			if(!emailPattern.test($scope.email)){
				layer.alert("邮箱错误!");
				return;
			}
			$http.get("../checkEmail?email="+$scope.email).success(function(data){
				if(data.success && data.user){
					$http({url:"../forgetPassword/"+data.user.id,method:'GET',params:{origin:location.origin}}).success(function(ret){
						var message="重置密码件已发送至您的邮箱!"
						if(!ret.success){
							message = ret.message;
						}
						layer.alert(message);
					});
				}else{
					layer.alert("邮箱不存在!");
				}
			})
		}
	});
})();