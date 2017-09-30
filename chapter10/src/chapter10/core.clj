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
