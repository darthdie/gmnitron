(ns gmnitron.commands.roll
  (:require [clj-discord.core :as discord]
            [gmnitron.common :as common]))

(defn die_str [effect_die min mid max]
      (str "Rolled **" effect_die "** (" min " " mid " " max ")"))

(defn roll_die [size]
      (+ 1 (rand-int size)))

(defn roll_dice [dice]
  (sort (map #(roll_die (Integer/parseInt (str %))) dice)))

(defn roll_min [type data arguments]
  (common/discord_response type data arguments "Expected !min (die 1) (die 2) (die 3)" 3 100
                    (let [[d1 d2 d3 & modifiers] arguments
                          rolls (roll_dice [d1 d2 d3])
                          [min mid max] (roll_dice rolls)]
                            (die_str min min mid max))))

(defn roll_mid [type data arguments]
  (common/discord_response type data arguments "Expected !mid (die 1) (die 2) (die 3)" 3 100
                    (let [[d1 d2 d3 & modifiers] arguments
                          rolls (roll_dice arguments)
                          [min mid max] (roll_dice rolls)]
                            (die_str mid min mid max))))

(defn roll_max [type data arguments]
  (common/discord_response type data arguments "Expected !max (die 1) (die 2) (die 3)" 3 3
                    (let [[d1 d2 d3 & modifiers] arguments
                          rolls (roll_dice arguments)
                          [min mid max] (roll_dice rolls)]
                            (die_str max min mid max))))

(def commands ["min" "mid" "max"])

(defn handle [command type data arguments]
      (println command)
      (cond
        (.startsWith command "min") (roll_min type data arguments)
        (.startsWith command "mid") (roll_mid type data arguments)
        (.startsWith command "max") (roll_max type data arguments)))