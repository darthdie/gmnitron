(defproject gmnitron "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-discord "0.1.0-SNAPSHOT"]]
  :main ^:skip-aot gmnitron.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
