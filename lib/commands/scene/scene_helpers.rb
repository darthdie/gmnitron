# frozen_string_literal: true

module Commands
  module SceneHelpers
    def respond_with_no_scene(event)
      event.respond(
        content: "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE /scene establish COMMAND.",
        ephemeral: true
      )
    end

    def scene_for_channel(event)
      Models::Scene.where(channel_id: event.channel_id).first
    end

    def respond_with_scene_recap(event, scene)
      content = Models::Formatters::SceneRecapFormatter.format(scene)
      event.respond(content: content)
    end

    def respond_with_error(event, error)
      event.respond(content: error, ephemeral: true)
    end
  end
end
