(ns exercises.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

; (defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))

; (defmethod full-moon-behavior :wolf
;   [were-creature]
;   (str (:name were-creature) " will howl and murdder"))

; (defmethod full-moon-behavior :simmons
;   [were-creature]
;   (str (:name were-creature) " will encourage people and sweat to oldies"))

; (defmethod full-moon-behavior :jeff
;   [were-creature]
;   (str (:name were-creature) " will smoke cigs"))

(defprotocol WereCreature
  (full-moon-behavior [x]))

(defrecord WereWolf [name title]
  WereCreature
  (full-moon-behavior [x]
    (str name " will howl and murder")))

(defrecord WereJeff [name title]
  WereCreature
  (full-moon-behavior [x]
    (str (:name x) " will smoke cigs")))


(defprotocol PlayerCharacter
  (attack [self target]))
(defrecord Mage [name]
  PlayerCharacter
  (attack [self target]
    (str (:name self) " attacks " (:name target) " with magic missle")))
(defrecord Warrior [name]
  PlayerCharacter
  (attack [self target]
    (str (:name self) " attacks " (:name target) " with sword")))
