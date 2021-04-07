(ns clojure-art.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def size 1000)
(def step 10)

(defn fifty-fifty [] (> (.random js/Math) 0.5))

(defn get-point-variance [j size]
  (let [dist-to-center (.abs js/Math (/ (- j size) 2))
        variance (.max js/Math (- (/ size 2) 50 dist-to-center) 0)]
    (* -1 (.random js/Math) (/ variance 2))))

(defn get-lines [step size]
  (map (fn [i]
         (map
          (fn [j] {:x j :y (+ i (get-point-variance j size))})
          (range 0 size step)))
       (range 0 size step)))

(defn setup [] (get-lines step size))
(defn update-state [state] state)
(defn draw-state [state]
  (q/stroke-weight 4)
  (let [last-point (atom nil)]
    (doseq [line state]
      (doseq [point line]
        (if @last-point
          (let
           [x (:x @last-point)
            y (:y @last-point)]
            (reset! last-point point)
            (q/line x y (:x point) (:y point)))
          (reset! last-point point)))
      (reset! last-point nil))))

(defn ^:export run-sketch []
  (q/defsketch clojure-art
    :host "first-sketch"
    :size [size size]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(run-sketch)
