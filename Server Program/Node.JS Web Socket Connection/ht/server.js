// Constant
DEFAULT_USER_LEVEL = 0;
SERVER_PORT = 80;
REGISTER_ACTION = 100;
LOGIN_ACTION = 101;
MESSAGE_ACTION = 102;
LOGIN_SUCCESS_CODE = 200;
LOGIN_USER_NON_EXIST_CODE = 201;
LOGIN_EXIST_CODE = 202;
RESISTER_SUCCESS = 300;
REGISTER_FAIL = 301;

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


function login_check(username, password, sock, client_uuid){
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
                clients.push(temp_info);
                sys_send_to_id(client_uuid, JSON.stringify(feedback));
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
        sys_send_to_id(client_uuid, JSON.stringify(feedback));
    }
}


function register(username, password, email, name, sock){
    var sql = "INSERT INTO account (username, password, email, name, user_level) VALUES ('" + username + "','" + password +"','" + email + "','" + name + "','" + DEFAULT_USER_LEVEL + "')";
    con.query(sql, function (err, result) {
        var feedback;
        if (err){
            feedback = {
                "status": "failed",
                "code": REGISTER_FAIL
            };
        }else{
            feedback = {
                "status": "success",
                "code": RESISTER_SUCCESS
            };
        }
        sys_send_to_sock(sock, JSON.stringify(feedback));
    });
}

function sys_send_to_id(id, message){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].id = id){
            var clientSocket = clients[i].ws;
            if (clientSocket.readyState === WebSocket.OPEN) {
                clientSocket.send(JSON.stringify({
                    "type": "notification",
                    "id": id,
                    "message": message
                }));
            }
            console.log('A system message has been sent');
            break;
        }
    }
}

function sys_send_to_sock(sock, message){
        if (sock.readyState === WebSocket.OPEN) {
            sock.send(JSON.stringify({
                "type": "notification",
                "message": message
            }));
        }
        console.log('A system message has been sent');
}

function user_send_to_name(from_id, to_name, message){
    for (var i = 0; i < clients.length; i++) {
        if (clients[i].username = to_name){
            var clientSocket = clients[i].ws;
            if (clientSocket.readyState === WebSocket.OPEN) {
                clientSocket.send(JSON.stringify({
                    "type": "notification",
                    "from": from_id,
                    "message": message
                }));
            }
            break;
        }
    }
}

function msg_send_all(type, client_uuid, message) {
    for (var i = 0; i < clients.length; i++) {
        var clientSocket = clients[i].ws;
        if (clientSocket.readyState === WebSocket.OPEN) {
            clientSocket.send(JSON.stringify({
                "type": type,
                "id": client_uuid,
                "message": message
            }));
        }
    }
}

// WebSocket open && stay connecting action
wss.on('connection', function(ws) {
    var client_uuid = uuid.v4();
    //clients.push({ "id": client_uuid, "ws": ws});
    console.log('client [%s] connected', client_uuid);

    // WebSocket received message action
    ws.on('message', function(message) {
        var data = JSON.parse(message);
        console.log('client [%s] message [%s]', client_uuid, message);
        if (data.action == LOGIN_ACTION){
            login_check(data.username, data.password, ws, client_uuid);
        }else if (data.action == REGISTER_ACTION){
            register(data.username, data.password, data.email, data.name, ws);
        }else if (data.action == MESSAGE_ACTION){
            register(data.username, data.password, data.email, data.name, ws);
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
