(ns gmnitron.commands.universes
  (:require [gmnitron.common :as common]
            [clj-discord.core :as discord]
            [clojure.string :as str]
            [clojure.core.matrix :as matrix]))

(def inverse-list [
  { :names ["Luminary" "Baron Blade"] :description "Princess Ivana Ramonat of Mordengrad, inventor and 'diplomancer.' Her versions of Baron Blade's doomsday devices are nonlethal and can be targeted more precisely."}
  { :names ["Peacemaker" "Spite"] :description "This universe's version of Spite. An experimental medical treatment gave him superhuman strength and empathic abilities but left him dependent on breathing ammonia gas." }
  { :names ["Express" "Fright Train" "Stephen Graves" "Stephanie Graves"] :description "Stephanie Graves, this universe's Fright Train. Wears a Momentum Dynamo Exo-Chassis, which allows her to convert momemtum into a shield and discharge it as kinetic energy. Still loves train puns." }
  { :names ["Flashbulb" "Cameron Lilya" "Ermine"] :description "Cameron Lilya, this universe's Ermine, extremely vain and foppish. Can emit blinding light from his hands. Carries a cane with a crystal mounted to the top, which he can focus light through to create specific effects. Loves photography puns." }
  { :names ["Blank" "Equity"] :description "This universe's version of Equity. Can assume a blank white form that can pass through solid objects, and is invisible to anyone he makes eye contact with." }
  { :names ["Legacy" "Legacy of Destruction" "Paul Parsons"] :description "An evil Legacy, an anarchist/nihilist who fights to hold back progress using Legacy's standard power set." }
  { :names ["Absolute Zero" "Black Frost" "Ryan Frost"] :description "An evil Absolute Zero." }
  { :names ["Rampart" "Bunker" "Tyler Vance" "Captain Tyler Vance"] :description "An evil Bunker." }
  { :names ["Terminal Velocity" "Tachyon" "Dr. Meredith Stinson"] :description "An evil Tachyon." }
  { :names ["White Wraith" "Wraith" "Maia Montgomery"] :description "This universe's version of Maia Montgomery. A mummy-like figure who animates her wrappings to attack. Her abilities are fueled by her rage." }
  { :names ["Singularity" "Unity" "Devra Thalia Caspit" "Devra Caspist"] :description "Not actually a member of the Frightful Five, but their minion, this universe's version of Unity. Has the ability to mentally co-opt control of computerized systems if she can reach their central core." }
  { :names ["The Omnitron Defense System" "Omnitron"] :description "A set of force fields, automated turrets, etc. invented by Luminary to defend cities from Legacy and other villains. It isn't sentient like the core universe's Omnitron, but it is controlled by a central core in Mordengrad." }
  { :names ["Mayor Pike" "Chairman" "The Chairman" "Graham Pike"] :description "This universe's version of The Chairman. Mayor of Overbrook City (this universe's Rook City)." }
  { :names ["Chief DeLeon" "The Operative" "Operative" "Sophia Anna Isabel DeLeon" "Sophia DeLeon"] :description "This universe's version of The Operative. Chief of the Overbrook City Police Department." }
  { :names ["FILTER" "F.I.L.T.E.R"] :description "The First International Laboratory for Testing Experimental Rehabilitation: A humane prison system in Antarctica with a special wing called The Block designed for super-powered criminals." }
  { :names ["John Rhodes" "Scholar" "The Scholar"] :description "We never learn if he's still called The Scholar, but he sucks out people's life force to maintain his immortality and power up his Philosopher's Stone." }
  { :names ["Zosimos Alchemista" "Biomancer"] :description "We never learn if he's still called Biomancer, but he travels the world with a (replaceable) homunculus companion, saving people and collecting knowledge." }
  { :names ["Negatron" "Omnitron-X"] :description "An evil Omnitron-X created by Singularity out of the main control core of the Omnitron Defense System in Mordengrad." }
  { :names ["Gloomweaver"] :description "In this universe, Gloomweaver seeks to extract people's gloom and weave it together to contain it away from people." }
  { :names ["Action Hero Stuntman" "Stuntmant" "Ansel Moreau" "Ansel G. Moreau"] :description "In this universe, Ansel Moreau never became a villain. He's an actor but also a real-life action hero." }
  { :names ["Seraph" "Apostate"] :description "This universe's version of Apostate. A human possessed by a spirit of Order." }
  { :names ["Hellion" "Fanatic"] :description "This universe's version of Fanatic. A spirit of Chaos who enters the world to counter Seraph." }
  { :names ["Jansa Vi Dero"] :description "Still collects the last surviving members of a race to preserve in the Enclave of the Endlings, but actively accelerates the extermination of races in decline, forcing them to fight against each other until only one remains." }
  { :names ["Parse" "Kim Howell"] :description "In this universe, Parse almost never gets her hands dirty. She stays at her computer and manipulates things behind the scenes." }
  { :names ["Heartbreaker" "Tony Taurus"] :description "Tony Taurus was a career criminal who turned his life around when the gladiators of the Bloodsword Colosseum encourage him to put on a knife-throwing performance for the crowd." }
  { :names ["The Tomb of Anubis"] :description "It's hard to find, but there are no traps to keep intruders out. Instead, it's full of temptations to keep visitors in. If Anubis finds a visitor, he'll offer them treasure or power, but if they accept, they'll end up dead." }
  { :names ["Miss Deeds" "Miss Information" "Aminia Twain"] :description "This universe's version of Miss Information. Offers to support Legacy of Destruction, but betrays him and tricks him into creating the Regression Serum, and allowing Luminary to (seemingly) defeat him for good." }
])

