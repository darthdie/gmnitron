# frozen_string_literal: true

RSpec.describe Models::Formatters::MinionRollFormatter do
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

  it "defeats a minion" do
    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil, save: 6)
    expect(message).to eq("Rolled **3** vs. 6 \r\nThe Minion is defeated!")
  end

  it "a minion can survive" do
    die = Models::Die.parse("d4").roll

    message = described_class.format(die, nil, save: 2)
    expect(message).to eq("Rolled **3** vs. 2 \r\nThe Minion survives to do more evil.")
  end

  it "a minion be reduced" do
    die = Models::Die.parse("d6").roll

    message = described_class.format(die, nil, save: 2)
    expect(message).to eq("Rolled **3** vs. 2 \r\nThe Minion is reduced to a d4.")
  end
end
