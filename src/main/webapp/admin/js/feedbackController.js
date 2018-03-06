(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('feedbackController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
		$scope.items = {};
		$scope.popup = {  opened: false  };
		 $scope.openDatePicker = function() {
		    $scope.popup.opened = true;
		  };
	
      //获取管理员信息
		$scope.find = function(pageNo){
		  $http.post("../feedback/list",
				  {
			  account:$scope.search.account,
			  content:$scope.search.content,
			  toDate:$scope.search.optime && $scope.search.optime.format("{yyyy}-{MM}-{dd}"),
			  tabletTypeId:$scope.search.tabletTypeId,
			  pid:$scope.first && $scope.first.id,
			  status:$scope.search.status,
			  email:$scope.search.email,
			  currentPage:pageNo||$scope.items.currentPage||1,
			  pageSize:$scope.items.pageSize||10
		  }).success(function(data, status, headers, config){
			  $scope.items = data;
		  }).error(function(data, status, headers, config) {
			  console.log("获取球员列表异常");
		  }); 
	   };
	
		   $scope.pageChanged = $scope.find;
		   $scope.find();
		   
		   //显示反馈详细
		   $scope.showFeedback = function(feedback){
			   if (feedback.status==0) {
				   $http.get("../feedback/updateStatus/"+feedback.id);
				   feedback.status = 1;
			   }
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'feedbackDetail.html',
				      controller: 'feedbackDetailController',
				      size: 'lg',
				      resolve: {feedback: function(){	
				    	  feedback = $.extend({},feedback);
					    	  return feedback;
				    	  }
				    }});
			       modal.result.then(function () {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		   }
		   
		   $scope.feedbackListSolo = function(item){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'feedbackListSolo.html',
				      controller: 'feedbackListSoloController',
				      size: 'lg',
				      resolve: {account:function(){
				    	  return item.user.account;}}
				    });
			    modal.result.then(function () {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		   }
		   
		   $scope.showinfo = function(user){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'userInfo.html',
				      controller: 'userInfoController',
				      resolve: {user: function(){	
				    	  user = $.extend({},user);
					      return user;
				    	  },
				    }});

			   modal.result.then(function () {
				   $scope.find();
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
       }
		 
		   		// 删除
			   $scope.del = function(feedback){
					  var url = "../feedback/del/"+feedback.id;
					  $scope.confirm(url,'DEL_MSG',function(data){
						  if(data){
							  $scope.find();
						  }
					  });
				};
				//关闭窗口
				$scope.cancel = function () {
			            $uibModalInstance.dismiss('cancel');
			        };
	});
	
	//添加回复
	app.controller('feedbackDetailController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,feedback) {
		var parent = $scope.$parent;
		 $scope.feedback = feedback;
		 
		 
		 function getLoginUser(){
				$http.get("../getUserbySession").success(function(data){
					$scope.admin = data;
				})
			}
		 getLoginUser();
		 
		 $scope.find=function(){
			 $http.post("../feedbackComment/list",{id:feedback.id}).success(function(data, status, headers, config){
					$scope.feedback.comments=data;
					$scope.comment="";
				  }).error(function(data, status, headers, config) {
					  console.log("获取球员列表异常");
				  }); 
		 }
		 
		 $scope.find();
		 
		 $scope.reply=function(feedback,comment){
			 $scope.form.$setSubmitted(true);
			  if(!$scope.form.$valid){
				  return;
			  }
			 var feedbackComment={feedbackId:feedback.id,content:comment,creater:$scope.admin,createtime:Date.parse(new Date())};
				$http.post("../feedbackComment/save",feedbackComment).success(function(data, status, headers, config) {
					$scope.find();
					
				}).error(function(data, status, headers, config) {
					console.error("保存配件失败!");
				});
		 };
			
			
			$scope.showUser = function(user){
				   var modal = $uibModal.open({
					      animation: true,
					      templateUrl: 'userInfo.html',
					      controller: 'userInfoController',
					      resolve: {user: function(){	
					    	  user = $.extend({},user);
						      return user;
					    	  },
					    }});

				   modal.result.then(function () {
					   $scope.find();
					   
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	       }
			
			
			$scope.showinfo = function(adminUser){
				   var modal = $uibModal.open({
					      animation: true,
					      templateUrl: 'adminDetail.html',
					      controller: 'adminDetailController',
					      resolve: {adminUser: function(){	
					    	  adminUser = $.extend({},adminUser);
						      return adminUser;
					    	  },
					    }});

				   modal.result.then(function () {
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	       }
			
			
			// 删除
			   $scope.del = function(comment){
				   $scope.confirm("../feedbackComment/del/"+comment.id,'DEL_MSG',function(data){
							$scope.feedback.comments.remove(comment);
						});
				};
				
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	      
	});
	
	
	
	app.controller('feedbackListSoloController', function($scope,$http,$uibModalInstance,account,$uibModal) {
		var parent = $scope.$parent;
		$scope.account = account;
		$scope.search={};
		$scope.items = {};
		  
		  $scope.showinfo = function(user){
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'userInfo.html',
				      controller: 'userInfoController',
				      resolve: {user: function(){	
				    	  user = $.extend({},user);
					      return user;
				    	  },
				    }});
			   modal.result.then(function () {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
      }
	
	
      //获取管理员信息
		$scope.find = function(pageNo){
		  $http.post("../feedback/list",
				  {
			  account:account,
			  content:$scope.search.content,
			  toDate:$scope.search.optime && $scope.search.optime.format("{yyyy}-{MM}-{dd}"),
			  currentPage:pageNo||$scope.items.currentPage||1,
			  pageSize:$scope.items.pageSize||10
		  }).success(function(data, status, headers, config){
			  $scope.items = data;
		  })
	   };
	
		   $scope.pageChanged = $scope.find;
		   $scope.find();
		   
		   //显示反馈详细
		   $scope.showFeedback = function(feedback){
			   if (feedback.status==0) {
				   $http.get("../feedback/updateStatus/"+feedback.id);
				   feedback.status = 1;
			   }
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'feedbackDetail.html',
				      controller: 'feedbackDetailController',
				      size: 'lg',
				      resolve: {feedback: function(){	
				    	  feedback = $.extend({},feedback);
					    	  return feedback;
				    	  }
				    }});
			      modal.result.then(function () {
				  }, function () {
				      console.info('Modal dismissed at: ' + new Date());
				  });
		   }
		   $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	});
		   
	
})();