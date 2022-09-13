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

        cmd.subcommand(:overcome, 'Rolls a dice pool and returns the overcome result.') do |sub|
          sub.string('effect_die', 'The effect die to be used.', required: true, choices: { 'min' => :min, 'mid' => :mid, 'max' => :max })
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        cmd.subcommand(:boost, 'Rolls a dice pool and returns the boost result.') do |sub|
          sub.string('effect_die', 'The effect die to be used.', required: true, choices: { 'min' => :min, 'mid' => :mid, 'max' => :max })
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        cmd.subcommand(:hinder, 'Rolls a dice pool and returns the hinder result.') do |sub|
          sub.string('effect_die', 'The effect die to be used.', required: true, choices: { 'min' => :min, 'mid' => :mid, 'max' => :max })
          sub.string('die_1', 'The first die to roll, e.g. d4', required: true)
          sub.string('die_2', 'The second die to roll, e.g. d6', required: true)
          sub.string('die_3', 'The third die to roll, e.g. d8', required: true)
          sub.string('modifier', 'The modifier to apply to the roll, e.g. +2')
        end

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
        bot.application_command(:roll).subcommand(:mid, &method(:roll_mid_command))
        bot.application_command(:roll).subcommand(:max, &method(:roll_max_command))
        bot.application_command(:roll).subcommand(:overcome, &method(:roll_overcome_command))
        bot.application_command(:roll).subcommand(:boost, &method(:roll_boost_command))
        bot.application_command(:roll).subcommand(:hinder, &method(:roll_hinder_command))
      end
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
      effect_die = event.options['effect_die'].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::OvercomeFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_boost_command(event)
      effect_die = event.options['effect_die'].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "+")

      event.respond(content: content)
    end

    def roll_hinder_command(event)
      effect_die = event.options['effect_die'].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "-")

      event.respond(content: content)
    end

    # (defn get-mod-size [result operator]
    #   (cond
    #     (<= result 0) "No bonus or penalty is created."
    #     (<= 1 result 3) (str operator 1)
    #     (<= 4 result 7) (str operator 2)
    #     (<= 8 result 11) (str operator 3)
    #     (>= result 12) (str operator 4)))

    # (defn roll-mod [effect-die dice modifiers operator]
    #   (if (is-effect-die? effect-die)
    #     (let [pool (roll-dice-pool dice (keyword effect-die) modifiers)
    #         die-display (dice-pool->display pool)
    #         mod-size (get-mod-size (:total pool) operator)]
    #       (common/fmt "\r\n#{die-display}.\r\nMod size: #{mod-size}"))
    #     unknown-effect-die-error))

    # (defn boost [data]
    #   (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    #     (roll-mod effect-die [d1 d2 d3] modifiers "+")))

    # (defn hinder [data]
    #   (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    #     (roll-mod effect-die [d1 d2 d3] modifiers "-")))
  end
end
