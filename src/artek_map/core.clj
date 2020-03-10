(ns artek-map.core
  (:require [org.httpkit.server :refer [run-server]]
            [cheshire.core :refer [generate-string parse-string]]
            [compojure.route :refer [not-found resources]]
            [compojure.core :refer [defroutes GET POST]]
            [overtone.at-at :refer [every mk-pool]]
            [artek-map.ping :refer [ping]]
            [artek-map.views :refer [index edit-nodes]]
            [buddy.auth.backends :refer [basic]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            )
  (:gen-class))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server [app]
  (when (nil? @server)
    (reset! server (run-server app {:port 80}))))

(def my-pool (mk-pool))
(def nodes (atom []))
(reset! nodes (parse-string (slurp "nodes.json") true))


(def pinged-nodes (atom []))


(defn update-pinged-nodes [n]
  (every n #(reset! pinged-nodes (map conj @nodes (pmap ping (map :ip @nodes))))
         my-pool))

(defn my-authfn
  [req authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (= username password "admin")))

(def backend (basic {:realm "MyApi"
                     :authfn my-authfn}))

(defroutes app
  (GET "/" [] #'index)

  (GET "/edit" [] #'edit-nodes)

  (GET "/nodes" []
    (fn [req]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    (generate-string @pinged-nodes)
       }))
  
  (POST "/change-nodes" []
    (fn [req]
      (reset! nodes (parse-string (slurp (:body req)) true))
      (spit "nodes.json" (generate-string @nodes))))

  (resources "/")
  (not-found "<p>Page not found.</p>"))

(def app-with-auth
  (wrap-authentication #'app backend))

(defn -main
  [& args]
  (update-pinged-nodes 4000)
  (reset! server (run-server #'app-with-auth {:port 80}))
  (println "Server is up.... port - 80")
  )
