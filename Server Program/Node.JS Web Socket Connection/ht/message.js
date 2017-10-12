// Message Management
var db = require("./database");
var WebSocket = require('ws');

exports.to_id = function(id, message, clients){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].db_id == id){
            this.to_sock(clients[i].ws, message);
            break;
        }
    }
}

exports.to_sock = function(sock, message){
    if (sock.readyState === WebSocket.OPEN) {
        sock.send(JSON.stringify(message)); // i dont know why it needs twice
        console.log('A system message has been sent');
    }else{
        console.log('Client socket is not ready');
    }
}

exports.to_name = function(userinfo, to_name, message, clients){
    var found = MESSAGE_UNREAD;
    var feedback_from;
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username == to_name){
            found = MESSAGE_READ;
            var feedback_to = {
                "action": MESSAGE_RECEIVE,
                "message": message,
                "from": userinfo.username
            };
            feedback_from = {
                "code": MESSAGE_SEND_SUCCESS_ONLINE
            };
            this.to_sock(clients[i].ws, JSON.stringify(feedback_to));
            //console.log(feedback_to);
            break;
        }
    }
    var sql = "INSERT INTO user_chatlog (content, from_user, to_user, read_status) VALUES ('" + message + "','" + userinfo.username + "', (SELECT id FROM account WHERE username = '" + to_name + "'), " + found + ")";
    db.execute(sql, MESSAGE_SEND_SUCCESS_OFFLINE, MESSAGE_SEND_FAIL, function(result) {
        if (result.code == MESSAGE_SEND_FAIL) {
            feedback_from = {
                "code": MESSAGE_SEND_FAIL
            };
        }
    });
    if (found == MESSAGE_UNREAD){
        feedback_from = {
            "code": MESSAGE_SEND_SUCCESS_OFFLINE
        };
    }
    this.to_sock(userinfo.ws, JSON.stringify(feedback_from));
}

exports.user_command = function(userinfo, to_name, message, clients){
    var found = false;
    var feedback_from;
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username == to_name){
            found = true;
            var feedback_to = {
                "action": MESSAGE_COMMAND_RECEIVE,
                "message": message,
                "from": userinfo.username
            };
            feedback_from = {
                "code": MESSAGE_COMMAND_SUCCESS
            };
            this.to_sock(clients[i].ws, JSON.stringify(feedback_to));
            break;
        }
    }
    if (!found){
        feedback_from = {
            "code": MESSAGE_COMMAND_FAIL
        };
    }
    this.to_sock(userinfo.ws, JSON.stringify(feedback_from));
}

exports.offline_msg_check = function(userinfo){
    var sql = "SELECT * FROM user_chatlog WHERE to_user = " + userinfo.db_id + " and read_status = 0";
    var msg;
    var that = this;
    db.execute(sql, 1, 0, function (result) {
        if (result.result.length>0){
            for (var i=0; i<result.result.length; i++){
                msg = {
                    "action": MESSAGE_RECEIVE,
                    "message": result.result[i].content,
                    "from": result.result[i].from_user
                }
                //msg_set_read(result.result[i].id);
                //this.to_sock(userinfo.ws, JSON.stringify(msg));
            }
        }else{
            msg = {};
        }
        that.to_sock(userinfo.ws, JSON.stringify(msg));
    });

}

function msg_set_read(msg_id){
    var sql = "UPDATE user_chatlog SET read_status = 1 WHERE id = " + msg_id;
    db.execute(sql, 1, 0, function(result){});
}
