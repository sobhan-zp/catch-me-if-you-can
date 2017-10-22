// Database Management
var mysql = require('mysql');

var con = mysql.createConnection({
    host: DATABASE_HOST,
    user: DATABASE_USERNAME,
    password: DATABASE_PASSWORD,
    database: DATABASE_DATABASE,
    port: DATABASE_PORT
});

// Connect to database
exports.set_database_con = function(){
    con.connect(function(err) {
        if (err) {
            console.log('Database Connection Failed');
            console.log('%s', err);
            process.exit();
        }
        //console.log("Database Connected");
    });
}

// Execute a single sql statement
exports.execute = function(sql, s_code, f_code, fn){
    var feedback = {};
    con.query(sql, function (err, result) {
        if (err){
            //console.log(err);
            feedback.code = f_code;
        }else{
            feedback.code = s_code;
            feedback.result = result;
            //feedback.result = JSON.stringify(result);
        }
        return fn(feedback);
    });
}

// Execute an insert sql
exports.insert = function(data, table, s_code, f_code, fn){
    var sql = "INSERT INTO " + table + " SET ?";
    query(sql, data, s_code, f_code, function(result){
        return fn(result);
    });
}

// Execute an update sql (only one condition can be applied)
exports.update = function(data, table, condition, s_code, f_code, fn){
    var sql = "UPDATE " + table + " SET ? WHERE ?";
    query(sql, [data, condition], s_code, f_code, function(result){
        console.log();
        return fn(result);
    });
}

// Execute an select sql (only one condition can be applied)
exports.select = function(condition, columns, table, s_code, f_code, fn){
    var sql = "SELECT " + columns + " FROM " + table + " WHERE ?";
    query(sql, condition, s_code, f_code, function(result){
        // console.log(result);
        // console.log(sql);
        return fn(result);
    });
}

function query(sql, data, s_code, f_code, fn){
    var feedback = {};
    con.query(sql, data, function (err, result) {
        if (err){
            //console.log(err);
            feedback.code = f_code;
        }else{
            feedback.code = s_code;
            feedback.result = result;
            //feedback.result = JSON.stringify(result);
        }
        return fn(feedback);
    });
}
