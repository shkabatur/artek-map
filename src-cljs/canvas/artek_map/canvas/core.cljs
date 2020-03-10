(ns artek-map.canvas.core)




(defn create-image
  "create new image"
  [img-path]
  (let [img (js/Image.)]
    (set! (.-src img) img-path)
    img))


(def green-img (create-image "img/green.png"))
(def red-img (create-image "img/red.png"))

(defn create-2d-context
  "return new 2d context"
  [canvas-dom]
  (let [ctx (.getContext canvas-dom "2d")]
    ctx))

(defn draw-image
  "Draws the image onto the canvas at the given position.
   If a map of params is given, the number of entries is used to
   determine the underlying call to make."
  ([ctx img x y]
   (. ctx (drawImage img x y))
   ctx)
  ([ctx img {:keys [x y w h
                    sx sy sw sh dx dy dw dh] :as params}]
   (condp = (count params)
     2 (. ctx (drawImage img x y))
     4 (. ctx (drawImage img x y w h))
     8 (. ctx (drawImage img sx sy sw sh dx dy dw dh)))
   ctx))


;;**************************************************
;; Pathing functions
;;**************************************************

(defn distance [origin target]
  (let [dx (- (:x target) (:x origin))
        dy (- (:y target) (:y origin))
        dir-x (if (= 0 dx)
                dx
                (/ dx (Math/abs dx)))
        dir-y (if (= 0 dy)
                dy
                (/ dy (Math/abs dy)))
        dist (Math/sqrt (+ (Math/pow dx 2) (Math/pow dy 2)))]
    {:delta {:x dx :y dy}
     :dir {:x dir-x :y dir-y}
     :dist dist}))

;;**************************************************
;; Bounding functions
;;**************************************************

(defn bottom-right [{:keys [x y w h r]}]
  ;;Account for circles whose x and y represent the center
  ;;instead of the top left
  (if r
    {:x (+ x r)
     :y (+ y r)}
    {:x (+ x w)
     :y (+ y h)}))

(defn top-left [{:keys [x y r]}]
  ;;Account for circles whose x and y represent the center
  ;;instead of the top left
  (if r
    {:x (- x r)
     :y (- y r)}
    {:x x
     :y y}))

(defn in-radius? [origin obj radius]
  (let [{:keys [dist]} (distance origin obj)]
    (< dist radius)))

(defn collision? [obj obj2]
  (let [br (bottom-right obj)
        tl (top-left obj)
        br2 (bottom-right obj2)
        tl2 (top-left obj2)]
    (and
      ;;If the tops are higher than the bottoms
      (and (< (:y tl) (:y br2))
           (< (:y tl2) (:y br)))
      ;;And the lefts are "lefter" than the rights
      (and (< (:x tl) (:x br2))
           (< (:x tl2) (:x br))))))

(defn contained? [container obj]
  (let [cbr (bottom-right container)
        ctl (top-left container)
        br (bottom-right obj)
        tl (top-left obj)]
    (and
      (and (< (:x ctl) (:x tl))
           (< (:y ctl) (:y tl)))
      (and (> (:x cbr) (:x br))
           (> (:y cbr) (:y br))))))

(defn in-bounds? [obj x2 y2]
  (let [br (bottom-right obj)
        tl (top-left obj)]
    (and (< (:x tl) x2 (:x br))
         (< (:y tl) y2 (:y br)))))
;-------------------------------------------
(defn text
  "Paints the given text at a starting point at (x, y), using the
   current fill style."
  [ctx {:keys [text x y]}]
  (. ctx (fillText text x y))
  ctx)

(defn font-style
  "Sets the font. Default value 10px sans-serif."
  [ctx font]
  (set! (.-font ctx) font)
  ctx)

(defn fill-style
  "Color or style to use inside shapes. Default #000 (black)."
  [ctx color]
  (set! (.-fillStyle ctx) (name color))
  ctx)

(defn stroke-style
  "Color or style to use for the lines around shapes. Default #000 (black)."
  [ctx color]
  (set! (.-strokeStyle ctx) (name color))
  ctx)

(defn stroke-width
  "Sets the line width. Default 1.0"
  [ctx w]
  (set! (.-lineWidth ctx) w)
  ctx)

;-----------------------------------------------------

(defn draw-nodes
  [ctx nodes r]
  (doseq [{:keys [x y name result]} nodes]
    (if result
      (do
        (fill-style ctx "#53DE0D")
        (text ctx {:text name :x x :y y})
        (draw-image ctx green-img {:x x :y y :w r :h r}))
      (do
        (fill-style ctx "red")
        (text ctx {:text name :x x :y y})
        (draw-image ctx red-img {:x x :y y :w r :h r})))))

