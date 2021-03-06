(ns stonecutter.admin
  (:require [stonecutter.config :as config]
            [stonecutter.validation :as v]
            [clojure.tools.logging :as log]
            [stonecutter.db.user :as u]))

(defn create-admin-user [config-m user-store]
  (let [admin-first-name (config/admin-first-name config-m)
        admin-last-name (config/admin-last-name config-m)
        admin-login (config/admin-login config-m)
        admin-password (config/admin-password config-m)
        duplication-checker (partial u/user-exists? user-store)
        errors (or (v/validate-registration-email admin-login duplication-checker)
                   (v/validate-password-format admin-password))]
    (when (and admin-login admin-password)
      (case errors
        :duplicate (log/info "Admin account already exists.")
        :invalid   (throw (Exception. "INVALID ADMIN LOGIN DETAILS - please check that admin login is in the correct format"))
        :too-short (throw (Exception. "ADMIN PASSWORD is too short - please check that admin password is in the correct format"))
        :too-long  (throw (Exception. "ADMIN PASSWORD is too long - please check that admin password is in the correct format"))
        nil (u/store-admin!
              user-store
              admin-first-name
              admin-last-name
              admin-login
              admin-password)))))
