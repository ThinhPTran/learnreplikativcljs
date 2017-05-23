(ns learnreplikativcljs.events
  (:require [clojure.string :as str]
            [learnreplikativcljs.db :as mydb :refer [action-store]]))

(defn send-message [msg]
  (if (str/blank? msg)
    (js/alert "Please enter a message")
    (do
      (.log js/console (str "Sending a message: " msg))
      (mydb/sendMessage! (str msg)))))




