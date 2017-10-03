// Constant
DEFAULT_USER_LEVEL = 0;
SERVER_PORT = 80;
REGISTER_ACTION = 100;
LOGIN_ACTION = 101;
UNKNOWN_ACTION = 199;
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
FRIEND_DELETE = 512;
FRIEND_DELETE_SUCCESS = 513;
FRIEND_DELETE_FAIL = 514;
MESSAGE_SEND = 600;
MESSAGE_RECEIVE = 601;
MESSAGE_SEND_SUCCESS_ONLINE = 602;
MESSAGE_SEND_SUCCESS_OFFLINE = 603;
MESSAGE_SEND_FAIL = 604;
MESSAGE_OFFLINE_GET = 605;
MESSAGE_COMMAND_SEND = 606;
MESSAGE_COMMAND_SUCCESS = 607;
MESSAGE_COMMAND_FAIL = 608;
MESSAGE_COMMAND_RECEIVE = 609;
MESSAGE_READ = 1;
MESSAGE_UNREAD = 0;
GAME_CREATE = 700;
GAME_CREATE_SUCCESS = 701;
GAME_CREATE_FAIL = 702;
GAME_ADD = 703;
GAME_ADD_SUCCESS = 704;
GAME_ADD_FAIL = 705;
GAME_EXIT = 706;
GAME_EXIT_SUCCESS = 707;
GAME_EXIT_FAIL = 708;
GAME_GET = 709;
GAME_GET_SUCCESS = 710;
GAME_GET_FAIL = 711;
GAME_DELETE = 712;
GAME_DELETE_SUCCESS = 713;
GAME_DELETE_FAIL = 714;
GAME_USER_REMOVE = 715;
GAME_USER_REMOVE_SUCCESS = 716;
GAME_USER_REMOVE_FAIL = 717;
GAME_GET_CURRENT = 718;
GAME_GET_CURRENT_SUCCESS = 719;
GAME_GET_CURRENT_FAIL = 720;
GAME_NOTIFICATION_SEND = 721;
GAME_NOTIFICATION_RECEIVE = 722;
GAME_GET_USER = 723;
GAME_GET_USER_SUCCESS = 724;
GAME_GET_USER_FAIL = 725;
GAME_OWNER = 1;
GAME_PLAYER = 0;


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

// Master Function

function m_delete_all_games(userinfo){
    var gameinfo = "SELECT * FROM account_in_game";
    con.query(gameinfo, function (error, result) {
        if (result.length>0) {
            for (var i=0; i<result.length; i++){
                delete_game(userinfo, result[i].game_id);
            }
        }
    });
}

// Database Management

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

// Account Management

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
            }else{
                fails = true;
                code = LOGIN_USER_NON_EXIST_CODE;
                var feedback = {
                    "status": "failed",
                    "code": code
                };
            }
            sys_send_to_sock(sock, JSON.stringify(feedback));
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

function register(username, password, email, name, sock){
    var sql = "INSERT INTO account (username, password, email, name, user_level) VALUES ('" + username + "','" + password +"','" + email + "','" + name + "','" + DEFAULT_USER_LEVEL + "')";
    db_excution_send_msg(sock, sql, RESISTER_SUCCESS, REGISTER_FAIL, false);
}

// Friend Management

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

function delete_friend(userinfo, delete_username){
    var sql = "DELETE FROM friend WHERE self_id = " + userinfo.db_id + " and friend_id = (SELECT id FROM account WHERE username = '" + delete_username + "')";
    db_excution_send_msg(userinfo.ws, sql, FRIEND_DELETE_SUCCESS, FRIEND_DELETE_FAIL, false);
}

// Game Management

function create_game(userinfo, name){
    var is_in_game = "SELECT * FROM account_in_game WHERE account_id = " + userinfo.db_id;
    var feedback;
    con.query(is_in_game, function (error, result1) {
        if (result1.length>0) {
            feedback = {
                "status": "failed",
                "code": GAME_CREATE_FAIL
            };
            sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
        }else{
            var sql = "INSERT INTO game (name) VALUES ('" + name + "')";
            con.query(sql, function (err, result) {
                if (err){
                    feedback = {
                        "status": "failed",
                        "code": GAME_CREATE_FAIL
                    };
                }else{
                    feedback = {
                        "status": "success",
                        "code": GAME_CREATE_SUCCESS,
                        "game_id": result.insertId
                    };
                    sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
                    join_game(userinfo, result.insertId, GAME_OWNER);
                }
            });
        }
    });
}

function get_game_list(userinfo){
    var sql = "SELECT * FROM game";
    db_excution_send_msg(userinfo.ws, sql, GAME_GET_SUCCESS, GAME_GET_FAIL, true);
}

function exit_game(userinfo){
    // If the user is owner of game(s), those game(s) will also be deleted.
    var is_owner_in_game = "SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + " and is_owner = " + GAME_OWNER;
    con.query(is_owner_in_game, function (error, result1) {
        if (result1.length>0) {
            for (var i=0; i<result1.length; i++){
                delete_game(userinfo, result1[i].game_id);
            }
        }else{
            var sql = "DELETE FROM account_in_game WHERE account_id = " + userinfo.db_id;
            db_excution_send_msg(userinfo.ws, sql, GAME_EXIT_SUCCESS, GAME_EXIT_FAIL, false);
        }
    });
}

function join_game(userinfo, game_id, is_owner){
    var sql = "INSERT INTO account_in_game (game_id, account_id, is_owner) VALUES (" + game_id + ", " + userinfo.db_id + ", " + is_owner + ")";
    con.query(sql, function (err, result) {
        var feedback;
        if (err){
            feedback = {
                "status": "failed",
                "code": GAME_ADD_FAIL
            };
        }else{
            feedback = {
                "status": "success",
                "code": GAME_ADD_SUCCESS
            };
            send_notification_to_all(userinfo, userinfo.username + " joined the game.");
        }
        sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
    });
}

