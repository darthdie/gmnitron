# frozen_string_literal: true

require "active_support"

module Models
  DiceRoll = Struct.new(:die_size, :value, keyword_init: true)

  DicePoolRoll = Struct.new(:rolls, :total, :effect_die_value, :modifier, keyword_init: true) do
    def modifier?
      modifier.present?
    end
  end

  class DicePool
    attr_reader :modifier

    def self.roll(options, effect_die)
      DicePool.new(options).roll(effect_die)
    end

    def initialize(options)
      @modifier = []
      @dice = (1..3).map { |i| options["die_#{i}"] }

      modifier = options["modifier"]
      @modifier = [modifier[0], modifier[1..].to_i] if modifier.present?
    end

    def die_sizes
      @dice.map { |die| die.scan(/\d+/).first.to_i }
    end

    def modifier?
      modifier.present?
    end

    def roll(effect_die)
      # Move to this to a Struct?
      rolls = die_sizes.map do |die_size|
        DiceRoll.new(die_size: die_size, value: rand(1..die_size))
      end
      rolls = rolls.sort_by(&:value)

      total = effect_die_value(rolls, effect_die)
      total = total.send(modifier.first, modifier.last) if modifier?

      DicePoolRoll.new(
        rolls: rolls,
        total: total,
        effect_die_value: effect_die_value(rolls, effect_die),
        modifier: (modifier if modifier?)
      )
    end

    def effect_die_value(dice_pool, effect_die)
      case effect_die
      when :min
        dice_pool.first
      when :mid
        dice_pool[1]
      when :max
        dice_pool.last
      end.value
    end
  end
end
