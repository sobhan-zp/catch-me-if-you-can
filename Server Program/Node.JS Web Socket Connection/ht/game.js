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

function send_location_to_creator(userinfo, location){
    var sql = "SELECT account_id FROM account_in_game WHERE game_id in (SELECT game_id FROM account_in_game WHERE account_id = " + userinfo.db_id + ") and is_owner = " + GAME_OWNER;
    con.query(sql, function (err, result) {
        var feedback;
        if (err){
            console.console.log("An ERROR occurs.");
            feedback = {
                "status": "error on delivering",
                "code": MESSAGE_LOCATION_FAIL
            }
        }else{
            for (var i=0; i<result.length; i++){
                var location_message = {
                    "action": MESSAGE_LOCATION_RECEIVE,
                    "location": location
                }
                sys_send_to_id(result[i].account_id, JSON.stringify(location_message));
            }
            feedback = {
                "status": "message delivered",
                "code": MESSAGE_LOCATION_SUCCESS
            }

        }
        sys_send_to_sock(userinfo.ws, JSON.stringify(feedback));
    });
}
