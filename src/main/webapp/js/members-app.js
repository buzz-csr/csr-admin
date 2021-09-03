var memberModule = angular.module('memberModule', ['ngSanitize']);


memberModule.controller('memberCtrl', ['$scope', '$http', '$sce', '$location', '$anchorScroll', '$interval', function($scope, $http, $sce, $location, $anchorScroll, $interval) {
    $scope.loading = true;
    $scope.currentTab = "players";
    $scope.activeCrew = "rouge";
    $scope.events = [];
    $scope.scoreEvents = [];
    $scope.conversations;
    $scope.playersName = [];
    
    $scope.loadEvents = function() {
          $http({
            method: 'get',
            url: '/csr-admin/events',
            headers: { 'Content-Type': 'application/json' },
            params: {
                action: 'list',
                crew: $scope.activeCrew,
            },
        }).then(function successCallback(response) {
            $scope.events = response.data;
            $scope.loading = false;
        })  
    }
    
    $scope.loadPlayers = function() {
        $http({
            method: 'get',
            url: '/csr-admin/crew',
            headers: { 'Content-Type': 'application/json' },
            params: {
                page: 'players',
                crew: $scope.activeCrew,
            },
        }).then(function successCallback(response) {
            $scope.players = response.data.list;
            $scope.playersName = response.data.names;
            $scope.loading = false;
        })
    }

    $scope.loadCrews = function() {
        $http({
            method: 'get',
            url: '/csr-admin/crew',
            headers: { 'Content-Type': 'application/json' },
            params: {
                page: 'crews',
                crew: $scope.activeCrew,
          },
        }).then(function successCallback(response) {
            $scope.crews = response.data;
            $location.hash('active-crew');
            $anchorScroll();
            $scope.loading = false;
        })
    }

    $scope.loadCrewsGraph = function() {
        $scope.loading = true;
        $http({
            method: 'get',
            url: '/csr-admin/crew',
            headers: { 'Content-Type': 'application/json' },
            params: {
                page: 'graph',
                crew: $scope.activeCrew,
          },
        }).then(function successCallback(response) {
            $scope.dataRange = [];
            $scope.dataRank = [];
            angular.forEach(response.data.histories, function(histo) {
                $scope.dataRange.push({
                   x: new Date(histo.snapshotDate),
                   y: histo.diff
                });
                $scope.dataRank.push({
                   x: new Date(histo.snapshotDate),
                   y: histo.rank
                });
            });
            
            var chart = new CanvasJS.Chart("chartContainer", {
                exportEnabled: true,
                axisY: {
                    title: "RP Crew / 5min",
                    titleFontColor: "#4F81BC",
                    lineColor: "#4F81BC",
                    labelFontColor: "#4F81BC",
                    tickColor: "#4F81BC"
                },
                axisY2: {
                    title: "Place",
                    interval: 1,
                    titleFontColor: "#C0504E",
                    lineColor: "#C0504E",
                    labelFontColor: "#C0504E",
                    tickColor: "#C0504E"
                },                
                data: [{
                    type: "area",
                    dataPoints: $scope.dataRange
                },{
                    type: "spline",
                    axisYType: "secondary",
                    dataPoints: $scope.dataRank
                }],
                zoomEnabled: true,
                animationEnabled: true,
                title: {
                    text: "Statistiques du Crew"
                },
            });
            chart.render();    
            $scope.loading = false;
            $interval($scope.loadCrewsGraph, 30000);
        })
    }
    
    $scope.loadWildcards = function() {
        $http({
            method: 'get',
            url: '/csr-admin/wildcards',
            headers: { 'Content-Type': 'application/json' },
            params: {
              crew: $scope.activeCrew,
            },
        }).then(function successCallback(response) {
            $scope.wildcards = response.data;
            $scope.loading = false;
        })
    }
    
    $scope.loadScoreEvents = function() {
        $scope.loading = true;
        $scope.scoreEvents = [];
        
        angular.forEach($scope.events, function(e){
            $http({
                method: 'get',
                url: '/csr-admin/events',
                headers: { 'Content-Type': 'application/json' },
                params: {
                  action: 'score',
                  eventName: e,
                  crew: $scope.activeCrew,
                },
            }).then(function successCallback(response) {
                $scope.scoreEvents.push(response.data);
                $scope.loading = false;
            })
                       
        });
    }
    
    $scope.loadConversations = function(){
        $scope.loading = true;
        $http({
            method: 'get',
            url: '/csr-admin/tchat',
            headers: { 'Content-Type': 'application/json' },
            params: {
              crew: $scope.activeCrew,
            },
        }).then(function successCallback(response) {
            $scope.conversations = response.data;
            $scope.loading = false;
        })

    }

    $scope.loadPlayers();
    $scope.loadEvents();
    
    $scope.changeTab = function(tab) {
        $scope.currentTab = tab;
    }

    $scope.isTabActive = function(tab) {
        if ($scope.currentTab == tab) {
            return "active";
        } else {
            return "";
        }
    }

    $scope.isTabPaneActive = function(tab) {
        if ($scope.currentTab == tab) {
            return "show active";
        } else {
            return "";
        }
    }

    $scope.changeCrew = function(crew) {
        $scope.activeCrew = crew;
        $scope.loading = true;

        $scope.loadCrews();
        $scope.loadPlayers();
        $scope.loadConversations();
        $scope.loadWildcards();
        $scope.loadScoreEvents();
        $scope.loadCrewsGraph();
    }

    $scope.isActiveCrew = function(crew) {
        if (crew.active) {
            return "active-crew";
        } else {
            return "";
        }
    }
    
    $scope.isPageActive = function(crew) {
        if($scope.activeCrew == crew){
            return "active";
        }else{
            return "";
        }
    }
    
    $scope.crewId = function(crew) {
        if (crew.active) {
            return "active-crew";
        } else {
            return crew.id;
        }
    }
    $scope.html = function(value) {
        return $sce.trustAsHtml(value);
    }
    
    $scope.cardTextClass = function(paid, cost) {
        if(paid == cost){
            return "card-text-full";
        }else{
            return "card-text";
        }
    }
    
}]);