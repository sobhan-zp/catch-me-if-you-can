// Friend Management
var db = require("./database");

// Add a friend
exports.add_friend = function(userinfo, add_username, fn){
    if (userinfo.username == add_username || add_username == ""){
        var feedback = {
            "code": FRIEND_ADD_FAIL
        };
        return fn(feedback);
    }else{
        var sql_is_in_db = "SELECT friend_id from friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + add_username + "')";
        var sql = "INSERT INTO friend (self_id, friend_id) VALUES (" + userinfo.db_id + ", (SELECT id FROM account WHERE username = '" + add_username + "'))";
        var sql2 = "INSERT INTO friend (friend_id, self_id) VALUES (" + userinfo.db_id + ", (SELECT id FROM account WHERE username = '" + add_username + "'))";
        db.execute(sql_is_in_db, 1, 0, function (result) {
            if (result.result.length>0){
                var feedback = {
                    "code": FRIEND_ADD_FAIL
                };
                return fn(feedback);
            }else{
                db.execute(sql, FRIEND_ADD_SUCCESS, FRIEND_ADD_FAIL, function(result2){
                    db.execute(sql2, FRIEND_ADD_SUCCESS, FRIEND_ADD_FAIL, function(result3){
                        var feedback = {
                            "code": result2.code
                        };
                        return fn(feedback);
                    });
                });
            }
        });
    }
}

// Search a userprofile using username
exports.search_user = function(search_user, fn){
    if (search_user == ""){
        var feedback = {
            "code": FRIEND_SEARCH_FAIL
        };
        return fn(feedback);
    }else{
        var columns = "username, email, name, status, location";
        var table = "account";
        var condition = {
            username: search_user
        };
        db.select(condition, columns, table, FRIEND_SEARCH_SUCCESS, FRIEND_SEARCH_FAIL, function(result){
            return fn(result);
        });
    }
}

// Search a userprofile using id
exports.search_user_id = function(id, fn){
    if (id == ""){
        var feedback = {
            "code": FRIEND_SEARCH_FAIL
        };
        return fn(feedback);
    }else{
        var columns = "username, email, name, status, location";
        var table = "account";
        var condition = {
            id: id
        };
        db.select(condition, columns, table, FRIEND_SEARCH_SUCCESS, FRIEND_SEARCH_FAIL, function(result){
            return fn(result);
        });
    }
}

// Get friend list
exports.fetch_friend_list = function(userinfo, fn){
    var sql = "SELECT username FROM account WHERE id IN (SELECT friend_id FROM friend WHERE self_id = " + userinfo.db_id + ")";
    db.execute(sql, FRIEND_GET_SUCCESS, FRIEND_GET_FAIL, function(result){
        return fn(result);
    });
}

// Delete a friend
exports.delete_friend = function(userinfo, delete_username, fn){
    var sql = "DELETE FROM friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + delete_username + "')";
    db.execute(sql, FRIEND_DELETE_SUCCESS, FRIEND_DELETE_FAIL, function(result){
        var feedback = {
            "code": result.code
        };
        return fn(feedback);
    });
}
