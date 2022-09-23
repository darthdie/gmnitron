# frozen_string_literal: true

module Commands
  class Scene
    class Ambush
      def self.name
        :ambush
      end

      def self.description
        "Adds an actor to the scene, ready to act."
      end

      def self.arguments
        byebug
        [
          CommandArgument.string(:name, "The actor name.", options: { required: true })
        ]
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        name = event.options[:name]
        scene.actors.push(Actor.new(name: name))

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
