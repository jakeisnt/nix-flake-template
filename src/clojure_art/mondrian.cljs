(ns clojure-art.mondrian
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

;; https://generativeartistry.com/tutorials/piet-mondrian/
;; I think I got it mostly right...

(def size (/ (.-innerWidth js/window) 2))
(def step (/ size 20))

(def colors '("#D40920" "#1356A2" "#F7D842"))

(defn split-on-x [square split-at]
  (list {:x (:x square)
         :y (:y square)
         :width (- (:width square) (- (:width square) (+ split-at (:x square))))
         :height (:height square)}
        {:x split-at
         :y (:y square)
         :width (- (:width square) (+ split-at (:x square)))
         :height (:height square)}))

(defn split-on-y [square split-at]
  (list {:x (:x square)
         :y (:y square)
         :width (:width square)
         :height (- (:width square) (- (:width square) (+ split-at (:y square))))}
        {:x (:x square)
         :y split-at
         :width (:width square)
         :height (- (:height square) (+ split-at (:y square)))}))

(defn fifty-fifty [] (> (.random js/Math) 0.5))

(defn split-squares-with [coordinates squares]
  (let [x (:x coordinates)
        y (:y coordinates)
        res (reduce (fn [acc square]
                      (let [splitx (if (and x (fifty-fifty) (< x (+ (:x square) (:width square)))) (split-on-x square x) nil)
                            splity (if (and y (fifty-fifty) (< y (+ (:y square) (:height square)))) (split-on-y square y) nil)]
                        (concat
                         splitx
                         splity
                         (if (or splitx splity) nil (list square))
                         acc)))
                    '() squares)]
    (println res)
    res))

(def initial-squares (list {:x 0 :y 0 :width size :height size}))
(defn setup []
  (q/frame-rate 1)
  ;; initial state
  {:step 0
   :squares initial-squares})

(defn random-in [range]
  (.floor js/Math (* (.random js/Math) range)))

(defn randomly-color [squares]
  (let [color-locs (dorun (map (fn [] (random-in (count squares))) colors))
        colors-left (atom colors)
        take-color! (fn [left]
                      (let [color (first @left)]
                        (reset! @left (rest @left))
                        color))]
    ;; if i is one of the color locs,
    ;; add the corresponding color to the square
    (dorun (map-indexed (fn [square i] (if (contains? color-locs i)
                                         {:color (take-color! colors-left)
                                          :x (:x square)
                                          :y (:y square)
                                          :width (:width square)
                                          :height (:height square)}
                                         square)
                          squares)))))

(defn draw-state [state]
  ;; set stroke weight of 8
  (q/stroke-weight 8)
  ;; Color the background white
  (q/background 255)
  ;; draw all of the calculated rectangles
  (doseq [rc (concat initial-squares (:squares state))]
    (q/fill (if (:color rc) (:color rc) "#F2F5F1"))
    (q/rect (:x rc) (:y rc) (:width rc) (:height rc))))

(defn update-state [state]
  (let
   [step (+ step (:step state))]
    {:step (if (= (count (:squares state)) 0) 0 step)
     :squares (cond
                ;; once we've filled the grid, we start decreasing the squares
                (>= step size) (rest (:squares state))
                ;; but when we have no squares left, we reset to the initial square
                (= (count (:squares state)) 0) initial-squares
                ;; otherwise, split!
                :else
                (split-squares-with
                 {:y step}
                 (split-squares-with
                  {:x step}
                  (:squares state))))}))

(defn ^:export run-sketch []
  (q/defsketch clojure-art
    :host "first-sketch"
    :size [size size]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

                                        ; uncomment this line to reset the sketch:
(run-sketch)
