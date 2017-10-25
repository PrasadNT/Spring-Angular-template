app.controller('fxTransactionController', function($scope, $http) {

	$scope.headingTitle = "FX Transaction List";
	$scope.displayTransactions = null;

	$http.get('/getFxTransactions/').success(function(data) {
		$scope.displayTransactions = data;
	})
});

app.controller('rolesController', function($scope, $http) {
	$scope.headingTitle = "Roles List";

	$scope.roles = null;

	$http.get('/getRoles/').success(function(data) {
		$scope.roles = data;
	})
});

app.controller('submitTransactionController', function($scope, $http) {
	$scope.headingTitle = "Submit FX Transaction";
	
	$scope.transaction = {};
	$scope.transactions = [];

	$scope.submit = function() {
		
		$http.post('/submitTransaction', $scope.transaction)
        .then(
            function (response) {
                $scope.transactions.push($scope.transaction);
                $scope.transaction = {};
            },
            function (errResponse) {
               console.error('Error while creating User : '+errResponse.data.errorMessage);
               alert('error');
            }
        );
		
	};

	
});
