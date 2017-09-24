(ns chapter9.core
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