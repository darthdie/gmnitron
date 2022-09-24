# frozen_string_literal: true

require 'mongoid'

require_relative "actor_helpers"

module Models
  class Actor
    extend Models::ActorHelpers
    include Mongoid::Document

    field :name, type: String
    field :search_name, type: String
    field :acted, type: Boolean, default: false
    field :current, type: Boolean, default: false

    belongs_to :scene, class_name: "Scene", inverse_of: :actor
    attr_readonly :search_name

    def name=(value)
      # TODO: I would love to remove this with some sort of official discord string list argument
      value = sanitize_name(value)
      write_attribute(:name, value)
      write_attribute(:search_name, value.downcase)
    end

    # Jesus fucking christ rspec fine - here's the fucking method you're having so much fucking trouble finding
    def sanitize_name(name)
      name.to_s.delete_prefix('"').delete_suffix('"').strip
    end
  end
end
