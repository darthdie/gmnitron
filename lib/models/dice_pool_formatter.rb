# frozen_string_literal: true

class DicePoolFormatter
  def self.format(dice_pool, effect_die)
    DicePoolFormatter.new(dice_pool).format(effect_die)
  end

  def initialize(dice_pool)
    @dice_pool = dice_pool
  end

  def format(effect_die)
  end
end
