// Constant
var SERVER_PORT = 80

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
    database: "housetarth"
});
var clients = [];

console.log('Server Starts At Port %d', SERVER_PORT);

con.connect(function(err) {
    if (err) {
        console.log('Database Connection Failed');
        console.log('%s', err);
        process.exit();
    }
    console.log("Database Connected");

});

con.query("SELECT * FROM test", function (err, result, fields) {
    if (err) throw err;
    console.log("Result:%s",JSON.stringify(result));
});


function ws_send_to(from_uuid, to_uuid, message){

}

function ws_send_all(type, client_uuid, message) {
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
    clients.push({ "id": client_uuid, "ws": ws});
    console.log('client [%s] connected', client_uuid);

    // WebSocket received message action
    ws.on('message', function(message) {
        console.log('client [%s] message [%s]', client_uuid, message);
    });

    // A client disconnected
    // Delete its ID from client array
    var closeSocket = function(customMessage) {
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
