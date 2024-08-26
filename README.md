# MCTG-bridge

<a href="https://github.com/fulcanelly/mctg-bridge/releases/"><img src="https://img.shields.io/github/downloads/fulcanelly/mctg-bridge/total.svg" alt="GitHub All Releases"/></a>
<img src="https://img.shields.io/github/stars/fulcanelly/mctg-bridge"/>
<img src="https://img.shields.io/github/workflow/status/fulcanelly/mctg-bridge/CI"/>

MCTG-bridge is a standalone working out of box plugin that creates a chat bridge between Telegram group and Minecraft chat.

### Preview 
![image](https://github.com/user-attachments/assets/7be1055c-c16a-4c6b-9ad3-d3d17319efc2)

![image](https://github.com/user-attachments/assets/a4e52cd1-e1a9-4790-a018-8ec457bb3a71)

![image](https://github.com/user-attachments/assets/4c6339c9-be85-4c71-bdbf-b8c2af984cb7)

 

### Setup 
1) [Get plugin binary](#how-to-build)
2) [Bot setup](#seting-up-telegram-bot)
3) Copy .jar file to the server `plugins/` folder.
4) Configure plugin by editing `tg-bridge/config.yml` file or following console hints.

After that, you have to restart your server, and then bot should be working.


### Seting up telegram bot

Head to [@BotFather](https://t.me/BotFather) and create bot using /newbot command

Then add this command hints using /setcommands

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

### How to build

- Build by yourself

  After cloning and entering repo directory run this
  ```
  mvn
  ```
  Then you should get tg-brgidge*.jar in `target` directory

- Download stable version. 

  You can download compiled package from releases [page](https://github.com/fulcanelly/mctg-bridge/releases), but it could be little outdated 

- Download experimental automatiacly built version from [github actions](https://github.com/fulcanelly/mctg-bridge/actions)

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
