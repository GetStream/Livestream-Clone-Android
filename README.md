# Livestream-Clone-Android

## Building the app
1. Add a file inside project root directory named `secret.properties` having the following contents:
```
StreamApiKey=xxx
UserToken=xxx
```
2. Replace `xxx` with your values. 

* `StreamApiKey` is a public key which you can get by registering your chat app on getstream.io.
* `UserToken` is a JWT token of your app user. You can generate one here: https://getstream.io/chat/docs/token_generator/?language=kotlin. You'll need to use there your private key (`secret` from getstream.io dashboard) and pass your desired user id.

3. `./gradlew build`

## Result
![Livestream demo app - Animated gif](demo/demo.gif)
