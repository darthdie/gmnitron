(ns gmnitron.database
  (:require [clojure.string :as str]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all])
  (:import [com.mongodb MongoOptions ServerAddress]))

(def db_uri (System/getenv "GMNITRON_DB_URI"))

(def conn (atom (mg/connect-via-uri db_uri)))

(defn find-scene [channel_id]
  (mc/find-one-as-map (:db @conn) "scenes" { :channel_id channel_id }))

(defn insert-scene [channel_id data]
  (mc/update (:db @conn) "scenes" {:channel_id channel_id} data {:upsert true}))

(defn tick-scene [channel_id ticks]
  (mc/update (:db @conn) "scenes" {:channel_id channel_id} {$inc {:current_tick ticks}} {:upsert true}))

(defn find-round [channel_id]
  (with-collection (:db @conn) "rounds"
    (find { :channel_id channel_id })
    (sort (array-map :acted -1))))

(defn delete-round [channel_id]
  (mc/remove (:db @conn) "rounds" { :channel_id channel_id }))

(defn add-round [channel_id actors]
  (mc/insert-batch (:db @conn) "rounds" (map #(merge % { :search_name (str/lower-case (get % :name)) }) actors)))

(defn has-actor-in-round [channel_id actor_name]
  (mc/any? (:db @conn) "rounds" { :search_name (str/lower-case actor_name) :channel_id channel_id }))

(defn update-actor-acted [channel_id actor_name acted]
  (mc/update (:db @conn) "rounds" { :search_name (str/lower-case actor_name) :channel_id channel_id } { $set { :acted acted } }))