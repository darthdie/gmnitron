(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(defn establish [data]
  (let [{arguments :arguments channel_id :channel} data
        [green_ticks yellow_ticks red_ticks] (map common/str->int arguments)]
    (database/insert-scene channel_id { :green_ticks green_ticks :yellow_ticks yellow_ticks :red_ticks red_ticks :current_tick 0 })
    (common/fmt "Scene has been established with #{green_ticks} green boxes, #{yellow_ticks} yellow boxes, and #{red_ticks} red boxes.")))

(defn report [data]
  (let [{channel_id :channel} data]
    (if-let [scene (database/find-scene channel_id)]
      (let [{green :green_ticks yellow :yellow_ticks red :red_ticks tick :current_tick} scene
            boxes (concat (replicate green "Green") (replicate yellow "Yellow") (replicate red "Red"))]
        (if (>= tick (count boxes))
          "Scene has reached the end"
          (let [current_box (nth boxes (max 0 (- tick 1)))
                remaining_boxes (drop tick boxes)
                formatted_remaining_boxes (common/oxford (map #(str (second %) " " (first %) " boxes") (frequencies remaining_boxes)))]
            (common/fmt "It is currently a #{current_box} status. There are #{formatted_remaining_boxes} left."))))
      "Alas! No scene found for this channel.")))

(defn tick [data]
  (let [{arguments :arguments channel_id :channel} data]
    (if-let [scene (database/find-scene channel_id)]
      (do
        (database/tick-scene channel_id 1)
        (report data))
      "Alas! No scene found for this channel.")))

(def command_list [
  { :name "scene_establish" :handler establish :min_args 3 :max_args 3 :usage "!scene_establish (number of green ticks) (number of yellow ticks) (number of red ticks)" }
  { :name "scene_tick" :handler tick :min_args 0 :max_args 0 :usage "!scene_tick" }
  { :name "scene" :handler report :min_args 0 :max_args 0 :usage "!report"}
])