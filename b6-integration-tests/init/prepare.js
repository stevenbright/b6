'use strict';

var stage = casper.cli.get('stage'),
    utils = require('utils');

var config = utils.mergeObjects({
    // TODO: default settings overridable by stage should go here
}, require('../init/setting/' + stage));

casper.options.verbose = true;
casper.options.logLevel = 'info';

function getTestUrl() {
  return 'http://getbootstrap.com/2.3.2/javascript.html#dropdowns';
}

casper.options.viewportSize = {
  width: 1280,
  height: 760
};

casper.on('page.error', function(msg, trace) {
  this.echo(msg, 'ERROR');
});

casper.on('remote.message', function(msg) {
  this.echo(msg, 'INFO');
});

casper.test.done();
