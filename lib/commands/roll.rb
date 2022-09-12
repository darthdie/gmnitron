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

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
        bot.application_command(:roll).subcommand(:mid, &method(:roll_mid_command))
        bot.application_command(:roll).subcommand(:max, &method(:roll_max_command))
      end
    end

    def roll_min_command(event)
      dice_pool = DicePool.new(event.options, :min)

      # dice_pool = parse_dice_and_modifier(event.options)
      # rolls = roll_dice_pool(dice_pool, :min)

      event.respond(content: dice_pool.format_for_display)
    end

    def roll_mid_command(event)
      dice_pool = parse_dice_and_modifier(event.options)
      rolls = roll_dice_pool(dice_pool, :mid)

      event.respond(content: format_dice_pool(rolls))
    end

    def roll_max_command(event)
      dice_pool = parse_dice_and_modifier(event.options)
      rolls = roll_dice_pool(dice_pool, :max)

      event.respond(content: format_dice_pool(rolls))
    end

    def parse_dice_and_modifier(options)
      {
        dice: (1..3).map { |i| options["die_#{i}"] },
        modifier: parse_modifier(options['modifier'])
      }
    end

    def parse_modifier(modifier)
      return [] unless modifier.present?

      # modifier.scan(/(\+|\-)\s*(\d+)/)

      [modifier[0], modifier[1..].to_i]
    end

    def roll_dice_pool(dice_pool, effect_die)
      rolls = dice_pool[:dice].map do |raw_die|
        size = die_size(raw_die)
        { 
          size: size,
          value: roll_die(size)
        }
      end.sort_by { |roll| roll[:value] }

      rolled_effect_die = effect_die_from_pool(rolls, effect_die)

      {
        rolls: rolls,
        effect: rolled_effect_die,
        total: sum_roll(rolled_effect_die, dice_pool[:modifier]),
        modifiers: dice_pool[:modifier]
      }
    end

    def die_size(die)
      die.scan(/\d+/).first.to_i
    end
    
    def roll_die(die_size)
      rand(1..die_size)
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

    def sum_roll(effect_die, modifier)
      return effect_die[:value] unless modifier.present?

      effect_die[:value].send(modifier.first, modifier.last)
    end

    def format_dice_pool(pool)
      rolls = pool[:rolls].map { |roll| "*d#{roll[:size]}:* **#{roll[:value]}**" }.join(' ')

      if pool[:modifiers].empty?
        return "Rolled **#{pool[:total]}** (#{rolls})"
      end

      modifier_expression = pool[:modifiers].join(' ') #pool[:modifiers].map { |modifier| modifier.join(' ') }

      "Rolled **#{pool[:total]}** = #{pool[:effect][:value]} #{modifier_expression} (#{rolls})"
    end
  end
end