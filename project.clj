(defproject gmnitron "0.5.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-discord "0.1.0-SNAPSHOT"]
                 [com.novemberain/monger "3.1.0"]
                 [clojure.java-time "0.3.2"]
                 [net.mikera/core.matrix "0.29.1"]
                 [random-seed "1.0.0"]
                 [clj-http "3.9.1"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-heroku "0.5.3"]]
  :main ^:skip-aot gmnitron.core
  :target-path "target/%s"
  :uberjar-name "gmnitron-standalone.jar"
  :min-lein-version "2.0.0"
  :heroku {
    :app-name "gmnitron"
    :jdk-version "1.8"
    :include-files ["target/uberjar/gmnitron-standalone.jar"]
    :process-types { "worker" "java -jar target/uberjar/gmnitron-standalone.jar" }}
  :profiles {:uberjar {:aot :all}})
