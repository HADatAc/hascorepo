# 1.1. Production Installation

This section provides a step-by-step guide to set up a production-ready installation of HASCO/Repo. Follow these instructions to have the full system running locally with both the Graphical User Interface (GUI) and the API.

## Prerequisites
Before starting, ensure that you have the following installed:

- Docker: Install Docker from the official website: https://docs.docker.com/get-docker/
- Docker Compose: This is required to manage multi-container Docker applications. Docker Compose usually comes with Docker Desktop, but if needed, you can check installation instructions here: https://docs.docker.com/compose/install/

## Steps
### Step 1: Clone the HASCO/Repo Repository

Begin by cloning the repository from GitHub. Open a terminal and run the following commands:


```
git clone https://github.com/HADatAc/hascorepo.git

cd hascorepo
```

### Step 2 (If Needed): Stop Existing HASCO/Repo Containers and Clear Volumes

If you have previously installed or run HASCO/Repo, it’s recommended to stop any existing containers and clear persistent volumes before starting the installation. This prevents conflicts, such as issues with saved volumes or active containers, which could cause unexpected behavior.

To stop existing HASCO/Repo containers and remove associated volumes, run:

```
docker-compose -f docker-compose-hascorepo.yml down -v
```

This command will:

- Stop all running HASCO/Repo containers, ensuring no residual processes remain.
- Remove any associated volumes to clear any stored data or configurations from previous installations, ensuring a clean slate for the current setup.

Running this command helps avoid any potential issues related to:

- **Stale volumes**: Previously saved data in volumes could interfere with the fresh installation.
- **Container conflicts**: If containers are already running, Docker Compose may encounter conflicts trying to start new ones on the same ports or networks.

### Step 3: Build and Start the Containers

Use Docker Compose to build and start the containers for both the Drupal interface and the database:

```
docker-compose -f docker-compose-hascorepo.yml up --build -d
```
This command will:

- Build the Docker images based on the Dockerfile provided.
- Set up the MariaDB database container (db service).
- Set up the Drupal container (drupal service) with all the necessary configurations.
- Set up the Fuseki service for SPARQL endpoints.
- Set up the hascoapi service for API access.

### Step 4: Access the HASCO/Repo Interface

Once the containers are up, you can access:

- Drupal GUI for HASCO/Repo at: http://localhost:8081 (the default admin username and password are set to admin)
- Fuseki Yasgui Front-End at: http://localhost:8888
- HASCO API at: http://localhost:9000

## Next Steps
Once the system is up and running, you can start working with the GUI and API, or proceed to configure additional settings as necessary.

For more information check the [Getting Started Guide](https://github.com/HADatAc/hascorepo/wiki)