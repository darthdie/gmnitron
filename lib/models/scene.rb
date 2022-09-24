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

    has_many :actors, class_name: "Actor", inverse_of: :scene, dependent: :destroy

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
      unacted_actors.count == 1 && unacted_actors.first.search_name == actor.search_name
    end

    def find_actor(name:)
      name = sanitize_name(name).downcase
      actors.filter { |actor| actor.search_name == name }.first
    end

    # Jesus fucking christ rspec fine - here's the fucking method you're having so much fucking trouble finding
    def sanitize_name(name)
      name.to_s.delete_prefix('"').delete_suffix('"').strip
    end
  end
end
