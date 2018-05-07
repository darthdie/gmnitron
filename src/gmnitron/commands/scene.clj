(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(def no-scene-message "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE !establish COMMAND.")
(def no-scene-or-actor-message "ERROR. UNABLE TO ACCESS SCENE OR ACTOR. USE !establish OR !introduce COMMANDS TO CREATE.")
(def no-actor-message "ERROR. UNABLE TO ACCESS ACTOR. USE !introduce COMMAND TO ADD.")
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

(defn pass [data] "The !pass command has been replaced with the !hand off command. You can use it: !hand-off Legacy Wraith")

(defn validate-hand-off [channel-id from to]
  (cond
    (and (database/has-current-actor? channel-id) (not (database/is-current-actor? channel-id from))) not-current-actor-message
    (not (database/has-actors-in-scene? channel-id [from to])) no-actor-message
    (and (not (database/is-last-actor? channel-id from)) (database/actor-has-acted? channel-id to)) actor-already-acted-message))

(defn hand-off-to [data]
  (let [{arguments :arguments channel-id :channel-id} data
        hand-off-to (str/join " " arguments)
        actor-name (str "<@" (get-in data [:author "id"]) ">")]
    (when (database/has-scene? channel-id)
      (if-let [error (validate-hand-off channel-id actor-name hand-off-to)]
        error
        (do
          (database/hand-off channel-id actor-name hand-off-to)
          (recap channel-id))))))

(defn hand-off [data]
  (println data)
  (let [{arguments :arguments channel-id :channel-id} data
        [actor-name hand-off-to] arguments]
    (if (database/has-scene? channel-id)
      (if-let [error (validate-hand-off channel-id actor-name hand-off-to)]
        error
        (do
          (database/hand-off channel-id actor-name hand-off-to)
          (recap channel-id)))
      no-scene-message)))

(defn hand-off-command [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (if (= (str/lower-case (first arguments)) "to")
      (hand-off-to (assoc data :arguments (rest arguments)))
      (hand-off data))))

(defn hand-command [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (when (= (str/lower-case (first arguments)) "off")
      (hand-off-command (assoc data :arguments (rest arguments))))))

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
        (database/add-actor channel-id (str/join " " arguments) true)
        (recap channel-id))
      no-scene-message)))

(defn ambush [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (if-let [scene (database/get-scene channel-id)]
      (do 
        (database/add-actor channel-id (str/join " " arguments))
        (recap channel-id))
      no-scene-message)))

(defn erase [data]
  (let [{arguments :arguments channel-id :channel-id} data
        actor-name (str/join " " arguments)]
    (if (database/has-actor-in-scene? channel-id actor-name)
      (do
        (database/remove-actor channel-id actor-name)
        (recap channel-id))
      no-scene-or-actor-message)))

(defn current-command [data]
  (let [{channel-id :channel-id} data]
    (if (database/has-current-actor? channel-id)
      (str (:name (database/current-actor channel-id)) " is the current actor.")
      "There is no current actor.")))

(def command-list [
  { :command "!hand" :handler hand-command :min-args 3 :max-args 3 :usage "!hand off (actor name) (actor to go next) OR !hand off to (actor to go next)" :description "Hands off the scene to the next actor" }
  { :command "!establish" :handler establish :min-args 4 :usage "!establish (number of green ticks) (number of yellow ticks) (number of red ticks) (actors)" :description "Sets up the scene with specified number of ticks and actors." }
  { :command "!recap" :handler recap-handler :max-args 0 :usage "!recap" :description "Displays the current scene and initiative status." }
  { :command "!pass" :handler pass }
  { :command "!advance" :handler tick :max-args 0 :usage "!advance" :description "Advances the scene tracker." }
  { :command ["!introduce" "!add"] :handler introduce :min-args 1 :usage "!introduce Citizen Dawn" :description "Adds an actor to the scene/initiative, ready to act *next* round." }
  { :command "!ambush", :handler ambush :usage "!ambush Baron Blade" :description "Adds an actor to the scene/initiative, ready to act." }
  { :command ["!erase" "!remove"] :handler erase :min-args 1 :usage "!erase Big Baddie" :description "Removes an actor from the scene/initiative." }
  { :command "!current" :handler current-command :usage "!current" :description "Displays the current actor." }
])