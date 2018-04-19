(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [clojure.string :as str]
              [gmnitron.commands.roll :as roll]
              [gmnitron.commands.scene :as scene]
              [gmnitron.commands.fun :as fun]
              [gmnitron.database :as database]
              [gmnitron.common :as common]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(def command-handlers (into [] (concat roll/command-list scene/command-list fun/command-list)))

(defn find-command [desired-name commands]
  (when (not-empty commands)
    (let [command (first commands)]
      (if (= desired-name (name (:command command)))
        command
        (recur desired-name (rest commands))))))

(defn command->help-message [command]
  (let [name (:command command)
        desc (get command :description "No description.")
        usage (get command :usage "")]
    (common/fmt "#{name}\r\n#{desc}\r\nUsage: #{usage}")))

(defn help [data]
  (if-let [command-name (first (:arguments data))]
    (if-let [command (find-command command-name command-handlers)]
      (str "\r\n" (command->help-message command))
      "ERROR. COMMAND NOT FOUND.")
    (str "\r\n" (str/join "\r\n\r\n" (map command->help-message command-handlers)))))

(def help-command { :command "!help" :handler help :max_args 1 :usage "!help [command]" })

(defn respond [data response]
  (discord/answer-command data (get data "content") response))

(defn fix-mention [part]
  (clojure.string/replace part #"<@!(\d*)>" "<@$1>"))

(defn parse-arguments [command]
  (as-> (str/trim command) $
    (str/split $ #" ")
    (filter some? $)
    (str/join " " $)
    (common/splitter $)
    (map fix-mention $)
    (vec $)))

(defn execute-command [command-name arguments type data]
  (when-let [command (find-command command-name (concat command-handlers [help-command]))]
    (let [min-args (get command :min-args 0)
          max-args (get command :max-args 100)
          usage (get command :usage "")
          handler (get command :handler)]
      (if (common/correct-argument-count arguments min-args max-args)
        (handler { :arguments arguments :author (get data "author") :channel-id (get data "channel_id") :message-id (get data "id") })
        usage))))

(defn command-handler [type data]
  (try
    (let [message (get data "content")
          [command-name & arguments] (parse-arguments message)]
      (if-let [response (execute-command command-name arguments type data)]
        (respond data response)))
    (catch java.lang.NumberFormatException e (respond data "ERROR. EXPECTED NUMERIC INPUT."))
    (catch Exception e
      (do
        (println (.getMessage e) e)
        (respond data (str "ERROR. ERR. INTERNAL CORRUPTION. CONTACT CREATOR."))))))

(defn -main [& args]
  (discord/connect {:token token
                    :functions {"MESSAGE_CREATE" [command-handler]}
                    :rate-limit 1}))