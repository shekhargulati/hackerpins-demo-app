'use strict';

var app = angular.module('hackerpins', [
        'ngCookies',
        'ngResource',
        'ngSanitize',
        'ngRoute',
        'ui.bootstrap'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .when('/stories/new', {
                templateUrl: 'views/submitstory.html',
                controller: 'SubmitstoryCtrl'
            })
            .when('/stories/upcoming', {
                templateUrl: 'views/upcoming.html',
                controller: 'UpcomingCtrl'
            })
            .when('/stories/:id', {
                templateUrl: 'views/viewstory.html',
                controller: 'ViewstoryCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });

app.filter('moment', function () {
    return function (text) {
        return moment(text, "MMDDYYYY HH mm ss").fromNow();
    }
});
