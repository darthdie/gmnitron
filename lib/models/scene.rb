# frozen_string_literal: true

module Models
  class Scene
    include Mongoid::Document

    field :channel_id, type: Integer
    field :green_ticks, type: Integer, default: 1
    field :yellow_ticks, type: Integer, default: 1
    field :red_ticks, type: Integer, default: 1
    field :current_tick, type: Integer, default: 0

    validates :channel_id, presence: true

    has_many :actors, class_name: "Actor", inverse_of: :scene

    def current_actor
      actors.filter { |actor| actor.current }.first
    end

    def acted_actors
      actors.filter { |actor| actor.acted }
    end

    def unacted_actors
      actors.filter { |actor| !actor.acted }
    end

    def is_last_actor?(actor)
      unacted_actors.count == 1 && unacted_actors.first.name == actor.name
    end
  end
end

# (defn establish [data]
#   (let [{arguments :arguments channel-id :channel-id} data
#         [green-ticks yellow-ticks red-ticks] (map common/str->int (take 3 arguments))
#         names (drop 3 arguments)
#         actors (map (fn [actor-name] {:name actor-name :acted false}) names)]
#     (database/insert-scene channel-id {
#       :green-ticks green-ticks
#       :yellow-ticks yellow-ticks
#       :red-ticks red-ticks
#       :current-tick 0
#       :initiative actors
#     })
#     (recap channel-id)))
