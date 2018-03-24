(ns gmnitron.commands.roll
  (:require [gmnitron.common :as common]
            [clojure.string :as str])
  (:import (javax.script ScriptEngineManager
                         ScriptEngine)))

(def unknown-effect-die-error "ERROR. UNKNOWN EFFECT DIE. EXPECTED min, mid, or max.")

(defn clean-modifiers
  ([output] (clean-modifiers ["+" "-" "*" "%" "/"] output))
  ([modifiers output] 
    (if (= (count modifiers) 0)
      output
      (let [modifier (first modifiers)]
        (recur (rest modifiers) (str/replace output (re-pattern (str "\\" modifier "(?=\\S)")) (str modifier " ")))))))

(defn dice-pool->display [pool modifiers]
  (let [rolls (str/join ", " [(:min pool) (:mid pool) (:max pool)])
        effect (:effect pool)
        total (:total pool)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (> (count modifiers) 0)
      (common/fmt "Rolled **#{total}** = #{effect} #{modifier-expression} (#{rolls})")
      (common/fmt "Rolled **#{total}** (#{rolls})"))))

(defn roll-die [size] (+ 1 (rand-int size)))

(defn apply-modifiers [num modifiers]
  (if (= 0 (count modifiers))
    num
    (let [engine (.getEngineByName (ScriptEngineManager.) "JavaScript")
          modifier-expression (clojure.string/join " " modifiers)]
      (.eval engine (str num " + (" modifier-expression ")")))))

(defn parse-die [die]
  (let [result (common/stripl (str/lower-case die) "d")]
    (common/str->int result)))

(defn roll-dice-pool [dice effect-die modifiers]
  (let [rolls (sort (map #(roll-die (parse-die %)) dice))
        pool { :min (first rolls) :mid (second rolls) :max (last rolls) }]
    (merge pool { :effect (effect-die pool) :total (apply-modifiers (effect-die pool) modifiers) })))

(defn roll-and-print-dice-pool [dice modifiers effect-die]
  (let [pool (roll-dice-pool dice effect-die modifiers)]
    (dice-pool->display pool modifiers)))

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
        roll (roll-die (parse-die die))
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
      (let [pool (roll-dice-pool [d1 d2 d3] (keyword effect-die) modifiers)
          die-display (dice-pool->display pool modifiers)
          outcome (get-overcome-outcome (:total modifiers))]
        (common/fmt "\r\n#{die-display}.\r\n#{outcome}"))
      unknown-effect-die-error)))

(defn get-mod-size [result operator]
  (cond
    (<= result 0) "No bonus or penalty is created"
    (<= 1 result 3) (str operator 1)
    (<= 4 result 7) (str operator 2)
    (<= 8 result 11) (str operator 3)
    (>= result 12) (str operator 4)))

(defn roll-mod [effect-die dice modifiers operator]
  (if (effect-die? effect-die)
    (let [pool (roll-dice-pool dice (keyword effect-die) modifiers)
        die-display (dice-pool->display pool modifiers)
        mod-size (get-mod-size (:total pool) operator)]
      (common/fmt "\r\n#{die-display}.\r\nMod size: #{mod-size}"))
    unknown-effect-die-error))

(defn boost [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (roll-mod effect-die [d1 d2 d3] modifiers "+")))

(defn hinder [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (roll-mod effect-die [d1 d2 d3] modifiers "-")))

(def command-list [
  { :name "min" :handler roll-min :min-args 3 :usage "!min (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the min die." }
  { :name "mid" :handler roll-mid :min-args 3 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the mid die." }
  { :name "max" :handler roll-max :min-args 3 :usage "!max (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the max die." }
  { :name "minion" :handler roll-minion :min-args 1 :usage "!minion (die) [modifiers]" :description "Rolls a minion save." }
  { :name "overcome" :handler overcome :min-args 4 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the overcome result." }
  { :name "boost" :handler boost :min-args 4 :usage "!boost (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the boost result." }
  { :name "hinder" :handler hinder :min-args 4 :usage "!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the hinder result." }
])