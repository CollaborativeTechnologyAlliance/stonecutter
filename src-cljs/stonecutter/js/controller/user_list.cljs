(ns stonecutter.js.controller.user-list
  (:require [ajax.core :refer [POST]]
            [dommy.core :as d])
  (:require-macros [dommy.core :as dm]))

;(defn handler [response]
;  (.log js/console (str "successful response: " status " " status-text)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn anti-forgery []
    (d/value (dm/sel1 :#__anti-forgery-token)))

(defn update-role [e]
  (let [checked (.-checked (.-target e))
        login  (.-id (.-target e))]
    (POST "/admin/set-user-trustworthiness"
          {:params  {"login"        login 
                     "trust-toggle" checked}
           :headers {:X-CSRF-Token (anti-forgery)}  
                    :format :json
                    ;:handler handler 
                    :error-handler error-handler})))
