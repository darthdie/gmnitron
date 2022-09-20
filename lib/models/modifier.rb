# frozen_string_literal: true

module Models
  class Modifier
    include Comparable
    def self.parse(string)
      string ||= ""
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

    def numeric_value
      0.send(operator, value)
    end

    def ==(other)
      [operator, value] == [other.operator, other.value]
    end

    # def <=>(other)
    #   return true if !present? && !other.present?
    #   [operator, value] <=> [other.operator, other.value]
    # end
  end
end
