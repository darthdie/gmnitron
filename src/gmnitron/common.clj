(ns gmnitron.common
  (:require [clj-discord.core :as discord]
            [clojure.string :as str]))

(defn correct_argument_count [arguments min max]
  (not (or (< (count arguments) min) (> (count arguments) max))))

(defn discord_response
  ([type data arguments usage f] (discord_response data arguments usage 0 100 f))
  ([type data arguments usage min max f]
    (discord/answer-command data
                            (get data "content")
                            (if (correct_argument_count arguments min max)
                                (f)
                                usage))))
                      
(defmacro fmt [^String string]
  (let [-re #"#\{(.*?)\}"
        fstr (str/replace string -re "%s")
        fargs (map #(read-string (second %)) (re-seq -re string))]
    `(format ~fstr ~@fargs)))

(defn oxford
  ([items] (oxford (rest items) (first items)))
  ([items msg]
    (if (< (count items) 1)
      msg
      (if (= (count items) 1)
        (str msg ", and " (first items))
        (recur (rest items) (str msg ", " (first items)))))))