(def ahora-list [
  { :names ["Lucky Strike" "Setback" "Pete Riske"] :description "This universe's version of Setback. He's a disciplined martial artist in addition to his usual strength and bad-luck powers." }
  { :names ["Kismet" "Lady Luck"] :description "This universe's version of Kismet. She's basically the same as her core universe counterpart except that she has a happier backstory and became a hero instead." }
  { :names ["Rat Beast" "Plague Rat" "Eduardo López"] :description "In this universe, Eduardo López was exposed to the mix of chemicals responsible for the creation of Plague Rat in the core universe, and turns into a very similar creature, but with normal human intelligence." }
  { :names ["Shockwave" "Tempest" "M’kk Dall’ton"] :description "This universe's version of Tempest. He's a bounty hunter who hangs out with Dark Watch because Earth is a backwater planet where the law will never think to look for him." }
  { :names ["Bloogo"] :description "Looks like the core universe's Bloogo but is a vicious monster that comes to Earth by unexplained means." }
  { :names ["The Wraith"] :description "An undead witch from the Coven of Gloom. No relation to the core universe's Wraith." }
  { :names ["Spite"] :description "There are lots of Spites in this universe, giant creatures of Discordian magic that resemble the core universe's Spite in his Agent of Gloom aspect." }
  { :names ["Officer Kel-Voss" "Kel-Voss" "Voss" "Grand Warlord Voss"] :description "This universe's version of Grand Warlord Voss, a space cop in search of Shockwave." }
  { :names ["Amy Knight" "Expatriette" "Night Terror"] :description "This universe's version of Expatriette. Lucky Strike's ex-girlfriend, who shoots short-lived guns made of shadowy energy. Ostensibly the leader of the anarchist Citizens of the Night, but it turns out she takes orders from...." }
  { :names ["The Chairman" "Citizen Dawn" "Dawn Cohen"] :description "This universe's version of Citizen Dawn. Nothing more is known about her than that." }
])

(def animal-list [
  { :names ["The Naturalist" "The Caturalist"] :description "Has many animal forms including an ugly naked ape thing (a human, but there are no humans in this reality, so no one knows what it is)." }
  { :names ["The Hippo"] :description "Is just a hippo. He's confused about why everyone calls him 'The Hippo' when there are lots of hippos." }
  { :names ["Akash'Flora" "Impalakash'Flora"] :description "Is just a tree with impala-shaped leaves. (The other Impalakash' evolutions are the same as the Akash' ones, just impala-shaped.)" }
  { :names ["Omnitron-Rex" "Omnitron-X"] :description "Is an anthropomorphic robotic tyrannosaurus." }
  { :names ["Plague Man" "Plague Rat" "Eduardo López"] :description "A rat that turns part human. He has a rat body and a human face, with a patchy beard and male-pattern baldness, who bites people (with difficulty) to spread his man-plague." }
  { :names ["Stargent Seal" "Sergeant Steel"] :description "Was the slip of Christopher's tongue that started this whole mess. In the animal-verse, 'stargent' means 'militaristic.'" }
])

