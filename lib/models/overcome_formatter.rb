# frozen_string_literal: true

module Models
  class OvercomeFormatter
    attr_reader :rolls

    def self.format(rolls)
      OvercomeFormatter.new(rolls).format()
    end

    def initialize(rolls)
      @rolls = rolls
    end

    def format()
      roll_value = rolls.total
      outcome = overcome_outcome_for(roll_value)

      die_display = DicePoolRollFormatter.format(rolls)

      "\r\n#{die_display}.\r\n#{outcome}"
    end

    def overcome_outcome_for(roll)
      case roll
      when (1..3)
        "Action fails, or succeeds with a major twist."
      when (4..7)
        "Action succeeds, but with a minor twist."
      when (8..11)
        "Action completely succeeds."
      else
        return "Action utterly, spectacularly fails." if roll <= 0

        "Action succeeds beyond expectations."
      end
    end
  end
end
