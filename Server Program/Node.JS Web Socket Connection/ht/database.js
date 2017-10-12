// Database Management
var mysql = require('mysql');

var con = mysql.createConnection({
    host: DATABASE_HOST,
    user: DATABASE_USERNAME,
    password: DATABASE_PASSWORD,
    database: DATABASE_DATABASE
});

exports.set_database_con = function(){
    con.connect(function(err) {
        if (err) {
            console.log('Database Connection Failed');
            console.log('%s', err);
            process.exit();
        }
        console.log("Database Connected");
    });
}

exports.execute = function(sql, s_code, f_code, fn){
    var feedback = {};
    con.query(sql, function (err, result) {
        if (err){
            feedback.code = f_code;
        }else{
            feedback.code = s_code;
            feedback.result = result;
            //feedback.result = JSON.stringify(result);
        }
        return fn(feedback);
    });
}
