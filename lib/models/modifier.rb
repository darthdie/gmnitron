module Models
  class Modifier
    def self.parse(string)
      Modifier.new(string[0], string[1..].to_i)
    end

    attr_reader :operator, :value

    def initialize(operator, value)
      @operator = operator
      @value = value
    end

    def present?
      operator.present?
    end
  end
end
