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
  (if (= (count commands) 0)
    nil
    (let [command (first commands)
          command-name (get command :name)]
      (if (= desired-name (name command-name))
        command
        (recur desired-name (rest commands))))))

(defn execute-command [command-name type data arguments]
  (if-let [command (find-command command-name command-handlers)]
    (let [min-args (get command :min-args 0)
          max-args (get command :max-args 100)
          usage (get command :usage "")
          handler (get command :handler)]
          (discord/answer-command data 
                          (get data "content")
                          (if (common/correct-argument-count arguments min-args max-args)
                              (handler { :arguments arguments :author (get data "author") :channel-id (get data "channel_id") })
                              usage)))
    nil))

(defn parse-arguments [arguments]
  (vec (common/splitter (clojure.string/join " " arguments))))

(defn command-handler [type data]
  (let [message (get data "content")]
        (if (.startsWith message "!")
            (let [raw-command (->> message (next) (apply str))
                 [command & command-arguments] (str/split raw-command #" ")]
                 (execute-command command type data (parse-arguments command-arguments))))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command-handler]}
                    :rate-limit 1}))