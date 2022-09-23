# frozen_string_literal: true

module Commands
  class Scene
    class Ambush
      extend Commands::SceneHelpers

      def self.name
        :recap
      end

      def self.description
        "Display a summary of the current scene and its initiative."
      end

      def self.handle(event)
        scene = scene_for_channel(event)

        return respond_with_no_scene(event) unless scene.present?

        content = Models::SceneRecapFormatter.format(scene)
        event.respond(content: content)
      end
    end
  end
end
