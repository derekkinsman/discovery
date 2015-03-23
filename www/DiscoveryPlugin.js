var exec = require('cordova/exec');

function Discovery() {}

Discovery.prototype.initChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "initChat", []);
};

Discovery.prototype.advertizeChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "advertizeChat", []);
};

Discovery.prototype.discoverChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "discoverChat", []);
};

Discovery.prototype.connectChat = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "connectChat", []);
};

Discovery.prototype.sendChatMessage = function(messageString, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Discovery", "sendChatMessage", [messageString]);
};

module.exports = new Discovery();