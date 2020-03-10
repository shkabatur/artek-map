(ns artek-map.ping)

(defn timed-ping
  "Time an .isReachable ping to a given domain"
  [domain timeout]
  (let [addr (java.net.InetAddress/getByName domain)
        start (. System (nanoTime))
        result (.isReachable addr timeout)
        total (/ (double (- (. System (nanoTime)) start)) 1000000.0)]
    {:ping (if result total 9999)
     :result result}))

(defn ping
  [ip]
  (timed-ping ip 2000))
