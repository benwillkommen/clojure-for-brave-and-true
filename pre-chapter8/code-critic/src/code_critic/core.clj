(ns code-critic.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(defn criticize-code
  [criticism code]
    `(println ~criticism (quote ~code)))
(defmacro code-critic
  [bad good]
    `(do ~@(map #(apply criticize-code %)
            [ ["Cursed bacteria of Liberia, this is bad code:" bad]
              ["Sweet sacred boa of Western and Eastern Samoa, this is good code:" good]])))

(def message "Good job!")
(defmacro with-mischief 
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
    ~@stuff-to-do))

(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
        ~@stuff-to-do
        (println "message from macro: " ~macro-message))))

(defmacro without-mischief-autogensym
  [& stuff-to-do]
    `(let [macro-message# "Oh, big deal!"]
        ~@stuff-to-do
        (println "message from macro: " macro-message#)))