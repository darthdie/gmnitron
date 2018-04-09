(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(def no-scene-message "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE !establish COMMAND.")
(def no-scene-or-actor-message "ERROR. UNABLE TO ACCESS SCENE OR ACTOR. USE !establish OR !introduce COMMANDS TO CREATE.")
(def actor-already-acted-message "ERROR. ACTOR HAS ALREADY GONE THIS INITIATIVE.")
(def not-current-actor-message "ERROR. ILLEGAL INSTRUCTION. ONLY CURRENT ACTOR MAY PASS OFF.")

(defn actor->display [actor]
  (str "**" (:name actor) "**" (if (:acted actor) " has acted this round." " hasn't acted this round.")))

(defn get-initiative-recap [scene]
    (let [initiative (group-by :acted (filter #(= (get % :current false) false) (get scene :initiative)))
          current-actor (first (filter #(= (get % :current false) true) (get scene :initiative)))
          current-actor-display (if current-actor (str "**" (str (:name current-actor)) "** is the current actor.") nil)
          acted (str/join "\r\n" (map actor->display (get initiative true [])))
          unacted (str/join "\r\n" (map actor->display (get initiative false [])))]
      (str/join "\r\n\r\n" (filter #(> (count %) 0) [current-actor-display acted unacted]))))

(defn get-scene-recap [scene]
  (let [{green :green-ticks yellow :yellow-ticks red :red-ticks tick :current-tick} scene
        boxes (concat (replicate green "Green") (replicate yellow "Yellow") (replicate red "Red"))]
    (if (>= tick (count boxes))
      "The scene has reached its end."
      (let [current-box (nth boxes (max 0 (- tick 1)))
            remaining-boxes (->> (drop tick boxes)
              (frequencies)
              (map #(str (second %) " " (first %) " boxes"))
              (common/oxford))]
        (common/fmt "It is currently a #{current-box} status. There are #{remaining-boxes} left.")))))

(defn recap [channel-id]
  (if-let [scene (database/get-scene channel-id)]
    (str "\r\n***The Story so Far***\r\n\r\n" (get-scene-recap scene) "\r\n\r\n" (get-initiative-recap scene))
    no-scene-message))

(defn recap-handler [data] (recap (:channel-id data)))

(defn establish [data]
  (let [{arguments :arguments channel-id :channel-id} data
        [green-ticks yellow-ticks red-ticks] (map common/str->int (take 3 arguments))
        names (drop 3 arguments)
        actors (map (fn [actor-name] {:name actor-name :acted false}) names)]
    (database/insert-scene channel-id {
      :green-ticks green-ticks
      :yellow-ticks yellow-ticks
      :red-ticks red-ticks
      :current-tick 0
      :initiative actors
    })
    (recap channel-id)))

(defn pass [data] "The !pass command has been replaced with the !hand-off command. You can use it: !hand-off Legacy Wraith")

(defn hand-off [data]
  (let [{arguments :arguments channel-id :channel-id} data
        actor-name (first arguments)
        pass-to (second arguments)]
    (cond
      (and (database/has-current-actor channel-id) (not (database/is-current-actor channel-id actor-name))) not-current-actor-message
      (not (database/has-actors-in-scene channel-id [actor-name pass-to])) no-scene-or-actor-message
      (and (not (database/is-last-actor channel-id actor-name)) (database/actor-has-acted channel-id pass-to)) actor-already-acted-message
      :else
        (do
          (database/update-scene-actor-acted channel-id actor-name true)
          (when (not (database/has-any-unacted-actors channel-id))
            (database/reset-scene-initiative channel-id))
          (database/update-scene-set-active channel-id pass-to)
          (recap channel-id)))))

(defn tick [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (if-let [scene (database/get-scene channel-id)]
      (do
        (database/tick-scene channel-id)
        (recap channel-id))
      no-scene-message)))

(defn introduce [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (if-let [scene (database/get-scene channel-id)]
      (do 
        (database/add-actor channel-id (first arguments))
        (recap channel-id))
      no-scene-message)))

(defn erase [data]
  (let [{arguments :arguments channel-id :channel-id} data
        actor-name (first arguments)]
    (if (database/has-actor-in-scene channel-id actor-name)
      (do
        (database/remove-actor channel-id actor-name)
        (recap channel-id))
      no-scene-or-actor-message)))

(def command-list [
  { :command "!establish" :handler establish :min-args 4 :usage "!establish (number of green ticks) (number of yellow ticks) (number of red ticks) (actors)" :description "Sets up the scene with specified number of ticks and actors." }
  { :command "!recap" :handler recap-handler :max-args 0 :usage "!recap" :description "Displays the current scene and initiative status." }
  { :command "!pass" :handler pass :min-args 1 :max-args 1 :usage "!pass (actor name)" :description "Marks the actor as having acted this round." }
  { :command "!hand-off" :handler hand-off :min-args 2 :max-args 2 :usage "!hand-off (actor name) (actor to go next)" :description "Hands off the scene to the actor" }
  { :command "!advance" :handler tick :max-args 0 :usage "!advance" :description "Advances the scene tracker." }
  { :command "!introduce" :handler introduce :min-args 1 :usage "!introduce \"Big Baddie\"" :description "Adds an actor to the scene/initiative." }
  { :command "!erase" :handler erase :min-args 1 :usage "!erase \"Big Baddie\"" :description "Removes an actor from the scene/initiative." }
])