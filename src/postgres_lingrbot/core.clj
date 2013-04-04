(ns postgres-lingrbot.core
  (:require [postgres-lingrbot.db :as db]
            [leiningen.core.project])
  (:use [compojure.core :only (defroutes GET POST)]
        [clojure.data.json :only (read-json)]
        [ring.adapter.jetty :only (run-jetty)])
  (:import java.util.concurrent.ExecutionException)
  (:gen-class))

(def version
  (:version (leiningen.core.project/read)))

(def start-time
  (java.util.Date.))

(defroutes routes
  (GET "/" []
       (str {:version version
             :homepage "https://github.com/ujihisa/postgres-lingrbot"
             :from start-time}))
  (POST "/" {body :body headers :headers}
        (when (#{"219.94.235.225" "54.251.132.173" "64.46.24.16" "106.189.116.59"} (headers "x-forwarded-for"))
          (let [results (for [message (map :message (:events (read-json (slurp body))))
                              :when (#{"computer_science" "lingr" "vim"} (:room message))
                              :let [query-str (:text message)]
                              :when (re-find #"^[A-Z].*;$" query-str)]
                          (let [result-str (db/go query-str)]
                            (if (< 1000 (count result-str))
                              (format "%s...(%d characters)"
                                      (clojure.string/join "" (take 500 result-str))
                                      (count result-str))
                              result-str)))]
            (when (< 0 (count results))
              (format "columns: %d, rows: %d" (count (first results)) (count results)))
            #_(if (and
                  (= 1 (count results))
                  (= 1 (count (first results))))
              (str (first (vals (first results))))
              (clojure.string/join "\n" results))))))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
    (run-jetty routes {:port port :join? false})))

; vim: set lispwords+=defroutes :
