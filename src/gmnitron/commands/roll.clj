(ns gmnitron.commands.roll
  (:require [gmnitron.common :as common]
            [clojure.string :as str])
  (:import (javax.script ScriptEngineManager
                         ScriptEngine)))

; TODO
; Add removal of d from die rolls, e.g. !min d8 d4 d6

(defn clean-modifiers
  ([output] (clean-modifiers ["+" "-" "*" "%" "/"] output))
  ([modifiers output] (if (= (count modifiers) 0)
                          output
                          (let [modifier (first modifiers)]
                            (recur (rest modifiers) (str/replace output (re-pattern (str "\\" modifier "(?=\\S)")) (str modifier " ")))))))

(defn dice-pool->display [pool sum modifiers]
  (let [rolls (str/join ", " [(:min pool) (:mid pool) (:max pool)])
        effect-die (:effect pool)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
        (common/fmt "Rolled **#{sum}** = #{effect-die} #{modifier-expression} (#{rolls})")
        (common/fmt "Rolled **#{effect-die}** (#{rolls})"))))

(defn roll-die [size]
  (+ 1 (rand-int size)))

(defn apply-modifiers [num modifiers]
  (if (= 0 (count modifiers))
      num
      (let [engine (.getEngineByName (ScriptEngineManager.) "JavaScript")
            modifier-expression (clojure.string/join " " modifiers)]
        (.eval engine (str num " + (" modifier-expression ")")))))

(defn roll-dice-pool [dice effect-die]
  (let [rolls (sort (map #(roll-die (common/str->int %)) dice))
        pool { :min (first rolls) :mid (second rolls) :max (last rolls) }]
    (merge pool { :effect (effect-die pool) })))

(defn roll-and-print-dice-pool [dice modifiers effect-die]
  (let [pool (roll-dice-pool dice effect-die)]
    (dice-pool->display pool (apply-modifiers (:effect pool) modifiers) modifiers)))

(defn roll-min [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] modifiers :min)))

(defn roll-mid [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] modifiers :mid)))

(defn roll-max [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] modifiers :max)))

(defn roll-minion [data]
  (let [[die & modifiers] (:arguments data)
        roll (roll-die (common/str->int die))
        total (apply-modifiers roll modifiers)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
      (common/fmt "Rolled **#{total}** = #{roll} #{modifier-expression}")
      (common/fmt "Rolled **#{roll}**"))))

(defn get-overcome-outcome [result]
  (cond
    (<= result 0) "Action utterly, spectacularly fails."
    (<= 1 result 3) "Action fails, or succeeds with a major twist."
    (<= 4 result 7) "Action succeeds, but with a minor twist."
    (<= 8 result 11) "Action completely succeeds."
    (>= result 12) "Action succeeds beyond expectations"))

(defn effect-die? [possible-die]
  (some #(= % (keyword possible-die)) [:min :mid :max]))

(defn overcome [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (if (effect-die? effect-die)
      (let [pool (roll-dice-pool [d1 d2 d3] (keyword effect-die))
          roll (:effect pool)
          die-display (dice-pool->display pool (apply-modifiers roll modifiers) modifiers)
          total (apply-modifiers roll modifiers)
          outcome (get-overcome-outcome total)]
        (common/fmt "You rolled #{die-display}.\r\n#{outcome}"))
      "ERROR. UNKNOWN EFFECT DIE. EXPECTED mid, min, or max.")))

(defn get-mod-size [result operator]
  (cond
    (<= result 0) "No bonus or penalty is created"
    (<= 1 result 3) (str operator 1)
    (<= 4 result 7) (str operator 2)
    (<= 8 result 11) (str operator 3)
    (>= result 12) (str operator 4)))

(defn roll-mod [effect-die d1 d2 d3 modifiers operator]
  (if (effect-die? effect-die)
    (let [pool (roll-dice-pool [d1 d2 d3] (keyword effect-die))
        roll (:effect pool)
        die-display (dice-pool->display pool (apply-modifiers roll modifiers) modifiers)
        total (apply-modifiers roll modifiers)
        mod-size (get-mod-size total operator)]
      (common/fmt "You rolled #{die-display}.\r\nMod size: #{mod-size}"))
    "ERROR. UNKNOWN EFFECT DIE. EXPECTED mid, min, or max."))

(defn boost [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (roll-mod effect-die d1 d2 d3 modifiers "+")))

(defn hinder [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (roll-mod effect-die d1 d2 d3 modifiers "-")))

(def command-list [
  { :name "min" :handler roll-min :min-args 3 :usage "!min (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the min die." }
  { :name "mid" :handler roll-mid :min-args 3 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the mid die." }
  { :name "max" :handler roll-max :min-args 3 :usage "!max (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the max die." }
  { :name "minion" :handler roll-minion :min-args 1 :usage "!minion (die) [modifiers]" :description "Rolls a minion save." }
  { :name "overcome" :handler overcome :min-args 4 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the overcome result." }
  { :name "boost" :handler boost :min-args 4 :usage "!boost (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the boost result." }
  { :name "hinder" :handler hinder :min-args 4 :usage "!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the hinder result." }
])