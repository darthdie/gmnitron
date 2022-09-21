# frozen_string_literal: true

module Commands
  class Scene
    def self.register(bot)
      @register ||= Scene.new(bot)
    end

    def initialize(bot)
      register_commands(bot)
    end

    def register_commands(bot)
      bot.register_application_command(:session, "Session commands") do |cmd|
      end
      # { :command "!hand" :handler hand-command :min-args 3 :max-args 3 :usage "!hand off (actor name) (actor to go next) OR !hand off to (actor to go next)" :description "Hands off the scene to the next actor" }
      # { :command "!establish" :handler establish :min-args 4 :usage "!establish (number of green ticks) (number of yellow ticks) (number of red ticks) (actors)" :description "Sets up the scene with specified number of ticks and actors." }
      # { :command "!recap" :handler recap-handler :max-args 0 :usage "!recap" :description "Displays the current scene and initiative status." }
      # { :command "!pass" :handler pass }
      # { :command "!advance" :handler tick :max-args 0 :usage "!advance" :description "Advances the scene tracker." }
      # { :command ["!introduce" "!add"] :handler introduce :min-args 1 :usage "!introduce Citizen Dawn" :description "Adds an actor to the scene/initiative, ready to act *next* round." }
      # { :command "!ambush", :handler ambush :usage "!ambush Baron Blade" :description "Adds an actor to the scene/initiative, ready to act." }
      # { :command ["!erase" "!remove"] :handler erase :min-args 1 :usage "!erase Big Baddie" :description "Removes an actor from the scene/initiative." }
      # { :command "!current" :handler current-command :usage "!current" :description "Displays the current actor." }
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

    def establish_command(event)
      green_ticks = event.options["green_ticks"]
      yellow_ticks = event.options["yellow_ticks"]
      red_ticks = event.options["red_ticks"]
      actors = event.options["actors"].split.map do |name|
        Actor.new(
          name: name.delete_prefix('"').delete_suffix('"')
        )
      end

      Models::Scene.create!(
        channel_id: event.channel_id,
        green_ticks: green_ticks,
        yellow_ticks: yellow_ticks,
        red_ticks: red_ticks,
        actors: todo
      )

      recap_command(event)
    end

    def recap_command(event)
      scene = Models::Scene.where(channel_id: event.channel_id).first
      if scene.present?
        return
      end

      event.respond(
        content: "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE /scene establish COMMAND."
      )

    #   (defn get-initiative-recap [scene]
    #     (let [initiative (group-by :acted (filter #(= (get % :current false) false) (get scene :initiative)))
    #           current-actor (first (filter #(= (get % :current false) true) (get scene :initiative)))
    #           current-actor-display (if current-actor (str "**" (str (:name current-actor)) "** is the current actor.") nil)
    #           acted (str/join "\r\n" (map actor->display (get initiative true [])))
    #           unacted (str/join "\r\n" (map actor->display (get initiative false [])))]
    #       (str/join "\r\n\r\n" (filter #(> (count %) 0) [current-actor-display acted unacted]))))

    # (defn get-scene-recap [scene]
    #   (let [{green :green-ticks yellow :yellow-ticks red :red-ticks tick :current-tick} scene
    #         boxes (concat (replicate green "Green") (replicate yellow "Yellow") (replicate red "Red"))]
    #     (if (>= tick (count boxes))
    #       "The scene has reached its end."
    #       (let [current-box (nth boxes tick)
    #             remaining-boxes (->> (drop tick boxes)
    #               (frequencies)
    #               (map #(str (second %) " " (first %) " boxes"))
    #               (common/oxford))]
    #         (common/fmt "It is currently a #{current-box} status. There are #{remaining-boxes} left.")))))
    end
  end
end
