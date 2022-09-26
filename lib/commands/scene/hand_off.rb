# frozen_string_literal: true

module Commands
  class Scene
    class HandOff
      extend Commands::SceneHelpers
      extend Models::ActorHelpers

      ERRORS = {
        already_acted: "ERROR. ACTOR HAS ALREADY GONE THIS INITIATIVE.",
        actors_not_found: "ERROR. UNABLE TO ACCESS ACTOR. USE THE /introduce COMMAND TO ADD.",
        not_current_actor: "ERROR. ILLEGAL INSTRUCTION. ONLY CURRENT ACTOR MAY PASS OFF.",
        cannot_pass_to_self: "ERROR. ILLEGAL INSTRUCTION. YOU MAY NOT HAND OFF TO YOURSELF."
      }

      def self.name
        :hand_off
      end

      def self.description
        "Hands off the scene to the next actor."
      end

      def self.arguments
        [
          CommandArgument.string("from", "The name of the actor to pass from, or 'to' if you're the current actor.", options: { required: true }),
          CommandArgument.string("to", "The name of the actor to pass to.", options: { required: true }),
        ]
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        from_name = sanitize_name(event.options["from"])
        from_name = "<@!#{event.user.id}>" if from_name == "to"
        from = scene.find_actor(name: from_name)

        to = scene.find_actor(name: event.options["to"])

        error_message = validate_hand_off(scene, from, to)
        return event.respond(content: error_message, ephemeral: true) if error_message.present?

        from.update(acted: true)
        if scene.actors.all? { |actor| actor.acted }
          Models::Actor.where(scene: scene).update_all(acted: false)
        end

        Models::Actor.where(scene: scene).update_all(current: false)
        to.update(current: true)

        respond_with_scene_recap(event, scene)
      end

      def self.validate_hand_off(scene, from, to)
        return ERRORS[:actors_not_found] unless from.present? && to.present?

        if scene.current_actor.present? && scene.current_actor.search_name != from.search_name
          return ERRORS[:not_current_actor]
        end

        if !scene.is_last_actor?(from) && to.acted
          return ERRORS[:already_acted]
        end

        if from.search_name == to.search_name
          return ERRORS[:cannot_pass_to_self]
        end
      end
    end
  end
end
