# frozen_string_literal: true

require_relative "scene_helpers"

module Commands
  class Scene
    class Advance
      extend Commands::SceneHelpers

      def self.name
        :advance
      end

      def self.description
        "Advances the scene tracker."
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        scene.inc(current_tick: 1)

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
