# JUG Tours with Spring Boot and React
 
This example app shows how to create a Spring Boot API and CRUD (create, read, update, and delete) its data with a React app.

Please read [Use React and Spring Boot to Build a Simple CRUD App](https://developer.okta.com/blog/2018/07/19/simple-crud-react-and-spring-boot) to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [Node.js 8+](https://nodejs.org/), and [Yarn](https://yarnpkg.com/en/docs/install). You can use npm instead of Yarn, but you'll need to translate the Yarn syntax to npm.

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

To install this example application, run the following commands:

```bash
git clone https://github.com/oktadeveloper/okta-spring-boot-react-crud-example.git spring-react
cd spring-react
```

This will get a copy of the project installed locally. To install all of its dependencies and start each app, follow the instructions below.

To run the server, run:
 
```bash
./mvnw spring-boot:run
```

To run the client, cd into the `app` folder and run:
 
```bash
yarn && yarn start
```

### Create an Application in Okta

Before you begin, you'll need a free Okta developer account. Install the [Okta CLI](https://cli.okta.com) and run `okta register` to sign up for a new account. If you already have an account, run `okta login`.

Then, run `okta apps create`. Select the default app name, or change it as you see fit. Choose **Web** and press **Enter**.

Select **Okta Spring Boot Starter**. Accept the default Redirect URI of `http://localhost:8080/login/oauth2/code/okta` and use `[http://localhost:3000,http://localhost:8080]` for the Logout Redirect URI.

The Okta CLI will create an OIDC Web App in your Okta Org. It will add the redirect URIs you specified and grant access to the Everyone group. You will see output like the following when it's finished:

```shell
Okta application configuration has been written to:
  /path/to/app/src/main/resources/application.properties
```

Open `src/main/resources/application.properties` to see the issuer and credentials for your app.

```properties
okta.oauth2.issuer=https://dev-133337.okta.com/oauth2/default
okta.oauth2.client-id=0oab8eb55Kb9jdMIr5d6
okta.oauth2.client-secret=NEVER-SHOW-SECRETS
```

NOTE: You can also use the Okta Admin Console to create your app. See [Create a Spring Boot App](https://developer.okta.com/docs/guides/sign-into-web-app/springboot/create-okta-application/) for more information.

#### Server Configuration

Copy the values from `application.properties` into `src/main/resources/application.yml` and delete `application.properties`.

```yaml
spring:
  profiles:
    active: @spring.profiles.active@
  security:
    oauth2:
      client:
        registration:
          okta:
            client-id: {clientId}
            client-secret: {clientSecret}
            scope: openid, email, profile
        provider:
          okta:
            issuer-uri: https://{yourOktaDomain}/oauth2/default
```

Run `./mvnw spring-boot:run -Pprod` and log in to your app at `http://localhost:8080`.

## Links

This example uses the following open source libraries:

* [React](https://reactjs.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Security](https://spring.io/projects/spring-security)

## Help

Please post any questions as comments on the [blog post](https://developer.okta.com/blog/2018/07/19/simple-crud-react-and-spring-boot), or visit our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if you'd like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).
