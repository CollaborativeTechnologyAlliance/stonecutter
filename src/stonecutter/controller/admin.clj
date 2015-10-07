(ns stonecutter.controller.admin
  (:require [ring.util.response :as r]
            [stonecutter.helper :as sh]
            [stonecutter.view.user-list :as user-list]
            [stonecutter.view.apps-list :as apps-list]
            [stonecutter.db.user :as u]
            [stonecutter.db.client :as c]
            [stonecutter.routes :as routes]
            [stonecutter.config :as config]))

(defn show-user-list [user-store request]
  (let [users (u/retrieve-users user-store)]
    (sh/enlive-response (-> request
                            (assoc-in [:context :users] users)
                            (user-list/user-list))
                        (:context request))))

(defn set-user-trustworthiness [user-store request]
  (let [email (get-in request [:params :login])
        trusted? (get-in request [:params :trust-toggle])
        role (if trusted?
               (:trusted config/roles)
               (:untrusted config/roles))
        flash-key (if trusted?
                    :user-trusted
                    :user-untrusted)]

    (u/update-user-role! user-store email role)
    (-> (r/redirect (routes/path :show-user-list))
        (assoc-in [:flash :translation-key] flash-key)
        (assoc-in [:flash :updated-account-email] email))))


(defn show-apps-list [client-store request]
  (let [clients (c/retrieve-clients client-store)]
    (sh/enlive-response (-> request
                            (assoc-in [:context :clients] clients)
                            (apps-list/apps-list))
                        (:context request))))

(defn create-client [client-store request]
  (let [client-name (get-in request [:params :name])
        client-url (get-in request [:params :url])]
    (when (and (not-empty client-name) (not-empty client-url))
      (c/store-client client-store client-name client-url))
    (r/redirect (routes/path :show-apps-list))))


