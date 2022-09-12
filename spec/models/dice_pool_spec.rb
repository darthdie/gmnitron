# frozen_string_literal: true

require 'models/dice_pool'
require 'byebug'

RSpec.describe DicePool do
  before(:each) do
    allow_any_instance_of(Object).to receive(:rand).and_return(5)
  end

  %i[min mid max].each do |type|
    context type.to_s do
      it "rolls 3 dice without a modifier" do
        options = {
          'die_1' => 'd4',
          'die_2' => 'd8',
          'die_3' => 'd6'
        }
        pool = described_class.new(options, type)
        expect(pool.format_for_display).to eq("Rolled **5** (*d8:* **5**, *d6:* **5**, *d4:* **5**)")
      end
  
      it "rolls 3 dice with a modifier" do
        options = {
          'die_1' => 'd4',
          'die_2' => 'd8',
          'die_3' => 'd6',
          'modifier' => '+2'
        }
        pool = described_class.new(options, type)
        expect(pool.format_for_display).to eq("Rolled **7** = 5 + 2 (*d8:* **5**, *d6:* **5**, *d4:* **5**)")
      end
    end
  end
end
  