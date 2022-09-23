# frozen_string_literal: true

module Commands
  class Scene
    class Hand
      extend Commands::SceneHelpers

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
          CommandArgument.string(:from, "The name of the actor to pass from, or 'to' if you're the current actor.", options: { required: true }),
          CommandArgument.string(:to, "The name of the actor to pass to.", options: { required: true }),
        ]
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        # validate that the from actor is the current actor
        # validate that we have actors?
        # validate that the from actor isn't the last one AND the to hasn't already acted
        # NEW: validate that that from actor isn't the same as the to actor

        from_name = event.options[:from].downcase.strip
        from = block do
          if from_name == "to"
            from_name = "<@#{event.author.id}>" if from_name == "to"
            # actor-name (str "<@" (get-in data [:author "id"]) ">"
            # actor-name becomes "<@AUTHOR_ID>"
            # find actor for current user
          else
            scene.actors.filter { |actor| actor.search_name == from_name }.first
          end
        end
        to_name = event.options[:to].downcase.strip
        to = scene.actors.filter { |actor| actor.search_name == to_name }.first

        error_message = block do
          next ERRORS[:actors_not_found] unless from.present? && to.present?

          if !scene.current_actor.present? || scene.current_actor.search_name != from.search_name
            next ERRORS[:not_current_actor]
          end

          if !scene.is_last_actor?(from) && to.acted
            next ERRORS[:already_acted]
          end

          if from.search_name == to.search_name
            next ERRORS[:cannot_pass_to_self]
          end
        end

        return event.respond(content: error_message, ephemeral: true) if error_message.present?

        # !hand off (actor name) (actor to go next) OR !hand off to (actor to go next)


        # (defn validate-hand-off [channel-id from to]
        #   (cond
        #     (and (database/has-current-actor? channel-id) (not (database/is-current-actor? channel-id from))) not-current-actor-message
        #     (not (database/has-actors-in-scene? channel-id [from to])) no-actor-message
        #     (and (not (database/is-last-actor? channel-id from)) (database/actor-has-acted? channel-id to)) actor-already-acted-message))

        # (defn hand-off-to [data]
        #   (let [{arguments :arguments channel-id :channel-id} data
        #         hand-off-to (str/join " " arguments)
        #         actor-name (str "<@" (get-in data [:author "id"]) ">")]
        #     (when (database/has-scene? channel-id)
        #       (if-let [error (validate-hand-off channel-id actor-name hand-off-to)]
        #         error
        #         (do
        #           (database/hand-off channel-id actor-name hand-off-to)
        #           (recap channel-id))))))

        # (defn hand-off [data]
        #   (println data)
        #   (let [{arguments :arguments channel-id :channel-id} data
        #         [actor-name hand-off-to] arguments]
        #     (if (database/has-scene? channel-id)
        #       (if-let [error (validate-hand-off channel-id actor-name hand-off-to)]
        #         error
        #         (do
        #           (database/hand-off channel-id actor-name hand-off-to)
        #           (recap channel-id)))
        #       no-scene-message)))


        # scene.inc(current_tick: 1)

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
