# frozen_string_literal: true

require "discordrb"
require "byebug"
require 'mongoid'
# require_relative "commands/fun"
# require_relative "commands/roll"
# require_relative "models/dice_pool"
# require_relative "models/die"
# require_relative "models/modifier"
# require_relative "models/dice_pool_roll_formatter"
# require_relative "models/overcome_formatter"
# require_relative "models/mod_formatter"
# require_relative "models/minion_roll_formatter"
require_relative "enumerable.rb"

Dir["./lib/models/*.rb"].sort.each { |file| require file }
Dir["./lib/commands/*.rb"].sort.each { |file| require file }

# Mongoid.configure do |config|
#   config.clients.default = {
#     uri: ENV['DB_URI']
#   }

#   config.log_level = :warn
# end

bot = Discordrb::Bot.new(token: ENV.fetch("GMNITRON_BOT_TOKEN"), intents: [:server_messages])

Commands::Fun.register(bot)
Commands::Roll.register(bot)
Commands::Scene.register(bot)

bot.run
