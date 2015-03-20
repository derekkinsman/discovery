var Discovery = function() {};

Discovery.prototype.identify = function(success, fail) {
    cordova.exec(success, fail, "Discovery", "identify", []);
};

var Discovery = new Discovery();
module.exports = Discovery;