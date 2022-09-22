# frozen_string_literal: true

module Models::Formatters
  class ReactionRollFormatter
    attr_reader :die, :modifier

    def self.format(die, modifier)
      new(die, modifier).format
    end

    def initialize(die, modifier)
      @die = die
      @modifier = modifier
    end

    def format
      [
        "Rolled **#{die.total}**",
        ("= #{die.value} #{modifier.operator} #{modifier.value}" if modifier.present?),
      ].compact.join(" ")
    end
  end
end
