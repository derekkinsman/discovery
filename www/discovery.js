var Discovery = function() {};

Discovery.prototype.say = function(success, fail) {
    cordova.exec(success, fail, "Discovery", "identify", []);
};

var discovery = new Discovery();
module.exports = discovery;