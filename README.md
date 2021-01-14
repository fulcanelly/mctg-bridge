# MCTG-bridge
MCTG-bridge is a lightweight plugin that creates a chat bridge between Telegram group and Minecraft chat.

### Setup 
- To get newest version 
1) Compile `mvn clean install`. 
2) Copy .jar file from `target` folder file to the server plugins/ folder.
3) Configure plugin by editing tg-bridge/config.yml file or following console hints.

Or you can download compiled package, but it could be little outdated

After that, you have to restart your server, and then bot should be working.

### Structure

```text
                             +-------------------------------------+
                             |                                     |
                        +------------------------------------------------------------------+
                        |    |                                     |                       |
                        |    |           +-----------+             v                       |
                        |    |           |           |          +--+--------+              |
+----------------+      |    |    +------+ Formatter +<-+       |           +----------+   |
|                |      |    |    |      |           |  |       |  Command  |          |   |
| login database |      |    |    |      +-----------+  |    +->+  handler  +<--+      |   |
|                |      |    |    |                     |    |  |           |   |      |   |
+-----+----------+      |    v    v                     |    |  +--------+--+   |      v   v
      ^              +--+----+----+----+                |    |           ^      |   +--+---+------+
      |              |                 |         +------+----+-----+     |      |   |             |
      |    +-------->+   Spigot API    |         |                 |     |      |   |    Stats    |
      |    |         |                 |         | Event dispatcher|     |      |   |  collector  |
      |    |         +-------+---------+         |                 |     |      |   |             |
      |    |                 |                   +---------+-------+     |      |   +------+------+
      v    v                 |                             ^             |      |          ^
 +----+----+---+    +--------+--------+  +-------+         |             |      |          |
 |             |    |                 |  |       | +-------+---------+   |      |          |
 |    Login    |    | Event dispatcher|  |   F   | |                 |   |      |          v
 |   manager   |    |                 |  |   o   | |  Telegram API   +<---------+   +------+----------+
 |             |    +--------+---+----+  |   r   | |                 |   |      |   |                 |
 +---------+---+             |   |       |   m   | +----+------------+   |      |   |  stats database |
           ^                 |   |       |   a   |      ^                |      |   |                 |
           |                 |   +------>+   t   +------+                |      |   +-----------------+
           |                 |           |       |                       |      |
           |                 |           +-------+                       |      |
           |                 |                                           |      |
           |                 +--------------------------------------------------+
           |                                                             |
           +-------------------------------------------------------------+






````


### What's Left to be Added/Fixed
- The Following Ideas
  - [ ] Message reduction
  - [x] Message merging
  - [ ] Login via telegram
  - [x] Flexible configurtation
  - [ ] Ingore switch
  - [ ] Custom localization
