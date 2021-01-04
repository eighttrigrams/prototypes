(ns melmac.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [ui.data-test]))

(doo-tests 'ui.data-test)