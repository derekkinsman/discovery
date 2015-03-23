window.initChat = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Discovery", "initChat", []);
};

window.advertizeChat = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Discovery", "advertizeChat", []);
};

window.discoverChat = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Discovery", "discoverChat", []);
};

window.connectChat = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Discovery", "connectChat", []);
};

window.sendChatMessage = function(messageString, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Discovery", "sendChatMessage", [messageString]);
};
