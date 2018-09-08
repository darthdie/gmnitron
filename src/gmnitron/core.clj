(ns gmnitron.core
    (:gen-class)
    (:require [clj-discord.core :as discord]
              [clojure.string :as str]
              [gmnitron.commands.roll :as roll]
              [gmnitron.commands.scene :as scene]
              [gmnitron.commands.fun :as fun]
              [gmnitron.database :as database]
              [gmnitron.common :as common]
              [gmnitron.commands.universes :as universes]))

(def token (System/getenv "GMNITRON_BOT_TOKEN"))

(def command-handlers (into [] (concat roll/command-list scene/command-list fun/command-list universes/command-list)))

(defn as-vector [x]
  (cond
    (vector? x) x
    (sequential? x) (vec x)
    :else (vector x)))

(defn command-names-match? [match names]
  (when (> (count names) 0)
    (if (or 
          (= match (first names))
          (= match (subs (first names) 1)))
      true
      (recur match (rest names)))))

(defn find-command [desired-name commands]
  (when (not-empty commands)
    (let [command (first commands)]
      (if (command-names-match? desired-name (as-vector (:command command)))
        command
        (recur desired-name (rest commands))))))

(defn command->help-message [command]
  (let [name (str/join ", " (as-vector (:command command)))
        desc (get command :description "No description.")
        usage (get command :usage "")]
    (common/fmt "#{name}\r\n#{desc}\r\nUsage: #{usage}")))

(defn help [data]
  (if-let [command-name (first (:arguments data))]
    (if-let [command (find-command command-name command-handlers)]
      (str "\r\n" (command->help-message command))
      "ERROR. COMMAND NOT FOUND.")
    "https://github.com/darthdie/gmnitron#commands"))

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
        (command->help-message command)))))

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