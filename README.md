# MCTG-bridge
![GitHub Tag](https://img.shields.io/github/v/tag/fulcanelly/mctg-bridge)
<a href="https://github.com/fulcanelly/mctg-bridge/releases/"><img src="https://img.shields.io/github/downloads/fulcanelly/mctg-bridge/total.svg" alt="GitHub All Releases"/></a>
<img src="https://img.shields.io/github/stars/fulcanelly/mctg-bridge"/>
![GitHub Build](https://img.shields.io/github/actions/workflow/status/fulcanelly/mctg-bridge/main.yml?branch=master)


<a><img src="https://img.shields.io/badge/MC-1.17.*-brightgreen.svg" alt="Minecraft"/></a>
<img src="https://img.shields.io/badge/MC-1.18.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.19.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.20.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.21.*-brightgreen.svg" alt="Minecraft"/>


MCTG-bridge is a standalone working out of box plugin that creates a chat bridge between Telegram group and Minecraft chat.

### How to use 
- [Get jar file](#get-jar-file)
- [Setup telegram bot](#setup-telegram-bot)
- [Configure plugin](#configure-plugin)
- Configure additional modules
   - ngrok proxy tunnel
   - LoginSecurity plugin
   - Invite system

### Get jar file
- Download stable version - you can download compiled package from releases [page](https://github.com/fulcanelly/mctg-bridge/releases)

- Download experimental-automatiacly-built version from [github actions](https://github.com/fulcanelly/mctg-bridge/actions)

- Build by yourself - `mvn clean install`

### Setup telegram bot 

- Head to https://t.me/BotFather
- Use command `/newbot` and follow instructions
- Use `/setprivacy` and set it to `DISABLED` if you want bot to see messages in chats
- Remember API token


### Configure plugin
 - Copy jar file to server's `plugins` folder
 - Start server to generate config for plugins
 - Put bot's API token to ```plugins/tg-bridge/config.yml``` at `api_token` column 
 - Restart server - at startup you should see message like this
```log
[17:10:59 WARN]: [tg-bridge] chat_id is null, use /attach <secretTempCode> to pin one
[17:10:59 WARN]: [tg-bridge] secretTempCode is set to -72683
```
After that add bot to target chat and execute that command in it:
```
/attach secretTempCode
```
Now you need to make final restart and plugin is ready to use 

- Adjust other config options by your needs
### Preview 

![image](https://github.com/user-attachments/assets/4c6339c9-be85-4c71-bdbf-b8c2af984cb7)


![image](https://github.com/user-attachments/assets/c424be96-3a73-4c80-a5f0-0e54234a5cd7)

 
![image](https://github.com/user-attachments/assets/a4e52cd1-e1a9-4790-a018-8ec457bb3a71)


![image](https://github.com/user-attachments/assets/7be1055c-c16a-4c6b-9ad3-d3d17319efc2)



### Telegram bot commands

Base command set

```
memory - show allocated memory
list - list online players
ping - pong
uptime - show uptime
stats - player stats
top - get top stats
kickme - kicks you...
```

From ivite system
```
invite - invite person to server (optional)
```

From login security
```
removepass - remove account password
changepass - change account password
```

For now all they by default enabled (except last ones since they depedns from corresponding plugin presence)

todo: add configurability and scripting 


### How to setup ngrok (`/tunnel` command)

To setup ngrok or telegram `/tunnel` command you need to put you ngrok auth to config file field `ngrok_auth`

you can get one from https://ngrok.com


### What's Left to be Added/Fixed
- The Following Ideas
  - [x] Message reduction
  - [x] Message merging
  - [x] Login via telegram
    - [x] Hook with existing login plugin
  - [x] Flexible configurtation
  - [x] Ingore switch
  - [ ] Custom localization
  - [ ] Config editor
  - [ ] Reply by click
