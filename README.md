[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/58TiBSsz)

## Authors
- Alessia Intini ([GitHub](https://github.com/AlessiaIntini), backend, system analitycs)
- Antonio Iorio ([GitHub](https://github.com/antonioior), backend, system analitycs)
- Matteo Tedesco (me, backend, frontend, deployment)
- Margherita Lavena ([GitHub](https://github.com/margheritalavena) backend)

## Project structure
* `./`: the main directory contains the documents readme
* `COOMUNICATION_MANAGER/`: is the folder that contains the implementation of the COMMUNCATION_MANAGER microservice. 
It runs on 8081 port
* `CRM`: is the folder that contains the implementation of the CRM microservice It runs on 8083 port 
* `DOCUMENT_STORE`: is the folder that contains the implementation of the DOCUMENT_STORE microservice. It runs on 8084
* `jobplacement-fe`: is the folder that contains the implementation of the frontend
* `IAM`: is the folder that contains the implementation of the IAM microservice. It runs on 8080 port
* `IAM/iam_keycloak-data`: contains the iam information that needs to be imported to the docker volume,
where the following are configured: roles and user.
  * Roles: 
    * __admin__: has all the permissions
    * __operator__: has the permission to read and write using all microservices
    * __guest__: has the permission to read all microservices except for messages
  * Users:
    * __admin__: 
      * username: admin
      * password: password
    * __operator__:
      * username: operator
      * password: password
    * __guest__:
      * username: guest
      * password: password
