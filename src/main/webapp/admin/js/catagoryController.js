(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('catagoryController', function($scope,$rootScope,$uibModal, $http,appData) {
		var parent = $scope.$parent;
		$scope.search={};
	   
	   //显示修改配件分类对话框
	   $scope.editProduct = function(){
		   var catagoryModal = $uibModal.open({
			      animation: true,
			      templateUrl: 'catagoryAdd.html',
			      controller: 'catagoryAddCtrl',
			      resolve: {
			        items: function(){return $scope.productTypes},
			        title:function(){return '商品类别'}
			      }
			    });

		   catagoryModal.result.then(function (args) {
			   var items = args.items;
			   var delParentIds = args.delParentIds;
			   var delSonIds = args.delSonIds;
			   if(delSonIds && delSonIds.length>0){
				   $http.post("../productType/del",delSonIds).success(function(data) {
					   console.log("../tabletType/delSecond:"+delSonIds);
					});
			   }
			   if(delParentIds && delParentIds.length>0){
					 $http.post("../productType/del",delParentIds).success(function(data) {
						 console.log("../tabletType/delFirst:"+delParentIds);
					 });
				}
			   
			      appData.saveProductType(items,function(data){
			    	  if(data.success){
			    		  $scope.findProductType();
			    	  }
			      })
			  }, function () {
			      console.info('Modal dismissed at: ' + new Date());
			  });
	   }
	});
	
	  app.controller('catagoryAddCtrl', function ($scope, $uibModalInstance, items,title) {
	        $scope.items = $.extend(true,[],items);
	        $scope.title = title;
	       var delParentIds = [];
	       var delSonIds = [];
	        $scope.addType = function(items){
	        	items.push({sons:[]});
	        }
	        
	        $scope.addSon = function(item){
	        	item.sons = item.sons ||[];
	        	item.sons.push({});
	        	item.opened = true;
	        }
	        
	        $scope.minusSon = function(item,son){
	        	item.sons.remove(son);
	        	if(son.id){
	        		delSonIds.push(son.id);
	        	}
	        }
	        $scope.minusItem = function(items,item){
	        	items.remove(item);
	        	if(item.id){
	        		delParentIds.push(item.id);
	        	}
	        }
	        $scope.changeVisible = function(son){
	        	if(son.isvisible == 2){
	        		son.isvisible =1;
	        	}else{
	        		son.isvisible = 2;
	        	}
	        }
	        
	        $scope.ok = function () {
	        	var isError = false;
	        	$scope.items.each(function(n){
	        		n.name = (n.name||"").trim();
	        		n.code = (n.code||"").trim();
	        		if(!/.+/.test(n.name)){
	        			n.nameErr="empty!"
	        			isError = true;
	        		}else{
	        			delete n.nameErr;
	        		}
	        		if(!/.+/.test(n.code)){
	        			n.codeErr="empty!"
	        			isError = true;
	        		}else{
	        			delete n.codeErr;
	        		}
	        		n.sons && n.sons.each(function(s){
	        			s.name = (s.name||"").trim();
	        			if(!/.+/.test(s.name)){
		        			s.nameErr="empty!"
		        			n.opened = true;	
		        			isError = true;
		        		}else{
		        			delete s.nameErr;
		        		}
	        			s.code = (s.code||"").trim();
	        			if(!/.+/.test(s.code)){
		        			s.codeErr="empty!"
		        			n.opened = true;	
		        			isError = true;
		        		}else{
		        			delete s.codeErr;
		        		}
	        		})
	        	})
	        	isError || $uibModalInstance.close({items:$scope.items,delParentIds:delParentIds,delSonIds:delSonIds});
	        };

	        $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	        };
	    });
})();