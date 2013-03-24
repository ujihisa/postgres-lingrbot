(ns postgres-lingrbot.core
  (:require [postgres-lingrbot.db :as db])
  (:use [compojure.core :only (defroutes GET POST)]
        [clojure.data.json :only (read-json)]
        [ring.adapter.jetty :only (run-jetty)])
  (:import java.util.concurrent.ExecutionException)
  (:gen-class))

(defroutes routes
  (GET "/" []
       (str {:version "1.0.0-SNAPSHOT" :homepage "https://github.com/ujihisa/postgres-lingrbot"}))
  (POST "/" {body :body}
        (let [results (for [message (map :message (:events (read-json (slurp body))))
                            :when (#{"computer_science" "lingr"} (:room message))
                            :let [query-str (:text message)]
                            :when (re-find #"^[A-Z].*;$" query-str)]
                        (let [result-str (db/go query-str)]
                          (if (< 1000 (count result-str))
                            (format "%s...(%d characters)"
                                    (clojure.string/join "" (take 500 result-str))
                                    (count result-str))
                            result-str)))]
          (clojure.string/join "\n" results))))

(defn -main []
 ) (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
     (run-jetty routes {:port port :join? false}))

; vim: set lispwords+=defroutes :
