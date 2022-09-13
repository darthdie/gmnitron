# frozen_string_literal: true

require 'models/dice_pool'
require 'models/overcome_formatter'
require 'byebug'

RSpec.describe Models::OvercomeFormatter do
  before(:each) do
    allow_any_instance_of(Object).to receive(:rand) do |range|
      3
    end
  end

  it "formats without a modifier" do
    options = {
      'die_1' => 'd4',
      'die_2' => 'd8',
      'die_3' => 'd6'
    }
    rolls = Models::DicePool.new(options).roll(:min)

    message = described_class.format(rolls)
    expect(message).to eq("\r\nRolled **3** (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\nAction fails, or succeeds with a major twist.")
  end

  it "formats with a modifier" do
    options = {
      'die_1' => 'd4',
      'die_2' => 'd8',
      'die_3' => 'd6',
      'modifier' => '+2'
    }
    rolls = Models::DicePool.new(options).roll(:min)

    message = described_class.format(rolls)
    expect(message).to eq("\r\nRolled **5** = 3 + 2 (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\nAction succeeds, but with a minor twist.")
  end
end
