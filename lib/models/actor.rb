# frozen_string_literal: true

require 'mongoid'

module Models
  class Initiative
    include Mongoid::Document

    field :name, type: String
    field :search_name, type: String
    field :acted, type: Boolean, default: false
    field :current, type: Boolean, default: false

    belongs_to :scene
    attr_readonly :search_name

    def name=(value)
      update_attributes(
        name: value,
        search_name: value.downcase
      )
    end
  end
end

# (defn get-initiative [channel-id]
#   (with-collection @db "initiatives" (find { :channel-id channel-id }) (sort (array-map :acted -1 :name 1))))
