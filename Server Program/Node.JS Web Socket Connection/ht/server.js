// Constant
DEFAULT_USER_LEVEL = 0;
SERVER_PORT = 80;
REGISTER_ACTION = 100;
LOGIN_ACTION = 101;
LOGIN_SUCCESS_CODE = 200;
LOGIN_USER_NON_EXIST_CODE = 201;
LOGIN_EXIST_CODE = 202;
RESISTER_SUCCESS = 300;
REGISTER_FAIL = 301;
FRIEND_GET = 500;
FRIEND_GET_FAIL = 501;
FRIEND_GET_SUCCESS = 502;
FRIEND_SEARCH = 503;
FRIEND_SEARCH_FAIL = 504;
FRIEND_SEARCH_SUCCESS = 505;
FRIEND_ADD = 506;
FRIEND_ADD_FAIL = 507;
FRIEND_ADD_SUCCESS = 508;
FRIEND_CHECK = 509;
FRIEND_CHECK_FAIL = 510;
FRIEND_CHECK_SUCCESS = 511;
MESSAGE_SEND = 600;
MESSAGE_RECEIVE = 601;
MESSAGE_SEND_SUCCESS_ONLINE = 602;
MESSAGE_SEND_SUCCESS_OFFLINE = 603;
MESSAGE_SEND_FAIL = 604;
MESSAGE_OFFLINE_GET = 605;
MESSAGE_READ = 1;
MESSAGE_UNREAD = 0;

TEST_MSG = 1000;

// Requires
var WebSocket = require('ws');
var uuid = require('node-uuid');
var mysql = require('mysql');

// Variables
var WebSocketServer = WebSocket.Server,
    wss = new WebSocketServer({ port: SERVER_PORT });
var con = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "ht!!!",
    database: "house_tarth"
});
var clients = [];

console.log('Server Starts At Port %d', SERVER_PORT);
set_database_con();

function set_database_con(){
    con.connect(function(err) {
        if (err) {
            console.log('Database Connection Failed');
            console.log('%s', err);
            process.exit();
        }
        console.log("Database Connected");
    });
}

function login_check(username, password, sock, client_uuid, user_status){
    var fails = false;
    var code = LOGIN_SUCCESS_CODE;
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username === username){
            fails = true;
            code = LOGIN_EXIST_CODE;
            break;
        }
    }
    if (!fails){
        var sql = "SELECT * FROM account where username = '" + username + "' and password = '" + password + "'";
        con.query(sql, function (err, result, rows, fields) {
            if (err) throw err;
            if (result.length>0){
                var temp_info = {
                    "id": client_uuid,
                    "ws": sock,
                    "username": result[0]['username'],
                    "name": result[0]['name'],
                    "email": result[0]['email'],
                    "db_id": result[0]['id'],
                    "lv": result[0]['user_level']
                };
                var feedback = {
                    "status": "success",
                    "code": code
                };
                user_status.login = true;
                user_status.info = temp_info;
                clients.push(temp_info);
                sys_send_to_sock(sock, JSON.stringify(feedback));
            }else{
                fails = true;
                code = LOGIN_USER_NON_EXIST_CODE;
            }
        });
    }
    if (fails){
        var feedback = {
            "status": "failed",
            "code": code
        };
        sys_send_to_sock(sock, JSON.stringify(feedback));
    }
}

function db_excution_send_msg(sock, sql_statement, success_code, fail_code, return_result){
    con.query(sql_statement, function (err, result) {
        var feedback;
        if (err){
            feedback = {
                "status": "failed",
                "code": fail_code
            };
        }else{
            if (return_result){
                feedback = {
                    "status": "success",
                    "code": success_code,
                    "result": result
                };
            }else{
                feedback = {
                    "status": "success",
                    "code": success_code
                };
            }

        }
        sys_send_to_sock(sock, JSON.stringify(feedback));
        console.log(JSON.stringify(result));
    });
}

function check_online(userinfo, username){
    var feedback;
    var online = false;
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username == username) {
            feedback = {
                "status": "success",
                "code": FRIEND_CHECK_SUCCESS
            };
            sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
            online = true;
            break;
        }
    }
    if (!online){
        feedback = {
            "status": "failed",
            "code": FRIEND_CHECK_FAIL
        };
        sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
    }
}

function add_friend(userinfo, add_username){
    var sql_is_in_db = "SELECT friend_id from friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + add_username + "')";
    var sql = "INSERT INTO friend (self_id, friend_id) VALUES (" + userinfo.db_id + ", (SELECT id FROM account WHERE username = '" + add_username + "'))";
    con.query(sql_is_in_db, function (err, result) {
        if (result.length>0){
            var feedback = {
                "status": "failed",
                "code": FRIEND_ADD_FAIL
            };
            sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
            console.log(JSON.stringify(result));
        }else{
            db_excution_send_msg(userinfo.ws, sql, FRIEND_ADD_SUCCESS, FRIEND_ADD_FAIL, false);
        }
    });
}

function search_user(userinfo, search_user){
    var sql = "SELECT username, email, name FROM account WHERE username = '" + search_user + "'";
    db_excution_send_msg(userinfo.ws, sql, FRIEND_SEARCH_SUCCESS, FRIEND_SEARCH_FAIL, true);
}

