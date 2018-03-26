(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(def no-scene-message "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE !establish COMMAND.")
(def no-scene-or-actor-message "ERROR. UNABLE TO ACCESS SCENE OR ACTOR. USE !establish OR !introduce COMMANDS TO CREATE.")

(defn actor->display [actor]
  (str "**" (get actor :name) "**" (if (get actor :acted) " has acted this round." " hasn't acted this round.")))

(defn get-initiative-recap [scene]
    (let [initiative (group-by :acted (get scene :initiative))
          acted (str/join "\r\n" (map actor->display (get initiative true [])))
          unacted (str/join "\r\n" (map actor->display (get initiative false [])))]
      (str (if (not-empty acted) (str acted "\r\n\r\n") "") unacted)))

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

(defn pass [data]
  (let [{arguments :arguments channel-id :channel-id} data
        actor-name (first arguments)]
    (if (database/has-actor-in-scene channel-id actor-name)
      (do
        (database/update-scene-actor-acted channel-id actor-name true)
        (when (not (database/has-any-unacted-actors channel-id))
          (database/reset-scene-initiative channel-id))
        (recap channel-id))
      no-scene-or-actor-message)))

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
  { :name "establish" :handler establish :min-args 4 :usage "!establish (number of green ticks) (number of yellow ticks) (number of red ticks) (actors)" :description "Sets up the scene with specified number of ticks and actors." }
  { :name "recap" :handler recap-handler :max-args 0 :usage "!recap" :description "Displays the current scene and initiative status." }
  { :name "pass" :handler pass :min-args 1 :usage "!pass (actor name)" :description "Marks the actor as having acted this round." }
  { :name "advance" :handler tick :max-args 0 :usage "!advance" :description "Advances the scene tracker." }
  { :name "introduce" :handler introduce :min-args 1 :usage "!introduce \"Big Baddie\"" :description "Adds an actor to the scene/initiative." }
  { :name "erase" :handler erase :min-args 1 :usage "!erase \"Big Baddie\"" :description "Removes an actor from the scene/initiative." }
])