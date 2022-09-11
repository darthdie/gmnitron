# frozen_string_literal: true

module Commands
  class Fun
    def Fun.register(bot)
      @instance ||= Fun.new(bot)
    end
  
    def initialize(bot)
      bot.register_application_command(:fun, 'Fun/meme commands', server_id: ENV['SLASH_COMMAND_BOT_SERVER_ID']) do |cmd|
        bot.register_application_command(:muffin, "It's muffin time!")
        bot.register_application_command(:censor, "Smhensors words") do |cmd|
          cmd.string('message', 'The message to censor', required: true)
        end
      
        bot.application_command(:muffin, &method(:muffin_command))
      
        bot.application_command(:censor, &method(:censor_command))
      end
    end
  
    def muffin_command(event)
      event.respond(content: "https://youtu.be/LACbVhgtx9I")
    end
  
    def censor_command(event)
      message = event.options['message']
      
      censored_message = message.split.map { |word| censor_word(word) }.join(' ')
  
      event.respond(content: censored_message)
    end
  
    def titleized?(s)
      !!s.match(/^[[:upper:]].*/)
    end
    
    def prepare_censor_word(word)
      return word[2..] if word[0..1] == 'St'
      return word.downcase if word.start_with?(/A|E|I|O|U/)
      
      word[1..]
    end
    
    def censor_word(word)
      return word unless titleized?(word[0]) && !%w[i by].include?(word.downcase)
    
      "shm#{prepare_censor_word(word)}"
    end
  end
end