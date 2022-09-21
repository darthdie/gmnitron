# frozen_string_literal: true

module Models::Formatters
  class LieutenantRollFormatter
    attr_reader :die, :save, :modifier

    def self.format(die, modifier, save: nil)
      new(die, modifier, save).format
    end

    def initialize(die, modifier, save)
      @die = die
      @save = save
      @modifier = modifier
    end

    def format
      [
        "Rolled **#{die.total}**",
        ("= #{die.value} #{modifier.operator} #{modifier.value}" if modifier.present?),
        ("vs. #{save}" if save.present?),
        ("\r\n#{save_outcome}" if save.present?)
      ].compact.join(" ")
    end

    def save_outcome
      failed_save = die.total < save
      return "The Lieutenant is defeated!" if die.die_size <= 4 && failed_save
      return "The Lieutenant is reduced to a d#{die.die_size - 2}." if failed_save

      "The Lieutenant lives another day."
    end
  end
end
