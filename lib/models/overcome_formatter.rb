# frozen_string_literal: true

class OvercomeFormatter
  def initialize(dice_pool)
    @dice_pool = dice_pool
  end

  def format(effect_die)
    roll_value = @dice_pool.fetch(effect_die)
    outcome = overcome_outcome_for(roll_value)

    die_display = "Rolled a 2 idk" #DicePoolFormatter.format()

    "\r\n#{die_display}.\r\n#{outcome}"
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