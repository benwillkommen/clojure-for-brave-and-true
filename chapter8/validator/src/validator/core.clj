(ns validator.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def order-details
  {:name "Mitchard Blimmons"
    :email "mitchard.blimmonsgmail.com"})

(def order-details-validations
  {:name
    ["Please enter a name" not-empty]

    :email
    [
      "Please enter an email address"
      not-empty
      


      "Your email address doesn't look like an email address"
      #(or (empty? %) (re-seq #"@" %))
      ]})

(defn error-messages-for 
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
    (partition 2 message-validator-pairs))))

(defn validate
  "Returns a map with a vector of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                   value (get to-validate fieldname)
                   error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(defn if-valid-fn
  [record validations success-code failure-code]
  (let [errors (validate record validations)]
    (if (empty? errors)
      success-code
      failure-code)))

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
    (if (empty? ~errors-name)
      ~@then-else)))

(defmacro when-valid
  [to-validate validations & body]
  `(if (validate ~to-validate ~validations)
    (do ~@body)))

(defmacro my-or
  ([] nil)
  ([x] x)
  ([x & next]
    `(let [or# ~x]
      (if or# or# (my-or ~@next)))))


(def character
  {:name "Smooches McCutes"
    :attributes {:intelligence 10
    :strength 4
    :dexterity 5}})
; (def c-int (comp :intelligence :attributes))
; (def c-str (comp :strength :attributes))
; (def c-dex (comp :dexterity :attributes))

; (defmacro defattrs1
;   [& name-keyword-pairs]
;   `(doseq [pair# (partition 2 ~name-keyword-pairs)]
;     (def (first pair#) (comp (second pair#) :attributes))))

; (defmacro defattrs
;   [& name-keyword-pairs]
;   `(doseq [pair# (partition 2 ~name-keyword-pairs)]
;     (def (first pair#) (comp (second pair#) :attributes))))


(defmacro defattrs
  "Stolen from: https://github.com/brstf/cftbat/blob/master/chapter8/src/chapter8/core.clj"
  [& name-keyword-pairs]
  `(do ~@(map 
    #(intern *ns* (first %) (comp (second %) :attributes))
    (partition 2 name-keyword-pairs))))
