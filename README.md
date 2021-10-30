# MCTG-bridge
MCTG-bridge is a lightweight plugin that creates a chat bridge between Telegram group and Minecraft chat.

### Preview 

todo 

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


### How to build

After cloning and entering repo directory run this
```
git submodule update --init lib/invitesys
cd lib/invitesys/core && mvn && cd # optional -- if you want fresh version of invitesys
mvn
```
Then you should get tg-brgidge*.jar in `target` directory


Or you can download compiled package, but it could be little outdated



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
