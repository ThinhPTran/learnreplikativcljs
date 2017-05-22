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
  (let [input-text (:input-text @mydb/app-store)
        name (:user @mydb/app-store)]
    [:div.col-sm-2 
     [:input  
      {:id "my-input-box"   
       :type "text"   
       :value input-text   
       :onChange (fn [_]               
                   (let [v (.-value (gdom/getElement "my-input-box"))]                 
                     (.log js/console "change something!!!: " v)  
                     (swap! mydb/app-store assoc :input-text v)))}] 
     [:button#btn-login  
      {:type "button"   
       :onClick (fn []              
                  (.log js/console "logging in!!!")
                  (events/user-login input-text))}  
      "Secure login!"] 
     [:div (str "input text: " input-text)] 
     [:div (str "user name: " name)]]))
        

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
