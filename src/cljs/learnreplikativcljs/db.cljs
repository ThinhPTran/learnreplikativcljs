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

(defonce action-store (r/atom {}))
(defonce tmp-store (r/atom {}))
(declare client-state)

;; Stream function 
(def eval-fnstream-eval-fns {'assoc (fn [old v]
                                      (swap! old assoc (uuid v) v)
                                      old)
                             'dissoc (fn [old v]
                                       (swap! old dissoc (uuid v))
                                       old)})

(defn sendMessage! [rawmsg]
  (let [msg {:msg rawmsg
             :inst (.getTime (js/Date.))}]
    (s/assoc! (:stage client-state)
              [user ormap-id]
              (uuid msg)
              [['assoc msg]])))

(defn handle-changes []
  (.log js/console (str "app-store: " @action-store)))

(add-watch action-store :key #(handle-changes))

(defn setup-replikativ []
  (go-try S
    (let [local-store (<? S (new-mem-store))
          local-peer (<? S (client-peer S local-store))
          stage (<? S (create-stage! user local-peer))
          stream (stream-into-identity!
                   stage
                   [user ormap-id]
                   eval-fnstream-eval-fns
                   action-store)]
      (<? S (s/create-ormap! stage :description "all datas" :id ormap-id))
      (connect! stage uri)
      {:store local-store
       :stage stage
       :stream stream
       :peer local-peer})))

(defn setupclientdata []
  (go-try S
    (def client-state (<? S (setup-replikativ)))))







