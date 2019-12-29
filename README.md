# spartan.test

[![CircleCI](https://circleci.com/gh/borkdude/spartan.test/tree/master.svg?style=shield)](https://circleci.com/gh/borkdude/spartan.test/tree/master)
[![Clojars Project](https://img.shields.io/clojars/v/borkdude/spartan.test.svg)](https://clojars.org/borkdude/spartan.test)
[![project chat](https://img.shields.io/badge/slack-join_chat-brightgreen.svg)](https://app.slack.com/client/T03RZGPFR/CLX41ASCS)

A spartan test framework compatible with
[babashka](https://github.com/borkdude/babashka) (>= 0.0.53) and Clojure.

## Rationale

Currently babashka doesn't have an implementation of `clojure.test`. This
library can be used meanwhile.

## Usage:

Usage in a `deps.edn` project:

``` clojure
{:deps {borkdude/spartan.test {:mvn/version "0.0.4"}}
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
FAIL in project.test/foo-test
expected: (= 2 (project/foo))
actual: (not (= 2 1))

Ran 2 tests containing 3 assertions.
1 failures, 0 errors.

$ deps.clj -A:test-vars -Scommand "bb -cp {{classpath}} {{main-opts}}"
Ran 1 tests containing 1 assertions.
0 failures, 0 errors.
```

## Tests

Install [babashka](https://github.com/borkdude/babashka) and [deps.clj](https://github.com/borkdude/deps.clj/).
Then run `script/test`.

## License

Copyright © 2019 Michiel Borkent

Distributed under the EPL License. See LICENSE.
