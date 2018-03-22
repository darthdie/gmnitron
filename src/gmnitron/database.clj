(ns gmnitron.database
  (:require [clojure.string :as str]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all])
  (:import [com.mongodb MongoOptions ServerAddress]))

(def db-uri (System/getenv "GMNITRON_DB_URI"))

(def db (atom (:db (mg/connect-via-uri db-uri))))

(defn insert-scene [channel-id data]
  (let [scene (merge (dissoc data :initiative) { :channel-id channel-id })
        initiative (:initiative data)]
    (mc/update @db "scenes" { :channel-id channel-id } scene {:upsert true})
    (mc/remove @db "initiatives" {:channel-id channel-id})
    (mc/insert-batch @db "initiatives" (map #(merge % { :channel-id channel-id }) initiative))))

(defn get-scene [channel-id]
  (let [scene (mc/find-one-as-map @db "scenes" { :channel-id channel-id })
        initiative (with-collection @db "initiatives" (find { :channel-id channel-id }) (sort (array-map :acted -1 :name 1)))]
    (when (and scene initiative) (merge scene { :initiative initiative }))))

(defn has-actor-in-scene [channel-id actor-name]
  (mc/any? @db "initiatives" { :search-name (str/lower-case actor-name) :channel-id channel-id }))

(defn update-scene-actor-acted [channel-id actor-name acted]
  (mc/update @db "initiatives" { :channel-id channel-id :search-name (str/lower-case actor-name) } { $set { :acted acted } }))

(defn has-any-unacted-actors [channel-id]
  (mc/any? @db "initiatives" { :channel-id channel-id :acted false } ))

(defn reset-scene-initiative [channel-id]
  (mc/update @db "initiatives" { :channel-id channel-id } { $set { :acted false } } {:multi true}))

(defn tick-scene [channel-id]
  (mc/update @db "scenes" { :channel-id channel-id } { $inc { :current-tick 1 } }))