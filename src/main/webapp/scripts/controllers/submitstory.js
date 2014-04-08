'use strict';

angular.module('hackerpins')
    .controller('SubmitstoryCtrl', function ($scope, $http, $location) {

        $scope.story = {};
        $scope.submitStory = function () {
            $http.post("api/v1/stories", $scope.story).success(function (data, status, header, config) {
                $location.path('/stories/upcoming');
            }).error(function (data, status, header, config) {
                alert(status);
            });
        }

        $scope.fetchUrlDetails = function () {
            if($scope.story && $scope.story.url){
                $scope.isFetchingDetails = true;
                $scope.spinner = "fa-spinner";
                $http.get("api/v1/stories/suggest?storyUrl=" + $scope.story.url).success(function (data, status, header, config) {
                    $scope.isFetchingDetails = false;
                    $scope.story.title = data.title;

                    $scope.story.description = data.description;
                    if(data.picUrl){
                        $scope.story.media = {
                            type: "PHOTO",
                            mediaUrl: data.picUrl
                        }
                    }
                }).error(function (data, status, header, config) {
                    $scope.isFetchingDetails = false;
                    alert(status);
                    console.log(data);
                });
            }

        }
    });
