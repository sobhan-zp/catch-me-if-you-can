House Tatch WebSocket Server

Requirements:

    1. node version 8.5.0 or above

    2. npm version 3.10.10 or above

    3. CentOS 5/Windows 2003 or above

    4. Port 80 accessible

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

To run the server

    - for Linux && Windows

        - cd /%YOUR PATH%//ht

        - node server.js
