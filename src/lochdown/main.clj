(ns lochdown.main
  (:gen-class)
  (:require
   [clojure.core.async :as a :refer [chan go go-loop <! >! <!! >!!  take! put! offer! poll! alt! alts! close! onto-chan!
                                     pub sub unsub mult tap untap mix admix unmix pipe
                                     timeout to-chan  sliding-buffer dropping-buffer
                                     pipeline pipeline-async]]
   [clojure.string]
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [cljfx.api]

   [expanse.fs.runtime.core :as fs.runtime.core]
   [expanse.fs.protocols :as fs.protocols])
  (:import
   (javafx.event Event EventHandler)
   (javafx.stage WindowEvent)
   (javafx.scene.control DialogEvent Dialog ButtonType ButtonBar$ButtonData)
   #_javafx.application.Platform
   (com.sun.jna Library Native Platform #_Function NativeLibrary)))

(println "clojure.compiler.direct-linking" (System/getProperty "clojure.compiler.direct-linking"))
(clojure.spec.alpha/check-asserts true)
(do (set! *warn-on-reflection* true) (set! *unchecked-math* true))

(s/def ::searchS string?)

(defn stage
  [{:as opts
    :keys [::searchS]}]
  {:fx/type :stage
   :showing true
   #_:on-close-request #_(fn [^WindowEvent event]
                           (println :on-close-request)
                           #_(.consume event))
   :width 1024
   :height 768
   :scene {:fx/type :scene
           :root {:fx/type :h-box
                  :children [{:fx/type :label :text "rename"}
                             {:fx/type :text-field
                              :text searchS}]}}})
(defonce stateA (atom nil))

(defn -main [& args]
  (println ::-main)
  (let [data-dir (fs.runtime.core/path-join (System/getProperty "user.dir"))
        renderer (cljfx.api/create-renderer)]
    (reset! stateA {:fx/type stage
                    ::searchS ""
                    ::renderer renderer})
    (add-watch stateA :watch-fn (fn [k stateA old-state new-state] (renderer new-state)))

    (javafx.application.Platform/setImplicitExit true)
    (renderer @stateA)
    #_(cljfx.api/mount-renderer stateA render)

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

  (def renderer (::renderer @stateA))

  (renderer @stateA)

  (swap! stateA assoc ::searchS "123")

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