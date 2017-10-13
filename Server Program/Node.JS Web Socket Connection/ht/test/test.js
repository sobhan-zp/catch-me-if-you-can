require("../constant");
var db = require("../database");
var accounts = require("../account");
var msg = require("../message");
var friend = require("../friend");
var game = require("../game");
var assert = require("assert");
var expect = require('chai').expect;
var WebSocket = require('ws');



describe('Message Process Test (Open Server Program Is Needed)', function() {
    var ws;
    beforeEach(function () {
        ws = new WebSocket("ws://localhost");
    });
    afterEach(function(){
        ws.close();
    })
    it('Send invalid JSON messages', function(done) {
        var count = 0;
        ws.on('open', function open() {
            ws.send('something');
            ws.send('{}');
            ws.send('123');
            ws.send('abc');
        });
        ws.on('message', function incoming(data) {
            var fm = JSON.parse(JSON.parse(data));
            expect(fm).to.have.a.property('code', 199);
            count = count + 1;
            if (count>=4){
                done();
            }
        });
    });

    it('Send valid JSON but not correct messages ', function(done) {
        ws.on('open', function open() {
            ws.send('{"haha": 123}');
        });
        ws.on('message', function incoming(data) {
            var fm = JSON.parse(JSON.parse(data));
            expect(fm).to.have.a.property('code', 199);
            done();
        });
    });
});
