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
          sub.string('effect_die', 'The effect die to be used in the overcome.', required: true, options: { 'min' => 'min', 'mid' => 'mid', 'max' => 'max' })
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
      dice_pool = DicePool.new(event.options. :min)

      # DicePoolFormatter.format(dice_pool, :min)

      event.respond(content: dice_pool.format_for_display)
    end

    def roll_mid_command(event)
      dice_pool = DicePool.new(event.options, :mid)

      event.respond(content: dice_pool.format_for_display)
    end

    def roll_max_command(event)
      dice_pool = DicePool.new(event.options, :max)

      event.respond(content: dice_pool.format_for_display)
    end

    def roll_overcome_command(event)
      dice_pool = DicePool.new(event.options, :max)
      # (defn overcome [data]
      #   (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
      #     (if (is-effect-die? effect-die)
      #       (let [pool (roll-dice-pool [d1 d2 d3] (keyword effect-die) modifiers)
      #           die-display (dice-pool->display pool)
      #           outcome (get-overcome-outcome (:total pool))]
      #         (common/fmt "\r\n#{die-display}.\r\n#{outcome}"))
      #       unknown-effect-die-error)))
    end

    def overcome_outcome_for(roll)
      case roll
      when (1..3) "Action fails, or succeeds with a major twist."
      when (4..7) "Action succeeds, but with a minor twist."
      when (8..11) "Action completely succeeds."
      else
        return "Action utterly, spectacularly fails." if roll <= 0

        "Action succeeds beyond expectations."
      end
    end
  end
end
