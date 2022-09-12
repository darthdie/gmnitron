# frozen_string_literal: true

module Commands
  class Roll
    def Roll.register(bot)
      @instance ||= Roll.new(bot)
    end

    def initialize(bot)
      bot.register_application_command(:roll, 'Die rolling commands', server_id: ENV['SLASH_COMMAND_BOT_SERVER_ID']) do |cmd|
        cmd.subcommand(:min, 'Rolls a dice pool and highlights the min die.') do |sub|
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        cmd.subcommand(:mid, 'Rolls a dice pool and highlights the mid die.') do |sub|
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        cmd.subcommand(:max, 'Rolls a dice pool and highlights the max die.') do |sub|
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        # "!overcome" :handler overcome :min-args 4 :max-args 6 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]"

        cmd.subcommand(:overcome, 'Rolls a dice pool and returns the overcome result.') do |sub|
          sub.string('effect_die', 'The effect die to be used in the overcome.', required: true, choices: { 'min' => :min, 'mid' => :mid, 'max' => :max })
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
        bot.application_command(:roll).subcommand(:mid, &method(:roll_mid_command))
        bot.application_command(:roll).subcommand(:max, &method(:roll_max_command))
        bot.application_command(:roll).subcommand(:overcome, &method(:roll_overcome_command))
      end
    end

    def roll_min_command(event)
      rolls = DicePool.roll(event.options, :min)

      content = DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_mid_command(event)
      rolls = DicePool.roll(event.options, :mid)

      content = DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_max_command(event)
      rolls = DicePool.roll(event.options, :max)

      content = DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_overcome_command(event)
      effect_die = event.options['effect_die'].to_sym
      rolls = DicePool.new(event.options).roll(effect_die)

      content = OvercomeFormatter.format(rolls)

      event.respond(content: content)
    end
  end
end
