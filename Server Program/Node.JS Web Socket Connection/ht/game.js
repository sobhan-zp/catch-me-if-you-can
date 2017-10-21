// Game Management
var db = require("./database");
var msg = require("./message");

// Create a new game
exports.new = function(userinfo, name){
    var the_game = this;
    new_game(userinfo, name, function(result){
        msg.to_sock(userinfo.ws, JSON.stringify(result));
        if (result.game_id){
            the_game.join(userinfo, result.game_id, GAME_OWNER);
        }
    });
}

// Get all active games
exports.get_all = function(userinfo, fn){
    var sql = "SELECT * FROM game";
    db.execute(sql, GAME_GET_SUCCESS, GAME_GET_FAIL, function(result){
        return fn(result);
    });
}

// Join a game
exports.join = function(userinfo, game_id, is_owner){
    var the_game = this;
    is_in_game(userinfo.db_id, function(result1){
        if(!result1){
            join_game(userinfo, game_id, is_owner, function(result){
                msg.to_sock(userinfo.ws, JSON.stringify(result));
                if (result.code == GAME_ADD_SUCCESS){
                    // send_notification_to_all
                }
            });
        }else{
            var feedback = {
                "code": GAME_ADD_FAIL
            };
            msg.to_sock(userinfo.ws, JSON.stringify(feedback));
        }
    });
}

// Exit a game
exports.exit = function(userinfo){
    exit_game(userinfo, function(result){
        msg.to_sock(userinfo.ws, JSON.stringify(result));
    });
}

// Create a waypoint
exports.new_waypoint = function(userinfo, location, info, fn){
    var g_id = "SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + " and is_owner = " + GAME_OWNER;
    if (location.x && location.y){
        db.execute(g_id, 1, 0, function(result){
            if (result.result.length>0){
                var table = "waypoint";
                var data = {
                    x: location.x,
                    y: location.y,
                    info: info,
                    game_id: result.result[0].game_id
                }
                db.insert(data, table, GAME_ADD_WAYPOINT_SUCCESS, GAME_ADD_WAYPOINT_FAIL, function(result){
                    var feedback = {
                        "code": result.code
                    };
                    return fn(feedback);
                });
            }
        });
    }else{
        var feedback = {
            "code": GAME_ADD_WAYPOINT_FAIL
        };
        return fn(feedback);
    }
}

// Get all waypoint of current game
exports.get_waypoint = function(userinfo, fn){
    var sql = "SELECT x, y, info FROM waypoint WHERE game_id = (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    db.execute(sql, GAME_GET_WAYPOINT_SUCCESS, GAME_GET_WAYPOINT_FAIL, function(result){
        return fn(result);
    });
}

// Create new game (only execute)
function new_game(userinfo, name, fn){
    var feedback;
    var table = "game";
    var data = {
        name: name
    };
    is_in_game(userinfo.db_id, function(result){
        if (!result){
            db.insert(data, table, GAME_CREATE_SUCCESS, GAME_CREATE_FAIL, function(result1){
                if (result1.code == GAME_CREATE_SUCCESS){
                    feedback = {
                        "code": result1.code,
                        "game_id": result1.result.insertId
                    };
                }else{
                    feedback = {
                        "code": result1.code
                    };
                }
                return fn(feedback);
            });
        }else{
            feedback = {
                "code": GAME_CREATE_FAIL
            };
            return fn(feedback);
        }
    });
}

// Whether the user in game already
function is_in_game(id, fn){
    var sql = "SELECT * FROM account_in_game WHERE account_id = " + id;
    var table = "account_in_game";
    var columns = "*";
    var condition = {
        account_id: id
    };
    db.select(condition, columns, table, 1, 0, function(result){
        if (result.result.length>0 || result.code == 0){
            return fn(true);
        }else{
            return fn(false);
        }
    });
}

// Whether the user is a game owner
function is_game_owner(userinfo, fn){
    var sql = "SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + " and is_owner = " + GAME_OWNER;
    db.execute(sql, 1, 0, function(result){
        if (result.result.length>0){
            fn(result.result);
        }else{
            fn(false);
        }
    })
}

