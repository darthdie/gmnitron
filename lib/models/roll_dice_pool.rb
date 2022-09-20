module Models
  class RollDicePool
    def self.call(dice_pool)
      rolls = dice_pool.map do |die|
        DiceRoll.new(die_size: die_size, value: die.roll)
      end

      RollDicePool.new(rolls.sort_by(&:value))
    end

    def initialize(rolls)
      @rolls = rolls
    end

    def die_value(die_type)
      case die_type
      when :min
        rolls.first
      when :mid
        rolls[1]
      when :max
        rolls.last
      end.value
    end
  end
end
