# mctg-bridge — Minecraft <-> Telegram Chat Bridge

![GitHub Tag](https://img.shields.io/github/v/tag/fulcanelly/mctg-bridge)
<a href="https://github.com/fulcanelly/mctg-bridge/releases/"><img src="https://img.shields.io/github/downloads/fulcanelly/mctg-bridge/total.svg" alt="GitHub All Releases"/></a>
<img src="https://img.shields.io/github/stars/fulcanelly/mctg-bridge"/>
![GitHub Build](https://img.shields.io/github/actions/workflow/status/fulcanelly/mctg-bridge/main.yml?branch=master)


<a><img src="https://img.shields.io/badge/MC-1.17.*-brightgreen.svg" alt="Minecraft"/></a>
<img src="https://img.shields.io/badge/MC-1.18.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.19.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.20.*-brightgreen.svg" alt="Minecraft"/>
<img src="https://img.shields.io/badge/MC-1.21.*-brightgreen.svg" alt="Minecraft"/>

MCTG-bridge is a Minecraft Telegram bridge plugin for Spigot and Paper servers. It synchronizes Minecraft chat with a Telegram group in both directions and includes server management commands, account linking, and optional plugin integrations.

### Preview 

![image](https://github.com/user-attachments/assets/4c6339c9-be85-4c71-bdbf-b8c2af984cb7)

![image](https://github.com/user-attachments/assets/c424be96-3a73-4c80-a5f0-0e54234a5cd7)

## Features

Compared with simple relay-only plugins, MCTG-bridge provides:

- Two-way Telegram group <-> Minecraft chat communication.
- Built-in Telegram commands for server status, players, uptime, memory, statistics, and top statistics.
- Optional account linking from Minecraft to Telegram.
- Optional hooks for LoginSecurity and InviteSystem; they activate only when those plugins are installed.
- Per-player chat visibility and message compaction.
- Telegram image rendering with optional dithering.
- Optional ngrok support for a tunnel started from Telegram.
- Configuration stored in the plugin folder; no separate bridge service is required.

## Requirements

- Spigot, Paper, or a compatible fork running Minecraft 1.17–1.21.
- Java 17+ for building the plugin.
- A Telegram bot and a Telegram group.

## Quick start

1. Download the latest JAR.
2. Put it into `plugins/`.
3. Start the server once to generate the config.
4. Create a Telegram bot with @BotFather and disable privacy mode.
5. Add `api_token` to `plugins/tg-bridge/config.yml`.
6. Run `/attach <code>` in your Telegram group.
7. Restart the server.

## Full detailed setup
### 1. Telegram setup

1. Open [@BotFather](https://t.me/BotFather) in Telegram.
2. Run `/newbot` and follow the prompts.
3. Copy the bot API token. Keep it private.
4. Run `/setprivacy`, select the bot, and set privacy to `Disabled` so it can receive group messages.
5. Add the bot to the target Telegram group. Give it the permissions needed to read and send messages.

### 2. Minecraft server setup

1. Download the latest stable JAR from [Releases](https://github.com/fulcanelly/mctg-bridge/releases/).
2. Copy it to the server's `plugins/` directory.
3. Start the server once. The plugin creates `plugins/tg-bridge/config.yml`.
4. Put the token into `api_token`:

```yaml
api_token: "123456:replace-with-your-token"
```

5. Restart the server. The console prints a temporary code, for example:

```text
[tg-bridge] chat_id is null, use /attach <secretTempCode> to pin one
[tg-bridge] secretTempCode is set to -72683
```

6. In the target Telegram group, send `/attach -72683` using the code printed by your server.
7. Restart the server again. The bridge is ready.

Never publish `api_token` or the temporary attach code.

## Usage examples

### Telegram group commands

```text
/list       Online Minecraft players
/ping       Check that the bot responds
/uptime     Show server uptime
/memory     Show allocated memory
/stats      Show player statistics
/top        Show top statistics
/kickme     Kick your linked Minecraft account
```


 ### Preview 

![image](https://github.com/user-attachments/assets/a4e52cd1-e1a9-4790-a018-8ec457bb3a71)

![image](https://github.com/user-attachments/assets/7be1055c-c16a-4c6b-9ad3-d3d17319efc2)


Available when the matching plugin is installed:

```text
/invite     Invite a person to the server (InviteSystem)
/removepass Remove your password (LoginSecurity)
/changepass Change your password (LoginSecurity)
```

### Minecraft commands

```text
/tg chat show
/tg chat hide
/tg account register
```

`/tg chat hide` hides bridged Telegram messages for the executing player. `/tg account register` starts the Telegram account-linking flow.

## Settings

Edit `plugins/tg-bridge/config.yml`, then restart the server.

| Setting | Purpose |
| --- | --- |
| `api_token` | Telegram bot token. Required. |
| `chat_id` | Linked Telegram group. Set automatically by `/attach`. |
| `enable_chat` | Enable or disable Telegram-to-Minecraft chat. |
| `enable_dithering` | Enable image dithering for Telegram photos. |
| `language` | Default registration-message language. |
| `max_mc_accounts_per_tg` | Maximum Minecraft accounts linked to one Telegram account. |
| `log_status` | Control status logging. |
| `ngrok_auth` | ngrok auth token for `/tunnel`. |

To use the optional tunnel, place the ngrok auth token in `ngrok_auth`, then send `/tunnel` in Telegram. Get the token from [ngrok](https://ngrok.com/).

## Build from source

```bash
mvn clean install
```

The built plugin JAR is produced in `target/`.

## Links

- [Stable releases](https://github.com/fulcanelly/mctg-bridge/releases/)
- [Experimental builds](https://github.com/fulcanelly/mctg-bridge/actions/)
