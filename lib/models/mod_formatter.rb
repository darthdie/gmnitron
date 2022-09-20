# frozen_string_literal: true

module Models
  class ModFormatter
    attr_reader :rolls, :operator

    def self.format(rolls, operator)
      ModFormatter.new(rolls, operator).format
    end

    def initialize(rolls, operator)
      @rolls = rolls
      @operator = operator
    end

    def format
      roll_value = rolls.effect_die.total
      outcome = mod_size_for(roll_value)

      die_display = DicePoolRollFormatter.format(rolls)

      "#{die_display}.\r\n#{outcome}"
    end

    def mod_size_for(roll)
      case roll
      when (1..3)
        "#{operator} 1"
      when (4..7)
        "#{operator} 2"
      when (8..11)
        "#{operator} 3"
      else
        if roll <= 0
          mod_type = operator == "+" ? "bonus" : "penalty"
          return "No #{mod_type} is created."
        end

        "#{operator} 4"
      end
    end
  end
end
