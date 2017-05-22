(ns learnreplikativcljs.db  
  (:require [hasch.core :refer [uuid]]
            [reagent.core :as r]
            [konserve.memory :refer [new-mem-store]]
            [replikativ.peer :refer [client-peer]]
            [replikativ.stage :refer [create-stage! connect! subscribe-crdts!]]
            [cljs.core.async :refer [>! chan timeout]]
            [superv.async :refer [S] :as sasync]
            [replikativ.crdt.ormap.realize :refer [stream-into-identity!]]
            [replikativ.crdt.ormap.stage :as s])  
  (:require-macros [superv.async :refer [go-try <? go-loop-try]] 
                   [cljs.core.async.macros :refer [go-loop]]))

(def user "trphthinh@gmail.com")
(def ormap-id #uuid "7d274663-9396-4247-910b-409ae35fe98d")
(def uri "ws://127.0.0.1:31744")

(declare client-state)

;; Stream function 
(def stream-eval-fns
  {'assoc (fn [a new]     
              (swap! a assoc (uuid new) new)          
              a) 
   'dissoc (fn [a new]           
             (swap! a dissoc (uuid new))           
             a)
   'changUser (fn [a new]
                (swap! a assoc :user new))})

(defn changeUser! [newname]
  (s/assoc! (:stage client-state)
    [user ormap-id]
    (uuid newname)
    [['changUser newname]]))

(defonce app-store (r/atom {:user "thinh"
                            :input-text "sample text"}))

(defn setup-replikativ []
  (go-try S
    (let [local-store (<? S (new-mem-store))
          local-peer (<? S (client-peer S local-store))
          stage (<? S (create-stage! user local-peer))
          stream (stream-into-identity! 
                   stage
                   [user ormap-id]
                   stream-eval-fns
                   app-store)]
      (<? S (s/create-ormap! stage :description "all datas" :id ormap-id))
      (connect! stage uri)
      {:store local-store
       :stage stage
       :stream stream
       :peer local-peer})))

(defn setupclientdata []
  (go-try S
    (def client-state (<? S (setup-replikativ)))))







