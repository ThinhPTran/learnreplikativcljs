(ns learnreplikativcljs.events
  (:require [clojure.string :as str]
            [learnreplikativcljs.db :as mydb :refer [app-store]]))

(defn user-login [name]
  (if (str/blank? name)
    (js/alert "Please enter a user name")
    (do 
      (.log js/console (str "Logging in with user: " name))
      (mydb/changeUser! name))))




