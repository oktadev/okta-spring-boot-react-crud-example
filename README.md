# JUG Tours with Spring Boot and React
 
This example app shows how to create a Spring Boot API and CRUD (create, read, update, and delete) its data with a React app.

<!--Please read []() to see how this app was created.-->

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [Node.js](https://nodejs.org/).

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

You can also run everything using Maven:

```bash
./mvnw package -Pprod
java -jar target/*.jar
```

### Create an Application in Okta

You will need to [create an OIDC Application in Okta]() to get your values to perform authentication. 

Log in to your Okta Developer account (or [sign up](https://developer.okta.com/signup/) if you don’t have an account) and navigate to **Applications** > **Add Application**. Click **Web**, click **Next**, and give the app a name you’ll remember. Add `http://localhost:8080/login` as a login redirect URI and click **Done**.

#### Server Configuration

Set the `issuer` and copy the `clientId` into `src/main/resources/application.yml`. 

**NOTE:** The value of `{yourOktaDomain}` should be something like `dev-123456.oktapreview.com`. Make sure you don't include `-admin` in the value!

```properties
okta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default
okta.oauth2.clientId={clientId}
```

## Links

This example uses the following libraries provided by Okta:

* [Okta Spring Boot Starter](https://github.com/okta/okta-spring-boot)
* [Okta React SDK](https://github.com/okta/okta-oidc-js/tree/master/packages/okta-react)

## Help

Please post any questions as comments on the [blog post](), or visit our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if you would like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).
