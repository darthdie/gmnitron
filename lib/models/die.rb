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
      self.class.roll(size)
    end
  end
end
