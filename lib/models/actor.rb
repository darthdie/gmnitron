# frozen_string_literal: true

require 'mongoid'

module Models
  class Actor
    include Mongoid::Document

    field :name, type: String
    field :search_name, type: String
    field :acted, type: Boolean, default: false
    field :current, type: Boolean, default: false

    belongs_to :scene, class_name: "Scene", inverse_of: :actor
    attr_readonly :search_name

    def name=(value)
      # Remove quotes in the event it's a quoted name
      # TODO: I would love to remove this with some sort of official discord list argument
      value = value.delete_prefix('"').delete_suffix('"')
      write_attribute(:name, value)
      write_attribute(:search_name, value.downcase)
    end
  end
end

# (defn get-initiative [channel-id]
#   (with-collection @db "initiatives" (find { :channel-id channel-id }) (sort (array-map :acted -1 :name 1))))
