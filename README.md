# GMnitron

A Sentinel Comics RPG Discord bot that has game-specific die rolling commands as well as scene/initiative tracking to help run games via Discord.

You can view the full documentation here: https://darthdie.github.io/gmnitron/

You can invite the bot via: [This Invite Link](https://discordapp.com/oauth2/authorize?client_id=425385281890418710&scope=bot)

## Development

To develop, install https://github.com/technomancy/leiningen

Then follow the steps to use the clj-discord library, https://github.com/yotsov/clj-discord#to-use-this-library

You'll need to set the GMNITRON_BOT_TOKEN & DB_URI environment variables, which is the discord bot token and mongodb URI respectively.

To run the bot locally, use the `lein run` command.

## Todo/Upcoming Possible Features

* [Internal] Scene cleanup after 30 days
* [Internal] Some unit testing...

## License

Copyright © 2018 Briar Bowser

The Sentinel Comics RPG is copyright [Greater than Games LLC.](https://greaterthangames.com/)
