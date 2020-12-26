# MCTG-bridge
MCTG-bridge is a lightweight plugin that creates a chat bridge between Telegram group and Minecraft chat.

### Setup 
1) Compile `mvn clean install`
2) Copy .jar file from `target` folder file to the server plugins/ folder.
3) Configure plugin by editing tg-bridge/config.json file.

After that, you have to restart your server, and then bot should be working.

### Structure

```text
         +-------------------------------------+
         |                                     |
         |           +-----------+             v
         |           |           |          +--+--------+
         |    +------+ Formatter +<-+       |           |
         |    |      |           |  |       |  Command  |
         |    |      +-----------+  |    +->+  handler  +<--+
         |    |                     |    |  |           |   |
         v    v                     |    |  +-----------+   |
 +-------+----+----+                |    |                  |
 |                 |         +------+----+-----+            |
 |   Spigot API    |         |                 |            |
 |                 |         | Event dispatcher|            |
 +-------+---------+         |                 |            |
         |                   +---------+-------+            |
         |                             ^                    |
+--------+--------+  +-------+         |                    |
|                 |  |       | +-------+---------+          |
| Event dispatcher|  |   F   | |                 |          |
|                 |  |   o   | |  Telegram API   +<---------+
+--------+---+----+  |   r   | |                 |          |
         |   |       |   m   | +----+------------+          |
         |   |       |   a   |      ^                       |
         |   +------>+   t   +------+                       |
         |           |       |                              |
         |           +-------+                              |
         |                                                  |
         +--------------------------------------------------+


                                                                                                                       PI

````


### What's Left to be Added/Fixed
- The Following Ideas
  - [ ] Message reduction
  - [x] Message merging
  - [ ] Login via telegram
  - [x] Flexible configurtation
  - [ ] Ingore switch
