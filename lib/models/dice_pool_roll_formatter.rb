# frozen_string_literal: true

module Models
  class DicePoolRollFormatter
    attr_reader :rolls

    def self.format(rolls)
      DicePoolRollFormatter.new(rolls).format
    end

    def initialize(rolls)
      @rolls = rolls
    end

    def format
      total_value = rolls.total
      dice_pool_display = rolls.rolls.map { |roll| "*d#{roll.die_size}:* **#{roll.value}**" }.join(", ")

      unless rolls.modifier?
        return "Rolled **#{total_value}** (#{dice_pool_display})"
      end

      effect_die_value = rolls.effect_die_value
      modifier_display = rolls.modifier.join(" ")

      "Rolled **#{total_value}** = #{effect_die_value} #{modifier_display} (#{dice_pool_display})"
    end
  end
end
