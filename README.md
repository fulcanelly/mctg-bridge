# MCTG-bridge
MCTG-bridge is a lightweight plugin that creates a chat bridge between Telegram group and Minecraft chat.

### Setup 
1) If you want fresh version head to [how to build guide](#how-to-build)
2) Copy .jar file to the server plugins/ folder.
3) Configure plugin by editing tg-bridge/config.yml file or following console hints.

After that, you have to restart your server, and then bot should be working.

### How to build

After cloning and entering repo directory run this
```
git submodule init
git submodule update
cd lib/invitesys/core && mvn && cd - && mvn
```
Then you should get tg-brgidge*.jar in `target` directory


Or you can download compiled package, but it could be little outdated



### What's Left to be Added/Fixed
- The Following Ideas
  - [x] Message reduction
  - [x] Message merging
  - [x] Login via telegram
    - [ ] Hook with existing login plugin
  - [x] Flexible configurtation
  - [x] Ingore switch
  - [ ] Custom localization
  - [ ] Config editor
  - [ ] Reply by click
