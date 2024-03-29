# frozen_string_literal: true

RSpec.describe Models::Formatters::OvercomeFormatter do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  it "formats without a modifier" do
    options = {
      "die_1" => "d4",
      "die_2" => "d8",
      "die_3" => "d6",
    }
    rolls = Models::DicePool.new(options).roll(:min)

    message = described_class.format(rolls)
    expected_message = [
      "Rolled **3** (*d8:* **3**, *d6:* **3**, *d4:* **3**).",
      "Action fails, or succeeds with a major twist."
    ].join("\r\n")
    expect(message).to eq(expected_message)
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
    expected_message = [
      "Rolled **5** = 3 + 2 (*d8:* **3**, *d6:* **3**, *d4:* **3**).",
      "Action succeeds, but with a minor twist."
    ].join("\r\n")
    expect(message).to eq(expected_message)
  end
end
