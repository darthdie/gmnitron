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