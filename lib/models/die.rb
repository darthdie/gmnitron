module Models
  class Die
    def self.roll(size)
      rand(1..size)
    end

    def self.parse(string)
      Die.new(string.scan(/\d+/).first.to_i)
    end

    attr_reader :size

    def initialize(size)
      @size = size
    end

    def roll
      DiceRoll.new(die_size: size, value: self.class.roll(size))
    end
  end

  class DiceRoll
    include Comparable

    attr_reader :die_size, :value, :total

    def initialize(die_size:, value:, total: nil)
      @die_size = die_size
      @value = value
      @total = total || value
    end

    def <=>(other)
      total <=> other.total
    end

    def apply!(modifier)
      return unless modifier.present?

      @total = @total.send(modifier.operator, modifier.value)
    end

    def to_s
      "d#{die_size}: #{roll} (#{total})"
    end
  end
end
