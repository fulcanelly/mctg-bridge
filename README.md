# mctg-bridge

MCTG-bridge is lightweight plugin that creates a chat bridge between a telegram chatroom and a minecraft chat.

### how to setup 

1) Compile `mvn clean install`
2) Copy .jar file from `target` folder file to the server's plugins folder.
3) Configure plugin by adding directory named `tg-bridge` to the plugins folder of the server and create a `config.json` file with the following:
```json
{
  "api_token": "token of telegram bot",
  "chat_id": "id of telegram chat where bot suppose to work"
}

```
After that, you have to reload your server, and the bot should work.


