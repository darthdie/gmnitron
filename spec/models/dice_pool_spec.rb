# frozen_string_literal: true

require 'models/dice_pool'
require 'byebug'

RSpec.describe Models::DicePool do
  before(:each) do
    allow_any_instance_of(Object).to receive(:rand) do |range|
      3
    end
  end

  [
    { type: :min, expected_value: 3 },
    { type: :mid, expected_value: 3 },
    { type: :max, expected_value: 3 },
  ].each do |roll_expectations|
    type = roll_expectations[:type]
    expected_value = roll_expectations[:expected_value]

    context type.to_s do
      it "rolls 3 dice without a modifier" do
        options = {
          'die_1' => 'd4',
          'die_2' => 'd8',
          'die_3' => 'd6'
        }
        pool = described_class.new(options)
        rolls = pool.roll(type)
        expect(rolls).to eq(Models::DicePoolRoll.new(
          rolls: [Models::DiceRoll.new(size: 8, value: 3), Models::DiceRoll.new(size: 6, value: 3), Models::DiceRoll.new(size: 4, value: 3)],
          total: expected_value,
          effect_die_value: 3
        ))
        # expect(pool.format_for_display).to eq("Rolled **5** (*d8:* **5**, *d6:* **5**, *d4:* **5**)")
      end

      it "rolls 3 dice with a modifier" do
        options = {
          'die_1' => 'd4',
          'die_2' => 'd8',
          'die_3' => 'd6',
          'modifier' => '+2'
        }
        pool = described_class.new(options)
        rolls = pool.roll(type)
        expect(rolls).to eq(Models::DicePoolRoll.new(
          rolls: [Models::DiceRoll.new(size: 8, value: 3), Models::DiceRoll.new(size: 6, value: 3), Models::DiceRoll.new(size: 4, value: 3)],
          total: expected_value + 2,
          effect_die_value: 3,
          modifier: ['+', 2]
        ))
        # expect(pool.format_for_display).to eq("Rolled **7** = 5 + 2 (*d8:* **5**, *d6:* **5**, *d4:* **5**)")
      end
    end
  end
end
