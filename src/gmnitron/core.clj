(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [gmnitron.commands.roll :as roll]
              [clojure.string :as str]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(defn command_handler [type data]
      (let [message (get data "content")]
           (if (.startsWith message "!")
               (let [raw_command (->> message (next) (apply str))
                    [command & command_arguments] (str/split raw_command #" ")]
                    (cond
                      (some #(= command %) roll/commands) (roll/handle command type data command_arguments))))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command_handler]}
                    :rate-limit 1}))