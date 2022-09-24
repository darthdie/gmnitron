# frozen_string_literal: true

module Commands
  class Scene
    class Introduce
      extend Commands::SceneHelpers

      def self.name
        :introduce
      end

      def self.description
        "Adds an actor to the scene, ready to act *next* round."
      end

      def self.arguments
        [
          CommandArgument.string("name", "The actor name.", options: { required: true })
        ]
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        name = event.options["name"]
        scene.actors.push(Models::Actor.new(name: name, acted: true))

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
