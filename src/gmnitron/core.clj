(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [gmnitron.commands.roll :as roll]
              [gmnitron.commands.scene :as scene]
              [clojure.string :as str]
              [gmnitron.database :as database]
              [gmnitron.common :as common]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(def command_handlers (into [] (apply merge roll/command_list scene/command_list)))

(defn find_command [desired_name commands]
  (if (= (count commands) 0)
    nil
    (let [command (first commands)
          command_name (get command :name)]
      (if (= desired_name (name command_name))
        command
        (recur desired_name (rest commands))))))

(defn execute_command [command_name type data arguments]
  (if-let [command (find_command command_name command_handlers)]
    (let [min_args (get command :min_args 0)
          max_args (get command :max_args 100)
          usage (get command :usage "")
          handler (get command :handler)]
          (discord/answer-command data 
                          (get data "content")
                          (if (common/correct_argument_count arguments min_args max_args)
                              (handler { :arguments arguments :author (get data "author") :channel_id (get data "channel_id") })
                              usage)))
    nil))

(defn command_handler [type data]
  (let [message (get data "content")]
        (if (.startsWith message "!")
            (let [raw_command (->> message (next) (apply str))
                 [command & command_arguments] (str/split raw_command #" ")]
                 (execute_command command type data command_arguments)))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command_handler]}
                    :rate-limit 1}))