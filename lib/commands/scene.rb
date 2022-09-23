# frozen_string_literal: true

Dir["./lib/commands/scene/*.rb"].sort.each { |file| require file }

module Commands
  class Scene
    def self.register(bot)
      @register ||= Scene.new(bot)
    end

    def initialize(bot)
      register_commands(bot)
    end

    def register_commands(bot)
      bot.register_application_command(:session, "Session commands", server_id: ENV.fetch("DISCORD_TEST_SERVER_ID", nil)) do |cmd|
        # cmd.subcommand(:establish, "Establish a scene with a tracker and actors.") do |sub|
        #   sub.integer("green_ticks", "The number of green ticks in a scene.", required: true)
        #   sub.integer("yellow_ticks", "The number of yellow ticks in a scene.", required: true)
        #   sub.integer("red_ticks", "The number of red ticks in a scene.", required: true)
        #   sub.string("actors", 'A list of actors to start with in the scene, e.g. "Baron Blade" Legacy Wraith "Absolute Zero"')
        # end
        # bot.application_command(:session).subcommand(:establish, &method(:establish_command))

        # cmd.subcommand(:recap, "Display a summary of the current scene and its initiative.")
        # bot.application_command(:session).subcommand(:recap, &method(:recap_command))

        # cmd.subcommand(:advance, "Advances the scene tracker.")
        # bot.application_command(:session).subcommand(:recap, &method(:advance_command))

        # cmd.subcommand(:current, "Displays the current actor.")
        # bot.application_command(:session).subcommand(:current, &method(:current_command))

        Commands::Scene.constants
          .select {|c| Commands::Scene.const_get(c).is_a? Class}
          .map { |c| Commands::Scene.const_get(c) }
          .each do |command|
            cmd.subcommand(command.name, command.description) do |sub|
              next unless command.respond_to?(:arguments) && command.arguments.present?

              command.arguments.to_a.each do |argument|
                sub.send(argument.type, argument.name, argument.description, *argument.options)
              end
            end

            bot.application_command(:session).subcommand(command.name) { |event| command.handle(event) }
          end

        # cmd.subcommand(:ambush, "Adds an actor to the scene/initiative, ready to act.")
        # bot.application_command.
      end
      # { :command "!hand" :handler hand-command :min-args 3 :max-args 3 :usage "!hand off (actor name) (actor to go next) OR !hand off to (actor to go next)" :description "Hands off the scene to the next actor" }
    end

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

    # (defn hand-off-command [data]
    #   (let [{arguments :arguments channel-id :channel-id} data]
    #     (if (= (str/lower-case (first arguments)) "to")
    #       (hand-off-to (assoc data :arguments (rest arguments)))
    #       (hand-off data))))

    # (defn hand-command [data]
    #   (let [{arguments :arguments channel-id :channel-id} data]
    #     (when (= (str/lower-case (first arguments)) "off")
    #       (hand-off-command (assoc data :arguments (rest arguments))))))

    def hand_off_command(event)
    end

    # def establish_command(event)
    #   green_ticks = event.options["green_ticks"]
    #   yellow_ticks = event.options["yellow_ticks"]
    #   red_ticks = event.options["red_ticks"]
    #   actors = event.options["actors"].split.map do |name|
    #     Actor.new(
    #       name: name
    #     )
    #   end

    #   Models::Scene.create!(
    #     channel_id: event.channel_id,
    #     green_ticks: green_ticks,
    #     yellow_ticks: yellow_ticks,
    #     red_ticks: red_ticks,
    #     actors: todo
    #   )

    #   recap_command(event)
    # end

    # def recap_command(event)
    #   scene = scene_for_channel(event)

    #   return respond_with_no_scene(event) unless scene.present?

    #   content = Models::SceneRecapFormatter.format(scene)
    #   return event.respond(content: content)
    # end

    # def advance_command(event)
    #   scene = scene_for_channel(event)
    #   return respond_with_no_scene(event) unless scene.present?

    #   scene.inc(current_tick: 1)

    #   recap_command(event)
    # end

    # def current_command(event)
    #   scene = scene_for_channel(event)

    #   return respond_with_no_scene(event) unless scene.present?

    #   current_actor = scene.actors.filter { |actor| actor.current }.first
    #   content = if current_actor.present?
    #     "**#{current_actor.name}** is the current actor."
    #   else
    #     "There is no current actor."
    #   end

    #   event.respond(content: content)
    # end

    # def scene_for_channel(event)
    #   Models::Scene.where(channel_id: event.channel_id).first
    # end

    # def respond_with_no_scene(event)
    #   event.respond(
    #     content: "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE /scene establish COMMAND."
    #   )
    # end
  end
end
