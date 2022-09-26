# frozen_string_literal: true

class DiscordEvent
  def initialize(options)
    @options = options.with_indifferent_access
  end

  def method_missing(m, *args, &block)
    return @options[m.to_sym] if @options.key?(m.to_sym)

    super
  end

  def respond(*args)
  end
end
