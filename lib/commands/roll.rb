# frozen_string_literal: true

module Commands
  class Roll
    def self.register(bot)
      @register ||= Roll.new(bot)
    end

    def initialize(bot)
      register_commands(bot)
    end

    def roll_min_command(event)
      rolls = Models::DicePool.roll(event.options, :min)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_mid_command(event)
      rolls = Models::DicePool.roll(event.options, :mid)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_max_command(event)
      rolls = Models::DicePool.roll(event.options, :max)

      content = Models::DicePoolRollFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_overcome_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::OvercomeFormatter.format(rolls)

      event.respond(content: content)
    end

    def roll_boost_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "+")

      event.respond(content: content)
    end

    def roll_hinder_command(event)
      effect_die = event.options["effect_die"].to_sym
      rolls = Models::DicePool.new(event.options).roll(effect_die)

      content = Models::ModFormatter.format(rolls, "-")

      event.respond(content: content)
    end

    def roll_minion_command(event)
      die = Die.parse(event.options['die']).roll
      modifier = Modifier.parse(event.options['modifier'])
      # total = roll + modifier.value
      versus = event.options['versus']
      # (defn roll-minion [die]
      #   (let [{ die-size :die modifiers :modifiers save :save } die
      #         roll (roll-die die-size)
      #         total (apply-modifiers roll modifiers)
      #         modifier-expression (when (not-empty modifiers) (str "= " roll " " (modifiers->str modifiers)))
      #         save-message (when save (str (get-minion-save-message total save die-size)))
      #         save-expression (when save (str "vs. " save ""))]
      #     (save-message-str total modifier-expression save-expression save-message)))

      # (defn str->minion-die [str]
      #   (let [[die & rest] (str/split str #" ")]
      #     (merge { :die (parse-die die) } (parse-modifier-save rest))))

      # (defn str->minion-roll [str save]
      #   (-> str
      #     (str/trim)
      #     (str->minion-die)
      #     (merge { :save (str->save save) })
      #     (roll-minion)))

      # (defn minion-command [data]
      #   (let [arguments (:arguments data)
      #         [_ rolls save] (re-matches villian-compound-regex (clojure.string/join " " arguments))
      #         dice (str/split rolls #",")]
      #     (str (str/join "\r\n\r\n" (map #(str->minion-roll % save) dice)))))
    end

    private

    def register_commands(bot)
      bot.register_application_command(:roll, "Die rolling commands") do |cmd|
        register_die_and_modifier_command(cmd, :min, "Rolls a dice pool and highlights the min die.")
        register_die_and_modifier_command(cmd, :mid, "Rolls a dice pool and highlights the mid die.")
        register_die_and_modifier_command(cmd, :max, "Rolls a dice pool and highlights the max die.")

        register_effect_and_die_and_modifier_command(cmd, :overcome, "Rolls a dice pool and returns the overcome result.")
        register_effect_and_die_and_modifier_command(cmd, :boost, "Rolls a dice pool and returns the boost result.")
        register_effect_and_die_and_modifier_command(cmd, :hinder, "Rolls a dice pool and returns the register result.")

        cmd.subcommand(:minion, "Rolls a minion save, optionally vs a number.") do |sub|
          sub.string("die", "The die to roll, e.g. d4", required: true)
          sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
          sub.string("versus", "The number to roll against.")
        end

        bot.application_command(:roll).subcommand(:min, &method(:roll_min_command))
        bot.application_command(:roll).subcommand(:mid, &method(:roll_mid_command))
        bot.application_command(:roll).subcommand(:max, &method(:roll_max_command))
        bot.application_command(:roll).subcommand(:overcome, &method(:roll_overcome_command))
        bot.application_command(:roll).subcommand(:boost, &method(:roll_boost_command))
        bot.application_command(:roll).subcommand(:hinder, &method(:roll_hinder_command))
      end
    end

    def register_die_and_modifier_command(cmd, name, description)
      cmd.subcommand(name, description) do |sub|
        sub.string("die_1", "The first die to roll, e.g. d4", required: true)
        sub.string("die_2", "The second die to roll, e.g. d6", required: true)
        sub.string("die_3", "The third die to roll, e.g. d8", required: true)
        sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
      end
    end

    def register_effect_and_die_and_modifier_command(cmd, name, description)
      cmd.subcommand(name, description) do |sub|
        sub.string(
          "effect_die",
          "The effect die to be used.",
          required: true,
          choices: { "min" => :min, "mid" => :mid, "max" => :max }
        )
        sub.string("die_1", "The first die to roll, e.g. d4", required: true)
        sub.string("die_2", "The second die to roll, e.g. d6", required: true)
        sub.string("die_3", "The third die to roll, e.g. d8", required: true)
        sub.string("modifier", "The modifier to apply to the roll, e.g. +2")
      end
    end
  end
end
