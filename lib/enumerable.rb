# frozen_string_literal: true

module Enumerable
  def oxford_join
    case size
    when 0
      ""
    when 1
      first
    when 2
      join(" and ")
    else
      [self[0..-2].join(", "), last].join(", and ")
    end
  end
end
