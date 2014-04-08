'use strict';

angular.module('hackerpins')
    .controller('TabCtrl', function ($scope, $location) {
        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }
);
