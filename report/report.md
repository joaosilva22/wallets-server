1.Introduction

The application is a collaborative budgeting application that enables people to track income and expenses across a family, an organization, or any other group of individuals that share monetary resources, as well as get an overview of spending trends and tendencies. The application is multi-platform and we provide both Android and native Desktop clients.

The application is powered by a REST server that stores client data and manages client interactions.

Wallets are the application's organizational unit. Users can create a Wallet to track monetary transactions, and invite others to their Wallets. Wallets are made of one or more Categories, to which Movements are associated. Movements correspond to transactions and can be associated to only one Category. All collaborators of a Wallet can add Movements, but Categories may only be created by the Wallet owner. 

IMAGE 1: Here, John Doe, a member of the Company X wallet logs the purchase of an item in the application.
[ Screenshot da aplicação Android ou Desktop com um movimento numa wallet]

2. Architecture

2.1. Overview

The server application is distributed across a few packages, each with it's distinct purpose. An effort was made to achieve separation of concerns, meaning that the main modules of the server, database accesses and api views, are segregated. Modules providing other funcionality, may it be related to authentication or utilities, are distributed across other appropriate packages. One can get an overview of the application's modules in Table 1, as well as in the package diagram in Image 2.

TABLE 1: Overview of the application's modules
Module   | Description
Auth     | Functionality related to user authentication, mainly an implementation of JSON Web Tokens
Crypto   | RSA key generators and related utilities
Database | Database accesses
Util     | Various general-purpose utilies, such as logging, and HTTP response building.
Views    | API views

IMAGE 2: Package diagram of the application
[ Package diagram ]

2.2. Resources

The application makes available a set of resources through a REST API. Each resource is made accessible through one API endpoint. Resources correspond to the database tables, and the REST API endpoints provide a gateway for the clients to safely perform CRUD operations on them. The resources that are made available through our application can be found on Table 2. It should be noted that even though most resources are exposed through an API endpoint, some API endpoints do not directly expose a resource and are instead used for other necessary functionality such as user authentication. Those are described in Table 3.

TABLE 2: Resources exposed by the api
Resource   | Description                                | URL endpoint
Wallets    | The organizational unit of the application | /wallets
Categories | Classify Movements                         | /categories
Movements  | A monetary transaction                     | /movements
Accounts   | The user account                           | /accounts

TABLE 3: Other API endpoints
URL endpoint | Description 
/auth        | Provides user authentication through email and password
/refresh     | Refreshes user access token

2.3. HTTP Verbs

Conforming to REST best practices, HTTP verbs are used in accord to their semantic meaning. As such POST can be used to create a resource, GET to retrieve a resource, PUT to update a resource and finally DELETE to delete a resource. Other HTTP methods are not supported. In addition, not all resources allow all four of the supported methods.

2.4. Request and Response Format

The API accepts arguments in the form of JSON objects or, in the case of GET requests, URL parameters. Responses are also given in JSON. This means that all resources must serialized before being sent to the client, and then deserialized in order to be of any use.

3. Implementation

3.1. Database

In order to store user data a SQLite database is used. This choice was based on the simplicity to setup and run and interoperability with the Android client, which also uses SQLite. All accesses to the database from the client must go through an API endpoint that provides security and manages permissions.

3.2. HTTPS

Users who whish to authenticate with the API must send their credentials, an email and password pair, at least once. Sending the password over the wire is a security threat. To supress this threat, HTTPS was implemented using self-signed certificates. Furthermore, encrypting request data also prevents man in the middle type attacks that would otherwise be a threat with token based authentication.

3.3. Authentication

Token based authentication is used in the application, because it is completely stateless. The user must provide their email and password in a request to the server's authentication endpoint once. At that point, the server will respond with a pair of tokens: an access token, that grants access to resources; and a refresh token that is used to get a new access token without the need to send the login credentials again. Once a user is in possession of an access token, all requests to the server's API must include it in the HTTP Authorization header.

The tokens are implemented in accord to the JSON Web Token standard (RFC 7519) that defines a "compact and self-contained way to securely transmit information between parties"(https://jwt.io/introduction). JSON Web Tokens, also known as JWTs, are composed of three parts: The header, the payload and the signature separated by periods.

The header identifies the algorithm used to sign the token, as well as the token type. In the case of this application, the header must look something like this: {"alg":"RSA", "typ":"JWT"}

The payload contains the claims, a number of assertions about the token issuer, the user it was issued to or about the token itself. The claims used in the tokens issued by the application's server are:
+ sub: The user the token was issued to
+ iat: Timestamp of the moment the token was issued
+ exp: Timestamp of the moment the token will expire
+ aud: To identify the token type
The inclusion of the time the token was issued and an expiration date is of particular importance to prevent replay attacks, in that it does not allow a malicious agent who became in possession of an access token to use it repeatedly and indefinitely to act in behalf of the user. That being said, a token issued by the application's server must look something like: {"sub":1, "iat":1422779638, "exp":1425779638, "aud":"access"}

The signature is the result of base64 encoding the header and the payload and concatenate them using a period as separator, encrypting the result using RSA and the server's private key and then base64 encoding that. The digital signature is a way for the server to verify that a token that it has recieved was in fact issued by itself, and has not been tampered with.

3.4. Authorization

Authorization is granted by the verification of the user's JWT access token to make sure they are who they claim to be, followed by the matching of the user identity against the users who are allowed access to the resource that they are trying to access. Authorization can be defined with a certain level of granularity accross HTTP methods, meaning that a user can be given read access to a resource but not write access.

3.5. Client Applications

The native desktop client application was developed using Java with Swing to create the GUI. The Android application was created using Android Java.

4. Conclusion

The application is the result of a successful attempt to implement a REST service. It uses stateless authentication and authorization methods, grants the validity of the tokens using RSA assymetric key encryption and token expiration and prevents the stealing of credentials on the wire by implementing HTTPS with self-signed certificates. However, it would still benefit from some improvements:
+ A way to revoke tokens. The only way tokens can be invalidated at this time is through expiration. This was not implemented as it is not trivial to revoke the tokens while at the same time mantaining a completely stateless service. 
+ The inclusion of access scopes in the tokens, in order to further increase access control.
