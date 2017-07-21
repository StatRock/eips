Two different ways of removing .html from the end of a string.

```clojure
(defn- advancement-name [file-name]
  (let [string (second (string/split (normalize-path file-name) #"/"))
        length (.length string)] 
      (.substring string 0 (- length 5)))))
  
(defn- advancement-name [file-name]
  (-> filename
    (normalize-path)
    (string/split #"/")
    (second)
    (string/replace #".html?$" "")))
```