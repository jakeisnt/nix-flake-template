(ns clojure-art.tiled-lines
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

;; inspo: https://generativeartistry.com/tutorials/tiled-lines/

(def size 1000)
(def step 80)
(defn fifty-fifty [] (> (.random js/Math) 0.5))

(defn fp-while [pred fun val]
  (if (pred val) val (fp-while pred fun (fun val))))

(defn get-steps [start end step] (reverse
                                  (fp-while
                    ;; stop when size is less than the first of the list
                                   (fn [ls] (< end (first ls)))
                    ;; add the step size to the first of the list then cons result on
                                   (fn [ls] (cons (+ (first ls) step) ls))
                    ;; start with 0
                                   (list start))))

(defn draw-line [x y step dir]
  (if dir
    (q/line x y (+ x step) (+ y step))
    (q/line (+ x step) y x (+ y step))))

(defn cross-product-steps [lx ly]
  (reduce
   (fn [rst x]
     (concat (map (fn [y] {:x x :y y :dir (fifty-fifty)}) ly) rst))
   '()
   lx))

(defn get-first-n [ls n]
  (concat (:ls (reduce (fn [acc cur] (if (> (:count acc) n)
                                       acc
                                       {:ls (cons cur (:ls acc))
                                        :count (+ 1 (:count acc))}))
                       {:ls `() :count 0}
                       ls))))

(defn draw-state [state]
  (q/stroke-weight 2)
  (doseq [line (get-first-n (:ls state) (:num-lines state))]
    (draw-line (:x line) (:y line) step (:dir line))))

(def lines (cross-product-steps
            (get-steps 0 size step)
            (get-steps 0 size step)))
(def lines-len (count lines))

(defn setup []
  (q/frame-rate 500)
  {:num-lines 0 :ls lines})

(defn update-state [state]
  {:dec (cond
          (= (:num-lines state) 0) nil
          (= (:num-lines state) lines-len) "yes"
          :else (:dec state))
   :num-lines ((if (:dec state) - +) 1 (:num-lines state))
   :ls (:ls state)})

(defn ^:export run-sketch []
  (q/defsketch clojure-art
    :host "first-sketch"
    :size [size size]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(run-sketch)
