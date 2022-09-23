# frozen_string_literal: true

module Commands
  class Scene
    class Current
      def self.name
        :current
      end

      def self.description
        "Displays the current actor."
      end

      def self.handle(event)
        scene = scene_for_channel(event)
        return respond_with_no_scene(event) unless scene.present?

        current_actor = scene.current_actor
        content = if current_actor.present?
          "**#{current_actor.name}** is the current actor."
        else
          "There is no current actor."
        end

        event.respond(content: content)
      end
    end
  end
end
