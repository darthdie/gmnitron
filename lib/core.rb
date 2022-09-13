# frozen_string_literal: true

require "discordrb"
require "byebug"
require_relative "commands/fun"
require_relative "commands/roll"
require_relative "models/dice_pool"
require_relative "models/dice_pool_roll_formatter"
require_relative "models/overcome_formatter"
require_relative "models/mod_formatter"

bot = Discordrb::Bot.new(token: ENV.fetch("GMNITRON_BOT_TOKEN"), intents: [:server_messages])

Commands::Fun.register(bot)
Commands::Roll.register(bot)

bot.run
