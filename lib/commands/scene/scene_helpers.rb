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
      content = Models::SceneRecapFormatter.format(scene)
      event.respond(content: content)
    end
  end
end
