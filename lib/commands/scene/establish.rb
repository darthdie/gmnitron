# frozen_string_literal: true

module Commands
  class Scene
    class Establish
      def self.name
        :establish
      end

      def self.description
        "Establish a scene with a tracker and actors."
      end

      def self.arguments
        [
          CommandArgument.integer(name: :green_ticks, description: "The number of green ticks in a scene.", options: { required: true }),
          CommandArgument.integer(name: :yellow_ticks, description: "The number of yellow ticks in a scene.", options: { required: true }),
          CommandArgument.integer(name: :red_ticks, description: "The number of red ticks in a scene.", options: { required: true }),
          CommandArgument.string(name: :actors, description: 'A list of actors to start with in the scene, e.g. "Baron Blade" Legacy Wraith "Absolute Zero"')
        ]
      end

      def self.handle(event)
        green_ticks = event.options["green_ticks"]
        yellow_ticks = event.options["yellow_ticks"]
        red_ticks = event.options["red_ticks"]
        actors = event.options["actors"].split.map do |name|
          Actor.new(name: name)
        end

        scene = Models::Scene.create!(
          channel_id: event.channel_id,
          green_ticks: green_ticks,
          yellow_ticks: yellow_ticks,
          red_ticks: red_ticks,
          actors: todo
        )

        respond_with_scene_recap(event, scene)
      end
    end
  end
end
