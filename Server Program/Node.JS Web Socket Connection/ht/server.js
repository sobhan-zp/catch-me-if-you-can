// Requires
require("./constant");
var WebSocket = require('ws');
var uuid = require('node-uuid');
var async = require('async');
var db = require("./database");
var accounts = require("./account");
var msg = require("./message");
var friend = require("./friend");
var game = require("./game");

// Remove max listeners
process.setMaxListeners(0);

// Variables
var WebSocketServer = WebSocket.Server,
    wss = new WebSocketServer({ port: SERVER_PORT });
var clients = [];

//console.log('Server Starts At Port %d', SERVER_PORT);
db.set_database_con();

function invalid_msg(ws){
    var feedback = {
        "code": UNKNOWN_ACTION
    };
    msg.to_sock(ws, JSON.stringify(feedback));
}

function is_json(str) {
    if (typeof str == 'string') {
        try {
            JSON.parse(str);
            return true;
        } catch(e) {
            //console.log("Received a non-JSON message");
            return false;
        }
    }
}

// WebSocket open && stay connecting action
wss.on('connection', function(ws) {
    var client_uuid = uuid.v4();
    var client_ws = ws;
    var user_status = {"login":false,"info":{}};
    //console.log('client [%s] connected', client_uuid);

    // WebSocket received message action
    ws.on('message', function(message) {
        if (is_json(message)){
            var data = JSON.parse(message);
            var new_username;
            //console.log('client [%s] message [%s]', client_uuid, message);
            if (user_status.login == false){
                switch(data.action){
                    case LOGIN_ACTION:
                        accounts.login_check(data.username, data.password, client_ws, client_uuid, user_status, clients, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case REGISTER_ACTION:
                        accounts.register(data.username, data.password, data.email, data.name, client_ws, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    default:
                        invalid_msg(client_ws);
                }
            }else{
                switch(data.action){
                    case MESSAGE_SEND:
                        msg.to_name(user_status.info, data.username, data.message, clients);
                        break;
                    case FRIEND_GET:
                        friend.fetch_friend_list(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case FRIEND_SEARCH:
                        if (data.username){
                            friend.search_user(data.username, function(result){
                                msg.to_sock(client_ws, JSON.stringify(result));
                            });
                        }else {
                            friend.search_user_id(data.id, function(result){
                                msg.to_sock(client_ws, JSON.stringify(result));
                            });
                        }
                        break;
                    case FRIEND_ADD:
                        friend.add_friend(user_status.info, data.username, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case FRIEND_CHECK:
                        accounts.check_online(data.username, clients, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case FRIEND_DELETE:
                        friend.delete_friend(user_status.info, data.username, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case MESSAGE_OFFLINE_GET:
                        msg.offline_msg_check(user_status.info, data.username);
                        break;
                    case MESSAGE_COMMAND_SEND:
                        msg.user_command(user_status.info, data.username, data.message, clients);
                        break;
                    case GAME_ADD:
                        game.join(user_status.info, data.id, GAME_PLAYER);
                        break;
                    case GAME_CREATE:
                        game.new(user_status.info, data.name);
                        break;
                    case GAME_EXIT:
                        game.exit(user_status.info);
                        break;
                    case GAME_GET:
                        game.get_all(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case GAME_ADD_WAYPOINT:
                        game.new_waypoint(user_status.info, data.location, data.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case GAME_GET_WAYPOINT:
                        game.get_waypoint(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case GAME_GET_USER:
                        game.user(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case GAME_GET_CURRENT:
                        game.get_current(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    // case GAME_NOTIFICATION_SEND:
                    //     send_notification_to_all(user_status.info, data.message);
                    //     break;
                    case PROFILE_ACTION:
                        accounts.fetch_account_info(user_status.info);
                        break;
                    case LOCATION_SEND:
                        game.store_location(user_status.info, data.location, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case LOCATION_GET:
                        game.get_location(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case LOCATION_GET2:
                        game.get_owner_location(user_status.info, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    case PROFILE_UPDATE:
                        accounts.update_user_infor(user_status.info, data.name, data.email, data.location, data.status, function(result){
                            msg.to_sock(client_ws, JSON.stringify(result));
                        });
                        break;
                    default:
                        invalid_msg(user_status.info.ws);
                }
            }
        }else{
            invalid_msg(client_ws);
        }
    });

    // A client disconnected
    // Delete its ID from client array
    var closeSocket = function() {
        for (var i = 0; i < clients.length; i++) {
            if (clients[i].id == client_uuid) {
                clients.splice(i, 1);
                ws.close();
            }
        }
    };

    // WebSocket close action
    ws.on('close', function () {
        if (user_status.login){
            closeSocket();
        }
        //console.log('client [%s] disconnected', client_uuid);
    });

    // Server close action
    process.on('SIGINT', function () {
        console.log("Server Closing.....");
        //closeSocket('Server has disconnected');
        process.exit();
    });
});
