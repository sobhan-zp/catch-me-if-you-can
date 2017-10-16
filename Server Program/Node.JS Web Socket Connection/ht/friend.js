// Friend Management
var db = require("./database");

exports.add_friend = function(userinfo, add_username, fn){
    if (userinfo.username == add_username || add_username == ""){
        var feedback = {
            "code": FRIEND_ADD_FAIL
        };
        return fn(feedback);
    }else{
        var sql_is_in_db = "SELECT friend_id from friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + add_username + "')";
        var sql = "INSERT INTO friend (self_id, friend_id) VALUES (" + userinfo.db_id + ", (SELECT id FROM account WHERE username = '" + add_username + "'))";
        db.execute(sql_is_in_db, 1, 0, function (result) {
            if (result.result.length>0){
                var feedback = {
                    "code": FRIEND_ADD_FAIL
                };
                return fn(feedback);
            }else{
                db.execute(sql, FRIEND_ADD_SUCCESS, FRIEND_ADD_FAIL, function(result){
                    var feedback = {
                        "code": result.code
                    };
                    return fn(feedback);
                });
            }
        });
    }
}

exports.search_user = function(search_user, fn){
    var sql = "SELECT username, email, name, status, location FROM account WHERE username = '" + search_user + "'";
    if (search_user == ""){
        var feedback = {
            "code": FRIEND_SEARCH_FAIL
        };
        return fn(feedback);
    }else{
        db.execute(sql, FRIEND_SEARCH_SUCCESS, FRIEND_SEARCH_FAIL, function(result){
            return fn(result);
        });
    }

}

exports.fetch_friend_list = function(userinfo, fn){
    var sql = "SELECT username FROM account WHERE id IN (SELECT friend_id FROM friend WHERE self_id = " + userinfo.db_id + ")";
    db.execute(sql, FRIEND_GET_SUCCESS, FRIEND_GET_FAIL, function(result){
        return fn(result);
    });
}

exports.delete_friend = function(userinfo, delete_username, fn){
    var sql = "DELETE FROM friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + delete_username + "')";
    db.execute(sql, FRIEND_DELETE_SUCCESS, FRIEND_DELETE_FAIL, function(result){
        var feedback = {
            "code": result.code
        };
        return fn(feedback);
    });
}
