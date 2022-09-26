# frozen_string_literal: true

require "discordrb"
require 'mongoid'
require_relative "enumerable.rb"

Dir["./lib/models/**/*.rb"].sort.each { |file| require file }
Dir["./lib/commands/*.rb"].sort.each { |file| require file }

Mongoid.load!("./config/mongoid.yml")

bot = Discordrb::Bot.new(token: ENV.fetch("GMNITRON_BOT_TOKEN"), intents: [:server_messages])

Commands::Fun.register(bot)
Commands::Roll.register(bot)
Commands::Scene.register(bot)

bot.run
