(defproject postgres-lingrbot "0.1.0-SNAPSHOT"
  :description "Use PostgreSQL from Lingr"
  :url "http://github.com/ujihisa/postgres-lingrbot"
  :license {:name "GNU GPL v3+"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql "9.1-901.jdbc4"]
                 [org.clojars.ccfontes/korma "0.3.0-beta12-pgssl"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [compojure "1.1.3"]]
  :main postgres-lingrbot.core)
