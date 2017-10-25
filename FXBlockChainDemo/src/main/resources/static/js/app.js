var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/fxTransaction',{
            templateUrl: '/views/FXTransaction.html',
            controller: 'fxTransactionController'
        })
        .when('/roles',{
            templateUrl: '/views/roles.html',
            controller: 'rolesController'
        })
        .when('/submitFXTransaction',{
            templateUrl: '/views/SubmitTransaction.html',
            controller: 'submitTransactionController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

