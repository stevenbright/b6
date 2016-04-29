'use strict';

casper.test.begin('a twitter bootstrap dropdown can be opened', 2, function(test) {
    casper.start(getTestUrl(), function() {
        test.assertExists('#navbar-example');
        this.click('#dropdowns .nav-pills .dropdown:last-of-type a.dropdown-toggle');
        this.waitUntilVisible('#dropdowns .nav-pills .open', function() {
            test.pass('Dropdown is open');
        });
    }).run(function() {
        test.done();
    });
});

