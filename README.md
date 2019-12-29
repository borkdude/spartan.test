# spartan.test

A spartan test framework compatible with
[babashka](https://github.com/borkdude/babashka) (>= 0.0.53) and Clojure.

## Rationale

Currently babashka doesn't have an implementation of `clojure.test`. This
library can be used meanwhile.

## Usage:

Usage in a `deps.edn` project:

``` clojure
{:deps {spartan.test {:git/url "https://github.com/borkdude/spartan.test" :sha "55d8ee0afabbd307da43ded8a9f17fffcaec9b19"}}
 :aliases {:test-namespaces {:main-opts ["-m" "spartan.test" "-n" "project.test"]}
           :test-vars {:main-opts ["-m" "spartan.test" "-v" "project.test/bar-test"]}}}
```

``` clojure
$ cat src/project/core.clj
(ns project.core)

(defn foo []
  1)

$ cat src/project/test.clj
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

Run with [deps.clj](https://github.com/borkdude/deps.clj/):

``` shell
$ deps.clj -A:test-namespaces -Scommand "bb -cp {{classpath}} {{main-opts}}"
FAIL in project.test/foo-test. Expected (= 2 (project/foo)) but got false.
Ran 3 tests in total, 1 tests failed, 2 succeeded.

$ deps.clj -A:test-vars -Scommand "bb -cp {{classpath}} {{main-opts}}"
Ran 1 tests in total, 1 succeeded.
```

## License

Copyright © 2019 Michiel Borkent

Distributed under the EPL License. See LICENSE.
