(ns gmnitron.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]
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