# frozen_string_literal: true

Dir["./lib/commands/scene/*.rb"].sort.each { |file| require file }

module Commands
  class Scene
    def self.register(bot)
      @register ||= Scene.new(bot)
    end

    def initialize(bot)
      register_commands(bot)
    end

    def register_commands(bot)
      bot.register_application_command(:scene, "Commands for running a scene.", server_id: ENV.fetch("DISCORD_TEST_SERVER_ID", nil)) do |cmd|
        Commands::Scene.constants
          .select {|c| Commands::Scene.const_get(c).is_a? Class}
          .map { |c| Commands::Scene.const_get(c) }
          .each do |command|
            cmd.subcommand(command.name, command.description) do |sub|
              next unless command.respond_to?(:arguments) && command.arguments.present?

              command.arguments.to_a.each do |argument|
                options = argument.options || {}
                sub.send(argument.type, argument.name, argument.description, **options)
              end
            end

            bot.application_command(:scene).subcommand(command.name) { |event| command.handle(event) }
          end
      end
    end
  end
end
