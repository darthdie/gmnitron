(ns gmnitron.fun-test
  (:require [clojure.test :refer :all]
            [gmnitron.commands.fun :refer :all]))

(deftest censor-word-test
  (testing "Censor word function"
    (is (= "bob" (censor-word "bob")))
    (is (= "shmob" (censor-word "Bob")))
    (is (= "disney" (censor-word "disney")))
    (is (= "shmisney" (censor-word "Disney")))
    (is (= "shmuperman" (censor-word "Superman")))))

(deftest censor-test
  (testing "censor function"
    (is (= "my name is shminigio shmontoya" (censor "my name is Inigio Montoya")))
    (is (= "shmatman was created in 1939 by shmob shmane" (censor "Batman was created in 1939 by Bob Kane")))
    (is (= "shmapple was founded by shmeve shmobs and shmeve shmozniak" (censor "Apple was founded by Steve Jobs and Steve Wozniak")))
    (is (= "I am shmriar" (censor "I am Briar")))))
