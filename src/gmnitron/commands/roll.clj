(ns gmnitron.commands.roll
  (:require [gmnitron.common :as common]
            [clojure.string :as str])
  (:import (javax.script ScriptEngineManager
                         ScriptEngine)))

(def unknown-effect-die-error "ERROR. UNKNOWN EFFECT DIE. EXPECTED min, mid, or max.")

(defn clean-modifiers
  ([output] (clean-modifiers ["+" "-"] output))
  ([modifiers output] 
    (if (empty? modifiers)
      (if (or (str/starts-with? output "+") (str/starts-with? output "-")) output (str "+ " output))
      (let [modifier (first modifiers)]
        (recur (rest modifiers) (str/replace output (re-pattern (str "\\" modifier "(?=\\S)")) (str modifier " ")))))))

(defn dice-pool->display [pool]
  (let [rolls (str/join ", " (map #(str "*" (first %) ":* **" (second %) "**") (:rolls pool)))
        effect (:effect pool)
        total (:total pool)
        modifiers (:modifiers pool)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (not-empty modifiers)
      (common/fmt "Rolled **#{total}** = #{effect} #{modifier-expression} (#{rolls})")
      (common/fmt "Rolled **#{total}** (#{rolls})"))))

(defn roll-die [size] (+ 1 (rand-int size)))

(defn strip-die [die]
  (common/stripl (str/lower-case die) "d"))

(def parse-die (comp common/str->int strip-die))

(def roll-parsed-die (comp roll-die parse-die))

(defn apply-modifiers [num modifiers]
  (if (empty? modifiers)
    num
    (let [engine (.getEngineByName (ScriptEngineManager.) "JavaScript")
          modifier-expression (clojure.string/join " " modifiers)]
      (.eval engine (str num " + (" modifier-expression ")")))))

(defn normalize-die-str [die]
  (str "d" (strip-die die)))

(defn effect-die-in-matrix [effect-die matrix]
  (cond
    (= effect-die :min) (second (first matrix))
    (= effect-die :mid) (second (second matrix))
    (= effect-die :max) (second (last matrix))))

(defn roll-dice-pool [dice effect-die modifiers]
  (let [roll-matrix (map vector (map normalize-die-str dice) (map roll-parsed-die dice))
        rolls (sort-by second roll-matrix)
        effect (effect-die-in-matrix effect-die rolls)]
    { :rolls rolls :effect effect :total (apply-modifiers effect modifiers) :modifiers modifiers }))

(def roll-and-print-dice-pool (comp dice-pool->display roll-dice-pool))

(defn roll-min [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] :min modifiers)))

(defn roll-mid [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] :mid modifiers)))

(defn roll-max [data]
  (let [[d1 d2 d3 & modifiers] (:arguments data)]
   (roll-and-print-dice-pool [d1 d2 d3] :max modifiers)))

(defn roll-minion [data]
  (let [[die & modifiers] (:arguments data)
        roll (roll-die (parse-die die))
        total (apply-modifiers roll modifiers)
        modifier-expression (clean-modifiers (str/join " " modifiers))]
    (if (not-empty modifiers)
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
          die-display (dice-pool->display pool)
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
        die-display (dice-pool->display pool)
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
  { :name "min" :handler roll-min :min-args 3 :max-args 4 :usage "!min (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the min die." }
  { :name "mid" :handler roll-mid :min-args 3 :max-args 4 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the mid die." }
  { :name "max" :handler roll-max :min-args 3 :max-args 4 :usage "!max (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the max die." }
  { :name "minion" :handler roll-minion :min-args 1 :max-args 2 :usage "!minion (die) [modifiers]" :description "Rolls a minion save." }
  { :name "overcome" :handler overcome :min-args 4 :max-args 5 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the overcome result." }
  { :name "boost" :handler boost :min-args 4 :max-args 5 :usage "!boost (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the boost result." }
  { :name "hinder" :handler hinder :min-args 4 :max-args 5 :usage "!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the hinder result." }
])