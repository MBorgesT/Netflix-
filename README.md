# Stream++

A project by Matheus Teixeira (up202110548)

The video streaming project comprises distinct modules, which are individually explained in the following sections.

Except for the Android apps, the other services are containerized using Docker and orchestrated through docker-compose, facilitating the migration to a cloud environment once needed.

### Client application

With a Model–view–viewmodel (MVVM) architectural pattern, this application allows the user watch videos via streaming or previously downloaded ones. Additionally, it hosts an HTTP resource server for the mesh system.

### CMS application

Also employing the MVVM pattern, is the frontend module for the upload of videos and management of administration and subscription accounts.

### Java backend

The interface for the backend system, hosts the API endpoints and internally redirects the resource requests to the NGINX server.

### NGINX server

Hosts processed video resources that are served to the Client application.

### MariaDB database

Serves as persistent storage for the backend system.

### Files on the main folder

* `docker-compose.yml`, as previously mentioned, orchestrates the project's containers;
* `packaging.sh` is utilized by the backend to process the videos into a HLS-compatible format;
* `db_ddl.sql` contains the script for creating the database.