# frozen_string_literal: true

RSpec.describe Models::Formatters::SceneRecapFormatter do
  ACTOR_NAMES = [
    "Baron Blade",
    "Legacy",
    "Wraith",
    "Thanos"
  ]

  it "formats a simple unacted 3-person scene" do
    scene = Models::Scene.new(
      channel_id: 1,
      green_ticks: 1,
      yellow_ticks: 2,
      red_ticks: 3,
      actors: ACTOR_NAMES.map { |name| Models::Actor.new(name: name) }
    )

    message = described_class.format(scene)

    expected_message = [
      "***The Story so Far***",
      "It is currently a Green status. There are 1 Green boxes, 2 Yellow boxes, and 3 Red boxes left.",
      ACTOR_NAMES.map { |name| "**#{name}** hasn't acted this round." }.join("\r\n")
    ].join("\r\n\r\n")

    expect(message).to eq(expected_message)
  end

  it "formats a simple 3-person scene with one acted actor" do
    actors = ACTOR_NAMES.map { |name| Models::Actor.new(name: name) }
    actors[0].acted = true

    scene = Models::Scene.new(
      channel_id: 1,
      green_ticks: 2,
      yellow_ticks: 3,
      red_ticks: 3,
      actors: actors
    )

    message = described_class.format(scene)

    expected_message = [
      "***The Story so Far***",
      "It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 3 Red boxes left.",
      "**Baron Blade** has acted this round.",
      *ACTOR_NAMES[1..].map { |name| "**#{name}** hasn't acted this round." }.join("\r\n")
    ].join("\r\n\r\n")

    expect(message).to eq(expected_message)
  end

  it "formats with a current actor" do
    baron_blade = Models::Actor.new(name: "Baron Blade", current: true)
    legacy = Models::Actor.new(name: "Legacy", acted: true)

    actors = [
      baron_blade,
      legacy,
      Models::Actor.new(name: "Wraith"),
      Models::Actor.new(name: "Thanos")
    ]

    scene = Models::Scene.new(
      channel_id: 1,
      green_ticks: 2,
      yellow_ticks: 3,
      red_ticks: 3,
      actors: actors
    )

    message = described_class.format(scene)

    expected_message = [
      "***The Story so Far***",
      "It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 3 Red boxes left.",
      "**Baron Blade** is the current actor.",
      "**Legacy** has acted this round.",
      *ACTOR_NAMES[2..].map { |name| "**#{name}** hasn't acted this round." }.join("\r\n")
    ].join("\r\n\r\n")

    expect(message).to eq(expected_message)
  end

  it "formats when at end of scene" do
    scene = Models::Scene.new(
      channel_id: 1,
      green_ticks: 2,
      yellow_ticks: 0,
      red_ticks: 0,
      current_tick: 3,
      actors: ACTOR_NAMES.map { |name| Models::Actor.new(name: name) }
    )

    message = described_class.format(scene)

    expected_message = [
      "***The Story so Far***",
      "The scene has reached its end.",
      *ACTOR_NAMES.map { |name| "**#{name}** hasn't acted this round." }.join("\r\n")
    ].join("\r\n\r\n")

    expect(message).to eq(expected_message)
  end

end
