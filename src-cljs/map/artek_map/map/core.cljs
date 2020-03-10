(ns artek-map.map.core
            (:require-macros [cljs.core.async.macros :refer [go]])
            (:require [cljs-http.client :as http]
                      [cljs.core.async :refer [<!]]
                      [reagent.core :as r]
                      #_[cljs.pprint :refer [pprint]]
                      [artek-map.canvas.core :as canvas]
                      [artek-map.map.components :as components]
                      ))


(enable-console-print!)

(def current-node (r/atom {}))
(def nodes (atom []))
(def node-radius (atom 10))


;;****************************************************************
;;setup canvas
(def canvas-dom (.getElementById js/document "canvas"))
(def ctx (canvas/create-2d-context canvas-dom))

(canvas/font-style ctx "italic 10pt Arial")

(def map-img (canvas/create-image "img/map-night.jpg"))

;;****************************************************************

(defn update-nodes-and-draw
  [nodes]
  (go (let [response (<! (http/get "/nodes"))]
        (reset! nodes (:body response))))
  (canvas/draw-nodes ctx @nodes @node-radius))


(set! (.-onload js/window)
      (fn []
        (r/render [components/node-component current-node]
                  (js/document.getElementById "mount-point"))
        (print "KEK")
        (canvas/draw-image ctx map-img {:x 0 :y 0 :w 1910 :h 859})
        ;;этот вызов необходим для мгновенной отрисовки точек при загрузке страницы,
        ;;если вызвать просто draw-nodes , отрисовка произойдет через несколько секунд
        (go (let [response (<! (http/get "/nodes"))]
              (reset! nodes (:body response))
              (canvas/draw-nodes ctx @nodes  @node-radius)
              ))
        ;(update-nodes-and-draw nodes)
        (js/setInterval
          (fn []
            (canvas/draw-image ctx map-img {:x 0 :y 0 :w 1910 :h 859})
            (update-nodes-and-draw nodes))

          3000)
        ))


(set! (.-ondblclick canvas-dom)
      (fn [event]
        (let [x (.-layerX event)
              y (.-layerY event)]
          (print "KEK KEK KEK")
          (print x y))))

(set! (.-onclick canvas-dom)
      (fn [event]
        (let [x (.-layerX event)
              y (.-layerY event)]
          (let [node (first (filter #(canvas/collision? {:x x      :y y      :r 5}
                                                 {:x (:x %) :y (:y %) :r 5})
                         @nodes))]
            (when node
              (print node)
              (reset! current-node node)
              )
            )
          (print x y))))
