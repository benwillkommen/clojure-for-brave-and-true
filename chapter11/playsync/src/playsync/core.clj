(ns playsync.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]))


; (def echo-chan (chan))
; (go (println (<! echo-chan)))
; (>!! echo-chan "ketchup")


(defn hot-dog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hot dog"))
    [in out]))

 (let [[in out] (hot-dog-machine)]
  (>!! in "pocket lint")
  (<!! out))

 (defn hot-dog-machine-v2
  [hot-dog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hot-dog-count]
      (if (> hc 0)
        (let [input (<! in)]
          (if (= 3 input)
            (do (>! out "hot dog")
              (recur (dec hc)))
            (do (>! out "wilted lettuce")
              (recur hc))))
        (do (close! in)
          (close! out)))))
    [in out]))

; (let [[in out] (hot-dog-machine-v2 2)]
;   (>!! in "pocket lint")
;   (println (<!! out))

;   (>!! in 3)
;   (println (<!! out))

;   (>!! in 3)
;   (println (<!! out))

;   (>!! in 3)
;   (<!! out))

; (let [c1 (chan)
;       c2 (chan)
;       c3 (chan)]
;   (go (>! c2 (clojure.string/upper-case (<! c1))))
;   (go (>! c3 (clojure.string/reverse (<! c2))))
;   (go (println (<! c3)))
;   (>!! c1 "redrum"))

(defn upload
  [headshot c]
  (go (Thread/sleep (rand 100))
    (>! c headshot)))

; (let [c1 (chan)
;       c2 (chan)
;       c3 (chan)]
;   (upload "serious.jpg" c1)
;   (upload "fun.jpg" c2)
;   (upload "sassy.jpg" c3)
;   (let [[headshot channel] (alts!! [c1 c2 c3])]
;     (println "Sending headshot notification for" headshot)))

(defn append-to-file
  "Write a string to the end of a file"
  [filename s]
  (spit filename s :append true))

(defn format-quote
  "Delineate trhe beginning and end of a quote because it's convenient"
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  "Retrieve a random quote and format it"
  []
  (format-quote (slurp "https://www.braveclojure.com/random-quote")))

(defn snag-quotes
  [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))