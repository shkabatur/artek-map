(ns artek-map.views
  (:use
    [hiccup.page :only [html5 include-css include-js]]))

(defn index [req]
  (html5 [:head
          [:meta {:charset "utf-8"}]]
         [:body
          [:div  {:style "position:absolute"}
           [:button  {:onclick "window.location='edit';"} "Edit"]
           [:div {:id "mount-point"}]
           ]

          [:canvas {:id "canvas"
                    :width "1911"
                    :height "860"}]
          [:script {:src "js/map.js"}]]))

(defn edit-nodes
  [req]
  (if (:identity req)
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (slurp "resources/html/editor.html")}

    {:status 401
     :headers {"WWW-Authenticate" "Basic"}}))