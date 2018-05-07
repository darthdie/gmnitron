(ns gmnitron.database
  (:require [clojure.string :as str]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all])
  (:import [com.mongodb MongoOptions ServerAddress]))

(def db-uri (System/getenv "MONGODB_URI"))

(def db (atom (:db (mg/connect-via-uri db-uri))))

(defn insert-scene [channel-id data]
  (let [scene (merge (dissoc data :initiative) { :channel-id channel-id })
        initiative (:initiative data)]
    (mc/update @db "scenes" { :channel-id channel-id } scene {:upsert true})
    (mc/remove @db "initiatives" {:channel-id channel-id})
    (mc/insert-batch @db "initiatives" (map #(merge % { :channel-id channel-id :search-name (str/lower-case (:name %)) :current false }) initiative))))

(defn get-initiative [channel-id]
  (with-collection @db "initiatives" (find { :channel-id channel-id }) (sort (array-map :acted -1 :name 1))))

(defn get-scene [channel-id]
  (let [scene (mc/find-one-as-map @db "scenes" { :channel-id channel-id })
        initiative (get-initiative channel-id)]
    (when (and scene initiative) (merge scene { :initiative initiative }))))

(defn has-scene? [channel-id]
  (mc/any? @db "scenes" { :channel-id channel-id } ))

(defn add-actor 
  ([channel-id actor-name] (add-actor channel-id actor-name false))
  ([channel-id actor-name acted]
    (mc/insert @db "initiatives" { :channel-id channel-id :name actor-name :search-name (str/lower-case actor-name) :acted acted })))

(defn remove-actor [channel-id actor-name]
  (mc/remove @db "initiatives" { :channel-id channel-id :search-name (str/lower-case actor-name) } ))

(defn has-actor-in-scene? [channel-id actor-name]
  (mc/any? @db "initiatives" { :search-name (str/lower-case actor-name) :channel-id channel-id }))

(defn has-actors-in-scene? [channel-id actors]
  (every? #(has-actor-in-scene? channel-id %) actors))

(defn update-scene-actor-acted [channel-id actor-name acted]
  (mc/update @db "initiatives" { :channel-id channel-id :search-name (str/lower-case actor-name) } { $set { :acted acted } }))

(defn update-scene-set-active [channel-id name]
  (mc/update @db "initiatives" { :channel-id channel-id } { $set { :current false } } { :multi true })
  (mc/update @db "initiatives" { :channel-id channel-id :search-name (str/lower-case name) } { $set { :current true } }))

(defn actor-has-acted? [channel-id name]
  (:acted (mc/find-one-as-map @db "initiatives" { :channel-id channel-id :search-name (str/lower-case name)})))

(defn is-last-actor? [channel-id name]
  (and
    (= (mc/count @db "initiatives" { :channel-id channel-id :acted false }) 1)
    (mc/any? @db "initiatives" { :channel-id channel-id :current true :acted false :search-name (str/lower-case name) } )))

(defn current-actor [channel-id]
  (mc/find-one-as-map @db "initiatives" { :channel-id channel-id :current true }))

(defn has-current-actor? [channel-id]
  (mc/any? @db "initiatives" { :current true :channel-id channel-id }))

(defn is-current-actor? [channel-id name]
  (mc/any? @db "initiatives" { :channel-id channel-id :current true :search-name (str/lower-case name)}))

(defn all-actors-acted? [channel-id]
  (= (mc/count @db "initiatives" { :channel-id channel-id :acted false }) 0))

(defn reset-scene-initiative [channel-id]
  (mc/update @db "initiatives" { :channel-id channel-id } { $set { :acted false } } {:multi true}))

(defn tick-scene [channel-id]
  (mc/update @db "scenes" { :channel-id channel-id } { $inc { :current-tick 1 } }))

(defn hand-off [channel-id from to]
  (update-scene-actor-acted channel-id from true)
  (when (all-actors-acted? channel-id)
    (reset-scene-initiative channel-id))
  (update-scene-set-active channel-id to))
