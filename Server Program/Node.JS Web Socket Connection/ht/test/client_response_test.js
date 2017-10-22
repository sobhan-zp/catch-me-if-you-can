// Testing users:
// admin   abcd
// 1       2
// Following users should not in database:
// aas4a4sd4651fwe

// Delay multiples, e.g. default delay is 5ms, now becomes 5*500 = 500ms
TEST_DELAY_TIMES = 100;

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
                expect(fm).to.have.a.property('code', UNKNOWN_ACTION);
                count = count + 1;
                if (count>=4){
                    done();
                }
            });
        });
        it('Send valid JSON but not correct messages', function(done) {
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
        it('Send invalid login information', function(done) {
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

        it('Send login information after login', function(done) {
            var count = 0;
            ws.on('open', function open() {
                setTimeout(function () {
                    ws.send('{"username":"admin","password":"abcd","action":101}');
                }, 1*TEST_DELAY_TIMES);
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

        it('Send exist login information', function(done) {
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
            });
            ws2.on('open', function open() {
                setTimeout(function () {
                    ws2.send('{"username":"admin","password":"abcd","action":101}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws2.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', LOGIN_EXIST_CODE);
                done();
            });
        });
    });
    describe('Register Test', function() {
        it('Send invalid register information', function(done) {
            var count = 0;
            ws.on('open', function open() {
                // no name
                ws.send('{"username":"!@#","password":"!@#fasd","name":"","email":"t@t.com","action":100}');
                // no password
                ws.send('{"username":"!@#","password":"","name":"haha","email":"t@t.com","action":100}');
                // no username
                ws.send('{"username":"","password":"!@#fasd","name":"haha","email":"","action":100}');
                // no all
                ws.send('{"username":"","password":"","name":"","email":"","action":100}');
                // missing property
                ws.send('{"name":"","email":"","action":100}');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                expect(fm).to.have.a.property('code', REGISTER_FAIL);
                count = count + 1;
                if (count>=5){
                    done();
                }
            });
        });
    });
    describe('Friend Test', function() {
        it('Send empty username to search', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"", "action":503}');
                }, 1*TEST_DELAY_TIMES);
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

        it('Send non-exist username to search', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"aas4a4sd4651fwe", "action":503}');
                }, 1*TEST_DELAY_TIMES);
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

        it('Send correct username to search', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"admin", "action":503}');
                    ws.send('{"username":"1", "action":503}');
                }, 1*TEST_DELAY_TIMES);
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

        it('Get friend list test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":500}');
                }, 1*TEST_DELAY_TIMES);
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
        it('Create a game multiple times', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    // Exit current all game
                    ws.send('{"action":706}');
                }, 1*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":700,"name":"hello??"}');
                }, 2*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":700,"name":"hello??"}');
                }, 3*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count==3){
                    expect(fm).to.have.a.property('code', GAME_CREATE_SUCCESS);
                    expect(fm).to.have.a.property('game_id');
                }
                if (count==4){
                    expect(fm).to.have.a.property('code', GAME_ADD_SUCCESS);
                }
                if (count>=5){
                    expect(fm).to.have.a.property('code', GAME_CREATE_FAIL);
                    done();
                }
            });
        });
        it('Get joined game test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    // Exit current all game
                    ws.send('{"action":706}');
                }, 1*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":718}');
                }, 2*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=3){
                    expect(fm).to.have.a.property('code', GAME_GET_CURRENT_FAIL);
                    done();
                }
            });
        });
        it('Get all game test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":709}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('result');
                    expect(fm).to.have.a.property('code', GAME_GET_SUCCESS);
                    done();
                }
            });
        });
        it('Get gaming users test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":723}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('result');
                    expect(fm).to.have.a.property('code', GAME_GET_USER_SUCCESS);
                    done();
                }
            });
        });
        it('Exit game test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":706}');
                }, 1*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":700,"name":"hello??"}');
                }, 2*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":706}');
                }, 3*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=5){
                    expect(fm).to.have.a.property('code', GAME_EXIT_SUCCESS);
                    done();
                }
            });
        });
        it('Location send test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":610, "location":{"x":1.0,"y":2.0}}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', LOCATION_SUCCESS);
                    done();
                }
            });
        });
        it('Location get test', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"action":706}');
                }, 1*TEST_DELAY_TIMES);
                setTimeout(function () {
                    // no joined game return nothing in result
                    ws.send('{"action":613}');
                }, 2*TEST_DELAY_TIMES);
                setTimeout(function () {
                    // create a game
                    ws.send('{"action":700,"name":"hello??"}');
                }, 3*TEST_DELAY_TIMES);
                setTimeout(function () {
                    ws.send('{"action":613}');
                }, 4*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count==3){
                    expect(fm).to.have.a.property('code', LOCATION_GET_SUCCESS);
                    expect(fm).to.have.a.property('result');
                }
                if (count>=6){
                    expect(fm).to.have.a.property('code', LOCATION_GET_SUCCESS);
                    expect(fm).to.have.a.property('result');
                    expect(fm.result.length).to.be.equal(0);
                    done();
                }
            });
        });
    });
    describe('Chat Test', function() {
        it('Send message to offline user', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"1","message":"abcd","action":600}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', MESSAGE_SEND_SUCCESS_OFFLINE);
                    done();
                }
            });
        });
        it('Send message to online user', function(done) {
            var count = 0;
            ws.on('open', function open() {
                ws.send('{"username":"admin","password":"abcd","action":101}');
                setTimeout(function () {
                    ws.send('{"username":"1","message":"abcd","action":600}');
                }, 1*TEST_DELAY_TIMES);
            });
            ws2.on('open', function open() {
                ws2.send('{"username":"1","password":"2","action":101}');
            });
            ws.on('message', function incoming(data) {
                var fm = JSON.parse(JSON.parse(data));
                count = count + 1;
                if (count>=2){
                    expect(fm).to.have.a.property('code', MESSAGE_SEND_SUCCESS_ONLINE);
                    done();
                }
            });
        });
    });
});
