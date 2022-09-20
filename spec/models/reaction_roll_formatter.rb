# frozen_string_literal: true

RSpec.describe Models::ReactionRollFormatter do
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
end
