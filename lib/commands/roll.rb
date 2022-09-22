# frozen_string_literal: true

require 'byebug'

module Commands
  class Roll
    def self.register(bot)
      @register ||= Roll.new(bot)
    end

    def initialize(bot)
      register_commands(bot)
    end

    def roll_min_command(event)
      rolls = Models::DicePool.roll(event.options, :min)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_mid_command(event)
      rolls = Models::DicePool.roll(event.options, :mid)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_max_command(event)
      rolls = Models::DicePool.roll(event.options, :max)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_overcome_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::OvercomeFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_boost_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "+")

      event.respond(content: content)
    end

    def roll_hinder_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "-")

      event.respond(content: content)
    end

    # TODO: Re-add support for any number of minions
    def roll_minion_command(event)
      die = Models::Die.parse(event.options["die"]).roll
      modifier = Models::Modifier.parse(event.options["modifier"])
      save = event.options["save_versus"]

      die.apply!(modifier)
      content = Models::MinionRollFormatter.format(die, modifier, save: save)

      event.respond(content: content)
    end

    def roll_lieutenant_command(event)
      die = Models::Die.parse(event.options["die"]).roll
      modifier = Models::Modifier.parse(event.options["modifier"])
      save = event.options["save_versus"]

      die.apply!(modifier)
      content = Models::LieutenantRollFormatter.format(die, modifier, save: save)

      event.respond(content: content)
    end

    def roll_reaction_command(event)
      die = Models::Die.parse(event.options["die"]).roll
      modifier = Models::Modifier.parse(event.options["modifier"])
      die.apply!(modifier)

      content = Models::ReactionRollFormatter.format(die, modifier)

      event.respond(content: content)
    end

    def roll_chuck_command(event)
      current_time_in_ms = (current_time.to_f * 1000).to_i
      srand(current_time_in_ms + Math::PI)

      event.respond(content: "Dice have been chucked, and new ones have been commissioned.")
    end

    private

    def register_commands(bot)
      bot.register_application_command(:roll, "Die rolling commands", server_id: ENV.fetch("DISCORD_TEST_SERVER_ID", nil)) do |cmd|
        register_die_and_modifier_command(cmd, :min, "Rolls a dice pool and highlights the min die.")
        register_die_and_modifier_command(cmd, :mid, "Rolls a dice pool and highlights the mid die.")
        register_die_and_modifier_command(cmd, :max, "Rolls a dice pool and highlights the max die.")

        register_effect_and_die_and_modifier_command(
          cmd,
          :overcome,
          "Rolls a dice pool and returns the overcome result."
        )
        register_effect_and_die_and_modifier_command(cmd, :boost, "Rolls a dice pool and returns the boost result.")
        register_effect_and_die_and_modifier_command(cmd, :hinder, "Rolls a dice pool and returns the register result.")

        cmd.subcommand(:minion, "Rolls a minion save, optionally vs a number.") do |sub|
          sub.string("die", "The die to roll, e.g. d4", required: true)
          sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
          sub.integer("save_versus", "The number to roll against.")
        end

        cmd.subcommand(:lieutenant, "Rolls a lieutenant save, optionally vs a number.") do |sub|
          sub.string("die", "The die to roll, e.g. d4", required: true)
          sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
          sub.integer("save_versus", "The number to roll against.")
        end

        cmd.subcommand(:reaction, "Rolls a reaction die.") do |sub|
          sub.string("die", "The die to roll, e.g. d4", required: true)
          sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
        end

        cmd.subcommand(:chuck, "Chucks the current dice.")

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
        bot.application_command(:roll).subcommand(:mid, &method(:roll_mid_command))
        bot.application_command(:roll).subcommand(:max, &method(:roll_max_command))
        bot.application_command(:roll).subcommand(:overcome, &method(:roll_overcome_command))
        bot.application_command(:roll).subcommand(:boost, &method(:roll_boost_command))
        bot.application_command(:roll).subcommand(:hinder, &method(:roll_hinder_command))
        bot.application_command(:roll).subcommand(:minion, &method(:roll_minion_command))
        bot.application_command(:roll).subcommand(:lieutenant, &method(:roll_lieutenant_command))
        bot.application_command(:roll).subcommand(:reaction, &method(:roll_reaction_command))
        bot.application_command(:roll).subcommand(:chuck, &method(:roll_chuck_command))
      end
    end

    def register_die_and_modifier_command(cmd, name, description)
      cmd.subcommand(name, description) do |sub|
        sub.string("die_1", "The first die to roll, e.g. d4", required: true)
        sub.string("die_2", "The second die to roll, e.g. d6", required: true)
        sub.string("die_3", "The third die to roll, e.g. d8", required: true)
        sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
      end
    end

    def register_effect_and_die_and_modifier_command(cmd, name, description)
      cmd.subcommand(name, description) do |sub|
        sub.string(
          "effect_die",
          "The effect die to be used.",
          required: true,
          choices: { "min" => :min, "mid" => :mid, "max" => :max }
        )
        sub.string("die_1", "The first die to roll, e.g. d4", required: true)
        sub.string("die_2", "The second die to roll, e.g. d6", required: true)
        sub.string("die_3", "The third die to roll, e.g. d8", required: true)
        sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
      end
    end
  end
end
