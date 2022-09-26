# frozen_string_literal: true

module Commands
  class Scene
    class Remove
      extend Commands::SceneHelpers
      extend Models::ActorHelpers

      def self.name
        :remove
      end

      def self.description
        "Removes an actor from the scene."
      end

      def self.arguments
        [
          CommandArgument.string("name", "The actor name.", options: { required: true })
        ]
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        actor = scene.find_actor(name: event.options["name"])
        unless actor.present?
          return respond_with_error(event, "ERROR. UNABLE TO ACCESS SCENE OR ACTOR. USE !establish OR !introduce COMMANDS TO CREATE.")
        end

        actor.destroy

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
