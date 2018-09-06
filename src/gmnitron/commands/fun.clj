(ns gmnitron.commands.fun
  (:require [gmnitron.common :as common]
            [clj-discord.core :as discord]
            [clojure.string :as str]
            [java-time :as j]))

(defn prepare-censor-word [word]
  (cond
    (and (= (first word) \S) (= (second word) \t)) (subs word 2)
    (common/in? ["A" "E" "I" "O" "U"] (common/first-char word)) (str/lower-case word)
    :else (subs word 1)))

(defn censor-word [word]
  (if (and (Character/isUpperCase (first word)) (not (common/in? ["i" "by"] (str/lower-case word))))
    (str "shm" (prepare-censor-word word))
    word))

(defn censor
  ([message]
    (censor "" (str/split message #" ")))
  ([message parts]
    (if (empty? parts)
      (str/join "" (drop-last message))
      (recur (str message (censor-word (first parts)) " ") (rest parts)))))

(defn censor-command [data]
  (let [{arguments :arguments channel-id :channel-id message-id :message-id} data
        message (str/join " " arguments)]
    (censor message)))

(defn died-command [data]
  "https://media.giphy.com/media/TJ8Pd0jQzyHmYoWZYU/giphy.gif")

(defn plan-command [data]
  "http://i64.tinypic.com/1214g9w.jpg")

(defn evil-command [data]
  "http://i63.tinypic.com/5dti6o.gif")

(defn cult-command [data]
  "http://i68.tinypic.com/aw2vet.jpg")

(defn gloat-command [data]
  (rand-nth [
    "\"Before this day is ended, the entire planet shall grovel helplessly at my feet!\" - Baron Blade, Moonfall #5"
    "\"The agony and shame of your defeat shall break you!\" - Baron Blade, Freedom Five #458"
    "\"Can you feel that, Mr. Parsons? That feeling is the inevitability of your death!\" - Baron Blade, Moonfall #4"
    "\"What's wrong, hero? Speak up! Tell me in righteous tones how you hope to defeat Baron Blade!\" - Baron Blade, Vengeance #5"
    "\"Indeed... perish you shall!\" - Apostate, Absolution #19"
    "\"You're in serious trouble, little hero. I have your scent. You'll be mine soon enough.\" - Bugbear, Mystery Comics #467"
    "\"Your reckoning is at hand! Embrace eternity!\" - La Capitan, The Tides of Time #5"
    "\"¡Tonta! I shall have my revenge!\" - La Capitan, Freedom Five #651"
    "\"I built this city; challenge me here and you will find out I am not just a creator...\" - The Chairman, Rook City Renegades #4"
    "\"You cannot hide. You tread on ground that shrieks with your passage. Come to me.\" - Chokepoint, Freedom Five #773"
    "\"As autumn leaves must dying fade, your futile acts shall be unmade.\" - Citizen Autumn, Sunrise #8"
    "\"You may have returned just in time to witness your demise!\" - Isis, Ra: Horus of Two Horizons"
    "\"You have failed, and once again the Ennead grow in strength as you diminish.\" - Osiris, Baptism by Fire #1"
    "\"Ain't nothin' that can stop me!\" - The Hippo, Exordium #1"
    "\"I'm handing out express tickets to the junkyard!\" - Fright Train, Freedom Five Annual #27"
    "\"All aboard! The Fright Train's a comin'!\" - Fright Train, Vengeful Five One-Shot"
    "\"Your tricks will not save you. Our might is undeniable!\" - Major Flay, Vengeance #5"
    "\"muahahahha\" - Somebody...Probably, At Some Point"
    "\"Let the graves of the damned by opened! My servants grow ever stronger!\" - Gloomweaver, Nightmist #42"
    "\"You will burn, and you will beg for true despair!\" - The Acolyte, Freedom Five Annual #6"
    "\"Mmmmuuuuhhhhh-grahhhhgg!!!\" - Zombie, Nightmist #42"
    "\"You shall witness the destruction of your wretched little planet!\" - Grand Warlord Voss, Freedom Five #512"
    "\"Those who stand in my way will not stand at all.\" - Rahazar, Cosmic Concurrence #14"
    "\"Enough! Your flimsy shell cannot protect you from justice!\" - Iron Legacy, The Final Legacy #3"
    "\"There is no room for mercy. Your punishment shall be swift.\" - Iron Legacy, Freedom Five Annual #25"
    "\"What now, hero?! What has your legacy wrought?!\" - Baron Blade, The Fall of Legacy One-Shot"
    "\"Your tricks are useless. Nothing can prevent justice!\" - Iron Legacy, Freedom Five Annual #25"
    "\"All things move. All things fail. You have no hope of escape.\" - S'sdari the Bloody, The Hero in the Arena #4"
    "\"Dance, my puppets! Your delusions of free will are hilarious.\" - Kismet, Mystery Comics #290"
    "\"Thus, I am the Matriarch! From the center to the sea, I rule as queen of the fowl!\" - The Matriarch, A Murder: Most Fowl"
    "\"That these fowl shall salt the Earth itself, leaving all groaning for burial!\" - The Matriarch, Freedom Five #691"
    "\"The ravens themselves are hoarse, croaking thy fatal exit!\" - The Matriarch, Night's Plutonian Shore #2"
    "\"Foolish heroes. So easily misled. Now that they're out of my way...\" - Miss Information, Adminstrative Assassin #5"
    "\"Much better. First Megalopolis; then, the world.\" - Miss Information, Reality Defaced #6"
    "\"All truths shall be revealed by the omnipotent mind of Judge Mental!\" - Judge Mental, Freedom Five #703"
    "\"You stand too close to danger. You can't defeat me. Can you even save yourself?\" - The Operative, Mystery Comics #490"
    "\"When all lay facedown in the muck, none will be greater than any other!\" - Professor Pollution, Reality Defaced #4"
    "\"...\" - Progeny, Every Issue He's In?"
    "\"Just as I am the people, the people are me. You cannot prevail.\" - Proletariat, Vengeance #5"
    "\"This walking tank shall fall before the might of the people!\" - Proletariat, Freedom Five Annual #27"
    "\"Energy? Power? These are things you crave? You shall have both in full!\" - The Radioactivist, Exordium #1"
    "\"I have found my place here on this wretched planet. All must suffer.\" - Vyktor, Exordium #3"
    "\"Where to go, where to go... Are you sure about the right turn you took a few turns back?\" - Wager Master, Freedom Five #175"
  ]))

(defn insult-command [data]
  (rand-nth [
    "\"To such simple minds, my advanced technology is indistinguishable from magic.\" - Baron Blade, Freedom Five #107"
    "\"I am hardly amused by your worthless attempts at competence.\" - Baron Blade, Freedom Five #124"
    "\"Huh. Some god.\" - Ambuscade, The Deadliest Game #1"
    "\"Pfft, amateurs. Do they really call you heroes?\" - Ambuscade, Freedom Five Annual #11"
    "\"You were rejected by your own people. There's no place for you in these times, either.\" - Desert Eagle, The Savage Haka #63"
    "\"You see? He is even more of a living person than you are! Which is not saying much.\" - Biomancer, Tome of the Bizarrer #39"
    "\"These were precious to you? I think I shall keep them forever.\" - La Capitan, Freedom Five #651"
    "\"Stay out of this, imbecile. Your very existence is laughable.\" - Citizen Dawn, Lucky Shot #14"
    "\"Hah! Not fast enough! Sucker!\" - Friction, Freedom Five Annual #27"
    "\"Cute Toys. Playtime's over.\" - Choke, Vengeance #4"
    "\"kek\" - Briar, Right Now"
    "\"Blinded by the vanity of power...\" - Gloomweaver, Nightmist #42"
    "\"Imposter! Your power is a mere echo of the true power of the cosmos.\" - Galactra, Prime Wardens #21"
    "\"Brute Force is all you savages understand!\" - Iron Legacy, The Final Legacy #4"
    "\"Whoops! How clumsy of you.\" - Kismet, Prime Wardens #11"
    "\"The poor dears. They never learn, do they? Same old tricks, same old fools.\" - Miss Information, Reality Defaced #1"
    "\"A key weakness of flesh is its susceptibility to a wide range of chemical triggers.\" - Omnitron, Freedom Five #307"
    "\"Such flawed beings with your obvious weaknesses...\" - Omnitron, Singularity #3"
    "\"Isn't he precious? I named him after you guys! Go get them, Misguided Fool!\" - Wager Master, Freedom Five #177"
  ]))

(defn date-command [data]
  (let [year-of-board (j/with-zone (j/zoned-date-time 1940 1) "UTC")
        now (j/with-zone (j/zoned-date-time) "UTC")]
    (str "It is currently " (j/format "cccc, MMMM d, yyyy " now) "in the year of our boar'd " (j/time-between year-of-board now :years) ".")))

(def command-list [
  { :command "!censor" :handler censor-command :min-args 1 :usage "!censor (message)" :description "'Censors' a message in true Letters Page fashion." }
  { :command "!died" :handler died-command :usage "!died" :description "And then they died." }
  { :command "!plan" :handler plan-command :usage "!plan" :description "All according to plan." }
  { :command ["!gloat" "!monologue"] :handler gloat-command :usage "!gloat OR !monologue" :description "Gloat your inevitable victory!" }
  { :command "!insult" :handler insult-command :usage "!insult" :description "Dumby." }
  { :command ["!evil" "!maniacal"] :handler evil-command :usage "!evil OR !maniacal" :description "maniacal laugh...maniacal laugh..."}
  { :command ["!cult" "!gloom"] :handler cult-command :usage "!cult OR !gloom" :description "The gloomy one will have his day." }
  { :command "!date" :handler date-command :usage "!date" :description "Tells you the UTC date." }
])