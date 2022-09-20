# frozen_string_literal: true

require "models/dice_pool"
require "models/dice_pool_roll_formatter"
require "byebug"

RSpec.describe Models::DicePoolRollFormatter do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  fit "formats without a modifier" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
    }
    rolls = Models::DicePool.new(options).roll(:min)

    message = described_class.format(rolls)
    expect(message).to eq("Rolled **3** (*d8:* **3**, *d6:* **3**, *d4:* **3**)")
  end

  it "formats with a modifier" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
      "modifier" => "+2",
    }
    rolls = Models::DicePool.new(options).roll(:min)

    message = described_class.format(rolls)
    expect(message).to eq("Rolled **5** = 3 + 2 (*d8:* **3**, *d6:* **3**, *d4:* **3**)")
  end
end
