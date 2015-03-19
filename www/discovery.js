var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

/**
 * Identify a broadcast service.
 */
 
var Discovery = {
  identify: function(successCallback, errorCallback, opts) {
    console.log('Doing identify', opts);
    exec(successCallback, errorCallback, "Discovery", "identify", [opts]);
  }
}

module.exports = Discovery;