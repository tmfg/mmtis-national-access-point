(ns ote.util.throttle
  "Utilities for intentionally slowing down to prevent abuse")

(defmacro with-throttle-ms
  "Execute body and make it last at least `throttle-ms` milliseconds.
  If execution of body is faster than that, sleep."
  [throttle-ms & body]
  `(let [until# (+ (System/currentTimeMillis) ~throttle-ms)]
     (try
       ~@body
       (finally
         (let [sleep-ms# (- until# (System/currentTimeMillis))]
           (when (pos? sleep-ms#)
             (Thread/sleep sleep-ms#)))))))
