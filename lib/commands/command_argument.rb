module Commands
  class CommandArgument
    class << self
      [
        :integer,
        :string
      ].each do |type|
        define_method :"#{type}" do |name:, description:, options: nil|
          new(type, name, description, options)
        end
      end
    end

    attr_reader :type, :name, :description, :options

    def initialize(type:, name:, description:, options: nil)
      @type = type
      @name = name
      @description = description
      @options = options
    end
  end
end
