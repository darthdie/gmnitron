(ns gmnitron.commands.universes
  (:require [gmnitron.common :as common]
            [clj-discord.core :as discord]
            [clojure.string :as str]
            [clj-fuzzy.metrics :as fuzzy]))

(def inverse-list [
  { :names ["Luminary" "Baron Blade"] :description "Princess Ivana Ramonat of Mordengrad, inventor and 'diplomancer.' Her versions of Baron Blade's doomsday devices are nonlethal and can be targeted more precisely."}
  { :names ["Peacemaker" "Spite"] :description "This universe's version of Spite. An experimental medical treatment gave him superhuman strength and empathic abilities but left him dependent on breathing ammonia gas." }
  { :names ["Express" "Fright Train" "Stephen Graves" "Stephanie Graves"] :description "Stephanie Graves, this universe's Fright Train. Wears a Momentum Dynamo Exo-Chassis, which allows her to convert momemtum into a shield and discharge it as kinetic energy. Still loves train puns." }
  { :names ["Flashbulb" "Cameron Lilya" "Ermine"] :description "Cameron Lilya, this universe's Ermine, extremely vain and foppish. Can emit blinding light from his hands. Carries a cane with a crystal mounted to the top, which he can focus light through to create specific effects. Loves photography puns." }
  { :names ["Blank" "Equity"] :description "This universe's version of Equity. Can assume a blank white form that can pass through solid objects, and is invisible to anyone he makes eye contact with." }
  { :names ["Legacy" "Legacy of Destruction" "Paul Parsons"] :description "An evil Legacy, an anarchist/nihilist who fights to hold back progress using Legacy's standard power set." }
  { :names ["Absolute Zero" "Black Frost" "Ryan Frost"] :description "An evil Absolute Zero." }
  { :names ["Rampart" "Bunker" "Tyler Vance" "Captain Tyler Vance"] :description "An evil Bunker." }
  { :names ["Terminal Velocity" "Tachyon" "Dr. Meredith Stinson" "Meredith Stinson"] :description "An evil Tachyon." }
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

(defn create-flat-list 
  ([source] (create-flat-list source []))
  ([source target]
    (if (empty? source)
    target
    (let [item (first source)]
      (recur (rest source) (concat target (map #(hash-map :name % :description (:description item)) (:names item))))))))

(defn first-match [source target]
  (let [fuzzy-list (map #(hash-map :data % :dice (fuzzy/dice (:name %) target)) source)
        best-match (apply max-key :dice fuzzy-list)]
    (if (= (:dice best-match) 0.0)
      nil
      best-match)))

(defn inverse-command [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (let [match (first-match (create-flat-list inverse-list) (str/join arguments))]
        (if match
          (str "\r\n" (:description (:data match)))
          "No character found with that name."))))

(def command-list [
  { :command "!inverse" :handler inverse-command :min-args 1 :usage "!inverse or !inverse 'character name'" :description "Hands off the scene to the next actor" }
])