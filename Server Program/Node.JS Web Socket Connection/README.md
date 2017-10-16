House Tatch WebSocket Server

Environments Requirements:

    1. node version 8.5.0 or above

    2. npm version 3.10.10 or above

    3. Port 80 accessible

Node JS Modules Requirements for Server:

    1. async

    2. mysql

    3. node-uuid

    4. ws

Node JS Modules Requirements:

    1. chai

    2. mocha

    3. assert

To run the program:

    Run 'node server.js' or 'sudo node server.js'

To test the program:

    1. Install Mocha globally and run 'mocha' in ht folder

    2. Install Mocha locally and run 'npm test' in ht folder

To install Node.JS

    - Download && install latest Node.JS from https://nodejs.org/

To install MariaDB

    - for CentOS 7

        - Enter following command to install

            yum install mariadb mariadb-server -y

        - Start MariaDB

            systemctl start Mariadb

        - Start on boot

            systemctl enable mariadb

        - MariaDB initiallize

            mysql_secure_installation
