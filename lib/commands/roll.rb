# frozen_string_literal: true

require 'active_support'

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

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
      end
    end

    def parse_modifier(modifier)
      return [] unless modifier.present?

      # modifier.scan(/(\+|\-)\s*(\d+)/)

      [modifier[0], modifier[1..].to_i]
    end

    def parse_dice_and_modifier(options)
      {
        dice: (1..3).map { |i| options["die_#{i}"] },
        modifier: parse_modifier(options['modifier'])
      }
    end

    def normalize_die(die)
      # TODO: do we need the d other than for display?
      # The answer is no, so uh...remove it and re-add in display
      size = die.scan(/\d+/).first
      "d#{size}"
    end

    def effect_die_from_pool(pool, effect_die)
      case effect_die
      when :min
        pool.first
      when :mid
        pool[1]
      when :max
        pool.last
      end
    end

    def roll_dice_pool(dice_pool, effect_die)
      rolls = dice_pool[:dice].map do |raw_die|
        normalized_die = normalize_die(raw_die)
        { 
          die: normalized_die,
          value: roll_die(normalized_die)
        }
      end.sort_by { |roll| roll[:value] }
      effect_die = effect_die_from_pool(rolls, effect_die)

      {
        rolls: rolls,
        effect: effect_die,
        total: sum_roll(effect_die, dice_pool[:modifier]),
        modifiers: dice_pool[:modifier]
      }
    end

    def format_dice_pool(pool)
      rolls = pool[:rolls].map { |roll| "*#{roll[:die]}:* **#{roll[:value]}**" }.join(' ')

      if pool[:modifiers].empty?
        return "Rolled **#{pool[:total]}** (#{rolls})"
      end

      modifier_expression = pool[:modifiers].join(' ') #pool[:modifiers].map { |modifier| modifier.join(' ') }

      "Rolled **#{pool[:total]}** = #{pool[:effect][:value]} #{modifier_expression} (#{rolls})"
    end

    def sum_roll(effect_die, modifier)
      return effect_die[:value] unless modifier.present?

      effect_die[:value].send(modifier.first, modifier.last)
    end

    def roll_die(die)
      rand(1..die[1..].to_i)
    end

    def roll_min_command(event)
      dice_pool = parse_dice_and_modifier(event.options)
      rolls = roll_dice_pool(dice_pool, :min)

      event.respond(content: format_dice_pool(rolls))
    end
  end
end