(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [gmnitron.commands.roll :as roll]
              [clojure.string :as str]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(def command_handlers (into [] (merge roll/command_list)))

(defn find_command [desired_name commands]
  (if (= (count commands) 0)
    nil
    (let [[command_name handler] (first commands)]
      (if (= desired_name (name command_name))
        handler
        (recur desired_name (rest commands))))))

(defn command_handler [type data]
      (let [message (get data "content")]
           (if (.startsWith message "!")
               (let [raw_command (->> message (next) (apply str))
                    [command & command_arguments] (str/split raw_command #" ")]
                    (if-let [handler (find_command command command_handlers)]
                      (handler type data command_arguments)
                      nil)))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command_handler]}
                    :rate-limit 1}))