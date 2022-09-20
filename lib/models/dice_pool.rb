# frozen_string_literal: true

require "active_support"

module Models
  DicePoolRoll = Struct.new(:rolls, :effect_die, :modifier, keyword_init: true) do
    def modifier?
      modifier.present?
    end

    def to_s
      "lol butts"
    end
  end

  class DicePool
    attr_reader :modifier, :dice

    def self.roll(options, effect_die)
      DicePool.new(options).roll(effect_die)
    end

    def initialize(options)
      @dice = (1..3).map { |i| Die.parse(options["die_#{i}"]) }
      @modifier = Modifier.parse(options["modifier"])
    end

    def roll(effect_die)
      rolls = dice.map(&:roll)

      rolls = rolls.sort_by(&:value)

      effective_die = effect_die(rolls, effect_die)
      effective_die.apply!(modifier)

      DicePoolRoll.new(
        rolls: rolls,
        effect_die: effective_die,
        modifier: modifier
      )
    end

    def effect_die(dice_pool, effect_die)
      case effect_die
      when :min
        dice_pool.first
      when :mid
        dice_pool[1]
      when :max
        dice_pool.last
      end
    end
  end
end
