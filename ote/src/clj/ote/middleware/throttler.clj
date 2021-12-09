(ns ote.components.middleware.throttler
  (:import (java.time Instant)))

(defn- hit!
  "Tracks hits in session based on timestamps and expiration."
  [session {root-key :session-key threshold-ms :threshold}]
  (update-in
    session
    [root-key :hits]
    (fn [old]
      (let [now (-> (Instant/now) (.toEpochMilli))]
        (conj
          (vec (drop-while #(> (- now threshold-ms) %) (or old [])))
          now)))))

(defn- block
  "Returns a future which will block exponentially based on hits in current session following formula `(2^hits) - 2`:

   - 1 hit  => 0 seconds
   - 2 hits => 2 seconds
   - 3 hits => 6 seconds
   - 4 hits => 14 seconds

  and so on."
  [session config]
  (future
    (let [delay (- (Math/pow 2 (count (get-in session [(:session-key config) :hits]))) 2)]
      (Thread/sleep (* delay 1000)))))

(defn- throttle*
  [session response config]
  (if (nil? (get #{200} (:status response)))
    ; failure adds throttling metadata and delays response
    (let [throttled-session (hit! session config)]
      @(block throttled-session config)
      (assoc response :session throttled-session))
    ; success clears throttling metadata
    (let [cleared-session (assoc-in session [(:session-key config) :hits] [])]
      (assoc response :session cleared-session))))

(defn throttle
  ([handler]
   (throttle handler {}))
  ([handler opts]
   (let [{:keys   [session-key             threshold]
          :or     {session-key  :throttler threshold   60000}} opts
         config   {:session-key session-key
                   :threshold   threshold}]
     (fn
       ([request]
        (throttle*
          (:session request)
          (handler request)
          config))
       ([request respond raise]
        (handler
          request
          (fn [response]
            (respond
              (throttle*
                (:session request)
                response
                config)))))))))
