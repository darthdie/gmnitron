(ns gmnitron.commands.fun
  (:require [gmnitron.common :as common]
            [clj-discord.core :as discord]
            [clojure.string :as str]))

(defn prepare-censor-word [word]
  (cond
    (and (= (first word) \S) (= (second word) \t)) (subs word 2)
    (common/in? ["A" "E" "I" "O" "U"] (common/first-char word)) (str/lower-case word)
    :else (subs word 1)))

(defn censor-word [word]
  (if (and (Character/isUpperCase (first word)) (not (common/in? ["i" "by"] (str/lower-case word))))
    (str "shm" (prepare-censor-word word))
    word))

(defn censor
  ([message]
    (censor "" (str/split message #" ")))
  ([message parts]
    (if (empty? parts)
      (str/join "" (drop-last message))
      (recur (str message (censor-word (first parts)) " ") (rest parts)))))

(defn censor-command [data]
  (let [{arguments :arguments channel-id :channel-id message-id :message-id} data
        message (str/join " " arguments)]
    (censor message)))

(defn died-command [data]
  "https://media.giphy.com/media/TJ8Pd0jQzyHmYoWZYU/giphy.gif")

(def command-list [
  { :command "!censor" :handler censor-command :min-args 1 :usage "!censor (message)" :description "'Censors' a message in true Letters Page fashion." }
  { :command "!died" :handler died-command :usage "!died" :description "And then they died." }
])