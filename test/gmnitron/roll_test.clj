(ns gmnitron.roll-test
  (:require [clojure.test :refer :all]
            [gmnitron.commands.roll :refer :all]
            [random-seed.core :refer :all]))

(defn set-rng-fixture [f]
        (set-random-seed! 9001)
        (f))

(use-fixtures :each set-rng-fixture)

; -- roll min --

(deftest roll-min-test
  (testing "roll min function with 3 dice"
    (is (= "Rolled **5** = 5 + 0 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-min { :arguments ["d8" "d8" "d8"] })))))

(deftest roll-min-positive-modifier-test
  (testing "roll min function with positive modifier"
    (is (= "Rolled **10** = 5 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-min { :arguments ["+5" "d8" "d8" "d8"] })))))

(deftest roll-min-positive-modifier-alternative-test
  (testing "roll min function with positive modifier"
    (is (= "Rolled **10** = 5 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-min { :arguments ["+ 5" "d8" "d8" "d8"] })))))

(deftest roll-min-negative-modifier-test
  (testing "roll min function with negative modifier"
    (is (= "Rolled **4** = 5 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-min { :arguments ["-1" "d8" "d8" "d8"] })))))

(deftest roll-min-negative-modifier-alternative-test
  (testing "roll min function with negative modifier"
    (is (= "Rolled **4** = 5 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-min { :arguments ["- 1" "d8" "d8" "d8"] })))))

(deftest roll-min-single-die-test
  (testing "roll min function with a single die"
    (is (= "Rolled **8** = 8 + 0 (*d8:* **8**)" (roll-min { :arguments ["d8"] })))))

(deftest roll-min-different-die-sizes-test
  (testing "roll min function with different die sizes"
    (is (= "Rolled **1** = 1 + 0 (*d8:* **1**, *d2:* **2**, *d4:* **3**, *d6:* **4**, *d20:* **8**, *d12:* **11**)" (roll-min { :arguments ["d2" "d4" "d6" "d8" "d12" "d20"] })))))

; ; -- roll mid --

(deftest roll-mid-test
  (testing "roll mid function with 3 dice"
    (is (= "Rolled **6** = 6 + 0 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-mid { :arguments ["d8" "d8" "d8"] })))))

(deftest roll-mid-positive-modifier-test
  (testing "roll mid function with positive modifier"
    (is (= "Rolled **11** = 6 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-mid { :arguments ["+5" "d8" "d8" "d8"] })))))

(deftest roll-mid-positive-modifier-alternative-test
  (testing "roll mid function with positive modifier"
    (is (= "Rolled **11** = 6 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-mid { :arguments ["+ 5" "d8" "d8" "d8"] })))))

(deftest roll-mid-negative-modifier-test
  (testing "roll mid function with negative modifier"
    (is (= "Rolled **5** = 6 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-mid { :arguments ["-1" "d8" "d8" "d8"] })))))

(deftest roll-mid-negative-modifier-alternative-test
  (testing "roll mid function with negative modifier"
    (is (= "Rolled **5** = 6 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-mid { :arguments ["- 1" "d8" "d8" "d8"] })))))

(deftest roll-mid-single-die-test
  (testing "roll mid function with a single die"
    (is (= "Rolled **8** = 8 + 0 (*d8:* **8**)" (roll-mid { :arguments ["d8"] })))))

(deftest roll-mid-different-die-sizes-test
  (testing "roll mid function with different die sizes (odd count)"
    (is (= "Rolled **3** = 3 + 0 (*d8:* **1**, *d2:* **2**, *d4:* **3**, *d6:* **4**, *d12:* **11**)" (roll-mid { :arguments ["d2" "d4" "d6" "d8" "d12"] })))))

(deftest roll-mid-different-die-sizes-even-count-test
  (testing "roll mid function with different die sizes (even count)"
    (is (= "Rolled **4** = 4 + 0 (*d8:* **1**, *d2:* **2**, *d4:* **3**, *d6:* **4**, *d20:* **8**, *d12:* **11**)" (roll-mid { :arguments ["d2" "d4" "d6" "d8" "d12" "d20"] })))))

; ; -- roll max --

(deftest roll-max-test
  (testing "roll max function with 3 dice"
    (is (= "Rolled **8** = 8 + 0 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-max { :arguments ["d8" "d8" "d8"] })))))

(deftest roll-max-positive-modifier-test
  (testing "roll max function with positive modifier"
    (is (= "Rolled **13** = 8 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-max { :arguments ["+5" "d8" "d8" "d8"] })))))

(deftest roll-max-positive-modifier-alternative-test
  (testing "roll max function with positive modifier"
    (is (= "Rolled **13** = 8 + 5 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-max { :arguments ["+ 5" "d8" "d8" "d8"] })))))

(deftest roll-max-negative-modifier-test
  (testing "roll max function with negative modifier"
    (is (= "Rolled **7** = 8 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-max { :arguments ["-1" "d8" "d8" "d8"] })))))

(deftest roll-max-negative-modifier-alternative-test
  (testing "roll max function with negative modifier"
    (is (= "Rolled **7** = 8 - 1 (*d8:* **5**, *d8:* **6**, *d8:* **8**)" (roll-max { :arguments ["- 1" "d8" "d8" "d8"] })))))

(deftest roll-max-single-die-test
  (testing "roll max function with a single die"
    (is (= "Rolled **6** = 6 + 0 (*d8:* **6**)" (roll-max { :arguments ["d8"] })))))

(deftest roll-max-different-die-sizes-test
  (testing "roll max function with different die sizes"
    (is (= "Rolled **11** = 11 + 0 (*d8:* **1**, *d2:* **2**, *d4:* **3**, *d6:* **4**, *d12:* **11**)" (roll-max { :arguments ["d2" "d4" "d6" "d8" "d12"] })))))

; ; -- overcome --

; ; [[effect-die d1 d2 d3 & modifiers] (:arguments data)]
; (deftest overcome-test
;     (testing "min overcome"
;         (is (= "\r\nRolled **2** (*d12:* **2**, *d12:* **5**, *d12:* **9**).\r\nAction fails, or succeeds with a major twist." (overcome { :arguments ["min" "d12" "d12" "d12"] })))
;         (is (= "\r\nRolled **1** (*d12:* **1**, *d12:* **3**, *d12:* **5**).\r\nAction fails, or succeeds with a major twist." (overcome { :arguments ["min" "d12" "d12" "d12"] })))
;         (is (= "\r\nRolled **4** (*d12:* **4**, *d12:* **8**, *d12:* **12**).\r\nAction succeeds, but with a minor twist." (overcome { :arguments ["min" "d12" "d12" "d12"] })))
;         (is (= "\r\nRolled **1** (*d12:* **1**, *d12:* **2**, *d12:* **9**).\r\nAction fails, or succeeds with a major twist." (overcome { :arguments ["min" "d12" "d12" "d12"] })))
;         (is (= "\r\nRolled **2** (*d12:* **2**, *d12:* **12**, *d12:* **12**).\r\nAction fails, or succeeds with a major twist." (overcome { :arguments ["min" "d12" "d12" "d12"] })))))

;   { :command "!overcome" :handler overcome :min-args 4 :max-args 6 :usage "!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the overcome result." }
;   { :command "!boost" :handler boost :min-args 4 :max-args 6 :usage "!boost (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the boost result." }
;   { :command "!hinder" :handler hinder :min-args 4 :max-args 6 :usage "!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifiers]" :description "Rolls a dice pool and returns the hinder result." }
;   { :command "!minion" :handler minion-command :min-args 1 :usage "!minion (die) [modifiers], ... [save vs]" :description "Rolls a minion save, optionally vs a number." }
;   { :command "!lt" :handler roll-lieutenant :min-args 1 :max-args 4 :usage "!lt (die) [modifiers] [save vs]" :description "Rolls a lieutenant save, optionally vs a number." }
;   { :command "!reaction" :handler reaction :min-args 1 :max-args 3 :usage "!reaction (die) [modifiers]" :description "Rolls a reaction die." }
;   { :command "!chuck" :handler chuck-command :usage "!chuck" :description "Chucks the current dice." }