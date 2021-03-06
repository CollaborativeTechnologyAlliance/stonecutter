(ns stonecutter.middleware
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as r]
            [ring.middleware.file :as ring-mf]
            [stonecutter.translation :as translation]
            [stonecutter.routes :as routes]
            [stonecutter.controller.user :as user]
            [stonecutter.helper :as helper]
            [stonecutter.config :as config]
            [stonecutter.controller.common :as common]))

(defn wrap-error-handling [handler err-handler dev-mode?]
  (if-not dev-mode?
    (fn [request]
      (try
        (handler request)
        (catch Exception e
          (log/error e e)
          (err-handler request))))
    handler))

(defn wrap-handle-403 [handler error-403-handler]
  (fn [request]
    (let [response (handler request)]
      (if (= (:status response) 403)
        (error-403-handler request)
        response))))

(defn wrap-config [handler config-m]
  (fn [request]
    (-> request
        (assoc-in [:context :config-m] config-m)
        handler)))

(defn wrap-handlers-except [handlers wrap-function exclusions]
  (into {} (for [[k v] handlers]
             [k (if (k exclusions) v (wrap-function v))])))

(defn wrap-disable-caching [handler]
  (fn [request]
    (-> request
        handler
        helper/disable-caching)))

(defn wrap-signed-in [handler]
  (fn [request]
    (if (common/signed-in? request)
      (handler request)
      (r/redirect (routes/path :index)))))

(defn wrap-custom-static-resources [handler config-m]
  (if-let [static-resources-dir-path (config/static-resources-dir-path config-m)]
    (do (log/info (str "All resources in " static-resources-dir-path " are now being served as static resources"))
        (ring-mf/wrap-file handler static-resources-dir-path))
    handler))

(defn wrap-just-these-handlers [handlers-m wrap-function inclusions]
  (into {} (for [[k v] handlers-m]
             [k (if (k inclusions) (wrap-function v) v)])))

(defn wrap-authorised [handler authorisation-checker]
  (fn [request]
    (when (authorisation-checker request)
      (handler request))))

