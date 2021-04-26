(ns yaml-splitter.core
  (:require [clojure.string :as str]
            [clj-yaml.core :as yaml]))


(def yaml-file "./example.yml")

(defn split-yaml->apply-fn
  "fn to split the yaml given milti-doc yaml file into "
  [file fn]
  (let [configs  (str/split (slurp file) #"---")]
    (doseq [config configs]
      (fn config))))


(defn gen<-mapper
  [path file-prefix]
  (fn [config]
    (let [profile (-> config
                      (yaml/parse-string)
                      (get-in [:spring :config :activate :on-profile]))
          file-to (str path file-prefix "-" profile ".yml")]

      (when (not (nil? profile))
        (println (str "writing to -->" file-to))
        (spit file-to (str/trim config))))))


(defn process-spring-yaml-config
  [yaml-file path prefix]
  (->>
   (gen<-mapper path prefix)
   (split-yaml->apply-fn yaml-file)))

(defn -main
  [& args]
  (let [path "./"
        prefix "application"]
    (process-spring-yaml-config  yaml-file path prefix)))