(def arataki-list [
  { :names ["The Argent Artist" "Argent Adept" "The Argent Adept" "Anthony Drake"] :description "A magical painter and leader of the Primal Wardens. This reality's Argent Adept." }
  { :names ["Haka" "Arataki"] :description "Haka." }
  { :names ["Sekhmet" "Ra"] :description "Daughter of Ra." }
  { :names ["The Discordian" "Portal Fiend"] :description "An anthropomorphic Portal Fiend from the Realm of Discord." }
  { :names ["Anchor" "Fanatic" "Helena"] :description "A woman who is able to embody the spirits of The Host. This reality's Fanatic." }
  { :names ["Citizen Storm" "Tempest" "M’kk Dall’ton"] :description "This reality's Tempest, a villain, who forms the equivalent of the core reality's Citizens of the Sun." }
  { :names ["Plague Beast" "Plague Rat" "Eduardo López"] :description "Nothing is known except that they are a villian." }
  { :names ["Ultratron" "Omnitron"] :description "Nothing is known except that they are a villian." }
])

(def iron-legacy-list [
  { :names ["Tachyon" "Dr. Meredith Stinson"] :description "The team leader." }
  { :names ["Absolute Zero" "Ryan Frost"] :description "Absolute Zero." }
  { :names ["The Wraith" "Maia Montgomery"] :description "Killed The Chairman and took over The Organization." }
  { :names ["Bunker" "Steven Graves" "Fright Train"] :description "Is actually Steven Graves, the former Fright Train." }
  { :names ["Unity" "Devra Thalia Caspit"] :description "Is actually a robot imbued with the original Unity's powers and personality by Biomancer." }
  { :names ["Tempest" "M’kk Dall’ton"] :description "Tempest." }
  { :names ["Parse" "Kim Howell"] :description "Gets her powers from an OblivAeon shard and is temporarily the head of Revo-Corp after Iron Legacy kills Revenant." }
  { :names ["Haka"] :description "Can't be killed, so he's initially imprisoned but breaks out and stages a riot. Then Iron Legacy encases him in concrete and sinks him in the ocean." }
  { :names ["The Iron Hand" "Ambuscade" "Ansel G. Moreau"] :description "Formerly Ambuscade, one of Iron Legacy's top lieutenants and leader of his Iron Guard." }
  { :names ["Citizen Dawn" "Dawn Cohen"] :description "Moves the Citizens of the Sun to Madagascar and sets up a giant bubble of energy to keep Iron Legacy out." }
  { :names ["Aminia Twain" "Miss Information" "Miss Chief"] :description "This universe's Aminia Twain. She never becomes Miss Information, but uses her superior organizational skills to try to bring down Iron Legacy from the inside." }
])

(def meat-list [
  { :names ["Apostate" "Aposteak"] :description "Aposteak" }
  { :names ["Omnitron" "Gyromnitron"] :description "Gyromnitron" }
  { :names ["Cueball" "Barbecueball"] :description "Barbecueball" }
  { :names ["Legacy" "Paul Parsons" "Leg-a-Lamb"] :description "Leg-a-Lamb" }
  { :names ["Setback" "Pete Riske" "Babyback"] :description "Babyback" }
  { :names ["Unity" "Devra Thalia Caspit" "Uni-T-Bone"] :description "Uni-T-Bone" }
  { :names ["Absolute Zero" "Absolutefisk Zero"] :description "Absolutefisk Zero" }
  { :names ["The Argent Adept" "Argent Adept" "The Asada Adept"] :description "The Asada Adept" }
  { :names ["Tempest" "Tempastrami" "M’kk Dall’ton"] :description "Tempastrami" }
  { :names ["Haka" "Hacarne"] :description "Hacarne" }
  { :names ["Fanatic" "Fanatikka" "Helena"] :description "Fanatikka" }
  { :names ["Captain Cosmic" "Captain Cosmixiote" "Hugh Lowsley"] :description "Captain Cosmixiote" }
  { :names ["Grand Warlord Vausage" "Kel-Voss" "Voss" "Grand Warlord Voss"] :description "Grand Warlord Vausage" }
])

