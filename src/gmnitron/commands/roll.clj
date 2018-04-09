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

(defn decrease-die-size [die]
  (- die 2))

(defn parse-modifier-save [args]
  (if (empty? args)
    { :modifiers [] :save nil }
    (if (str/starts-with? (str/lower-case (last args)) "v")
      { :modifiers (butlast args) :save (common/str->int (common/stripl (str/lower-case (last args)) "v"))}
      { :modifiers args :save nil })))

(defn omnitron-insult []
  (rand-nth [
    "LOADING HUMOUR SUB-ROUTINE... FAILURE."
    "ATTEMPTING TO UNDERSTAND... FAILURE.\r\nPAGE 17, SECTION 'Minions and Lieutenants', PARAGRAPH 4 STATES: 'If a minion is Attacked when at a d4, it is always removed and does not get a minion save.'"
    "YOU MIGHT AS WELL HAVE TRIED TO ROLL TO BRING OblivAeon BACK."
    "MY PROBABILITY SUB-ROUTINE PREDICTS SUCCESS."
  ]))

(defn get-minion-save-message [roll save die-size]
  (cond
    (<= die-size 4) (str (omnitron-insult) "\r\n" "The Minion is defeated!")
    (>= roll save) (str "The Minion is reduced to a d" (- die-size 2) ".")
    :else "The Minion is defeated!"))

(defn modifiers->str [modifiers]
  (->> modifiers
    (str/join " ")
    (clean-modifiers)))

(defn roll-minion [data]
  (let [[die & rest] (:arguments data)
        { modifiers :modifiers save :save } (parse-modifier-save rest)
        die-size (parse-die die)
        roll (roll-die die-size)
        total (apply-modifiers roll modifiers)
        modifier-expression (if (empty? modifiers) nil (str "= " roll " " (modifiers->str modifiers)))
        save-message (if save (str "\r\n" (get-minion-save-message total save die-size)) nil)
        save-expression (if save (str "vs. " save "") nil)]
    (str "Rolled **" total "** " (str/join " " (filter some? [modifier-expression save-expression save-message])))))

(defn get-lieutenant-save-message [roll save die-size]
  (cond
    (and (< roll save) (<= die-size 4)) "The Lieutenant is defeated!"
    (< roll save) (str "The Lieutenant is reduced to a d" (- die-size 2) ".")
    :else "The Lieutenant lives another day."))

(defn roll-lieutenant [data]
  (let [[die & rest] (:arguments data)
        { modifiers :modifiers save :save } (parse-modifier-save rest)
        die-size (parse-die die)
        roll (roll-die die-size)
        total (apply-modifiers roll modifiers)
        modifier-expression (if (empty? modifiers) "" (str "= " roll " " (modifiers->str modifiers)))
        save-message (if save (str "\r\n" (get-lieutenant-save-message total save die-size)) "")
        save-expression (if save (str "vs. " save "") "")]
    (str "Rolled **" total "** " (str/join " " [modifier-expression save-expression save-message]))))

(defn get-overcome-outcome [result]
  (cond
    (<= result 0) "Action utterly, spectacularly fails."
    (<= 1 result 3) "Action fails, or succeeds with a major twist."
    (<= 4 result 7) "Action succeeds, but with a minor twist."
    (<= 8 result 11) "Action completely succeeds."
    (>= result 12) "Action succeeds beyond expectations."))

(defn effect-die? [possible-die]
  (some #(= % (keyword possible-die)) [:min :mid :max]))

(defn overcome [data]
  (let [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
    (if (effect-die? effect-die)
      (let [pool (roll-dice-pool [d1 d2 d3] (keyword effect-die) modifiers)
          die-display (dice-pool->display pool)
          outcome (get-overcome-outcome (:total pool))]
        (common/fmt "\r\n#{die-display}.\r\n#{outcome}"))
      unknown-effect-die-error)))

(defn get-mod-size [result operator]
  (cond
    (<= result 0) "No bonus or penalty is created."
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

(defn reaction [data]
  (let [[die & modifiers] (:arguments data)
        roll (roll-parsed-die die)
        total (apply-modifiers roll modifiers)
        modifier-expression (modifiers->str modifiers)]
    (if (not-empty modifiers)
      (common/fmt "Rolled **#{total}** = #{roll} #{modifier-expression}")
      (common/fmt "Rolled **#{total}**"))))

(def command-list [
  { :command "!min" :handler roll-min :min-args 3 :max-args 5 :usage "!min (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the min die." }
  { :command "!mid" :handler roll-mid :min-args 3 :max-args 5 :usage "!mid (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the mid die." }
  { :command "!max" :handler roll-max :min-args 3 :max-args 5 :usage "!max (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and highlights the max die." }
  { :command "!overcome" :handler overcome :min-args 4 :max-args 6 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the overcome result." }
  { :command "!boost" :handler boost :min-args 4 :max-args 6 :usage "!boost (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the boost result." }
  { :command "!hinder" :handler hinder :min-args 4 :max-args 6 :usage "!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the hinder result." }
  { :command "!minion" :handler roll-minion :min-args 1 :max-args 4 :usage "!minion (die) [modifiers] [save vs]" :description "Rolls a minion save, optionally vs a number." }
  { :command "!lt" :handler roll-lieutenant :min-args 1 :max-args 4 :usage "!lt (die) [modifiers] [save vs]" :description "Rolls a lieutenant save, optionally vs a number." }
  { :command "!reaction" :handler reaction :min-args 1 :max-args 3 :usage "!reaction (die) [modifiers]" :description "Rolls a reaction die." }
])