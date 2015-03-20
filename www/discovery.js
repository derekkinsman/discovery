var exec = require('cordova/exec');
var Discovery = function() {};

Discovery.prototype.identify = function(success, fail) {
    exec(success, fail, "Discovery", "identify", []);
};

var discovery = new Discovery();
module.exports = discovery;