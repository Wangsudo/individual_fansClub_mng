(function() {
	"use strict";
	var app = angular.module("admin.mainframe");
	// 控制器
	app.controller('adsController', function($scope,$rootScope,$uibModal, $http,$sce,appData) {
			
   /*   //获取管理员信息
		$scope.find = function(pageNo){
			appData.getPageResult("../ads/list",{
				  name:$scope.search.name,
				  account:$scope.search.account,
				  position:$scope.search.position,
				  fromDate:$scope.search.fromDate,
				  currentPage:pageNo||$scope.items.currentPage||1,
				  pageSize:$scope.items.pageSize
			  },function(data){
				  data.list.each(function(n){
					  n.trustedContent =  $sce.trustAsHtml(n.content);
				  })
				  $scope.items = data;
			  });
	   };*/
	   
		$scope.items = {};
	      //获取管理员信息
		$scope.find = function(pageNo){
			$.extend($scope.search,{currentPage:pageNo||1,pageSize:$scope.items.pageSize||10});
		  appData.getPageResult("../ads/list",$scope.search,function(data){
			  data.list.each(function(n){
				  n.trustedContent =  $sce.trustAsHtml(n.content);
			  })
			  $scope.items = data;
		  });
	    };
		   
	    $rootScope.preparePromise.then(function(){
			$scope.search={};
			$scope.find();
		});
		   
	   
	   $scope.del = function(id){
			  var url = "../ads/del/"+id;
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
					      templateUrl: 'adsAdd.html',
					      controller: 'adsAddController',
					      resolve: {id: id}
					    });
				   modal.result.then(function () {
					   $scope.find();
					  }, function () {
					      console.info('Modal dismissed at: ' + new Date());
					  });
	      }
	});
	   
	
	app.controller('adsAddController', function($scope,$http,$uibModalInstance,$uibModal,$rootScope,id,appData) {
		if(id){
			$http.get("../ads/"+id).success(function(resp){
				if(resp.success){
					$scope.ads = resp.data;
				}
			})
		}else{
			$scope.ads = {};
		}
		   /**
	         * 添加商品说明图片
	         */
        $scope.editImage= function(){
        	appData.uploadImage({},function(data){
        		$scope.ads.url = data.picPath;
        		$scope.synData();
			})
        }
        
	      //添加或更新管理员
		 $scope.save=function(ads){
			  $http.post("../ads/saveOrUpdate",ads).success(function(data, status, headers, config) {
					if(data.success){
						$uibModalInstance.close();
					}
			  })
		}; 
		
		//显示预览对话框
		   $scope.previewModal = function(ads){
		       //点击后 校验不通过的会变红
			   $scope.form.$setSubmitted(true);
			   if(!$scope.form.$valid){
				  return;
			   }
			
			   if(!ads.url){
				   $scope.alert("MISSING_PICTURE");
				   return;
			   }
			   
			   if($scope.form.$dirty){
				   ads.status = 1;
			   }
			   
			   if(ads.stopTime && ads.stopTime < new Date().getTime()){
				   $scope.alert("STOPTIME_WRONG");
				   return;
			   }
			
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'adsPreview.html',
				      controller: 'adsPreviewController',
				      size: 'lg',
				      resolve: {me:$.extend(true,{},ads)}
				    });
		      modal.result.then(function () {
		    	   $uibModalInstance.close();
			  },
			  function () {
			      console.info('Modal dismissed at: ' + new Date());
			  });
		   }
			
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	      };
	});
	
	app.controller('adsPreviewController', function($scope,$http,appData,$uibModal,$uibModalInstance,$rootScope,me) {
		  $scope.me = me;
		  if( new Date(me.startTime)< new Date()){
			  me.startTime = new Date().getTime();
		  }
		  function findBanners(me){
			  
			  $http.post("../ads/list",
					  {
				  orderby:"seq asc,u.modifyTime desc",
				  position:me.position,
				  fromDate:me.startTime,//ads有效日期(处于上架与下架时间之间)
//				  condition:" and u.status in (1,2)",
				  currentPage:1,
				  pageSize:100
			  }).success(function(data, status, headers, config){
				  data.list.remove(function(n){return n.id == me.id});
				  data.list.add(me,0);
				  for(var i = 0,l = data.list.length;i<l;i++){
					  data.list[i].seq = i; 
				  }
				  $scope.items = data.list ;
				  $scope.showCarousel();
			  }).error(function(data, status, headers, config) {
				  console.log("获取ads列表异常");
			  }); 
		  };
		  
		  findBanners($scope.me);
		  
		  $scope.toggle = function(ads){
			  ads.off = !ads.off;
			  $scope.showCarousel();
		  }
		  $scope.cancel = function () {
	            $uibModalInstance.dismiss('cancel');
	      };
	      
	     $scope.moveImg = function(pics,pic,dct){
			//dct 为1 时右移, -1时左移
	    	var nextSeq = (pic.seq+dct+pics.length)%(pics.length);
	    	var other = pics.find(function(n){return n.seq === nextSeq});
	    	other.seq = pic.seq;
	    	pic.seq = nextSeq;
	    	 $scope.showCarousel();
		 } 
	  	
	  	 //显示添加对话框
	/*	$scope.showCarousel = function(){
				var list = $scope.items.findAll(function(n){return !n.off});
				list = list.sortBy(function(n) {return n.seq; });
			   var modal = $uibModal.open({
				      animation: true,
				      templateUrl: 'carousel.html',
				      controller: 'carouselController',
				      size: 'lg',
				      resolve: {slides: list}
				    });
			     modal.result.then(function () {
				  }, function () {
				      console.info('add carousel dismissed at: ' + new Date());
				  });
		   }*/
		
		$scope.showCarousel = function(){
			var list = $scope.items.findAll(function(n){return !n.off});
			$scope.slides = [];
			$scope.items.each(function(n){
				if(!n.off){
					$scope.slides.push({seq:n.seq,url:n.url})
				}
			})
		};
	      
	  	//发布
	  	$scope.release= function(oriList){
	  		var items = $.extend(true,[],oriList);
	  		var data = [];
	  		var draftCnt =0,downs =0, reOrders =0;
	  		items.each(function(n){
	  			if(n.status ==2 && n.off == true){
	  				downs++;
	  				//待下档的
	  				n.stopTime = new Date($scope.me.startTime).getTime();
	  			
	  			}else if(n.status !=2 && !n.off){
	  			    //待上档的(包含已上档,但需要改变次序)
	  				draftCnt ++;
	  				n.status = 2;
	  			}else if(n.status ==2 && !n.off ){
	  				reOrders++
	  			}
	  			data.push(n);
	  			
	  		})
	  		if(draftCnt == 0 && reOrders ==0){
	  			$rootScope.alert('NO_BANNER');
	  		}else{
	  			var message = "";
	  			if(draftCnt >0){
	  				message += draftCnt +" drafts will be released!"
	  			}
	  			if(downs>0){
	  				message += downs +" adss will expire!"
	  			}
	  			message +=" Are you sure to release?"
	  			
	  			layer.confirm(message,{btn: ['ok','cancel'],title:false},function(index){
					$http.post("../ads/release",data).success(function(data){
						if(data.success){
							 $uibModalInstance.close();
						}
						layer.close(index);
					}).error(function(data){
					});
			    });
	  		}
	  	}
	  	
	});
	
})();


