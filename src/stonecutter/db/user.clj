(ns stonecutter.db.user
  (:require [clauth.user :as cl-user]
            [clauth.auth-code :as cl-auth-code]
            [clauth.store :as cl-store]
            [clojure.string :as s]
            [stonecutter.db.mongo :as m]
            [stonecutter.util.uuid :as uuid]
            [stonecutter.db.storage :as storage]))

(defn create-user [id-gen email password]
  (let [lower-email (s/lower-case email)]
    (->
      (cl-user/new-user lower-email password)
      (assoc :confirmed? false)
      (assoc :uid (id-gen)))))

(defn store-user! [user-store email password]
  (let [user (create-user uuid/uuid email password)]
    (-> (cl-user/store-user user-store user)
        (dissoc :password))))

(defn retrieve-user [user-store email]
  (cl-user/fetch-user user-store email))

(defn is-duplicate-user? [user-store email]
  (not (nil? (retrieve-user user-store (s/lower-case email)))))

(defn delete-user! [user-store email]
  (cl-store/revoke! user-store email))

(defn authenticate-and-retrieve-user [user-store email password]
  (-> (cl-user/authenticate-user user-store email password)
      (dissoc :password)))

(defn retrieve-user-with-auth-code [code]
  (-> (cl-auth-code/fetch-auth-code @storage/auth-code-store code) :subject))

(defn confirm-email! [user]
  (m/update! @storage/user-store (:login user)
             (fn [user] (-> user
                            (assoc :confirmed? true)))))

(defn unique-conj [things thing]
  (let [unique-things (set things)
        unique-things-list (into [] unique-things)]
    (if (unique-things thing)
      unique-things-list
      (conj unique-things-list thing))))

(defn add-client-id [client-id]
  (fn [user]
    (update-in user [:authorised-clients] unique-conj client-id)))

(defn add-authorised-client-for-user! [email client-id]
  (m/update! @storage/user-store email (add-client-id client-id)))

(defn remove-client-id [client-id]
  (fn [user]
    (update-in user [:authorised-clients] (partial remove #(= % client-id)))))

(defn remove-authorised-client-for-user! [email client-id]
  (m/update! @storage/user-store email (remove-client-id client-id)))

(defn is-authorised-client-for-user? [email client-id]
  (let [user (retrieve-user @storage/user-store email)
        authorised-clients (set (:authorised-clients user))]
    (boolean (authorised-clients client-id))))

(defn update-password [password]
  (fn [user]
    (assoc user :password (cl-user/bcrypt password))))

(defn change-password! [email new-password]
  (m/update! @storage/user-store email (update-password new-password)))
