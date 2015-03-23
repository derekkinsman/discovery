var exec = require('cordova/exec');

function Discovery() {}

Discovery.prototype.initChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "initChat", []);
};

Discovery.prototype.advertizeChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "advertizeChat", []);
};

Discovery.prototype.identify = function(serviceName, serviceType, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "identify", [serviceName, serviceType]);
};

Discovery.prototype.connectChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "connectChat", []);
};

Discovery.prototype.sendChatMessage = function(messageString, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "sendChatMessage", [messageString]);
};

module.exports = new Discovery();