(def quando-list [
  { :names ["The Ashen Heir" "Argent Adept" "The Argent Adept" "Anthony Drake"] :description "This universe's version of the Argent Adept. Angela Drake finds a panpipe belonging to her ancestor, Abigail Gray, whose spirit is trapped in the pipe. Abigail can temporarily fuse with Angela, granting her an appearance similar to that of Nightmist, and together they are the Ashen Heir in that form." }
  { :names ["Unity" "Devra Thalia Caspit"] :description "No relation to the core universe's Unity. A robot created by this universe's version of Dr. Meredith Stinson, using her knowledge and personality on top of Omnitron technology." }
  { :names ["Apex" "Bugbear"] :description "Although his power set is similar to that of The Naturalist, this is actually Moris Dugal, who in the core universe becomes Bugbear. A big-game hunter who accidentally kills another shapechanger and gains his powers through blood-to-blood contact. He has to focus to maintain a human form, and otherwise transforms into a green apex predator of some sort." }
  { :names ["Plaything" "The Dreamer" "Visionary" "Vanessa Long"] :description "This universe's version of The Dreamer. The big Dreamer event (Nightmare World) happened in this universe as well, but young Vanessa Long became a hero thereafter, using her projections intentionally." }
  { :names ["Infinitor" "Nigel Lowrey"] :description "This universe's version of Infinitor was a hero from the beginning, but instead of creating monsters, uses his powers to augment his physical abilities, or grant himself simple powers like energy blasts. After being defeated by Empyreon, his essence coalesces into a crystal, which Plaything embeds into the forehead of her giant ape projection. This grants the projection solidity and permanence, with Infinitor's voice, psyche, and powers." }
  { :names ["The Steel Sentinel" "Sergeant Steel"] :description "This universe's version of Sergeant Steel. A hero who pilots a mech suit, similar to but smaller than that of Bunker." }
  { :names ["Empyreon"] :description "Basically the same as his core universe incarnation." }
])

(def xtreme-list [
  { :names ["Ambuscade" "Ansel G. Moreau"] :description "A bounty hunter working for Revo-Corp drugs." }
  { :names ["Setback" "Pete Riske"] :description "A boy, seemingly without powers, of particular interest to Revo-Corp." }
  { :names ["The MistFeathers" "Nightmist" "The Harpy"] :description "This universe's version of Nightmist and The Harpy. They are twins, except that one has black hair with a white streak and the other has white hair with a black streak. They can turn partially or wholly into mist and summon clouds of feathers made of mist." }
  { :names ["The Fixer" "Mr. Fixer" "H. R. Walker" "Slim Walker"] :description "This universe's version of Mr. Fixer. He is a blind, twisted old man who runs an auto shop, but takes his payment in qi instead of money." }
  { :names ["The Expatriettes" "Expatriette"] :description "A gang of cloned bikers. They ride grip-lock bikes, which have laser spikes coming out of the tires that allow them to drive up the sides of buildings." }
  { :names ["Legacy" "Paul Parsons"] :description "Mostly the same as his core-universe version, just grimmer. Drives a Plymouth Road Runner with an American flag painted on it (minus a few stars)." }
  { :names ["Wraith" "Maia Montgomery"] :description "An agile knife fighter wrapped in bandages." }
  { :names ["Tachyon" "Dr. Meredith Stinson"] :description "Rides an unnaturally fast motorcycle and attacks with lightning." }
  { :names ["Absolute Zero" "Ryan Frost"] :description "Has a modular ice cannon that has to be connected to a water supply." }
  { :names ["Bunker" "Tyler Vance" "Vernon Carter"] :description "Actually two characters. Vernon Carter drives a trailer truck that conceals a massive turret cannon, manned by Tyler Vance." }
  { :names ["Zhu Long"] :description "A 'normal' Chinese dragon that turns into a bigger, mecha dragon with lasers and missiles and stuff." }
  { :names ["Biomancer" "Fleshmonger"] :description "This universe's Biomancer. He creates bio-mutants by hybridizing humans with the cryptids originally seen in The Final Wasteland." }
  { :names ["Xtremetron" "Omnitron"] :description "This universe's Omnitron." }
  { :names ["The Scholar" "The Edgeucator"] :description "This universe's Scholar." }
])

