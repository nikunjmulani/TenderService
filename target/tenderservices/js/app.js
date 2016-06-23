'use strict';

var myApp = angular.module('myApp', ['ngRoute']);
 
myApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider

	.when('/', {
		templateUrl : 'login.html',
		controller : 'loginCtrl'
	})
	
	.when('/dashboard', {
		templateUrl : 'dashboard.html',
		controller : 'namesCtrl'
	})
	
	.when('/addComplaint', {
		templateUrl : 'addComplaint.html',
		controller : 'addController'
	})

	.when('/addBill', {
		templateUrl : 'addBill.html',
		controller : 'addController'
	})

	.when('/itemMaster', {
		templateUrl : 'itemMaster.html',
		controller : 'itemMasterController'
	})

} ]);

myApp.controller('loginCtrl',['$scope','$location','$http','data','$timeout', '$rootScope', function($scope, $location, $http, data, $timeout, $rootScope) {
	$scope.loginButton = function() {
		
		$rootScope.loggedUser = false;
		
		$http({
            url : "/tenderservices/rest/items/login",
            method : 'POST',
            params : {'username':$scope.username, "password":$scope.password},
            headers : {
                'Content-type' : 'application/html',
            }
        }).success(function(response) {
        	if(response=="success"){
        		$location.path('/dashboard');
        		$rootScope.loggedUser = true;
        	}else{
        		$scope.loginFail=true;
            	$scope.errormsg = "Invalid credentials";
            
            	$location.path('/');
            	$scope.username="";
            	$scope.password="";
            	
        	}
        	
        }).error(function(data, status, headers, config) {
        	$scope.loginFail=true;
        	$scope.errormsg = "Invalid login";
        
        	$location.path('/');
        	$scope.username="";
        	$scope.password="";
        });	
	}; 
	
}]);

myApp.controller('namesCtrl',['$scope','$location','$http','data','$timeout', '$rootScope', function($scope, $location, $http, data, $timeout, $rootScope) {
	console.log("hey there");
	$scope.name = "Angular";
	$scope.complainList = [];
	
	$scope.home = function() {
		$location.path('/dashboard');
	}

	$scope.logout = function() {
		$rootScope.loggedUser=false;
		$location.path('/');
	}
	
	var dataArr = data.getData();
	if (dataArr != undefined) {
		$scope.billYear = dataArr[0];
		$scope.billMonth = dataArr[1];
		$scope.complainId = '0';
		$scope.complainCode = '';
		$scope.complainDesc = '';
	} else {
		var currDate = new Date();
		
		var currMonth = currDate.getMonth();
		var currYear = currDate.getFullYear();
		
		if (currMonth == 0 || currMonth == 1 || currMonth == 2) {
			$scope.billYear = (currYear - 1) + "-" + currYear;
			
		} else { 
			$scope.billYear = (currYear) + "-" + (currYear + 1);
		}
		
		if (currMonth == 0) {
			$scope.billMonth = 11;	
		} else {
			$scope.billMonth = currMonth - 1;
		}
		
		$scope.billMonth = $scope.billMonth+"";
	}
	
	getComplains();
	
	function getComplains() {
		if ($scope.billYear && $scope.billMonth) {
			var URL = '/tenderservices/rest/items/complaint/' + $scope.billYear + "/" + $scope.billMonth;
			$http.get(URL).then(function(response) {
				$scope.complainList = [];
				if ( response.data != undefined && response.data != null && response.data.Complain != undefined) {
					$scope.complainList = $scope.complainList.concat(response.data.Complain);	
				}		
			});
		}	
	}
	
	$scope.addComplaint = function() {
		var complainCode = $scope.complainList.length + 1;
		data.setData([$scope.billYear, $scope.billMonth, '0', complainCode ,'']);
		$location.path('/addComplaint');
	}

	$scope.addBill = function() {
		$location.path('/addBill');
	}

	$scope.itemMaster = function() {
		$location.path('/itemMaster');
	}

	$scope.showBill = function() {
		getComplains();
	}
	
	var editKeyIndex = -1;
	$scope.editComplaint = function(complain, index) {
		editKeyIndex = index;
		$scope.complainId = complain.complainId;
		$scope.complainCode = complain.complainCode;
		$scope.complainDesc = complain.complainDesc;
		data.setData([$scope.billYear, $scope.billMonth, $scope.complainId, $scope.complainCode, $scope.complainDesc]);
		$location.path('/addComplaint');
	};

	$scope.delComplaint = function(complain, index) {
		var URL = '/tenderservices/rest/items/deleteComplaint/' + complain.complainId;
		$http.delete(URL).then(function(response) {
			
			$scope.complainList.splice(index, 1);
			$scope.submissionSuccess = true;
			$scope.msg = 'Complaint Deleted Successfully';
			$timeout(function () { $scope.submissionSuccess = false; }, 3000);
		});
	};
	
	$scope.viewReport = function(reportName) {
		downloadReport(reportName);
    }
	
	function downloadReport(reportName) {
		var fileName = reportName+$scope.billYear +'-'+ $scope.billMonth+'.pdf';
		var actualURL = '/tenderservices/rest/items/getReport/' + $scope.billYear + "/" + $scope.billMonth+"/"+reportName;
		$http({
            url : actualURL,
            method : 'POST',
            params : {},
            headers : {
                'Content-type' : 'application/pdf',
            },
            responseType : 'arraybuffer'
        }).success(function(data, status, headers, config) {
            var file = new Blob([ data ], {
                type : 'application/pdf'
            });
            // trick to download store a file having its URL
            var fileURL = URL.createObjectURL(file);
            var a         = document.createElement('a');
            a.href        = fileURL; 
            a.target      = '_blank';
            a.download    = fileName;
            document.body.appendChild(a);
            a.click();
        }).error(function(data, status, headers, config) {

        });
    };
}]);

