(ns postgres-lingrbot.db
  (:use [clojure.java.jdbc :only (with-connection with-query-results print-sql-exception)])
  (:import [org.postgresql.util PSQLException])
  (:gen-class))

(def psql-db
  (when-let [url (System/getenv "DATABASE_URL")]
    (let [[_ user pass host port db]
          (re-matches #"postgres://([^:]+):([^@]+)@([^:]+):(\d+)/(.*)" url)]
      {:subprotocol "postgresql"
       :subname
       (format "//%s:%s/%s/?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
               host port db)
       :user user
       :password pass})))

(defn- hashmap-key-map [f hashmap]
  (into {}
        (mapcat (fn [[k v]]
                  {(keyword (f (name k))) v})
                hashmap)))

(defn go [query-str]
  (with-connection psql-db
    (try
      (with-query-results xs [query-str]
        (if xs
          (let [vect (mapv #(hashmap-key-map clojure.string/upper-case %)
                              (vec xs))
                vect2 (or (when-let [hashmap (first vect)]
                            (when (= 1 (count hashmap))
                              (mapv #(first (vals %)) vect)))
                          vect)
                vect-or-single (if (= 1 (count vect2)) (first vect2) vect2)]
            (str vect-or-single))
          "(empty)"))
      (catch PSQLException e (let [msg (str (.getServerErrorMessage e))]
                               (if (= "" msg) "(no results)" msg))))))

(defn -main []
  (let [result-str (go "SELECT url FROM 画像;")]
    (if (< 1000 (count result-str))
      (format "%s...(%d characters)"
              (clojure.string/join "" (take 500 result-str))
              (count result-str))
      result-str)))

; vim: set lispwords+=with-connection,with-query-results :
