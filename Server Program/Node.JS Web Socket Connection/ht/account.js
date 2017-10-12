// Account Management
var db = require("./database");
var msg = require("./message");

exports.fetch_account_info = function(userinfo){
    var feedback = {
        "action": PROFILE_GET,
        "name": userinfo.name,
        "username": userinfo.username,
        "email": userinfo.email,
        "id": userinfo.db_id,
        "lv": userinfo.lv,
        "location": userinfo.location,
        "status": userinfo.status
    }
    msg.to_sock(userinfo.ws, JSON.stringify(feedback));
}

exports.update_user_infor = function(userinfo, name, email, location, status, fn){
    var sql = "UPDATE account SET name = '"+name+"', email = '"+email+"', location = '"+location+"', status = '"+status+"' WHERE id = " + userinfo.db_id;
    db.execute(sql, PROFILE_UPDATE_SUCCESS, PROFILE_UPDATE_FAIL, function(result){
        var feedback = {
            "code": result.code
        };
        return fn(feedback);
    });
}

exports.login_check = function(username, password, sock, client_uuid, user_status, clients, fn){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username === username){
            var feedback = {
                "code": LOGIN_EXIST_CODE
            };
            return fn(feedback);
        }
    }
    var sql = "SELECT * FROM account where username = '" + username + "' and password = '" + password + "'";
    db.execute(sql, LOGIN_SUCCESS_CODE, LOGIN_USER_NON_EXIST_CODE, function(result){
        if (result.result.length>0){
            var temp_info = {
                "id": client_uuid,
                "ws": sock,
                "username": result.result[0]['username'],
                "name": result.result[0]['name'],
                "email": result.result[0]['email'],
                "db_id": result.result[0]['id'],
                "lv": result.result[0]['user_level'],
                "location": result.result[0]['location'],
                "status": result.result[0]['status']
            };
            var feedback = {
                "code": result.code
            };
            user_status.login = true;
            user_status.info = temp_info;
            console.log(result.result[0]['username']);
            clients.push(temp_info);
        }
        return fn(feedback);
    });
}

exports.check_online = function(username, clients, fn){
    var feedback = {};
    var online = false;
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username == username) {
            feedback.code = FRIEND_CHECK_SUCCESS
            online = true;
            break;
        }else{
            feedback.code = FRIEND_CHECK_FAIL;
        }
    }
    return fn(feedback);
}

exports.register = function(username, password, email, name, sock, fn){
    if (username == "" || password == "" || email == "" || name == ""){
        var feedback = {
            "code": REGISTER_FAIL
        }
        return fn(feedback);
    }
    var sql = "INSERT INTO account (username, password, email, name, user_level) VALUES ('" + username + "','" + password +"','" + email + "','" + name + "','" + DEFAULT_USER_LEVEL + "')";
    db.execute(sql, RESISTER_SUCCESS, REGISTER_FAIL, function(result){
        var feedback = {
            "code": result.code
        }
        return fn(result);
    });
}
