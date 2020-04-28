# mctg-bridge

MCTG-bridge is lightweight plugin for a chat bridge between the telegram group and the minecraft chat.

### how to setup 

1) Compile `mvn clean install`
2) Copy .jar file from `target` folder file to the server's plugins folder.
3) Configure plugin by adding directory named `tg-bridge` in plugins folder of the server and create there `config.json` file with such content:
```json
{
  "api_token": "token of telegram bot",
  "chat_id": "id of telegram chat where bot suppose to work"
}

```
After that, you have to reload your server, and bot should start to work.


