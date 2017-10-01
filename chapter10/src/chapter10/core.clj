(ns chapter10.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
    (- 100 (:percent-deteriorated zombie))))

(defn shuffle-alert
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run, you fool!")
        (println "The zombie's SPH is now " sph)
        (println "This message brought to you courtesy of " key))
      (do
        (println "All's well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
        (<= percent-deteriorated 100)))

(def bobby
  (atom
    {:cuddle-hunger-level 0 :percent-deteriorated 0 }
    :validator percent-deteriorated-validator))

(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
   "passive-aggressive" "striped" "polka-dotted"
   "athletic" "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
    :count count})

(defn generate-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
    :socks #{}})

(def sock-gnome (ref (generate-sock-gnome "Barumpharumph")))
(def dryer (ref {:name "LG l337"
                  :socks (set (map #(sock-count % 2) sock-varieties))}))

(defn steal-sock
  [gnome dryer]
  (dosync
    (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
      (let [updated-count (sock-count (:variety pair) 1)]
        (alter gnome update-in [:socks] conj updated-count)
        (alter dryer update-in [:socks] disj pair)
        (alter dryer update-in [:socks] conj updated-count)))))

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))

;((def receiver-a (ref #{})) (def receiver-b (ref #{})) (def giver (ref #{1}))

(defn unsafe-giver
  [giver receiver-a receiver-b]  
  (do (future (dosync (let [gift (first @giver)]
                    (Thread/sleep 10)
                    (commute receiver-a conj gift)
                    (commute giver disj gift))))
      (future (dosync (let [gift (first @giver)]
                    (Thread/sleep 50)
                    (commute receiver-b conj gift)
                    (commute giver disj gift))))))

(defn safe-giver
  [giver receiver-a receiver-b]  
  (do (future (dosync (when-let [gift (first (ensure giver))]
                    (Thread/sleep 10)
                    (commute receiver-a conj gift)
                    (commute giver disj gift))))
      (future (dosync (when-let [gift (first (ensure giver))]
                    (Thread/sleep 50)
                    (commute receiver-b conj gift)
                    (commute giver disj gift))))))

(def alphabet-length 26)

(def letters (mapv (comp str char (partial + 65)) (range alphabet-length)))

(defn random-string
  "Returns a random string of specified length"
  [length]
  (apply str (take length (repeatedly #(rand-nth letters)))))

(defn random-string-list
  [list-length string-length]
  (doall (take list-length (repeatedly (partial random-string string-length)))))

(def orc-names (random-string-list 300 700))

(def my-atom (atom 0))

(swap! my-atom inc)

(defn count-words-in-string
  [some-string]
  (println some-string)
  (reduce 
    (fn [word-map word]
      (assoc word-map word (inc (get word-map word 0))))
  {}
  (filter #(not= % "") (clojure.string/split some-string #"[ \n\.-]"))))

(defn quote-word-count
  "I would not have designed this function in this way, but that the excercise explicitly said to make the http request and swap the atom in the same future."
  [quote-count]
  (let [word-map (atom {})
        ; watcher (add-watch word-map :word-count-monitor (fn [key watched old-state new-state]
        ;   (do (println key)
        ;     (println old-state)
        ;     (println new-state))))
        quote-futures (repeatedly quote-count          
            (fn [] (future (let [next-word-map (count-words-in-string (slurp "https://www.braveclojure.com/random-quote"))]
              (swap! 
                word-map 
                (fn [current-word-map] 
                  (merge-with + current-word-map next-word-map)))))))
        ]
    (last (map deref quote-futures))))


(def warrior (ref { :hp 15 :max-hp 40 :inventory #{}}))
(def cleric (ref { :hp 22 :max-hp 22 :inventory #{ :potion }}))

(defn heal
  [healer target]
  (dosync (when-let [potion (get-in @healer [:inventory :potion])]
    (let [target-max-hp (get @target :max-hp)]
      ((alter target assoc-in [:hp] target-max-hp)
      (alter healer update-in [:inventory] disj potion))))))