(ns gmnitron.commands.universes
  (:require [gmnitron.common :as common]
            [clj-discord.core :as discord]
            [clojure.string :as str]
            [clj-fuzzy.metrics :as fuzzy]))

(def inverse-list [
  { :name "Luminary" :description "Princess Ivana Ramonat of Mordengrad, inventor and 'diplomancer.' Her versions of Baron Blade's doomsday devices are nonlethal and can be targeted more precisely."}
])

(defn first-match [list target]
  (let [best-match (apply max-key :dice (map #(hash-map :data % :dice (fuzzy/dice (:name %) target)) list))]
    (if (= (:dice best-match) 0.0)
      nil
      best-match)))

(defn inverse-command [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (let [match (first-match inverse-list (str/join arguments))]
        (if match
          (str (:name (:data match)) "\r\n" (:description (:data match)))
          "No character found with that name."))))

(def command-list [
  { :command "!inverse" :handler inverse-command :min-args 1 :usage "!inverse or !inverse 'character name'" :description "Hands off the scene to the next actor" }
])