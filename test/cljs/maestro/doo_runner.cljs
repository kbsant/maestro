(ns maestro.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [maestro.core-test]))

(doo-tests 'maestro.core-test)

