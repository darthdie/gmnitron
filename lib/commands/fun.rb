# frozen_string_literal: true

require 'uri'
require 'net/http'
require 'json'

module Commands
  class Fun
    def self.register(bot)
      @register ||= Fun.new(bot)
    end

    def initialize(bot)
      bot.register_application_command(:muffin, "It's muffin time!")
      bot.register_application_command(:censor, "Smhensors words") do |cmd|
        cmd.string("message", "The message to censor.", required: true)
      end

      bot.register_application_command(:editor, "Editorialize a message") do |cmd|
        cmd.string("message", "The message to editorialize.", required: true)
      end

      bot.register_application_command(:died, "And then they died.")
      bot.register_application_command(:plan, "All according to plan.")
      bot.register_application_command(:evil, "The laugh of the evil one.")
      bot.register_application_command(:cult, "The cult is pleased.")
      bot.register_application_command(:proletariat, "Proletariat approves.")
      bot.register_application_command(:approves, "Proletariat approves.")

      bot.application_command(:muffin, &method(:muffin_command))

      bot.application_command(:censor, &method(:censor_command))
      bot.application_command(:editor, &method(:editor_command))

      bot.application_command(:died, &method(:died_command))
      bot.application_command(:plan, &method(:plan_command))
      bot.application_command(:evil, &method(:evil_command))
      bot.application_command(:cult, &method(:cult_command))
      bot.application_command(:proletariat, &method(:proletariat_command))
      bot.application_command(:approves, &method(:proletariat_command))
    end

    def died_command(event)
      event.respond(content: "https://i.imgur.com/unAdMfv.mp4")
    end

    def plan_command(event)
      event.respond(content: "https://i.imgur.com/56PGolV.jpg")
    end

    def evil_command(event)
      event.respond(content: "https://i.imgur.com/twzosas.mp4")
    end

    def cult_command(event)
      event.respond(content: "https://i.imgur.com/CiuXENB.jpg")
    end

    def proletariat_command(event)
      event.respond(content: "https://i.imgur.com/2AG2349.png")
    end

    def muffin_command(event)
      event.respond(content: "https://youtu.be/LACbVhgtx9I")
    end

    def censor_command(event)
      message = event.options["message"]

      censored_message = message.split.map { |word| censor_word(word) }.join(" ")

      event.respond(content: censored_message)
    end

    def censor_word(word)
      return word unless titleized?(word[0]) && !%w[i by].include?(word.downcase)

      "shm#{prepare_censor_word(word)}"
    end

    def titleized?(word)
      !!word.match(/^[[:upper:]].*/)
    end

    def prepare_censor_word(word)
      return word[2..] if word[0..1].start_with?("St")
      return word.downcase if word.start_with?(/A|E|I|O|U/)

      word[1..]
    end

    def editor_command(event)
      new_words = event.options["message"]
        .strip
        .downcase
        .split
        .map do |word|
          next word if is_common_word?(word) || word.empty?

          find_synonym_for(word)
        end

      event.respond(content: new_words.join(" "))
      # (defn editor-command [data]
      #   (let [parts (:arguments data)]
      #     (str/join " " (map #(thesaurus-word (str/trim (str/lower-case %))) parts))))

      # (defn thesaurus-word [word]
      #   (if (or (is-common-word? word) (empty? word))
      #     word
      #     (if-let [thesaurus (datamuse-response (str "http://api.datamuse.com/words?rel_syn=" word))]
      #       thesaurus
      #       (if-let [related (datamuse-response (str "https://api.datamuse.com/words?ml=" word))]
      #         related
      #         word))))

      # (defn datamuse-response [url]
      #   (let [http-response (:body (client/get url))
      #         response (json/read-str http-response)]
      #       (when (> (count response) 1)
      #       (str (second (first (rand-nth response)))))))

      # (defn is-common-word? [word]
      #   (some #{word} common-words))

      #   (def common-words ["the" "a" "and" "then" "than" "about" "below" "excepting" "off" "toward" "above" "beneath" "for" "on" "under" "across" "beside" "besides" "from" "onto" "underneath" "after" "between" "in" "out" "until" "against" "beyond" "in front of" "outside" "up" "along" "but" "inside" "over" "upon" "among" "by" "in spite of" "past" "up to" "around" "concerning" "instead of" "regarding" "with" "at" "despite" "into" "since" "within" "because of" "down" "like" "through" "without" "before" "during" "near" "throughout" "with regard to" "behind" "except" "of" "to" "with respect to" "all"])

    end

    def find_synonym_for(word)
      synonym = get_and_parse_datamuse("http://api.datamuse.com/words?rel_syn=#{word}")
      return synonym if synonym.present?

      # sometimes we don't get a synonym response, so grab a "related" word
      get_and_parse_datamuse("http://api.datamuse.com/words?ml=#{word}")
    end

    def get_and_parse_datamuse(url)
      uri = URI(url)
      res = Net::HTTP.get_response(uri)
      if res.is_a?(Net::HTTPSuccess)
        res = JSON.parse(res.body)
        return if !res.present? || res.empty?

        res.sample()["word"] if res.present?
      end
    end

    def is_common_word?(word)
      [
        "the",
        "a",
        "and",
        "then",
        "than",
        "about",
        "below",
        "excepting",
        "off",
        "toward",
        "above",
        "beneath",
        "for",
        "on",
        "under",
        "across",
        "beside",
        "besides",
        "from",
        "onto",
        "underneath",
        "after",
        "between",
        "in",
        "out",
        "until",
        "against",
        "beyond",
        "outside",
        "up",
        "along",
        "but",
        "inside",
        "over",
        "upon",
        "among",
        "by",
        "past",
        "up to",
        "around",
        "concerning",
        "regarding",
        "with",
        "at",
        "despite",
        "into",
        "since",
        "within",
        "down",
        "like",
        "through",
        "without",
        "before",
        "during",
        "near",
        "throughout",
        "behind",
        "except",
        "of",
        "to",
        "all",
      ].include?(word)
    end
  end
end
