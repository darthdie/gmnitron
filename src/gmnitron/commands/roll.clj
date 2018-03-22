(ns gmnitron.commands.roll
  (:require [gmnitron.common :as common]
            [clojure.string :as str])
  (:import (javax.script ScriptEngineManager
                         ScriptEngine)))

; TODO
; Add removal of d from die rolls, e.g. !min d8 d4 d6
; Add !boost, !hinder, !...other ones

(defn clean-modifiers
  ([output] (clean-modifiers ["+" "-" "*" "%" "/"] output))
  ([modifiers output] (if (= (count modifiers) 0)
                          output
                          (let [modifier (first modifiers)]
                            (recur (rest modifiers) (str/replace output (re-pattern (str "\\" modifier "(?=\\S)")) (str modifier " ")))))))

(defn dice-pool->display [effect-die sum min mid max modifiers]
  (let [rolls (str/join ", " [min mid max])
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
        (common/fmt "Rolled **#{sum}** = #{effect-die} #{modifier-expression} (#{rolls})")
        (common/fmt "Rolled **#{effect-die}** (#{rolls})"))))

(defn roll-die [size]
  (+ 1 (rand-int size)))

(defn str->int [str] (Integer. str))

(defn roll-dice [dice]
  (sort (map #(roll-die (common/str->int %)) dice)))

(defn apply-modifiers [num modifiers]
  (if (= 0 (count modifiers))
      num
      (let [engine (.getEngineByName (ScriptEngineManager.) "JavaScript")
            modifier-expression (clojure.string/join " " modifiers)]
        (.eval engine (str num " + (" modifier-expression ")")))))

(defn roll-min [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll-dice [d1 d2 d3])
          [min mid max] (roll-dice rolls)]
          (dice-pool->display min (apply-modifiers min modifiers) min mid max modifiers)))

(defn roll-mid [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll-dice [d1 d2 d3])
          [min mid max] (roll-dice rolls)]
          (dice-pool->display mid (apply-modifiers mid modifiers) min mid max modifiers)))

(defn roll-max [data]
  (let [[d1 d2 d3 & modifiers] (get data :arguments)
          rolls (roll-dice [d1 d2 d3])
          [min mid max] (roll-dice rolls)]
          (dice-pool->display max (apply-modifiers max modifiers) min mid max modifiers)))

(defn roll-minion [data]
  (let [[die & modifiers] (get data :arguments)
        roll (roll-die (str->int die))
        total (apply-modifiers roll modifiers)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
      (common/fmt "Rolled **#{total}** = #{roll} #{modifier-expression}")
      (common/fmt "Rolled **#{roll}**"))))

(def command-list [
  { :name "min" :handler roll-min :min-args 3 :usage "!min (die 1) (die 2) (die 3) [modifiers]" }
  { :name "mid" :handler roll-mid :min-args 3 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" }
  { :name "max" :handler roll-max :min-args 3 :usage "!max (die 1) (die 2) (die 3) [modifiers]" }
  { :name "minion" :handler roll-minion :min-args 1 :usage "!minion (die) [modifiers]" }
])