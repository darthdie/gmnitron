(defproject gmnitron "0.1.2-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-discord "0.1.0-SNAPSHOT"]
                 [com.novemberain/monger "3.1.0"]]
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
