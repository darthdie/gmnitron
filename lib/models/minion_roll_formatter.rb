# frozen_string_literal: true

module Models
  class MinionRollFormatter
    attr_reader :die, :save, :modifier

    def self.format(die, modifier, save: nil)
      MinionRollFormatter.new(die, modifier, save).format
    end

    def initialize(die, modifier, save)
      @die = die
      @save = save
      @modifier = modifier
    end

    def format
      die.apply!(modifier)

      [
        "Rolled **#{die.total}**",
        ("= #{die.value} #{modifier.operator} #{modifier.value}" if modifier.present?),
        ("vs. #{save}" if save.present?),
        (save_outcome if save.present?)
      ].compact.join(" ")
    end

    def save_outcome
      return "The Minion is defeated!" if die.die_size < 4 || die.total < save
      return "The Minion survives to do more evil." if die.die_size == 4

      "The Minion is reduced to a d#{die.die_size - 2}."
    end
  end
end
