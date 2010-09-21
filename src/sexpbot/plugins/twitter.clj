; Written by Michael D. Ivey <ivey@gweezlebur.com>
; Licensed under the EPL

; Usage:
; * Go to http://dev.twitter.com
; * Login as the Twitter account associated with your bot
; * Register a new application
; * Get the consumer key and consumer secret
; * Click My Access Token
; * Get access token and access token secret
; * Add a section to info.clj under the server like this:
;    :twitter {:consumer-key "-----"
;              :consumer-secret "-----"
;              :access-token "-----"
;              :access-token-secret "-----"}
; * Use the 'tweet' command to post tweets from your bot (admin only)

(ns sexpbot.plugins.twitter
  (:use sexpbot.respond
        twitter
        [oauth.client :as oauth]))

(defn tweet [message bot irc]
  (let [server (:server @irc)
        config (:twitter ((:config @bot) server))
        consumer-key (:consumer-key config)
        consumer-secret (:consumer-secret config)
        access-token (:access-token config)
        access-token-secret (:access-token-secret config)
        consumer (oauth/make-consumer consumer-key consumer-secret
                                      "https://api.twitter.com/oauth/request_token"
                                      "https://api.twitter.com/oauth/access_token"
                                      "https://api.twitter.com/oauth/authorize"
                                      :hmac-sha1)]
    (twitter/with-oauth consumer access-token access-token-secret
      (twitter/update-status message))))

(defplugin
  (:cmd
   "Sends a tweet. ADMIN ONLY."
   #{"tweet" "twit"}
   (fn [{:keys [irc bot channel args nick] :as irc-map}]
     (if-admin nick irc-map bot
               (do
                 (tweet (->> args (interpose " ") (apply str)) bot irc)
                 (send-message irc bot channel "Tweet tweet!"))))))