(defn create-flat-list 
  ([source] (create-flat-list source []))
  ([source target]
    (if (empty? source)
    target
    (let [item (first source)]
      (recur (rest source) (concat target (map #(hash-map :name % :description (:description item)) (:names item))))))))

(defn da-lev [str1 str2]
  (let [l1 (count str1)
        l2 (count str2)
        mx (matrix/new-matrix :ndarray (inc l1) (inc l2))]
   (matrix/mset! mx 0 0 0)
   (dotimes [i l1]
     (matrix/mset! mx (inc i) 0 (inc i)))
   (dotimes [j l2]
     (matrix/mset! mx 0 (inc j) (inc j)))
   (dotimes [i l1]
     (dotimes [j l2]
       (let [i+ (inc i) j+ (inc j)
             i- (dec i) j- (dec j)
             cost (if (= (.charAt str1 i)
                         (.charAt str2 j))
                    0 1)]
         (matrix/mset! mx i+ j+
                (min (inc (matrix/mget mx i j+))
                     (inc (matrix/mget mx i+ j))
                     (+ (matrix/mget mx i j) cost)))
         (if (and (pos? i) (pos? j)
                  (= (.charAt str1 i)
                     (.charAt str2 j-))
                  (= (.charAt str1 i-)
                     (.charAt str2 j)))
           (matrix/mset! mx i+ j+
                  (min (matrix/mget mx i+ j+)
                       (+ (matrix/mget mx i- j-) cost)))))))
   (matrix/mget mx l1 l2)))

(defn first-match [source target]
  (let [fuzzy-list (map #(hash-map :data % :dice (da-lev (:name %) target)) source)
        best-match (apply min-key :dice fuzzy-list)]
    (if (> (:dice best-match) 5)
      nil
      best-match)))

(defn first-match-or-else [source-list target]
  (let [match (first-match (create-flat-list source-list) target)]
    (if match
      (str "\r\n" (:description (:data match)))
      "No character found with that name.")))

(defn inverse-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else inverse-list (str/join arguments))))

(defn ahora-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else ahora-list (str/join arguments))))

(defn animal-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else animal-list (str/join arguments))))

(defn arataki-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else arataki-list (str/join arguments))))

(defn iron-legacy-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else iron-legacy-list (str/join arguments))))

(defn meat-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else meat-list (str/join arguments))))

(defn quando-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else quando-list (str/join arguments))))

(defn xtreme-command [data]
  (let [{arguments :arguments} data]
    (first-match-or-else xtreme-list (str/join arguments))))

(def command-list [
  { :command "!inverse" :handler inverse-command :min-args 1 :usage "!inverse 'character name'" :description "Gives information about the character from the inverse universe" }
  { :command ["!ahora" "!ahora!"] :handler ahora-command :min-args 1 :usage "!ahora | !ahora! 'character name" :description "Gives information about the character from the ¡Ahora! universe" }
  { :command ["!animal" "!animal-verse" "!animalverse"] :handler animal-command :min-args 1 :usage "!animal | !animal-verse | !animalverse 'character name" :description "Gives information about the character from the Animal-Verse" }
  { :command "!arataki" :handler arataki-command :min-args 1 :usage "!arataki 'character name'" :description "Gives information about the character from Arataiki's universe" }
  { :command ["!iron-legacy" "!ironlegacy"] :handler iron-legacy-command :min-args 1 :usage "!iron-legacy | !ironlegacy 'character name'" :description "Gives information about the character from The Iron Legacy universe" }
  { :command ["!meat" "!meat-verse" "!meatverse"] :handler meat-command :min-args 1 :usage "!meat | !meat-verse | !meatverse 'character name'" :description "Gives information about the character from Meat-Verse universe" }
  { :command ["!quando" "!quando?"] :handler quando-command :min-args 1 :usage "!quando | !quando? 'character name'" :description "Gives information about the character from the ¿Quando? universe" }
  { :command ["!xtreme" "!xtremeverse"] :handler xtreme-command :min-args 1 :usage "!xtreme | !xtremeverse 'character name'" :description "Gives information about the character from the Xtremeverse" }
])