function delete_game(userinfo, game_id){
    // Delete game will also remove all users in the game
    var sql = "DELETE FROM game WHERE id = " + game_id;
    // Remove owner self
    remove_user_from_game(userinfo, game_id, userinfo.db_id);
    // Remove all other users
    remove_all_user_from_game(userinfo, game_id);
    // Delte game itself
    db_excution_send_msg(userinfo.ws, sql, GAME_DELETE_SUCCESS, GAME_EXIT_FAIL, false);
}

function remove_all_user_from_game(userinfo, game_id){
    var sql = "DELETE FROM account_in_game WHERE game_id = " + game_id + " and is_owner = " + GAME_PLAYER;
    db_excution_send_msg(userinfo.ws, sql, GAME_USER_REMOVE_SUCCESS, GAME_USER_REMOVE_FAIL, false);
}

function remove_user_from_game(userinfo, game_id, userid){
    var sql = "DELETE FROM account_in_game WHERE game_id = " + game_id + " and account_id = " + userid;
    db_excution_send_msg(userinfo.ws, sql, GAME_USER_REMOVE_SUCCESS, GAME_USER_REMOVE_FAIL, false);
}

function get_your_current_game(userinfo){
    var sql = "SELECT * FROM game WHERE id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    db_excution_send_msg(userinfo.ws, sql, GAME_GET_CURRENT_SUCCESS, GAME_GET_CURRENT_FAIL, true);
}

function get_all_game_user(userinfo){
    var sql = "SELECT * FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    db_excution_send_msg(userinfo.ws, sql, GAME_GET_USER_SUCCESS, GAME_GET_USER_FAIL, true);
}

function send_notification_to_all(userinfo, message){
    var sql = "SELECT account_id FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    var notification = {
        "action": GAME_NOTIFICATION_RECEIVE,
        "message": message
    };
    con.query(sql, function (err, result) {
        for (var i=0; i<result.length; i++){
            if (result[i].account_id != userinfo.db_id){
                sys_send_to_id(result[i].account_id, JSON.stringify(notification));
            }
        }
    });
}

// Message Management

function sys_send_to_id(id, message){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].db_id == id){
            sys_send_to_sock(clients[i].ws, message);
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
            //console.log(feedback_to);
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

function user_command(userinfo, to_name, message){
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
                "status": "success",
                "code": MESSAGE_COMMAND_SUCCESS
            };
            sys_send_to_sock(clients[i].ws, JSON.stringify(feedback_to));
            console.log(feedback_to);
            break;
        }
    }
    if (!found){
        feedback_from = {
            "status": "failed",
            "code": MESSAGE_COMMAND_FAIL
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

function invalid_msg(ws){
    var feedback = {
        "status": "unknow",
        "code": UNKNOWN_ACTION
    };
    sys_send_to_sock(ws, JSON.stringify(feedback));
}

function is_json(str) {
    if (typeof str == 'string') {
        try {
            JSON.parse(str);
            return true;
        } catch(e) {
            console.log(e);
            return false;
        }
    }
}

// WebSocket open && stay connecting action
wss.on('connection', function(ws) {
    var client_uuid = uuid.v4();
    var client_ws = ws;
    var user_status = {"login":false,"info":{}};
    console.log('client [%s] connected', client_uuid);

    // WebSocket received message action
    ws.on('message', function(message) {
        if (is_json(message)){
            var data = JSON.parse(message);
            console.log('client [%s] message [%s]', client_uuid, message);
            if (user_status.login == false){
                switch(data.action){
                    case LOGIN_ACTION:
                        login_check(data.username, data.password, client_ws, client_uuid, user_status);
                        break;
                    case REGISTER_ACTION:
                        register(data.username, data.password, data.email, data.name, client_ws);
                        break;
                    default:
                        invalid_msg(client_ws);
                }
            }else{
                switch(data.action){
                    case MESSAGE_SEND:
                        user_send_to_name(user_status.info, data.username, data.message);
                        break;
                    case FRIEND_GET:
                        fetch_friend_list(user_status.info);
                        break;
                    case FRIEND_SEARCH:
                        search_user(user_status.info, data.username);
                        break;
                    case FRIEND_ADD:
                        add_friend(user_status.info, data.username);
                        break;
                    case FRIEND_CHECK:
                        check_online(user_status.info, data.username);
                        break;
                    case FRIEND_DELETE:
                        delete_friend(user_status.info, data.username);
                        break;
                    case MESSAGE_OFFLINE_GET:
                        offline_msg_check(user_status.info);
                        break;
                    case MESSAGE_COMMAND_SEND:
                        user_command(user_status.info, data.username, data.message);
                        break;
                    case GAME_ADD:
                        join_game(user_status.info, data.id, GAME_PLAYER);
                        break;
                    case GAME_CREATE:
                        create_game(user_status.info, data.name);
                        break;
                    case GAME_EXIT:
                        exit_game(user_status.info);
                        break;
                    case GAME_GET:
                        get_game_list(user_status.info);
                        break;
                    case GAME_GET_USER:
                        get_all_game_user(user_status.info);
                        break;
                    case GAME_GET_CURRENT:
                        get_your_current_game(user_status.info);
                        break;
                    case GAME_NOTIFICATION_SEND:
                        send_notification_to_all(user_status.info, data.message);
                        break;
                    //case GAME_DELETE:
                        //delete_game(user_status.info, data.id);
                        //break;
                    default:
                        invalid_msg(user_status.info.ws);
                }
            }
        }else{
            invalid_msg(user_status.info);
        }
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
