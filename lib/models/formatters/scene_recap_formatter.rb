# frozen_string_literal: true

module Models::Formatters
  class SceneRecapFormatter
    attr_reader :scene

    def self.format(scene)
      new(scene).format
    end

    def initialize(scene)
      @scene = scene
    end

    def format
      [
        "***The Story so Far***",
        scene_formatted,
        initiative_formatted
      ].compact.join("\r\n\r\n")
    end

    def scene_formatted
      return "The scene has reached its end." if end_of_scene?

      "It is currently a #{current_box} status. There are #{remaining_boxes_formatted} left."
    end

    def end_of_scene?
      scene.current_tick >= boxes.length
    end

    def current_box
      boxes[scene.current_tick]
    end

    def remaining_boxes
      boxes[scene.current_tick..].tally
    end

    def remaining_boxes_formatted
      remaining_boxes.map { |box, count| "#{count} #{box} boxes" }.oxford_join
    end

    def boxes
      @boxes ||= [
        *(["Green"] * scene.green_ticks),
        *(["Yellow"] * scene.yellow_ticks),
        *(["Red"] * scene.red_ticks)
      ]
    end

    def initiative_formatted
      [
        current_actor_formatted,
        acted_formatted,
        unacted_formatted
      ].compact.join("\r\n\r\n")
    end

    def current_actor_formatted
      actor = scene.actors.filter { |actor| actor.current }.first
      return unless actor.present?

      "**#{actor.name}** is the current actor."
    end

    def acted_formatted
      actors = scene.actors.filter { |actor| actor.acted && !actor.current }
      return unless actors.any?

      actors.map { |actor| format_actor(actor) }.join("\r\n")
    end

    def unacted_formatted
      actors = scene.actors.filter { |actor| !actor.acted && !actor.current }
      return unless actors.any?

      actors.map { |actor| format_actor(actor) }.join("\r\n")
    end

    def format_actor(actor)
      acted_text = actor.acted ? "has acted this round." : "hasn't acted this round."
      "**#{actor.name}** #{acted_text}"
    end
  end
end