myApp.controller('itemMasterController', [
            '$http',
			'$scope',
			'$log',
			'$location', 'data', '$timeout', 
			function($http, $scope, $log, $location, data, $timeout) {
				console.log("Item Master controller");
				console.log("appData::"+data.getData());
		
				$scope.home = function() {
					$location.path('/dashboard');
				}
				
				$scope.logout = function() {
					$location.path('/');
				}
                         			
         }]);

myApp.controller('addController', [
		'$http',
		'$scope',
		'$log',
		'$location', 'data', '$timeout', '$rootScope',
		function($http, $scope, $log, $location, data, $timeout, $rootScope) {
			console.log("complaint controller");
			console.log("appData::"+data.getData());

			$scope.home = function() {
				$location.path('/dashboard');
			}
			
			$scope.logout = function() {
				$location.path('/');
			}
			
			var dataArr = data.getData();
			$scope.billYear = dataArr[0];
			$scope.billMonth = dataArr[1];
			$scope.complainId = dataArr[2];
			$scope.complainCode = dataArr[3];
			$scope.complainDesc = dataArr[4];
			
			var month = new Array();
			month[0] = "January";
			month[1] = "February";
			month[2] = "March";
			month[3] = "April";
			month[4] = "May";
			month[5] = "June";
			month[6] = "July";
			month[7] = "August";
			month[8] = "September";
			month[9] = "October";
			month[10] = "November";
			month[11] = "December";
			
			$scope.billMonthName = month[$scope.billMonth];
			
			
			// ****************** ITEM AUTOCOMPLETE
			// *******************************
			var autoCompleteCacheData = $rootScope.autoCompleteRootCacheData;
			if (autoCompleteCacheData == undefined ) {
				$http.get('/tenderservices/rest/items/items').then(
						function(response) {
							var i = 0;
							var rec;
							var responseItems = response.data.item;			
							$scope.itemList = [];		
							$scope.itemKeyValue = [];
							for (rec in response.data.item) {
								var options = responseItems[i].itemId +"|"+ responseItems[i].itemName + "|" + responseItems[i].rate + "|" + responseItems[i].unit;
								$scope.itemList.push(options);
								$scope.itemKeyValue[options]=options;
								i++;
							}
							
							var cacheData = [];
							cacheData[0] = $scope.itemList;
							cacheData[1] = $scope.itemKeyValue;
							
							$rootScope.autoCompleteRootCacheData = cacheData;
							
							$("#tags").autocomplete({
								source : $scope.itemList
							});
				});
			} else {
				
				$scope.itemList = autoCompleteCacheData[0];
				$scope.itemKeyValue = autoCompleteCacheData[1];
				
				$("#tags").autocomplete({
					source : $scope.itemList
				});
			}

			// ****************** /ITEM AUTOCOMPLETE
			// *******************************
			
			if ($scope.complainId != undefined && $scope.complainId != 0) {
				$http.get('/tenderservices/rest/items/getComplainItems/'+$scope.complainId).then(
						function(response) {
							$scope.itemsDataArr = [];
							
							if (response.data.item != undefined) {
								$scope.itemsDataArr = $scope.itemsDataArr.concat(response.data.item);
							}						
							$scope.data = $scope.itemsDataArr;
				});
			}

			// This is a function which will be called on click of Save & New
			// button

			function requestToSaveComplain() {
				
				if(!$scope.complainCode) {
					$scope.submissionError = true;
					$scope.errorMsg="Please enter Complaint No.";
					 $timeout(function () { $scope.submissionError = false; }, 3000); 
				}else if(!$scope.complainDesc) {
					$scope.submissionError = true;
					$scope.errorMsg="Please enter Complaint Description";
					 $timeout(function () { $scope.submissionError = false; }, 3000); 
				}else if($scope.itemsDataArr == undefined){
					$scope.submissionError = true;
					$scope.errorMsg="Please enter atleast one Item";
					$timeout(function () { $scope.submissionError = false; }, 3000);
				}else{
					var dataObj = {
						billYear : $scope.billYear,
						billMonth : $scope.billMonth,
						complainId : $scope.complainId,
						complainCode : $scope.complainCode,
						complainDesc : $scope.complainDesc,
						itemList : $scope.itemsDataArr
					}
					return $http.post('/tenderservices/rest/items/saveComplain', dataObj);
				}
			}
			
			$scope.clickSaveComplainNewBtn = function() {
				var res = requestToSaveComplain();
				
				res.success(function(data, status, headers, config) {
					$scope.message = data;
					$scope.submissionSuccess = true;
					$scope.msg="Complaint has been saved successfully!";
					 $timeout(function () { $scope.submissionSuccess = false; }, 3000); 
				
					 // Elements clear
					 $scope.complainCode = eval($scope.complainCode) + 1;
					 $scope.complainDesc = "";
					 $scope.itemsDataArr = [];
					
					 $scope.data = $scope.itemsDataArr;
					 document.getElementById("complainDescId").focus();
				});
				res.error(function(data, status, headers, config) {
					return "failure message: " + JSON.stringify({
						data : data
					});
				});
			};

			// This is a function which will be called on click of Save & Exit
			// button

			$scope.clickSaveComplainExitBtn = function() {
				var res = requestToSaveComplain();
				res.success(function(data, status, headers, config) {
					$scope.message = data;
					$location.path('/dashboard');
					// redirect to main page
				});

				res.error(function(data, status, headers, config) {
					return "failure message: " + JSON.stringify({
						data : data
					});
				});
			};
			
			$scope.clickComplainExitBtn = function() {
				$location.path('/dashboard');
			}

			// This is a function which will be called on click of Add/Update
			// Item button
			var editKeyIndex = -1;
			$scope.clickAddUpdateItemBtn = function() {
				
				$scope.itemName = $('#tags').val();
				if (!$scope.itemName) {
					$scope.submissionError = true;
					$scope.errorMsg="Please enter Item Name";
					 $timeout(function () { $scope.submissionError = false; }, 3000); 
					 
				} else if ($scope.itemKeyValue[$scope.itemName] == undefined) {
					$scope.submissionError = true;
					$scope.errorMsg="Please enter valid Item Name";
					$timeout(function () { $scope.submissionError = false; }, 3000); 
					 
				} else if (!$scope.itemQuantity) {
					$scope.submissionError = true;
					$scope.errorMsg="Please enter Quantity";
					$timeout(function () { $scope.submissionError = false; }, 3000); 
				}else if(!$scope.itemQuantity.match(/^[1-9]\d*$/)){
					$scope.submissionError = true;
					$scope.errorMsg="Please enter Numeric Value";
					$timeout(function () { $scope.submissionError = false; }, 3000);
					
				} else {
					if ($scope.data) {
						$scope.itemsDataArr = $scope.data;
					} else {
						$scope.itemsDataArr = [];
					}
					
					var selectedItemSplit = $scope.itemName.split("|");
					selectedItemSplit[3] = (angular.isUndefined(selectedItemSplit[3]) || selectedItemSplit[3] == 0)? 1 : selectedItemSplit[3]; // UNIT
					var selectedItemObj = {
						"itemId" : selectedItemSplit[0],
						"itemName" : selectedItemSplit[1],
						"rate" : selectedItemSplit[2],
						"unit" : selectedItemSplit[3],
						"itemQuantity" : $scope.itemQuantity,
						"amount" : Math.round(($scope.itemQuantity /  selectedItemSplit[3]) * selectedItemSplit[2])
					} 
					
					if (editKeyIndex != undefined && editKeyIndex != -1) {
						$scope.itemsDataArr[editKeyIndex] = selectedItemObj;
						editKeyIndex = -1;
					} else {
						$scope.itemsDataArr.push(selectedItemObj);	
					}

					$scope.data = $scope.itemsDataArr;
					$scope.itemName = '';
					$scope.itemQuantity = '';
					document.getElementById("tags").focus();
				}
			};

			$scope.editItem = function(item, index) {
				editKeyIndex = index;
				$scope.itemName = item.itemId +"|"+ item.itemName + "|" + item.rate + "|" + item.unit;
				$scope.itemQuantity = item.itemQuantity;
			};

			$scope.delItem = function(id) {
				$scope.itemsDataArr.splice(id, 1);
			};

		} ]);

/*
 * service to communicate data between controller1 and controller2
 */
 myApp.service('data', function(){
	 this.common = function(data){
	     return data;
	 };
     this.setData=function(data){
         this.data = data;
     };
     this.getData= function(){
         return this.data;
     };
});

myApp.directive('enterAsTab', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                event.preventDefault();
                var elementToFocus = element.next('td').find('input')[1];
                if(angular.isDefined(elementToFocus))
                    elementToFocus.focus();
                
            }
        });
    };
});

myApp.run( function($rootScope, $location) {

    // register listener to watch route changes
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
      if ( $rootScope.loggedUser == null || $rootScope.loggedUser == false) {
          $location.path( "/" );
       }
    });

    document.onkeydown = function(){
    			  switch (event.keyCode){
    			        case 116 : //F5 button
    			            event.returnValue = false;
    			            event.keyCode = 0;
    			            return false;
    			        case 82 : //R button
    			            if (event.ctrlKey){ 
    			                event.returnValue = false;
    			                event.keyCode = 0;
    			                return false;
    			            	}
    			  	}
    		}; 
});