(ns gmnitron.commands.initiative
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(defn actor->display [actor]
  (println actor)
  (str "**" (get actor :name) "**" (if (get actor :acted) " has acted this round." " hasn't acted this round.")))

(defn round [data]
  (let [{arguments :arguments channel_id :channel} data]
    (if-let [round (database/find-round channel_id)]
      (str "The round so far:\r\n" (str/join "\r\n" (map actor->display round)))
      "Nothing found for this channel.")))

(defn set_actors [data]
  (let [{arguments :arguments channel_id :channel} data
        names (get data :arguments)
        actors (map (fn [actor_name] {:name actor_name :acted false}) names)]
    (database/delete-round channel_id)
    (database/add-round channel_id actors)
    (round data)))

(defn pass [data]
  (let [{arguments :arguments channel_id :channel} data
        actor_name (first (get data :arguments))]
    (if (database/has-actor-in-round channel_id actor_name)
      (do
        (database/update-actor-acted channel_id actor_name true)
        (round data))
      "Nothing found for this channel.")))

(def command_list [
  { :name "set_actors" :handler set_actors :min_args 1 :usage "!set_actors [list of actors]" }
  { :name "round" :handler round :max_args 0 :usage "!round" }
  { :name "pass" :handler pass :min_args 1 :usage "!pass [actor name]" }
])