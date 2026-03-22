// AngularJS App for OSManager
var osmanagerApp = angular.module('osmanagerApp', []);

// Controller for main page
osmanagerApp.controller('MainController', function($scope) {
    $scope.message = 'Bem-vindo ao OSManager com AngularJS!';
    $scope.menuItems = [
        { name: 'Ordens de Serviço', link: 'orders/list' },
        { name: 'Clientes', link: 'clients/list' },
        { name: 'Veículos', link: 'vehicles/list' },
        { name: 'Serviços', link: 'services/list' }
    ];
});
