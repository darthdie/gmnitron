# frozen_string_literal: true

RSpec.describe Models::Formatters::LieutenantRollFormatter do
  before do
    allow_any_instance_of(Object).to receive(:rand).and_return(3)
  end

  fit "formats without a modifier" do
    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil)
    expect(message).to eq("Rolled **3**")
  end

  it "formats with a modifier" do
    die = Models::Die.parse("d6").roll
    modifier = Models::Modifier.new("+", 4)
    die.apply!(modifier)

    message = described_class.format(die, modifier)
    expect(message).to eq("Rolled **7** = 3 + 4")
  end

  it "defeats a lieutenant" do
    die = Models::Die.parse("d4").roll

    message = described_class.format(die, nil, save: 6)
    expect(message).to eq("Rolled **3** vs. 6 \r\nThe Lieutenant is defeated!")
  end

  it "a lieutenant can survive" do
    die = Models::Die.parse("d4").roll

    message = described_class.format(die, nil, save: 2)
    expect(message).to eq("Rolled **3** vs. 2 \r\nThe Lieutenant lives another day.")
  end

  it "a lieutenant be reduced" do
    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil, save: 4)
    expect(message).to eq("Rolled **3** vs. 4 \r\nThe Lieutenant is reduced to a d4.")
  end
end
