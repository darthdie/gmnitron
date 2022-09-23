# frozen_string_literal: true

module Commands
  class Scene
    class Remove
      def self.name
        :remove
      end

      def self.description
        "Removes an actor from the scene."
      end

      def self.arguments
        [
          CommandArgument.string(:name, "The actor name.", options: { required: true })
        ]
      end

      def self.handle(event)
        # TODO: test this
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        name = event.options[:name]
        scene.actors.pull(name: name)

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
