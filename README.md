# spartan.test

A spartan test framework compatible with [babashka](https://github.com/borkdude/babashka).
Requires babashka version 0.0.53 or newer.

## Usage:

Usage in a `deps.edn` project:

``` clojure
{:deps {spartan.test {:git/url "https://github.com/borkdude/spartan.test" :sha "610b4b9664caacb4402caff838d8724c73e21d4d"}}
 :aliases {:test-namespaces {:main-opts ["-m" "spartan.test" "-n" "project.test"]}
           :test-vars {:main-opts ["-m" "spartan.test" "-v" "project.test/bar-test"]}}}
```

``` clojure
(ns project.core)

(defn foo []
  1)

(ns project.test
  (:require
   [project.core :as project]
   [spartan.test :as test :refer [deftest is]]))

(deftest foo-test
  (is (= 1 (project/foo)))
  (is (= 2 (project/foo))))

(deftest bar-test
  (is (= 1 (project/foo))))
```

``` shell
$ deps.clj -A:test-vars -Scommand "bb -cp {{classpath}} {{main-opts}}"
FAIL in project.test/foo-test. Expected (= 2 (project/foo)) but got false.
Ran 3 tests in total, 1 tests failed, 2 succeeded.

$ deps.clj -A:test-vars -Scommand "bb -cp {{classpath}} {{main-opts}}"
Ran 1 tests in total, 1 succeeded.
```