function fetch_friend_list(userinfo){
    var sql = "SELECT username FROM account WHERE id IN (SELECT friend_id FROM friend WHERE self_id = " + userinfo.db_id + ")";
    db_excution_send_msg(userinfo.ws, sql, FRIEND_GET_SUCCESS, FRIEND_GET_FAIL, true);
}

function register(username, password, email, name, sock){
    var sql = "INSERT INTO account (username, password, email, name, user_level) VALUES ('" + username + "','" + password +"','" + email + "','" + name + "','" + DEFAULT_USER_LEVEL + "')";
    db_excution_send_msg(sock, sql, RESISTER_SUCCESS, REGISTER_FAIL, false);
}

function sys_send_to_id(id, message){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].id == id){
            var clientSocket = clients[i].ws;
            if (clientSocket.readyState === WebSocket.OPEN) {
                clientSocket.send(JSON.stringify(message));
            }
            console.log('A system message has been sent');
            break;
        }
    }
}

function sys_send_to_sock(sock, message){
        if (sock.readyState === WebSocket.OPEN) {
            sock.send(JSON.stringify(message)); // i dont know why it needs twice
            console.log('A system message has been sent');
        }else{
            console.log('Client socket is not ready');
        }

}

function user_send_to_name(userinfo, to_name, message){
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
                "status": "success",
                "code": MESSAGE_SEND_SUCCESS_ONLINE
            };
            sys_send_to_sock(clients[i].ws, JSON.stringify(feedback_to));
            console.log(feedback_to);
            break;
        }
    }
    var sql = "INSERT INTO user_chatlog (content, from_user, to_user, read_status) VALUES ('" + message + "','" + userinfo.username + "', (SELECT id FROM account WHERE username = '" + to_name + "'), " + found + ")";
    con.query(sql, function (err, result) {
        if (err) {
            feedback_from = {
                "status": "success",
                "code": MESSAGE_SEND_FAIL
            };
        }
    });
    if (found == MESSAGE_UNREAD){
        feedback_from = {
            "status": "success",
            "code": MESSAGE_SEND_SUCCESS_OFFLINE
        };
    }
    console.log(feedback_from);
    sys_send_to_sock(userinfo.ws, JSON.stringify(feedback_from));
}

function msg_send_all(type, client_uuid, message) {
    for (var i = 0; i < clients.length; i++) {
        var clientSocket = clients[i].ws;
        if (clientSocket.readyState === WebSocket.OPEN) {
            clientSocket.send(JSON.stringify(message));
        }
    }
}

function offline_msg_check(userinfo){
    var sql = "SELECT * FROM user_chatlog WHERE to_user = " + userinfo.db_id + " and read_status = 0";
    con.query(sql, function (err, result) {
        console.log(JSON.stringify(result));
        if (result.length>0){
            for (var i=0; i<result.length; i++){
                var msg = {
                    "action": MESSAGE_RECEIVE,
                    "message": result[i].content,
                    "from": result[i].from_user
                }
                msg_set_read(result[i].id);
                sys_send_to_sock(userinfo.ws, JSON.stringify(msg));
            }
        }else{
            sys_send_to_sock(userinfo.ws, JSON.stringify([]));
        }

    });
}

function msg_set_read(msg_id){
    var sql = "UPDATE user_chatlog SET read_status = 1 WHERE id = " + msg_id;
    con.query(sql, function (err, result) {
        if (err) {
            console.log(err)
        }
    });
}

// WebSocket open && stay connecting action
wss.on('connection', function(ws) {
    var client_uuid = uuid.v4();
    var client_ws = ws;
    var user_status = {"login":false,"info":{}};
    //clients.push({ "id": client_uuid, "ws": ws});
    console.log('client [%s] connected', client_uuid);

    // WebSocket received message action
    ws.on('message', function(message) {
        var data = JSON.parse(message);
        console.log('client [%s] message [%s]', client_uuid, message);
        if (user_status.login == false){
            if (data.action == LOGIN_ACTION){
                login_check(data.username, data.password, client_ws, client_uuid, user_status);
            }else if (data.action == REGISTER_ACTION){
                register(data.username, data.password, data.email, data.name, client_ws);
            }
        }else{
            if (data.action == MESSAGE_SEND){
                user_send_to_name(user_status.info, data.username, data.message);
            }else if (data.action == FRIEND_GET){
                fetch_friend_list(user_status.info);
            }else if (data.action == FRIEND_SEARCH){
                search_user(user_status.info, data.username);
            }else if (data.action == FRIEND_ADD){
                add_friend(user_status.info, data.username);
            }else if (data.action == FRIEND_CHECK){
                check_online(user_status.info, data.username);
            }else if (data.action == MESSAGE_OFFLINE_GET){
                offline_msg_check(user_status.info);
            }
        }
        //console.log('Status [%s] [%s]', user_status.login, user_status.info.id);
    });

    // A client disconnected
    // Delete its ID from client array
    var closeSocket = function() {
        for (var i = 0; i < clients.length; i++) {
            if (clients[i].id == client_uuid) {
                console.log('client [%s] disconnected', client_uuid);
                clients.splice(i, 1);
            }
        }
    };

    // WebSocket close action
    ws.on('close', function () {
        closeSocket();
    });

    // Server close action
    process.on('SIGINT', function () {
        console.log("Server Closing.....");
        closeSocket('Server has disconnected');
        process.exit();
    });
});
