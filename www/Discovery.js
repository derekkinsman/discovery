var exec = require('cordova/exec');

function noOp() { };

var Discovery = {

  identify: function(serviceName, serviceType, successCallback, errorCallback) {
      exec(successCallback, errorCallback, "Discovery", "startDiscovery", [serviceName, serviceType]);
  },

  startDiscovery: function(serviceName, serviceType, successCallback, errorCallback) {
      exec(successCallback, errorCallback, "Discovery", "startDiscovery", [serviceName, serviceType]);
  },

  stopDiscovery: function() {
      exec(noOp, noOp, "Discovery", "stopDiscovery", []);
  }

};

module.exports = Discovery;
