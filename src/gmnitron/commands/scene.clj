(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]))

(defn establish [data]
  (let [{arguments :arguments author :author} data]
    (get author "username")))

(def command_list [
  { :name "establish" :handler establish :min_args 3 :max_args 3 :usage "!establish [number of green scenes] [number of yellow scenes] [number of red scenes]" }
])