(ns artek-map.map.components)

(defn node-component
  [node]
  [:div {:id "current-node"}
   [:font {:size 5 :color "#53DE0D"}
    [:p (:name @node) ": " (:ip @node)]]]
  )