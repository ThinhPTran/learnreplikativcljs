(ns learnreplikativcljs.core
  (:require  [goog.dom :as gdom]
             [reagent.core :as reagent]
             [learnreplikativcljs.db :as mydb]
             [learnreplikativcljs.events :as events]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn loginform []
  (let [input-text (:input-text @mydb/tmp-store)
        mgs @mydb/action-store]
    [:div.col-sm-2 
     [:input  
      {:id "my-input-box"   
       :type "text"   
       :value input-text   
       :onChange (fn [_]               
                   (let [v (.-value (gdom/getElement "my-input-box"))]
                     (.log js/console "change something!!!: " v)
                     (swap! mydb/tmp-store assoc :input-text v)))}]
     [:button#btn-login  
      {:type    "button"
       :onClick (fn []
                  (.log js/console "sending a message!!!")
                  (events/send-message input-text))}
      "Send a message!"]
     [:div (str "input text: " input-text)] 
     [:div (str "msgs: " mgs)]]))
        

(defn page []
  [:div nil
   [loginform]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))
    

(defn reload []
  (reagent/render [page]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload)
  (mydb/setupclientdata))
