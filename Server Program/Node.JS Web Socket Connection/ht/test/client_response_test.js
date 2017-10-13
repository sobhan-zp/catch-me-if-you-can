require("../constant");
var db = require("../database");
var accounts = require("../account");
var msg = require("../message");
var friend = require("../friend");
var game = require("../game");
var server = require("../server");
var assert = require("assert");
var expect = require('chai').expect;
var WebSocket = require('ws');

process.setMaxListeners(0);

describe('Simulate Client Message Send Test', function() {
    var ws;
    var ws2;
    beforeEach(function () {
        ws = new WebSocket("ws://localhost");
        ws2 = new WebSocket("ws://localhost");
    });
    afterEach(function(){
        ws.close();
        ws2.close();
    })
    describe('Actions Test', function() {
        it('Send invalid JSON messages should return {code:UNKNOWN_ACTION}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('something');
                ws.send('{}');
                ws.send('123');
                ws.send('abc');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', UNKNOWN_ACTION);
                count = count + 1;
                if (count>=4){
                    done();
                }
            });
        });
        it('Send valid JSON but not correct messages should return {code:UNKNOWN_ACTION}', function(done) {
            ws.on('open', function open() {
                ws.send('{"haha": 123}');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', UNKNOWN_ACTION);
                done();
            });
        });
    });
    describe('Login Test', function() {
        it('Send invalid login information should return {code:LOGIN_USER_NON_EXIST_CODE}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"!@#","password":"!@#fasd","action":101}');
                ws.send('{"username":"!@#","password":"","action":101}');
                ws.send('{"username":"","password":"!@#fasd","action":101}');
                ws.send('{"username":"","password":"","action":101}');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', LOGIN_USER_NON_EXIST_CODE);
                count = count + 1;
                if (count>=4){
                    done();
                }
            });
        });

        it('Send login information after login should return {code:UNKNOWN_ACTION}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                setTimeout(function () {
                    ws.send('{"username":"admin","password":"abcd","action":101}');
                }, 50);
                ws.send('{"username":"admin","password":"abcd","action":101}');
            });
            ws.on('message', function incoming(data) {
                count = count + 1;
                if (count>=2){
                    var fm = JSON.parse(JSON.parse(data));
                    expect(fm).to.have.a.property('code', UNKNOWN_ACTION);
                    done();
                }
            });
        });

        it('Send exist login information should return {code:LOGIN_EXIST_CODE}', function(done) {
            ws.on('open', function open() {
                setTimeout(function () {
                    ws2.send('{"username":"admin","password":"abcd","action":101}');
                }, 50);
                ws.send('{"username":"admin","password":"abcd","action":101}');
            });
            ws2.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', LOGIN_EXIST_CODE);
                done();
            });
        });
    });
    describe('Register Test', function() {
        it('Send invalid register information should return {code:REGISTER_FAIL}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"!@#","password":"!@#fasd","name":"","email":"t@t.com","action":100}');
                ws.send('{"username":"!@#","password":"","name":"haha","email":"t@t.com","action":100}');
                ws.send('{"username":"","password":"!@#fasd","name":"haha","email":"","action":100}');
                ws.send('{"username":"","password":"","name":"","email":"","action":100}');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', REGISTER_FAIL);
                count = count + 1;
                if (count>=4){
                    done();
                }
            });
        });
    });
    describe('Friend Test', function() {
        it('Send empty username to search should return {code:FRIEND_SEARCH_FAIL}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"", "action":503}');
                }, 50);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', FRIEND_SEARCH_FAIL);
                    done();
                }
            });
        });

        it('Send non-exist username to search should return {code:FRIEND_SEARCH_SUCCESS, result:[]}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"aas4a4sd4651fwe", "action":503}');
                }, 50);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', FRIEND_SEARCH_SUCCESS);
                    done();
                }
            });
        });

        it('Send correct username to search should return {code:FRIEND_SEARCH_SUCCESS}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"admin", "action":503}');
                    ws.send('{"username":"1", "action":503}');
                }, 50);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', FRIEND_SEARCH_SUCCESS);
                }
                if (count>=3){
                    done();
                }
            });
        });

        it('Get friend list should always return {code:FRIEND_GET_SUCCESS, result:[friend details]}', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":500}');
                }, 50);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', FRIEND_GET_SUCCESS);
                    done();
                }
            });
        });
    });
    describe('Game Test', function() {

    });
    describe('Chat Test', function() {

    });
});
