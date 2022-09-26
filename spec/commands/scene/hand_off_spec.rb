# frozen_string_literal: true

RSpec.describe Commands::Scene::HandOff do
  it "without a scene it returns an error" do
    event = DiscordEvent.new({channel_id: -1})

    expect(event).to receive(:respond)
    .with(
      content: "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE /scene establish COMMAND.",
      ephemeral: true
    )

    described_class.handle(event)
  end

  it "without a valid from actor it returns an error" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka"),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Nightmist", "to" => "Haka" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. UNABLE TO ACCESS ACTOR. USE THE /introduce COMMAND TO ADD.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "without a valid to actor it returns an error" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka"),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Nightmist" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. UNABLE TO ACCESS ACTOR. USE THE /introduce COMMAND TO ADD.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "throws an error when not the current actor" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka"),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest", current: true)
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Argent Adept" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. ILLEGAL INSTRUCTION. ONLY CURRENT ACTOR MAY PASS OFF.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "throws an error when the to actor has already acted" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka", current: true),
        Models::Actor.new(name: "Argent Adept", acted: true),
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Argent Adept" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. ACTOR HAS ALREADY GONE THIS INITIATIVE.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "throws an error when attempting to hand off to self" do
    scene = Models::Scene.new(
      actors: [
        Models::Actor.new(name: "Haka", current: true),
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Haka" }})
    expect(event).to receive(:respond)
    .with(
      content: "ERROR. ILLEGAL INSTRUCTION. YOU MAY NOT HAND OFF TO YOURSELF.",
      ephemeral: true
    )
    described_class.handle(event)
  end

  it "hands off when given valid actors" do
    haka = Models::Actor.new(name: "Haka", current: true)
    argent = Models::Actor.new(name: "Argent Adept")
    scene = Models::Scene.create!(
      channel_id: -1,
      actors: [
        haka,
        argent,
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Argent Adept" }})

    expect {
      described_class.handle(event)
    }.to change { haka.reload.acted }.from(false).to(true)
     .and change { argent.reload.current }.from(false).to(true)
  end

  it "resets initiative when the last actor goes" do
    haka = Models::Actor.new(name: "Haka", current: true)
    argent = Models::Actor.new(name: "Argent Adept", acted: true)
    tempest = Models::Actor.new(name: "Tempest", acted: true)
    scene = Models::Scene.create!(
      channel_id: -1,
      actors: [
        haka,
        argent,
        tempest
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    event = DiscordEvent.new({channel_id: 1, options: { "from" => "Haka", "to" => "Argent Adept" }})

    expect {
      described_class.handle(event)
    }.to change { haka.reload.current }.from(true).to(false)
     .and change { tempest.reload.acted }.from(true).to(false)
     .and change { argent.reload.acted }.from(true).to(false)
     .and change { argent.current }.from(false).to(true)
  end

  it "hands off when the current actor and you pass 'to' to it" do
    current_user_id = 3141595265
    current_user = Models::Actor.new(name: "<@#{current_user_id}>", current: true)
    haka = Models::Actor.new(name: "Haka")

    scene = Models::Scene.create!(
      channel_id: -1,
      actors: [
        current_user,
        haka,
        Models::Actor.new(name: "Argent Adept"),
        Models::Actor.new(name: "Tempest")
      ]
    )

    allow_any_instance_of(Commands::SceneHelpers).to receive(:scene_for_channel).and_return(scene)

    User = Struct.new(:id)

    event = DiscordEvent.new({channel_id: 1, "user" => User.new(current_user_id), options: { "from" => "to", "to" => "Haka" }})

    expect {
      described_class.handle(event)
    }.to change { current_user.reload.current }.from(true).to(false)
     .and change { current_user.acted }.from(false).to(true)
     .and change { haka.reload.current }.from(false).to(true)
  end
end
