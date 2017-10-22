# House Tatch WebSocket Server

## Environments Requirements:

    1. node version 8.5.0 or above

    2. npm version 3.10.10 or above
    
    3. MySQL 5 or MariaDB

    4. Port 80 accessible
    
## To run the unit test (mocha has installed locally):

    1. Install Mocha globally (-g), then run 'mocha' in ht folder

    2. Install Mocha locally, then run 'npm test' in ht folder
    
    3. Note On Server Testing: 
    
        there may sometimes be few fail test, because of the delay of the
        
        program and database, in this case, try one (few) more time(s).
    
## To run the manual test
    
    1. run the server
    
    2. run ht/client_test.html
    
    3. There are few samples of input, or you can input your own tests

## To run the server:

    Run 'node server.js' or 'sudo node server.js'

## Node JS Modules Requirements for Server:

    1. async

    2. mysql

    3. node-uuid

    4. ws

## Node JS Modules Requirements for Testing:

    1. chai

    2. mocha

    3. assert
    
## Structure
    
    - ht/
    
    -- database.js // the connection modules to database
    
    -- account.js // handle account messages
    
    -- constant.js // all constant are defined here
    
    -- message.js // handle send message to client
    
    -- game.js // handle actions of game part
    
    -- friend.js // handle actions of friend part
    
    -- server.js // the main program of the server
    
    -- test/ // mocha will run all test file in this folder
    
    --- function_test.js // auto test functions validity
    
    --- client_response_test.js // connect as a client to auto test the response from server
    
    -- node_modules/ // the installed modules of node js

## To install Node.JS

    - Download && install latest Node.JS from https://nodejs.org/

## To install MariaDB

    - for CentOS 7

        - Enter following command to install

            yum install mariadb mariadb-server -y

        - Start MariaDB

            systemctl start Mariadb

        - Start on boot

            systemctl enable mariadb

        - MariaDB initiallize

            mysql_secure_installation
