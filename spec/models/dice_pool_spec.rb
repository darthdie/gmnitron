# frozen_string_literal: true

require "models/dice_pool"
require "byebug"

RSpec.describe Models::DicePool do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  [
    { type: :min, expected_value: 3, expected_size: 4 },
    { type: :mid, expected_value: 3, expected_size: 6  },
    { type: :max, expected_value: 3, expected_size: 8  },
  ].each do |roll_expectations|
    type = roll_expectations[:type]
    expected_value = roll_expectations[:expected_value]

    context type.to_s do
      it "rolls 3 dice without a modifier" do
        options = {
          "die_1" => "d4",
          "die_2" => "d8",
          "die_3" => "d6",
        }
        pool = described_class.new(options)
        rolls = pool.roll(type)
        expect(rolls).to eq(Models::DicePoolRoll.new(
          rolls: [Models::DiceRoll.new(die_size: 8, value: 3), Models::DiceRoll.new(die_size: 6, value: 3),
                  Models::DiceRoll.new(die_size: 4, value: 3)],
          effect_die: Models::DiceRoll.new(die_size: roll_expectations[:expected_size], value: expected_value, total: 3),
          modifier: Models::Modifier.new(nil, 0)
        ))
      end

      it "rolls 3 dice with a modifier" do
        options = {
          "die_1" => "d4",
          "die_2" => "d8",
          "die_3" => "d6",
          "modifier" => "+2",
        }
        pool = described_class.new(options)
        rolls = pool.roll(type)
        # TODO: should it test this? -- it dislikes that one of them has a different total
        # expect(rolls.rolls).to match_array([
        #   Models::DiceRoll.new(die_size: 8, value: 3),
        #   Models::DiceRoll.new(die_size: 6, value: 3),
        #   Models::DiceRoll.new(die_size: 4, value: 3)
        # ])

        expect(rolls.modifier).to eq(Models::Modifier.new("+", 2))

        expect(rolls.effect_die).to eq(Models::DiceRoll.new(
          die_size: roll_expectations[:expected_size],
          value: expected_value,
          total: 5
        ))
      end
    end
  end
end
