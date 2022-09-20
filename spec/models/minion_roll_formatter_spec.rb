# frozen_string_literal: true

require "models/dice_pool"
require "models/dice_pool_roll_formatter"
require "byebug"

RSpec.describe Models::MinionRollFormatter do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  fit "formats without a modifier" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
    }

    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil)
    expect(message).to eq("Rolled **3**")
  end

  it "formats with a modifier" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
      "modifier" => "+2",
    }

    die = Models::Die.parse("d6").roll
    modifier = Models::Modifier.new("+", 4)

    message = described_class.format(die, modifier)
    expect(message).to eq("Rolled **7** = 3 + 4")
  end

  it "defeats a minion" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
      "modifier" => "+2",
    }

    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil, save: 6)
    expect(message).to eq("Rolled **3** vs. 6 The Minion is defeated!")
  end

  it "a minion can survive" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
      "modifier" => "+2",
    }

    die = Models::Die.parse("d4").roll

    message = described_class.format(die, nil, save: 2)
    expect(message).to eq("Rolled **3** vs. 2 The Minion survives to do more evil.")
  end

  it "a minion be reduced" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
      "modifier" => "+2",
    }

    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil, save: 2)
    expect(message).to eq("Rolled **3** vs. 2 The Minion is reduced to a d4.")
  end
end
