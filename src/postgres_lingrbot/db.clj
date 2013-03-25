(ns postgres-lingrbot.db
  (:use [clojure.java.jdbc :only (with-connection with-query-results print-sql-exception)])
  (:import [org.postgresql.util PSQLException])
  (:gen-class))

#_(def psql-db {:subprotocol "postgresql"
              :subname "//ec2-54-243-125-2.compute-1.amazonaws.com:5432/da186to3ucn6ja/?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
              :user "fwuvhocstogrhv"
              :password ""})

(def psql-db
  (when-let [url (System/getenv "DATABASE_URL")]
    (let [[_ user pass host port db]
          (re-matches #"postgres://([^:]+):([^@]+)@([^:]+):(\d+)/(.*)" url)]
      {:subprotocol "postgresql"
       :subname (format "//%s:%s/%s/?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
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
      (with-query-results xs
        [#_"DROP TABLE cities"
         #_"CREATE TABLE cities ( name varchar(80), country_name text)"
         #_"INSERT INTO cities VALUES ('Vancouver', 'Canada')"
         query-str]
        (if xs
          (str
            (mapv #(hashmap-key-map clojure.string/upper-case %)
                  (vec xs)))
          "(empty)"))
      (catch PSQLException e (let [msg (str (.getServerErrorMessage e))]
                               (if (= "" msg) "(no results)" msg))))))

(defn -main []
  (let [result-str (go "SELECT * FROM cities WHERE name = 'Vancouver';")]
    (if (< 1000 (count result-str))
      (format "%s...(%d characters)"
              (clojure.string/join "" (take 500 result-str))
              (count result-str))
      result-str)))

; vim: set lispwords+=with-connection,with-query-results :
