(ns lochdown.main
  (:gen-class)
  (:require
   [clojure.core.async :as a :refer [chan go go-loop <! >! <!! >!!  take! put! offer! poll! alt! alts! close! onto-chan!
                                     pub sub unsub mult tap untap mix admix unmix pipe
                                     timeout to-chan  sliding-buffer dropping-buffer
                                     pipeline pipeline-async]]
   [clojure.string]
   [clojure.java.io :as io])
  (:import
   (com.sun.jna Library Native Platform #_Function NativeLibrary)))

(println "clojure.compiler.direct-linking" (System/getProperty "clojure.compiler.direct-linking"))
(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(defonce stateA (atom nil))

(defn -main [& args]
  (println ::-main)
  (let [data-dir (-> (io/file (System/getProperty "user.dir")) (.getCanonicalPath))]
    (reset! stateA {})
    (add-watch stateA :watch-fn (fn [k stateA old-state new-state] new-state))

    (go)))


#_(definterface ICLibrary
  (^void printf [^String format ^"[Ljava.lang.Object;" args]))

#_(def clibrary-proxy (proxy [Library ICLibrary] []))

#_(def clibrary-proxy-class (class clibrary-proxy))

(gen-interface
 :name lochdown.main.ICLibrary
 :extends [com.sun.jna.Library]
 :methods
 [[printf [String int #_"[Ljava.lang.Object;"] void]])

(comment

  (require
   '[lochdown.main]
   '[expanse.fs.runtime.core :as fs.runtime.core]
   :reload)

  (-main)

  (swap! stateA assoc ::random (rand-int 1000))

  (.isInterface lochdown.main.ICLibrary)

  (def clibrary (Native/load "c"
                             lochdown.main.ICLibrary))

  (.printf clibrary "text %d" (int 1))

  (.invoke
   (com.sun.jna.Function/getFunction "c" "printf")
   Integer
   (to-array ["text %d" (int 2)]))

  (def clibrary (NativeLibrary/getInstance "c"))

  (def cprintf (.getFunction clibrary "printf"))

  (.invoke cprintf Integer (to-array ["text %d" 3]))

  (def csqrt (.getFunction clibrary "sqrt"))

  (.invoke csqrt Integer (to-array [(int 4)]))

  ;
  )