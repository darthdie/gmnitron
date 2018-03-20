(ns gmnitron.commands.roll
  (:require [gmnitron.common :as common]
            [clojure.string :as str])
  (:import (javax.script ScriptEngineManager
                         ScriptEngine)))

(defn clean_modifiers
  ([output] (clean_modifiers ["+" "-" "*" "%" "/"] output))
  ([modifiers output] (if (= (count modifiers) 0)
                          output
                          (let [modifier (first modifiers)]
                            (recur (rest modifiers) (str/replace output (re-pattern (str "\\" modifier "(?=\\S)")) (str modifier " ")))))))

(defn die_str [effect_die sum min mid max modifiers]
  (let [rolls (str/join ", " [min mid max])
        modifier_expression (clean_modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
        (common/fmt "Rolled **#{sum}** = #{effect_die} #{modifier_expression} (#{rolls})")
        (common/fmt "Rolled **#{effect_die}** (#{rolls})"))))

(defn roll_die [size]
  (+ 1 (rand-int size)))

(defn roll_dice [dice]
  (sort (map #(roll_die (Integer/parseInt (str %))) dice)))

(defn apply_modifiers [num modifiers]
  (if (= 0 (count modifiers))
      num
      (let [engine (.getEngineByName (ScriptEngineManager.) "JavaScript")
            modifier_expression (clojure.string/join " " modifiers)]
        (.eval engine (str num " + (" modifier_expression ")")))))

(defn roll_min [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll_dice [d1 d2 d3])
          [min mid max] (roll_dice rolls)]
          (die_str min (apply_modifiers min modifiers) min mid max modifiers)))

(defn roll_mid [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll_dice [d1 d2 d3])
          [min mid max] (roll_dice rolls)]
          (die_str mid (apply_modifiers mid modifiers) min mid max modifiers)))

(defn roll_max [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll_dice [d1 d2 d3])
          [min mid max] (roll_dice rolls)]
          (die_str max (apply_modifiers max modifiers) min mid max modifiers)))

(def command_list [
  { :name "min" :handler roll_min :min_args 3 :usage "!min (die 1) (die 2) (die 3) [modifiers]" }
  { :name "mid" :handler roll_mid :min_args 3 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" }
  { :name "max" :handler roll_max :min_args 3 :usage "!max (die 1) (die 2) (die 3) [modifiers]" }
])