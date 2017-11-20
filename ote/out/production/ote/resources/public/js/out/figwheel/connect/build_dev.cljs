(ns figwheel.connect.build-dev (:require [ote.main] [figwheel.client] [figwheel.client.utils]))
(figwheel.client/start {:on-jsload (fn [& x] (if js/ote.main.reload-hook (apply js/ote.main.reload-hook x) (figwheel.client.utils/log :debug "Figwheel: :on-jsload hook 'ote.main/reload-hook' is missing"))), :build-id "dev", :websocket-url "ws://localhost:3449/figwheel-ws"})

