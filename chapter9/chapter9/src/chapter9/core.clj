(ns chapter9.core
  (:require [clj-http.client :as http])
  (:require [net.cgrand.enlive-html :as html])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
    (println "First deref:" message)
    message)))

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(defmacro enqueue
  ([q concurrent-promise-name concurrent serialized]
    `(let [~concurrent-promise-name (promise)]
      (future (deliver ~concurrent-promise-name ~concurrent))
      (deref ~q)
      ~serialized
      ~concurrent-promise-name))
  ([concurrent-promise-name concurrent serialized]
    `(enqueue (future) ~concurrent-promise-name ~concurrent ~serialized)))


(def search-engines {
    "google" { :name "google" :url-format "https://www.google.com/search?q=%s"}
    "bing" { :name "bing" :url-format "https://www.bing.com/search?q=%s"}
  })


(defn search
  [q search-engines]
  (let [search-promise (promise)]
    (doseq [url-format (map #(:url-format (second %)) search-engines)] 
      (future 
        (deliver search-promise (:body (http/get (format url-format q))))))
    @search-promise))

(defn search-all
  [q search-engines]
  (let [results 
        (map #(future (http/get (format (:url-format (second %)) q)))
        search-engines)]
    (reduce 
      (fn [urls html-future]
        (let [a-tags (html/select (html/html-snippet (:body @html-future)) [:a])]
          (into urls (map  #(:href (:attrs %)) a-tags))))
      []
      results)))

; (defn search-all
;   [q search-engines]
;   (let [results (map #(->> (second %)
;                             (:url-format)
;                             (fn [f] (format f q))
;                             (:body)) search-engines)]))

;(doseq [engine search-engines] (println (:url-format (second engine))))

; (defn get-search
;   [q]
;   (let [search-result-promise (promise)]
;    (deliver search-result-promise (:body (http/get (format "https://www.google.com/search?q=%s" q))))
;    @search-result-promise))



; (defn get-first-search-result-href-
;   [q]
;   (->>  q
;     (format "https://www.google.com/search?q=%s")
;     (http/get)
;     (:body)
;     (html/html-snippet)
;     (#(html/select % [:h3.r :a]))
;     (first)
;     (#(get-in % [:attrs :href]))))