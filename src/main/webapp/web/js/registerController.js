(function() {
	"use strict";
	/*var app = angular.module("football");*/
	var app = angular.module("football", ["ngRoute","dataService",'ui.bootstrap',"ngAnimate",'ngCookies']);
	// 路由
	app.config(function($routeProvider) {
		$routeProvider.when('/:module', {
			templateUrl : function($routeParams) {
                return $routeParams.module+".html";
            }
		}).when('/:module/:detail', {
			templateUrl : function($routeParams) {
                return $routeParams.detail+".html";
            }
		}).otherwise({
			redirectTo : '/registerForm'
		});
	});
	
	
	// 控制器
	app.controller('registerController', function($rootScope,$scope, $http,$uibModal,appData, $q,$location,$interval) {
		var parent = $scope.$parent;
		// 强制刷新视图
		$scope.synData = function(){
			$scope.$$phase || $scope.$apply();
		};
		
		$scope.gotoPage = function(hash){
			$location.url(hash);
		}
		
	    $scope.registerSms="点击获取验证码";
		$scope.player ={gender:1,birth:'1980-01-01'};
		
		 var stop;
		 var curCount;
	      var countDown = function() {
		      if ( angular.isDefined(stop) ) return;
		      curCount = 60;
		      $scope.registerSms = '重新发送('+curCount+')'
		      stop = $interval(function() {
		        if (curCount>1) {
		        	curCount--;
		            $scope.registerSms = '重新发送('+curCount+')'
		        } else {
		            stopCount();
		            $scope.registerSms="点击获取验证码";
		        }
		      }, 1000);
	    };

	    var stopCount = function() {
	      if (angular.isDefined(stop)) {
	        $interval.cancel(stop);
	        stop = undefined;
	      }
	    };

	    $scope.$on('$destroy', function() {
	        // Make sure that the interval nis destroyed too
	    	stopCount();
	      });
	    
	    $scope.sendSmsForPassword = function(form){
			if(!form.mobile.$valid){
				form.mobile.error = true;
				return;
			}else{
				form.mobile.error = false;
			}
			$scope.verifyCodeErr = "";
			appData.sendSmsForPassword($scope.player.mobile,function(){
				countDown();
			});
		}
	    
	    
		$scope.showLoginModal = function(row){
			//	$("body").css("overflow","hidden");
				$scope.message = "";
				$scope.iuser = {};
				$("#loginModal").modal({show:true});  
				$('#loginModal').on('hide.bs.modal', function () {
					document.onkeydown  = function(){
					}; 
				});
				document.forms[0].setAttribute("autocomplete","off");
				document.onkeydown  = function(){
				  if(event.keyCode  ==  13)   { 
						$scope.login($scope.iuser.account,$scope.iuser.password)
						// event.keyCode=9;
				  } 
				}; 
		};
		$rootScope.getImgUrl = function(paths,size){
			size = size||1;
			var defaultImg = "/web/img/headIcon.gif";
			try{
				var pathObj =JSON.parse(paths)||{};
				if(pathObj[size]){
					return pathObj[size];
				}
			}catch(e){
			}
			return defaultImg;
		}
		
		$scope.uploadImage = function(){
			appData.uploadImage({params:{original:true}},function(data){
				 var modalInstance = $uibModal.open({
				      templateUrl: 'jcrop.html',
				      controller: 'jcropCtrl',
				      resolve: {
				        data: function(){return data}
				      }
				    });
				    modalInstance.result.then(function (args) {
				    	$scope.player.iconUrl = args.picPath;
				    	$scope.synData();
				    }, function () {
				        console.info('Modal dismissed at: ' + new Date());
				    });
			})
		};
		
		$scope.showLoginModal = function(){
			var modalInstance = $uibModal.open({
			      templateUrl: 'loginDiv.html',
			      controller: 'loginController',
			      size:'sm',
			      resolve: {hideRegister:function(){return true;}
			      }
			    });
			    modalInstance.result.then(function (args) {
			    	window.location.href='/web/main.html';
			    }, function () {
			        console.info('Modal dismissed at: ' + new Date());
			    });
		};
		
	/*	
		$scope.checkAccount = function(registerForm){
			if(registerForm.account.$invalid){
				return;
			}
			$scope.accountPromise = checkAccount();
			$scope.accountPromise.then(function(data){
			     if(data&&data.account&&data.auditStatus){
					$scope.accountErr="此帐号已存在，请更换或直接登陆"
					$scope.accountCheck = "";
				}else if(data&&data.account&&!data.auditStatus){
					$scope.accountErr="此帐号已被注册，正在审核中"
						$scope.accountCheck = "";
				}else{
					$scope.accountErr=undefined;
					$scope.accountCheck = "ok";
				}
		  })
		}
		
		function checkAccount(){
			$scope.accountCheck = "ing";
			var deferred = $q.defer();
			$http.get("/app/playerAction!findPlayerByAccount.action?search.name="+$scope.player.account
					).success(function(data, status, headers, config) {
						deferred.resolve(data);
				   }).error(function(data, status, headers, config) {
	    				$scope.accountErr = "系统故障，请稍后重试";
	    				$scope.accountCheck = "";
				  })	
				  return deferred.promise;
		}*/
		
		$scope.add = function(registerForm){
			registerForm.submited = true;
			if(registerForm.$invalid ){
				return;
			}
			var player = $scope.player;
			if(!player.iconUrl){
				return;
			}
			$http.post("/app/player/register",player).success(function(data, status, headers, config) {
					//after successfully uploaded, clear the form
					if(data.success){
						 layer.confirm("注册成功,等待管理员审核!", {btn: ['确定'],title:false},function(index){
							 location.assign("/web/main.html");
		   		       });
					}else{
						layer.msg(data.error, {time: 2000, icon:5});
					}
			}).error(function(data, status, headers, config) {
    				$scope.message = "添加球员失败!";
			});	
		}
	});
	
	
	app.controller('findPwdController',function($http,$scope,appData,$interval){
		$scope.step = 1;
		$scope.btnTip="点击获取验证码";
		 var stop;
	      var countDown = function() {
	      if ( angular.isDefined(stop) ) return;
	      $scope.totalSec = 60;
	      $scope.btnTip = '重新发送('+$scope.totalSec+')'
	      stop = $interval(function() {
	        if ($scope.totalSec>1) {
	        	$scope.totalSec--;
	            $scope.btnTip = '重新发送('+$scope.totalSec+')'
	        } else {
	            stopCount();
	            $scope.btnTip="点击获取验证码";
	        }
	      }, 1000);
	    };

	    var stopCount = function() {
	      if (angular.isDefined(stop)) {
	        $interval.cancel(stop);
	        stop = undefined;
	      }
	    };

	    $scope.$on('$destroy', function() {
	        // Make sure that the interval nis destroyed too
	    	stopCount();
	      });
		
	    $scope.checkMobile = function(){
			if(!/^(1)\d{10}$/.test($scope.mobile)){
				$scope.mobileErr="请输入注册手机号";
				$scope.mobileCheck = "";
				return ;
			}
			$scope.mobileCheck = "ing";
			 $http({
		          method: 'POST',
		          url: "/player/checkUnique",
		          params: {mobile:$scope.mobile}
		        }).success(function(data, status, headers, config) {
					if(data&&data.isUnique){
						$scope.mobileErr="此手机未注册，请重新输入";
						$scope.mobileCheck = "";
					}else{
						$scope.mobileErr=""
					    $scope.mobileCheck = "ok";
					}
			   }).error(function(data, status, headers, config) {
    				$scope.mobileErr = "系统故障，请稍后重试";
    				$scope.mobileCheck = "";
			  })	
		}
	    
		$scope.sendSmsForPassword = function(){
			if($scope.mobileCheck != "ok"){
				$scope.checkMobile();
				return;
			}
			$scope.verifyCodeErr = "";
			appData.sendSmsForPassword($scope.mobile,function(){
				countDown();
			});
		}
		
		$scope.verify = function(form){
			form.submited = true;
			if(form.$invalid ){
				return;
			}
			appData.verifyCode($scope.mobile,$scope.verifyCode,function(data){
				if(data.success){
					$scope.step = 2;
				}else{
					$scope.verifyCodeErr = "验证码错误"; 
				}
			});
		}
		
		$scope.checkPassword =  function(){
			if($scope.password2){
				$scope.checkPassword2();
			}
			if(/\s/.test($scope.password)){
				$scope.passwordErr="密码中不可包含空格";
				return false;
			}
			var patrn =/^[\w!,@#$%^&*?_~]{6,20}$/;
			if (!patrn.test($scope.password)){
				$scope.passwordErr="请输入6-20个字符";
				return false;
			}else{
				$scope.passwordErr="";
				return true;
			}
		}
		
		$scope.checkPassword2 =  function(){
			if($scope.password != $scope.password2){
				$scope.password2Err="前后密码不一致";
				return false;
			}else{
				$scope.password2Err="";
				return true;
			}
		}
		
		$scope.confirm = function(){
			if(!$scope.checkPassword()||!$scope.checkPassword2()){
				return;
			}
			appData.changePassword($scope.mobile,$scope.password,function(data){
				if(data){
					$scope.step = 3;
				}
			})
		}
		
	});
	
	
})();