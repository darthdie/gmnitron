(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [clojure.string :as str]
              [gmnitron.commands.roll :as roll]
              [gmnitron.commands.scene :as scene]
              [gmnitron.database :as database]
              [gmnitron.common :as common]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(def command-handlers (into [] (concat roll/command-list scene/command-list)))

(defn find-command [desired-name commands]
  (if (not (seq commands))
    nil
    (let [command (first commands)
                command-name (get command :name)]
            (if (= desired-name (name command-name))
              command
              (recur desired-name (rest commands))))))

(defn command->help-message [command]
  (let [name (:name command)
        desc (get command :description "No description.")
        usage (get command :usage "")]
    (common/fmt "#{name}\r\n#{desc}\r\nUsage:#{usage}")))

(defn clean-command-name [name]
  (if (str/starts-with? name "!") (subs name 1) name))

(defn help [data]
  (if-let [command-name (first (:arguments data))]
    (if-let [command (find-command (clean-command-name command-name) command-handlers)]
      (str "\r\n" (command->help-message command))
      "ERROR. COMMAND NOT FOUND.")
    (str "\r\n" (str/join "\r\n\r\n" (map command->help-message command-handlers)))))

(def help-command { :name "help" :handler help :max_args 1 :usage "!help [command]" })

(defn respond [data response]
  (discord/answer-command data (get data "content") response))

(defn execute-command [command-name type data arguments]
  (if-let [command (find-command command-name (concat command-handlers [help-command]))]
    (let [min-args (get command :min-args 0)
          max-args (get command :max-args 100)
          usage (get command :usage "")
          handler (get command :handler)]
          (respond data
                  (if (common/correct-argument-count arguments min-args max-args)
                      (handler { :arguments arguments :author (get data "author") :channel-id (get data "channel_id") })
                      usage)))
    nil))

(defn parse-arguments [arguments]
  (vec (common/splitter (clojure.string/join " " arguments))))

(defn command-handler [type data]
  (try
    (let [message (get data "content")]
        (if (.startsWith message "!")
            (let [raw-command (->> message (next) (apply str))
                 [command & command-arguments] (str/split raw-command #" ")]
                 (execute-command command type data (parse-arguments command-arguments)))))
    (catch java.lang.NumberFormatException e (respond data "ERROR. EXPECTED NUMERIC INPUT."))
    (catch Exception e (println (.getMessage e) e))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command-handler]}
                    :rate-limit 1}))