(ns clojure-noob.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn matching-part
  [part]
  { :name (clojure.string/replace (:name part) #"^left-" "right-")
    :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size",
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
          (into final-body-parts 
            (set [part (matching-part part)])))))))

(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
    (into final-body-parts (set [part (matching-part part)])))
    []
    asym-body-parts))

(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
            accumulated-size (:size part)]
          (if (> accumulated-size target)
            part
            (recur remaining (+ accumulated-size (:size (first remaining))))))))

(def asym-hobbit-body-parts [{:name "head" :size 3}
  {:name "left-eye" :size 1}
  {:name "left-ear" :size 1}
  {:name "mouth" :size 1}
  {:name "nose" :size 1}
  {:name "neck" :size 2}
  {:name "left-shoulder" :size 3}
  {:name "left-upper-arm" :size 3}
  {:name "chest" :size 10}
  {:name "back" :size 10}
  {:name "left-forearm" :size 3}
  {:name "abdomen" :size 6}
  {:name "left-kidney" :size 1}
  {:name "left-hand" :size 2}
  {:name "left-knee" :size 2}
  {:name "left-thigh" :size 4}
  {:name "left-lower-leg" :size 3}
  {:name "left-achilles" :size 1}
  {:name "left-foot" :size 2}])

(defn mapset
  [f col]
  (set (map f col)))

(defn hobbit-part-expander
  [part]
  (set [part (matching-part part)]))

(defn alien-part-expander
  [part]
  (if (re-find #"^left-" (:name part))
    (map (fn [num] 
        { :name (str (clojure.string/replace (:name part) #"^left-" "") "-" num)
          :size (:size part) }
      ) [1 2 3 4 5])
    part))

(defn generic-part-expander
  [part part-num]
  (if (re-find #"^left-" (:name part))
    (map (fn [num] 
        { :name (str (clojure.string/replace (:name part) #"^left-" "") "-" num)
          :size (:size part) }
      ) (range part-num))
    part))

(defn generic-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts part-expander]
  (reduce (fn [final-body-parts part]
    (into final-body-parts (part-expander part)))
    []
    asym-body-parts))

(defn variable-symmetrize-body-parts
  [asym-body-parts body-part-num]
  (reduce (fn [final-body-parts part]
    (into final-body-parts (generic-part-expander part body-part-num)))
    []
    asym-body-parts))

(defn my-map 
  [f col]
  (reduce (fn [new-seq elem]
    (conj new-seq (f elem) ))
    '()
    col))