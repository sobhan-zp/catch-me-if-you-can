require("../constant");
var db = require("../database");
var accounts = require("../account");
var msg = require("../message");
var friend = require("../friend");
var game = require("../game");
var assert = require("assert");
var expect = require('chai').expect;

SUCCESS = 1;
FAIL = 0;

describe('JS Functions Test', function() {
    // database.js test
    describe('SQL Statement Execution Test', function() {
        it('SELECT all item in the account', function(done) {
            db.execute("SELECT * FROM account", SUCCESS, FAIL, function(result1){
                expect(result1).to.be.an('object');
                done();
            });

        });
        it('SELECT an not exist item in account', function(done) {
            db.execute("SELECT * FROM account WHERE id = 'abc'", SUCCESS, FAIL, function(result1){
                expect(result1.code).to.be.equal(SUCCESS);
                done();
            });
        });
        it('Execution an empty string', function(done) {
            db.execute("", SUCCESS, FAIL, function(result1){
                expect(result1.code).to.be.equal(FAIL);
                done();
            });
        });
        it('Execution an invalid SQL string', function(done) {
            db.execute("SELECT * FR", SUCCESS, FAIL, function(result1){
                expect(result1).to.have.a.property('code', 0);
                done();
            });
        });
    });

    // account.js test
    describe('Login Test', function() {
        it('Login with an invalid (not exist) username should return NON-EXIST', function(done) {
            var fake_info = {"login":false,"info":{}};
            var fake_clinets = [];
            accounts.login_check("ddddd", "123", "sock", "client_uuid", fake_info, fake_clinets, function(result1){
                expect(result1).to.have.a.property('code', LOGIN_USER_NON_EXIST_CODE);
                done();
            });
        });
        it('Login with empty username and password should return FAIL', function(done) {
            var fake_info = {"login":false,"info":{}};
            var fake_clinets = [];
            accounts.login_check("", "", "sock", "client_uuid", fake_info, fake_clinets, function(result1){
                expect(result1).to.have.a.property('code', LOGIN_USER_NON_EXIST_CODE);
                done();
            });
        });
        it('Login with right username and password should return SUCCESS', function(done) {
            var fake_info = {"login":false,"info":{}};
            var fake_clinets = [];
            accounts.login_check("admin", "abcd", "sock", "client_uuid", fake_info, fake_clinets, function(result1){
                expect(result1).to.have.a.property('code', LOGIN_SUCCESS_CODE);
                done();
            });
        });
    });

    describe('Register Test', function() {
        it('Register with an empty username should return FAIL', function(done) {
            accounts.register("", "password", "email", "name", "sock", function(result1){
                expect(result1).to.have.a.property('code', REGISTER_FAIL);
                done();
            });
        });
        it('Register with exist username should return FAIL', function(done) {
            accounts.register("admin", "password", "email", "name", "sock", function(result1){
                expect(result1).to.have.a.property('code', REGISTER_FAIL);
                done();
            });
        });
        // it('Register with non-exist username and password should return SUCCESS', function(done) {
        //     accounts.register("66333", "password", "email", "name", "sock", function(result1){
        //         expect(result1).to.have.a.property('code', RESISTER_SUCCESS);
        //         done();
        //     });
        // });
    });

    // friend.js test
    describe('Friend Search Test', function() {
        it('Search with a valid (exist) user should return SUCCESS', function(done) {
            friend.search_user("admin", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_SEARCH_SUCCESS);
                done();
            });
        });
        it('Search with an invalid (non-exist) user should return SUCCESS but empty array', function(done) {
            friend.search_user("admissssssn", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_SEARCH_SUCCESS);
                done();
            });
        });
        it('Search with an empty username should return FAIL', function(done) {
            friend.search_user("", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_SEARCH_FAIL);
                done();
            });
        });
    });
    describe('Friend Add Test', function() {
        // it('Add with a valid (exist) user should return SUCCESS', function(done) {
        //     var fake_info = {
        //         "db_id":1
        //     }
        //     friend.add_friend(fake_info, "admin", function(result1){
        //         expect(result1).to.have.a.property('code', FRIEND_ADD_SUCCESS);
        //         done();
        //     });
        // });
        it('Add with non-exist user should return FAIL', function(done) {
            var fake_info = {
                "db_id":1
            }
            friend.add_friend(fake_info, "imnotexist99999", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_ADD_FAIL);
                done();
            });
        });
        it('Add with an empty user should return FAIL', function(done) {
            var fake_info = {
                "db_id":1
            }
            friend.add_friend(fake_info, "", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_ADD_FAIL);
                done();
            });
        });
        it('Add with self should return FAIL', function(done) {
            var fake_info = {
                "db_id":1,
                "username": "fakeusername"
            }
            friend.add_friend(fake_info, "fakeusername", function(result1){
                expect(result1).to.have.a.property('code', FRIEND_ADD_FAIL);
                done();
            });
        });
    });
});