// Exit game (execute only)
function exit_game(userinfo, fn){
    // If the user is owner of game(s), those game(s) will also be deleted.
    is_game_owner(userinfo, function(result){
        var feedback;
        if (result){
            for (var i=0; i<result.length; i++){
                delete_game(userinfo, result[i].game_id);
                remove_all_waypoint_of_game(result[i].game_id);
            }
            feedback = {
                "code": GAME_EXIT_SUCCESS
            }
            return fn(feedback);
        }else{
            var sql = "DELETE FROM account_in_game WHERE account_id = " + userinfo.db_id;
            db.execute(sql, GAME_EXIT_SUCCESS, GAME_EXIT_FAIL, function(result){
                feedback = {
                    "code": result.code
                }
                return fn(feedback);
            })
        }
    })
}

// Join a game (execute only)
function join_game(userinfo, game_id, is_owner, fn){
    var sql = "INSERT INTO account_in_game (game_id, account_id, is_owner) VALUES (" + game_id + ", " + userinfo.db_id + ", " + is_owner + ")";
    db.execute(sql, GAME_ADD_SUCCESS, GAME_ADD_FAIL, function(result){
        var feedback;
        if (result.code == GAME_ADD_FAIL){
            feedback = {
                "code": GAME_ADD_FAIL
            };
        }else{
            feedback = {
                "code": GAME_ADD_SUCCESS,
                "is_owner": is_owner
            };
            //send_notification_to_all(userinfo, userinfo.username + " joined the game.");
        }
        //sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
        return fn(feedback);
    });
}

// Delete a game
function delete_game(userinfo, game_id){
    // Delete game will also remove all users in the game
    var sql = "DELETE FROM game WHERE id = " + game_id;
    // Remove owner self
    remove_user_from_game(userinfo, game_id, userinfo.db_id);
    // Remove all other users
    remove_all_user_from_game(userinfo, game_id);
    // Delte game itself
    db.execute(sql, GAME_DELETE_SUCCESS, GAME_EXIT_FAIL, function(result){});
}

function remove_all_user_from_game(userinfo, game_id){
    var sql = "DELETE FROM account_in_game WHERE game_id = " + game_id + " and is_owner = " + GAME_PLAYER;
    db.execute(sql, GAME_USER_REMOVE_SUCCESS, GAME_USER_REMOVE_FAIL, function(result){});
}

function remove_all_waypoint_of_game(game_id){
    var sql = "DELETE FROM waypoint WHERE game_id = " + game_id;
    db.execute(sql, 1, 0, function(result){});
}

function remove_user_from_game(userinfo, game_id, userid){
    var sql = "DELETE FROM account_in_game WHERE game_id = " + game_id + " and account_id = " + userid;
    db.execute(sql, GAME_USER_REMOVE_SUCCESS, GAME_USER_REMOVE_FAIL, function(result){});
}

exports.get_current = function(userinfo, fn){
    var sql = "SELECT * FROM game WHERE id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    db.execute(sql, GAME_GET_CURRENT_SUCCESS, GAME_GET_CURRENT_FAIL, function(result){
        if (result.result.length>0){
            var sql2 = "SELECT is_owner FROM account_in_game WHERE account_id = " + userinfo.db_id;
            db.execute(sql2, 1, 0, function(result2){
                var feedback = {
                    "code": result.code,
                    "is_owner": result2.result[0].is_owner,
                    "id": result.result[0].id,
                    "name": result.result[0].name
                };
                return fn(feedback);
            });
        }else{
            var feedback = {
                "code": GAME_GET_CURRENT_FAIL
            };
            return fn(feedback);
        }

    });
}

exports.user = function(userinfo, fn){
    var sql = "SELECT game_id, account_id, is_owner FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ")";
    db.execute(sql, GAME_GET_USER_SUCCESS, GAME_GET_USER_FAIL, function(result){
        return fn(result);
    });
}

exports.get_owner_location = function(userinfo, fn){
    var sql = "SELECT account_id, x, y FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ") and is_owner = " + GAME_OWNER;
    db.execute(sql, LOCATION_GET2_SUCCESS, LOCATION_GET2_FAIL, function(result){
        return fn(result);
    });
}

exports.get_location = function(userinfo, fn){
    var sql = "SELECT account_id, x, y FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ") and is_owner = " + GAME_PLAYER;
    db.execute(sql, LOCATION_GET_SUCCESS, LOCATION_GET_FAIL, function(result){
        return fn(result);
    });
}

exports.store_location = function(userinfo, location, fn){
    var sql = "UPDATE account_in_game SET x = "+location.x+", y = "+location.y+" WHERE account_id = " + userinfo.db_id;
    db.execute(sql, LOCATION_SUCCESS, LOCATION_FAIL, function(result){
        var feedback = {
            "code": result.code
        }
        return fn(feedback);
    });
}
