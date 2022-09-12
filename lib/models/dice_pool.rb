# frozen_string_literal: true

require 'active_support'

class DicePool
    attr_reader :modifier

    def initialize(options, effect_die)
      @modifier = []
      @dice = (1..3).map { |i| options["die_#{i}"] }
      @effect_die_type = effect_die

      modifier = options['modifier']
      if modifier.present?
        @modifier = [modifier[0], modifier[1..].to_i]
      end
    end

    def dice_pool
      @dice_pool ||= die_sizes.map do |die_size|
        { 
          size: die_size,
          value: rand(1..die_size)
        }
      end.sort_by { |roll| roll[:value] }
    end

    def die_sizes
      @dice.map { |die| die.scan(/\d+/).first.to_i }
    end

    def has_modifier?
      modifier.present?
    end

    def total_value
      return effect_die_value unless has_modifier?

      effect_die_value.send(modifier.first, modifier.last)
    end

    def effect_die_value
      die = case @effect_die_type
      when :min
        dice_pool.first
      when :mid
        dice_pool[1]
      when :max
        dice_pool.last
      end
      
      die[:value]
    end

    def format_for_display
      unless has_modifier?
        return "Rolled **#{total_value}** (#{dice_pool_display})"
      end

      "Rolled **#{total_value}** = #{effect_die_value} #{modifier_display} (#{dice_pool_display})"
    end

    def dice_pool_display
      dice_pool.map { |roll| "*d#{roll[:size]}:* **#{roll[:value]}**" }.join(', ')
    end

    def modifier_display
      @modifier.join(' ')
    end
  end