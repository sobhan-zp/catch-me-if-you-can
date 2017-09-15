var http = require('http');

http.createServer(function (request, response) {
    response.end('Hello World\n');
}).listen(80);

console.log('Server running at http://127.0.0.1/');
