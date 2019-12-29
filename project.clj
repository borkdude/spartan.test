(defproject borkdude/spartan.test "0.0.1"
  :description "A spartan test framework compatible with babashka."
  :url "https://github.com/borkdude/spartan.test"
  :scm {:name "git"
        :url "https://github.com/borkdude/babashka"}
  :license {:name "Eclipse Public License 1.0"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass
                                    :sign-releases false}]])
