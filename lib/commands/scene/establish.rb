# frozen_string_literal: true

module Commands
  class Scene
    class Establish
      extend Commands::SceneHelpers

      def self.name
        :establish
      end

      def self.description
        "Establish a scene with a tracker and actors."
      end

      def self.arguments
        [
          CommandArgument.integer("green_ticks", "The number of green ticks in a scene.", options: { required: true }),
          CommandArgument.integer("yellow_ticks", "The number of yellow ticks in a scene.", options: { required: true }),
          CommandArgument.integer("red_ticks", "The number of red ticks in a scene.", options: { required: true }),
          CommandArgument.string("actors", 'A list of actors to start with in the scene, e.g. "Baron Blade" Legacy Wraith "Absolute Zero"')
        ]
      end

      def self.handle(event)
        channel_id = event.channel_id
        green_ticks = event.options["green_ticks"]
        yellow_ticks = event.options["yellow_ticks"]
        red_ticks = event.options["red_ticks"]
        actors = parse_actors(event.options["actors"])

        Models::Scene.where(channel_id: channel_id).destroy_all

        scene = Models::Scene.create!(
          channel_id: channel_id,
          green_ticks: green_ticks,
          yellow_ticks: yellow_ticks,
          red_ticks: red_ticks,
          actors: actors
        )

        respond_with_scene_recap(event, scene)
      end

      def self.parse_actors(actors)
        actors
        .to_s
        .split(/\s(?=(?:[^"]|"[^"]*")*$)/)
        .map do |name|
          Models::Actor.new(name: name)
        end
      end
    end
  end
end
