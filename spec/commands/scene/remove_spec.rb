# frozen_string_literal: true

RSpec.describe Commands::Scene::Remove do
  it "without a scene it returns an error" do
    event = DiscordEvent.new({channel_id: -1})

    expect(event).to receive(:respond)
    .with(
      content: "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE /scene establish COMMAND.",
      ephemeral: true
    )

    described_class.handle(event)
  end

  it "without a valid actor it returns an error" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka"),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest"),
        Models::Actor.new(name: "Fanatic"),
        Models::Actor.new(name: "Captain Cosmic")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "name" => "Nightmist" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. UNABLE TO ACCESS SCENE OR ACTOR. USE !establish OR !introduce COMMANDS TO CREATE.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "with a valid actor it removes them" do
    scene = Models::Scene.create!(
      channel_id: -1,
      green_ticks: 1,
      yellow_ticks: 1,
      red_ticks: 1,
      actors: [
        Models::Actor.new(name: "Haka"),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest"),
        Models::Actor.new(name: "Fanatic"),
        Models::Actor.new(name: "Captain Cosmic")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "name" => "Haka" }})
    expect {
      described_class.handle(event)
    }.to change { scene.reload.actors.length }.by(-1)
  end
end
