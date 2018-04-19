(ns gmnitron.fun-test
  (:require [clojure.test :refer :all]
            [gmnitron.commands.fun :refer :all]))

(deftest censor-word-test
  (testing "Censor word function"
    (is (= "bob" (censor-word "bob")))
    (is (= "smhob" (censor-word "Bob")))
    (is (= "disney" (censor-word "disney")))
    (is (= "smhisney" (censor-word "smhisney")))
    (is (= "smhuperman" (censor-word "Superman")))))

(deftest censor-test
  (testing "censor function"
    (is (= "my name is smhnigio smhontoya" (censor "my name is Inigio Montoya")))
    (is (= "smhatman was created in 1939 by smhob smhane" (censor "Batman was created in 1939 by Bob Kane")))
    (is (= "smhapple was founded by smheve smhobs and smheve smhozniak" (censor "Apple was founded by Steve Jobs and Steve Wozniak")))
    (is (= "I am smhriar" (censor "I am Briar")))))
