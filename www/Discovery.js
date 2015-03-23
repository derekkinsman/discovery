var exec = require('cordova/exec');

var Discovery = {

  identify: function(serviceName, serviceType, successCallback, errorCallback) {
      exec(successCallback, errorCallback, "Discovery", "identify", [serviceName, serviceType]);
  }

};

module.exports = Discovery;