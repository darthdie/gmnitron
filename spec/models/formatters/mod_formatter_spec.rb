# frozen_string_literal: true

RSpec.describe Models::Formatters::ModFormatter do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  context "boost" do
    it "formats without a modifier" do
      options = {
        "die_1" => "d4",
        "die_2" => "d8",
        "die_3" => "d6",
      }
      rolls = Models::DicePool.new(options).roll(:min)

      message = described_class.format(rolls, "+")
      expect(message).to eq("Rolled **3** (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\n+ 1")
    end

    it "formats with a modifier" do
      options = {
        "die_1" => "d4",
        "die_2" => "d8",
        "die_3" => "d6",
        "modifier" => "+2",
      }
      rolls = Models::DicePool.new(options).roll(:min)

      message = described_class.format(rolls, "+")
      expect(message).to eq("Rolled **5** = 3 + 2 (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\n+ 2")
    end
  end

  context "hinder" do
    it "formats without a modifier" do
      options = {
        "die_1" => "d4",
        "die_2" => "d8",
        "die_3" => "d6",
      }
      rolls = Models::DicePool.new(options).roll(:min)

      message = described_class.format(rolls, "-")
      expect(message).to eq("Rolled **3** (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\n- 1")
    end

    it "formats with a modifier" do
      options = {
        "die_1" => "d4",
        "die_2" => "d8",
        "die_3" => "d6",
        "modifier" => "+2",
      }
      rolls = Models::DicePool.new(options).roll(:min)

      message = described_class.format(rolls, "-")
      expect(message).to eq("Rolled **5** = 3 + 2 (*d8:* **3**, *d6:* **3**, *d4:* **3**).\r\n- 2")
    end
  end
end
