(function() {
	"use strict";
	var app = angular.module("football");
	
	app.directive('ensureUnique', ['$http', function($http) {
		  return {
		    require: 'ngModel',
		    link: function(scope, ele, attrs, c) {
		      scope.$watch(attrs.ngModel, function(v) {
		    	  if(!v){
		    		  return;
		    	  }
		    	  if(attrs.ensureUnique == 'idno'){
		    		  if(!IdCardValidate(v)){
		  		    	c.$setValidity("pattern",false);
		  		    	return;
		  			}else{
		  				c.$setValidity("pattern",true);
		  			}
		    	  }
		    	  var pack ={id:attrs.id}
			    	pack[attrs.ensureUnique] = v;
		        $http({
		          method: 'POST',
		          url: attrs.api,
		          params: pack
		        }).success(function(data, status, headers, cfg) {
		          c.$setValidity('unique', data.isUnique);
		          c.auditStatus = data.status;
		        }).error(function(data, status, headers, cfg) {
		          c.$setValidity('unique', false);
		        });
		      });
		    }
		  }
		}]);
	
	app.directive('compile', function($compile) {
		      // directive factory creates a link function
		      return function(scope, element, attrs) {
		        scope.$watch(
		          function(scope) {
		             // watch the 'compile' expression for changes
		            return scope.$eval(attrs.compile);
		          },
		          function(value) {
		            // when the 'compile' expression changes
		            // assign it into the current DOM
		            element.html(value);
		            // compile the new DOM and link it to the current
		            // scope.
		            // NOTE: we only compile .childNodes so that
		            // we don't get into infinite loop compiling ourselves
		            $compile(element.contents())(scope);
		          }
		        );
		      };
		    });
	app.directive('dateFormat', function() {  
	    return {  
	        require: 'ngModel',  
	        link: function(scope, elm, attrs, ctrl) {  
	            function formatter(value) {  
	            	var out = value;
	            	if(/\d{2}:\d{2}:\d{2}/.test(input)){
	    				out = input.substr(0,input.lastIndexOf(":"))
	    				ctrl.$setViewValue(out);
	    				ctrl.$render();
	    			}
	                return out; //format  
	            }  
	            //ctrl.$formatters.push(formatter);  
	            ctrl.$parsers.push(formatter);  
	        }  
	    };  
	});
	
	app.directive('imageLoad', function () {
		    return {
		        restrict: 'A', link: function (scope, element, attrs) {
		            element.bind('load', function () { 
		                //call the function that was passed 
		                scope.$apply(attrs.imageLoad);
		            });
		        }
		    };
	})
		 
})();