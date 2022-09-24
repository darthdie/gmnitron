# frozen_string_literal: true

module Models
  module ActorHelpers
    def sanitize_name(name)
      name.to_s.delete_prefix('"').delete_suffix('"').strip
    end
  end
end
