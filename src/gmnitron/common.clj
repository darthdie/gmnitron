(ns gmnitron.common
  (:require [clj-discord.core :as discord]
            [clojure.string :as str]))

(defn correct-argument-count [arguments min max]
  (and (>= (count arguments) min) (<= (count arguments) max)))

(defn discord-response
  ([type data arguments usage f] (discord-response data arguments usage 0 100 f))
  ([type data arguments usage min max f]
    (discord/answer-command data
                            (get data "content")
                            (if (correct-argument-count arguments min max)
                                (f)
                                usage))))
                      
(defmacro fmt [^String string]
  (let [-re #"#\{(.*?)\}"
        fstr (str/replace string -re "%s")
        fargs (map #(read-string (second %)) (re-seq -re string))]
    `(format ~fstr ~@fargs)))

(defn str->int [str] (Integer. str))

(defn stripl [str s] (if (str/starts-with? str s) (subs str (count s)) str))

(defn oxford
  ([items] (oxford (rest items) (first items)))
  ([items msg]
    (if (empty? items)
      msg
      (if (= (count items) 1)
        (str msg ", and " (first items))
        (recur (rest items) (str msg ", " (first items)))))))

(defn is-quote? [c]
  (or (= \“ c) (= \" c) (= \” c)))

(def is-not-quote? (complement is-quote?))

(defn splitter [s]
  ((fn step [xys]
     (lazy-seq
      (when-let [c (ffirst xys)]
        (cond
         (Character/isSpace c)
         (step (rest xys))
         (is-quote? c)
         (let [[w* r*]
               (split-with (fn [[x y]] (or (is-not-quote? x) (not (or (nil? y) (Character/isSpace y)))))
                           (rest xys))]
           (if (is-quote? (ffirst r*))
             (cons (apply str (map first w*)) (step (rest r*)))
             (cons (apply str (map first w*)) nil)))
         :else
         (let [[w r] (split-with (fn [[x y]] (not (Character/isSpace x))) xys)]
           (cons (apply str (map first w)) (step r)))))))
   (partition 2 1 (lazy-cat s [nil]))))

(defn first-char [str]
  (subs str 0 1))

(defn in? [coll elm]  
  (some #(= elm %) coll))

(defn as-vector [x]
  (cond
    (vector? x) x
    (sequential? x) (vec x)
    :else (vector x)))