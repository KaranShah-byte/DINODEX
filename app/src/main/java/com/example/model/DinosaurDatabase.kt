package com.example.model

import com.example.graphics3d.Vector3D

object DinosaurDatabase {

    val dinosaurs: List<Dinosaur> = listOf(
        Dinosaur(
            id = "trex",
            name = "Tyrannosaurus Rex",
            pronunciation = "/tɪˌrænəˈsɔːrəs ˈrɛks/",
            meaning = "Tyrant Lizard King",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (68 – 66 Ma)",
            diet = DinoDiet.CARNIVORE,
            lengthMeters = 12.3f,
            heightMeters = 3.9f,
            weightTons = 8.4f,
            topSpeedKmh = 27,
            discoveryCountry = "United States (Hell Creek)",
            discoveredYear = 1902,
            discoveredBy = "Barnum Brown",
            description = "The ultimate apex predator of the Cretaceous period. T-Rex possessed the strongest recorded bite force of any terrestrial animal in Earth history—up to 12,800 pounds of pressure per square inch, capable of crushing solid bone.",
            anatomicalFeatures = listOf(
                "Serrated 30 cm banana-shaped teeth anchored in deep jaw sockets",
                "Binocular forward vision superior to modern eagles and hawks",
                "Hyper-sensitive olfactory bulb delivering elite sense of smell",
                "Short 2-clawed forelimbs that could still curl over 400 lbs each"
            ),
            funFacts = listOf(
                "T-Rex teeth had serrations that acted like steak knives, puncturing and sawing flesh.",
                "Sue, found in South Dakota, is one of the most complete T-Rex skeletons, with 90% bone recovery.",
                "Its brain was double the size of most other giant predatory dinosaurs."
            ),
            modelType = "TREX",
            hotspots = listOf(
                Hotspot3D("jaw", "Bone-Crushing Jaws", "Generates 12,800 lbs of bite force, capable of shattering adult Triceratops bones.", Vector3D(0f, 0.45f, 2.1f)),
                Hotspot3D("teeth", "Serrated Teeth", "Up to 30 cm long including roots, constantly shed and regrown throughout life.", Vector3D(0f, 0.55f, 2.2f)),
                Hotspot3D("tail", "Counterbalance Tail", "Heavy muscular tail worked as a precise pivot for steering during lunges.", Vector3D(0f, 0.1f, -2.2f)),
                Hotspot3D("arms", "Vestigial Arms", "Small but heavily muscled, possibly used to hold prey or aid in standing up.", Vector3D(0.45f, 0.1f, 0.8f))
            )
        ),
        Dinosaur(
            id = "triceratops",
            name = "Triceratops",
            pronunciation = "/traɪˈsɛrətɒps/",
            meaning = "Three-Horned Face",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (68 – 66 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 9.0f,
            heightMeters = 3.0f,
            weightTons = 10.0f,
            topSpeedKmh = 32,
            discoveryCountry = "United States (Wyoming & Montana)",
            discoveredYear = 1887,
            discoveredBy = "John Bell Hatcher",
            description = "One of the most famous ceratopsians, Triceratops lived alongside T-Rex. Its skull was one of the largest and most robust of any land animal, measuring up to 2.5 meters in length.",
            anatomicalFeatures = listOf(
                "Solid bone neck frill protecting vital arteries in the neck",
                "Two 1-meter brow horns made of bone covered in keratin sheaths",
                "Powerful narrow shearing beak designed to slice tough fibrous palms and cycads",
                "Dental battery of up to 800 teeth constantly replaced as they wore down"
            ),
            funFacts = listOf(
                "Fossilized Triceratops frills have been found bearing healed T-Rex bite marks, proving intense battles.",
                "Its skull alone accounted for nearly one-third of the animal's entire length.",
                "Triceratops moved with an upright columnar front-limb stance, unlike modern sprawling lizards."
            ),
            modelType = "TRICERATOPS",
            hotspots = listOf(
                Hotspot3D("horns", "Brow Horns", "Over 1 meter long, used for defense against T-Rex and wrestling for dominance.", Vector3D(0.35f, 1.2f, 1.9f)),
                Hotspot3D("frill", "Solid Bone Frill", "Shielded the neck and jugular vein; may have flushed with blood to intimidate rivals.", Vector3D(0f, 0.9f, 0.85f)),
                Hotspot3D("beak", "Narrow Keratin Beak", "Sharp shears that could easily slice through hard prehistoric woody plants.", Vector3D(0f, -0.1f, 2.0f))
            )
        ),
        Dinosaur(
            id = "stegosaurus",
            name = "Stegosaurus",
            pronunciation = "/ˌstɛɡəˈsɔːrəs/",
            meaning = "Roof Lizard",
            era = DinoEra.JURASSIC,
            periodDetail = "Late Jurassic (155 – 150 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 9.0f,
            heightMeters = 4.0f,
            weightTons = 5.3f,
            topSpeedKmh = 18,
            discoveryCountry = "United States (Morrison Formation)",
            discoveredYear = 1877,
            discoveredBy = "Othniel Charles Marsh",
            description = "With its distinctive double row of upright kiteshaped plates and tail spikes, Stegosaurus is an icon of the Morrison Formation. Its tiny walnut-sized brain was once thought to require a second brain near the hips.",
            anatomicalFeatures = listOf(
                "Up to 17 alternating dermal plates along its vertebral column",
                "Four 60-90 cm pointed spikes at the tip of the tail (thagomizer)",
                "Extremely low head carriage adapted for ground foraging on ferns and mosses",
                "Small horny beak and small peg-like leaf-shaped teeth"
            ),
            funFacts = listOf(
                "The tail spikes are officially named the 'thagomizer' after a 1982 Far Side comic by Gary Larson.",
                "Plates were not attached to the skeleton directly, but embedded in the skin (osteoderms).",
                "Blood vessels inside the plates likely regulated body temperature or flashed colors."
            ),
            modelType = "STEGOSAURUS",
            hotspots = listOf(
                Hotspot3D("plates", "Dorsal Plates", "Highly vascularized bone plates for thermoregulation and visual intimidation.", Vector3D(0f, 1.1f, 0.2f)),
                Hotspot3D("thagomizer", "Thagomizer Spikes", "Four sharp tail spikes wielded like a heavy mace against predators like Allosaurus.", Vector3D(0f, 0.3f, -2.3f)),
                Hotspot3D("head", "Walnut Brain", "Its brain weighed only 80 grams, about the size of a modern dog's lime.", Vector3D(0f, -0.1f, 1.8f))
            )
        ),
        Dinosaur(
            id = "brachiosaurus",
            name = "Brachiosaurus",
            pronunciation = "/ˌbrækiəˈsɔːrəs/",
            meaning = "Arm Lizard",
            era = DinoEra.JURASSIC,
            periodDetail = "Late Jurassic (154 – 153 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 22.0f,
            heightMeters = 13.0f,
            weightTons = 46.0f,
            topSpeedKmh = 15,
            discoveryCountry = "United States (Grand River Valley)",
            discoveredYear = 1900,
            discoveredBy = "Elmer S. Riggs",
            description = "A colossal sauropod known for its towering giraffe-like neck and longer front legs (hence 'arm lizard'). Brachiosaurus grazed the highest canopies of Jurassic conifer and ginkgo forests unreachable to other herbivores.",
            anatomicalFeatures = listOf(
                "Longer front legs than rear legs creating a 45-degree upward slope",
                "Enormous nasal openings situated on top of the head crest",
                "Chisel-like spatulate teeth built to strip high foliage without chewing",
                "Massive heart estimated at over 400 kg to pump blood up its 9-meter neck"
            ),
            funFacts = listOf(
                "Brachiosaurus could swallow up to 400 kilograms of conifer needles and ferns every single day.",
                "Its high-positioned nostrils were once mistakenly thought to function as an underwater snorkel.",
                "Gastroliths (stomach stones) were swallowed to grind up coarse vegetation in its muscular gizzard."
            ),
            modelType = "BRACHIOSAURUS",
            hotspots = listOf(
                Hotspot3D("head_apex", "Crowned Skull", "Perched 13 meters in the air, allowing it to strip treetops like a Jurassic giraffe.", Vector3D(0f, 2.9f, 1.6f)),
                Hotspot3D("neck", "Giraffe-Like Neck", "Composed of 14 elongated cervical vertebrae with air sacs to reduce weight.", Vector3D(0f, 1.8f, 1.2f)),
                Hotspot3D("front_pillars", "Arm Pillars", "Unlike other sauropods, its front legs were longer than its hind legs.", Vector3D(0.7f, -1.0f, 0.5f))
            )
        ),
        Dinosaur(
            id = "spinosaurus",
            name = "Spinosaurus",
            pronunciation = "/ˌspaɪnəˈsɔːrəs/",
            meaning = "Spine Lizard",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (99 – 93.5 Ma)",
            diet = DinoDiet.PISCIVORE,
            lengthMeters = 15.0f,
            heightMeters = 5.4f,
            weightTons = 7.5f,
            topSpeedKmh = 24,
            discoveryCountry = "Egypt & Morocco (Kem Kem Beds)",
            discoveredYear = 1912,
            discoveredBy = "Ernst Stromer",
            description = "The largest known carnivorous dinosaur, longer than T-Rex. Spinosaurus was semi-aquatic with a crocodile-like skull, conical teeth for gripping slippery prehistoric fish, and a dramatic 1.8-meter tall dorsal neural sail.",
            anatomicalFeatures = listOf(
                "1.8-meter dorsal sail supported by elongated neural spines",
                "Elongated narrow jaws filled with straight conical interlocking teeth",
                "Dense paddle-like tail adapted for lateral swimming propulsion in river systems",
                "Neurovascular pressure sensors on snout tip to detect aquatic prey movements"
            ),
            funFacts = listOf(
                "It lived alongside giant prehistoric sawfish (Onchopristis) that reached 8 meters long.",
                "The original 1912 fossil specimen was destroyed in Munich during an Allied bombing raid in WWII.",
                "Recent fossil discoveries in Morocco proved its tail had a fin structure like a giant newt or crocodile."
            ),
            modelType = "SPINOSAURUS",
            hotspots = listOf(
                Hotspot3D("sail", "Dorsal Neural Sail", "Stood up to 1.8 meters tall, likely used for aquatic stability, heat exchange, or display.", Vector3D(0f, 1.2f, -0.2f)),
                Hotspot3D("snout", "Crocodilian Snout", "Packed with neurovascular pits to detect aquatic pressure vibrations from fish.", Vector3D(0f, 0.4f, 2.4f)),
                Hotspot3D("paddle", "Paddle Tail", "Deep, fin-like tail provided powerful lateral propulsion through prehistoric rivers.", Vector3D(0f, 0.2f, -2.1f))
            )
        ),
        Dinosaur(
            id = "velociraptor",
            name = "Velociraptor",
            pronunciation = "/vɪˈlɒsɪræptər/",
            meaning = "Swift Seizer",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (75 – 71 Ma)",
            diet = DinoDiet.CARNIVORE,
            lengthMeters = 2.0f,
            heightMeters = 0.5f,
            weightTons = 0.018f,
            topSpeedKmh = 40,
            discoveryCountry = "Mongolia (Gobi Desert)",
            discoveredYear = 1923,
            discoveredBy = "Peter Kaisen",
            description = "Unlike the scaly, oversized Hollywood movie raptors, the real Velociraptor was the size of a turkey, covered in bird-like plumage and quill knobs. It was a cunning, lightning-fast predator of the Mongolian dunes.",
            anatomicalFeatures = listOf(
                "Iconic 6.5 cm curved sickle-shaped claw on the second toe of each foot",
                "Quill knobs on forearm bones proving full plumage and wings",
                "Stiff rod-like bony tendons along tail to stabilize sharp high-speed turns",
                "Slender upturned skull with 26-28 widely spaced teeth on each side"
            ),
            funFacts = listOf(
                "A famous 'Fighting Dinosaurs' fossil shows a Velociraptor locked in mortal combat with a Protoceratops.",
                "Its feathers were not for flying, but for balance, brooding eggs, and high-speed cornering.",
                "Real Velociraptors weighed about 15-20 kg—roughly equivalent to a medium-sized dog."
            ),
            modelType = "VELOCIRAPTOR",
            hotspots = listOf(
                Hotspot3D("sickle_claw", "Killing Sickle Claw", "Retractable 6.5 cm sickle claw held off the ground to keep razor sharp.", Vector3D(0.45f, -0.8f, 0.3f)),
                Hotspot3D("feather_wings", "Wing Plumage", "Feathered arms used for aerodynamic stability during prey pinning.", Vector3D(0.4f, 0.1f, 0.4f)),
                Hotspot3D("stiff_tail", "Dynamic Gyro Tail", "Fused bony rods turned the tail into a rigid counterbalancing rudder.", Vector3D(0f, 0.1f, -1.8f))
            )
        ),
        Dinosaur(
            id = "ankylosaurus",
            name = "Ankylosaurus",
            pronunciation = "/ˌæŋkɪloʊˈsɔːrəs/",
            meaning = "Fused Lizard",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (68 – 66 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 8.0f,
            heightMeters = 1.7f,
            weightTons = 6.0f,
            topSpeedKmh = 12,
            discoveryCountry = "United States & Canada (Alberta)",
            discoveredYear = 1906,
            discoveredBy = "Barnum Brown",
            description = "A walking fortress. Ankylosaurus was covered in thick bony armor plates (osteoderms) from its eyelids to its tail club. Even apex predators like T-Rex could rarely penetrate its armored back.",
            anatomicalFeatures = listOf(
                "Fused dermal bone plates, nodules, and spikes covering its back and neck",
                "Heavy bony tail club consisting of seven fused vertebrae and massive osteoderms",
                "Wide barrel-shaped torso designed to digest enormous volumes of plant matter",
                "Triangular cranial horns projecting backward from skull corners"
            ),
            funFacts = listOf(
                "The tail club could impact with enough kinetic force to shatter a T-Rex's shin bone.",
                "Even its eyelids had tiny embedded armor plates for eye protection.",
                "Ankylosaurus had a very low center of gravity, crouching to ground level to protect its unarmored belly."
            ),
            modelType = "ANKYLOSAURUS",
            hotspots = listOf(
                Hotspot3D("club", "Tail Bone Club", "A massive 60 kg solid bone sledgehammer wielded with bone-shattering force.", Vector3D(0f, 0.1f, -2.1f)),
                Hotspot3D("armor", "Osteoderm Carapace", "Interlocking bone plates embedded into skin like medieval chainmail.", Vector3D(0.7f, 0.3f, 0f)),
                Hotspot3D("skull_horns", "Cranial Horns", "Pyramidal horns protected the temples and jaw joints from lateral bites.", Vector3D(0.5f, 0.3f, 1.0f))
            )
        ),
        Dinosaur(
            id = "pteranodon",
            name = "Pteranodon",
            pronunciation = "/tɪˈrænədɒn/",
            meaning = "Winged and Toothless",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (86 – 84.5 Ma)",
            diet = DinoDiet.PISCIVORE,
            lengthMeters = 5.6f, // wingspan up to 6m
            heightMeters = 1.8f,
            weightTons = 0.035f,
            topSpeedKmh = 80,
            discoveryCountry = "United States (Niobrara Formation, Kansas)",
            discoveredYear = 1870,
            discoveredBy = "Othniel Charles Marsh",
            description = "One of the largest flying reptiles (pterosaurs) of the Late Cretaceous. Soaring effortlessly over the Western Interior Seaway, Pteranodon caught fish with its elongated toothless beak and steered with its backward skull crest.",
            anatomicalFeatures = listOf(
                "Tremendous wingspan reaching up to 6 meters (20 feet)",
                "Extremely lightweight hollow bones filled with pneumatic air sacs",
                "Long backwards-pointing cranial crest acting as an aerodynamic rudder and display",
                "Toothless pelican-like jaw mechanism for scooping ocean fish on the wing"
            ),
            funFacts = listOf(
                "Though popular in dinosaur books, pterosaurs are technically flying reptiles, not true dinosaurs.",
                "Male Pteranodons had much larger crests than females, showing strong sexual dimorphism.",
                "They could ride thermal sea updrafts for hundreds of kilometers without flapping."
            ),
            modelType = "PTERANODON",
            hotspots = listOf(
                Hotspot3D("wings", "Membranous Wings", "Supported by an immensely elongated fourth finger, stretching over 2 meters each.", Vector3D(-1.5f, 0.2f, 0f)),
                Hotspot3D("crest", "Aerodynamic Crest", "Counterbalanced the long beak and acted as a rudder during high-speed sea glides.", Vector3D(0f, 0.6f, 0.3f)),
                Hotspot3D("beak", "Fish Scoop Beak", "Sharp toothless jaws with an expandable throat pouch for diving on fish.", Vector3D(0f, 0.25f, 1.5f))
            )
        ),
        Dinosaur(
            id = "parasaurolophus",
            name = "Parasaurolophus",
            pronunciation = "/ˌpærəˌsɔːrəˈloʊfəs/",
            meaning = "Near Crested Lizard",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (76.5 – 73 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 9.5f,
            heightMeters = 3.2f,
            weightTons = 2.7f,
            topSpeedKmh = 35,
            discoveryCountry = "Canada (Alberta, Dinosaur Park)",
            discoveredYear = 1920,
            discoveredBy = "William Parks",
            description = "Famous for its extraordinary backwards-sweeping cranial crest. The hollow crest contained an internal labyrinth of acoustic nasal tubes that acted as a natural trumpet, blasting low-frequency resonant sound over miles.",
            anatomicalFeatures = listOf(
                "Long curved tubular crest containing almost 2 meters of acoustic air passages",
                "Broad duck-billed snout filled with hundreds of grinding cheek teeth",
                "Facultative bipedalism: walked on four legs while grazing, ran on two legs to flee",
                "Tall neural spines on back supporting strong ligaments and possibly a display sail"
            ),
            funFacts = listOf(
                "Scientists reconstructed the sound of Parasaurolophus using CT scans—it sounds like a deep foggy trombone.",
                "The crest was also used as a visual visual billboard to identify herd members across vast plains.",
                "Its teeth were organized in batteries that ground against each other like modern millstones."
            ),
            modelType = "PARASAUROLOPHUS",
            hotspots = listOf(
                Hotspot3D("crest_horn", "Acoustic Resonator Crest", "Hollow tubular chamber connected to nasal passages to blast low trombone-like calls.", Vector3D(0f, 1.5f, 0.8f)),
                Hotspot3D("duckbill", "Broad Duck Bill", "Specialized beak for gathering pine needles, twigs, leaves, and marsh grasses.", Vector3D(0f, 0.8f, 2.0f)),
                Hotspot3D("dual_gait", "Flexible Locomotion", "Switched from quadrupedal browsing to fast bipedal sprints when alarmed.", Vector3D(0.55f, -0.6f, 0.1f))
            )
        ),
        Dinosaur(
            id = "allosaurus",
            name = "Allosaurus",
            pronunciation = "/ˌæləˈsɔːrəs/",
            meaning = "Different Lizard",
            era = DinoEra.JURASSIC,
            periodDetail = "Late Jurassic (155 – 145 Ma)",
            diet = DinoDiet.CARNIVORE,
            lengthMeters = 10.5f,
            heightMeters = 3.2f,
            weightTons = 2.3f,
            topSpeedKmh = 34,
            discoveryCountry = "United States (Cleveland-Lloyd Quarry)",
            discoveredYear = 1877,
            discoveredBy = "Othniel Charles Marsh",
            description = "The top apex predator of the Jurassic period. Allosaurus hunted massive sauropods and Stegosaurs. Its skull was lightly built, and it used its upper jaw like a surgical battleaxe to slash flesh from giant prey.",
            anatomicalFeatures = listOf(
                "Pair of prominent crests / horns directly above and in front of the eyes",
                "Large three-clawed forelimbs built for grappling and securing struggling prey",
                "Loosely hinged skull bones that allowed widening the throat to swallow massive chunks",
                "D-shaped serrated teeth designed for slicing muscle rather than crushing bone"
            ),
            funFacts = listOf(
                "Over 75% of dinosaur fossils found at Utah's Cleveland-Lloyd Dinosaur Quarry are Allosaurus.",
                "Fossilized Stegosaurus neck bones have been found with puncture wounds matching Allosaurus teeth.",
                "Its bite was relatively weak for its size, so it used its head like a slashing hatchet."
            ),
            modelType = "ALLOSAURUS",
            hotspots = listOf(
                Hotspot3D("eye_ridges", "Supraorbital Crests", "Bony horn-like ridges above eyes, possibly bright red or orange in life.", Vector3D(0.35f, 1.35f, 1.45f)),
                Hotspot3D("hatchet_jaw", "Hatchet Jaw", "Slashed open wide to drive the upper teeth down into prey like a battleaxe.", Vector3D(0f, 0.7f, 2.2f)),
                Hotspot3D("grapple_claws", "Grasping Arms", "Heavy arms with 20 cm curved claws to latch onto Stegosaurs and sauropods.", Vector3D(0.42f, -0.2f, 0.8f))
            )
        ),
        Dinosaur(
            id = "coelophysis",
            name = "Coelophysis",
            pronunciation = "/ˌsɛləˈfaɪsɪs/",
            meaning = "Hollow Form",
            era = DinoEra.TRIASSIC,
            periodDetail = "Late Triassic (216 – 196 Ma)",
            diet = DinoDiet.CARNIVORE,
            lengthMeters = 3.0f,
            heightMeters = 1.0f,
            weightTons = 0.025f,
            topSpeedKmh = 38,
            discoveryCountry = "United States (Ghost Ranch, New Mexico)",
            discoveredYear = 1881,
            discoveredBy = "Edward Drinker Cope",
            description = "One of the earliest true dinosaurs. Coelophysis was an agile, slender hunter that roamed the arid Triassic floodplains in massive cooperative packs, preying on early reptiles and insects.",
            anatomicalFeatures = listOf(
                "Extremely lightweight hollow bones filled with air spaces",
                "Slender S-curved flexible neck and forward-pointing binocular eyes",
                "Four-fingered grasping hands with sharp recurved claws",
                "Blade-like curved teeth with serrated leading and trailing edges"
            ),
            funFacts = listOf(
                "Thousands of Coelophysis skeletons were found buried together at Ghost Ranch, NM, trapped in a flash flood.",
                "Ghost Ranch Coelophysis fossils went to space aboard the Space Shuttle Endeavour in 1998.",
                "It was the state fossil of New Mexico due to the monumental scientific finds there."
            ),
            modelType = "VELOCIRAPTOR",
            hotspots = listOf(
                Hotspot3D("neck", "Flexible S-Neck", "Allowed rapid lightning strikes at darting Triassic lizards and insects.", Vector3D(0f, 0.5f, 0.8f)),
                Hotspot3D("tail", "Slender Rudder Tail", "Kept its lightweight body balanced at speeds exceeding 38 km/h.", Vector3D(0f, 0.1f, -1.8f))
            )
        ),
        Dinosaur(
            id = "plateosaurus",
            name = "Plateosaurus",
            pronunciation = "/ˌpleɪti.oʊˈsɔːrəs/",
            meaning = "Broad Lizard",
            era = DinoEra.TRIASSIC,
            periodDetail = "Late Triassic (214 – 204 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 8.0f,
            heightMeters = 3.5f,
            weightTons = 4.0f,
            topSpeedKmh = 20,
            discoveryCountry = "Germany & Switzerland",
            discoveredYear = 1834,
            discoveredBy = "Christian Erich Hermann von Meyer",
            description = "The first giant herbivore dinosaur in history. Plateosaurus was an early sauropodomorph that walked predominantly on two legs, using its long neck and grasping thumbs to pull down high Triassic cycad branches.",
            anatomicalFeatures = listOf(
                "Bipedal stance with massive muscular hind limbs",
                "Enlarged hand thumb armed with a powerful recurved defense claw",
                "Small skull with leaf-shaped serrated teeth suited for shearing plants",
                "Deep chest cavity housing primitive gastroliths for plant fermentation"
            ),
            funFacts = listOf(
                "Plateosaurus was the very first large dinosaur ever discovered in Europe.",
                "It did not chew food in its mouth; instead, it swallowed stones to grind plants in its gizzard.",
                "Bone histology proves it had variable growth rates depending on food availability, much like reptiles."
            ),
            modelType = "BRACHIOSAURUS",
            hotspots = listOf(
                Hotspot3D("thumb", "Thumb Defense Spike", "Large curved thumb claw used to grip branches and ward off predators.", Vector3D(0.5f, 0.1f, 0.5f)),
                Hotspot3D("reach", "High Browsing Stance", "Reared up to graze on conifer branches 4 meters off the ground.", Vector3D(0f, 1.8f, 1.0f))
            )
        ),
        Dinosaur(
            id = "oviraptor",
            name = "Oviraptor",
            pronunciation = "/ˌoʊvɪˈræptər/",
            meaning = "Egg Seizer / Egg Thief",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (75 – 71 Ma)",
            diet = DinoDiet.OMNIVORE,
            lengthMeters = 1.6f,
            heightMeters = 0.9f,
            weightTons = 0.035f,
            topSpeedKmh = 35,
            discoveryCountry = "Mongolia (Djadokhta Formation)",
            discoveredYear = 1923,
            discoveredBy = "Roy Chapman Andrews",
            description = "Falsely accused of stealing eggs! When first found atop a nest of eggs, scientists named it 'Egg Thief.' Decades later, embryos inside those eggs revealed Oviraptor was actually a devoted parent lovingly brooding its own clutch.",
            anatomicalFeatures = listOf(
                "Deep, toothless beak with powerful crushing palatal prongs",
                "Prominent high crest on the skull used for display and species recognition",
                "Feathered wings and tail fan used to insulate eggs in the nest",
                "Slender legs with sharp claws adapted for an opportunistic omnivorous diet"
            ),
            funFacts = listOf(
                "Its skull was toothless, functioning like a modern parrot's beak to crack nuts, seeds, and shellfish.",
                "Fossils preserved in brooding position show the parent died during sandstorms rather than abandon the nest.",
                "One of the best early proofs linking dinosaur behavior directly to modern bird nesting habits."
            ),
            modelType = "VELOCIRAPTOR",
            hotspots = listOf(
                Hotspot3D("crest", "Cranial Display Crest", "Casque-like crest similar to modern cassowaries.", Vector3D(0f, 0.8f, 1.2f)),
                Hotspot3D("beak", "Crushing Beak", "Toothless keratin beak that crushed hard seeds, mollusk shells, and plants.", Vector3D(0f, 0.5f, 1.8f))
            )
        ),
        Dinosaur(
            id = "gallimimus",
            name = "Gallimimus",
            pronunciation = "/ˌɡælɪˈmaɪməs/",
            meaning = "Rooster Mimic",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (70 Ma)",
            diet = DinoDiet.OMNIVORE,
            lengthMeters = 6.0f,
            heightMeters = 2.0f,
            weightTons = 0.45f,
            topSpeedKmh = 55,
            discoveryCountry = "Mongolia (Nemegt Formation)",
            discoveredYear = 1963,
            discoveredBy = "Zofia Kielan-Jaworowska",
            description = "The ostrich of the dinosaur world. Gallimimus was built for supreme speed, with long graceful legs, a toothless beak with keratinous filter strainers, and large eyes positioned for 360-degree situational awareness.",
            anatomicalFeatures = listOf(
                "Extremely long lower leg bones (tibiae) and feet engineered for sustained high speed",
                "Toothless keratinous beak with ridges resembling modern ducks for filter feeding",
                "Slender neck and lightweight skull giving minimal drag while running",
                "Short hands with parallel claws used for raking vegetation and catching small prey"
            ),
            funFacts = listOf(
                "Gallimimus could reach speeds of 55 km/h, easily outrunning contemporary predators like Tarbosaurus.",
                "A famous stampede scene in Jurassic Park ('They\\'re flocking this way!') made Gallimimus world-famous.",
                "Its beak may have functioned as a sieve to strain tiny water plants and insects from shallow rivers."
            ),
            modelType = "VELOCIRAPTOR",
            hotspots = listOf(
                Hotspot3D("stride", "Sprint Limbs", "Tibia was longer than femur, the classic anatomical hallmark of an elite sprinter.", Vector3D(0.4f, -0.6f, 0f)),
                Hotspot3D("beak_sieve", "Filter Beak", "Lamellae inside beak sifted pond water for invertebrates and seeds.", Vector3D(0f, 0.6f, 1.8f))
            )
        ),
        Dinosaur(
            id = "dilophosaurus",
            name = "Dilophosaurus",
            pronunciation = "/daɪˌlɒfəˈsɔːrəs/",
            meaning = "Two-Crested Lizard",
            era = DinoEra.JURASSIC,
            periodDetail = "Early Jurassic (193 Ma)",
            diet = DinoDiet.CARNIVORE,
            lengthMeters = 7.0f,
            heightMeters = 2.2f,
            weightTons = 0.4f,
            topSpeedKmh = 36,
            discoveryCountry = "United States (Kayenta Formation, Arizona)",
            discoveredYear = 1942,
            discoveredBy = "Jesse Williams",
            description = "One of the earliest large predatory dinosaurs of the Jurassic. Famous for a distinctive pair of thin, semicircular bony crests running along the top of its skull. Unlike the fictional movie version, it did not spit venom or have a neck frill, but was a formidable 7-meter predator.",
            anatomicalFeatures = listOf(
                "Pair of delicate semicircular crests made of thin bone along skull roof",
                "Subnarial gap or kink in the upper jaw behind the nostrils",
                "Slender agile body with strong hind legs and long grasping arms",
                "Long, thin teeth adapted for catching fast-moving prey and fish"
            ),
            funFacts = listOf(
                "Fossils show healed bone fractures, proving Dilophosaurus was tough and survived harsh battles.",
                "The dual head crests were too fragile for headbutting, so they were likely brightly colored for display.",
                "Real Dilophosaurus was over 7 meters long—far bigger than the small dog-sized creature in movies."
            ),
            modelType = "ALLOSAURUS",
            hotspots = listOf(
                Hotspot3D("dual_crest", "Twin Cranial Crests", "Two parallel thin bone crests used as visual signals to attract mates.", Vector3D(0f, 1.3f, 1.4f)),
                Hotspot3D("kink_jaw", "Notched Upper Jaw", "Distinctive notch in the snout designed to hook and secure wriggling prey.", Vector3D(0f, 0.7f, 2.3f))
            )
        ),
        Dinosaur(
            id = "pachycephalosaurus",
            name = "Pachycephalosaurus",
            pronunciation = "/ˌpækɪˌsɛfələˈsɔːrəs/",
            meaning = "Thick-Headed Lizard",
            era = DinoEra.CRETACEOUS,
            periodDetail = "Late Cretaceous (66 Ma)",
            diet = DinoDiet.HERBIVORE,
            lengthMeters = 4.5f,
            heightMeters = 1.6f,
            weightTons = 0.45f,
            topSpeedKmh = 25,
            discoveryCountry = "United States (Hell Creek, Montana)",
            discoveredYear = 1938,
            discoveredBy = "William Winkley",
            description = "Renowned for its bowling-ball-like dome skull, made of solid bone up to 25 cm (10 inches) thick. Surrounded by bony knobs and short spikes, it used this skull to clash heads or flank-butt rivals for territorial dominance.",
            anatomicalFeatures = listOf(
                "25 cm thick solid bone dome skull roof made of dense collagenous bone",
                "Row of blunt bony spikes and studs ringing the snout and rear of the skull",
                "Reinforced cervical vertebrae to absorb heavy kinetic impact forces",
                "Wide torso accommodating complex fermentation chambers for digesting leaves, seeds, and berries"
            ),
            funFacts = listOf(
                "Fossilized domes show deep gouges and healed lesions consistent with combat impacts.",
                "Young individuals (once classified as Dracorex and Stygimoloch) changed skull shapes as they matured.",
                "Despite its aggressive headbutting fame, it was an herbivore or possibly omnivore."
            ),
            modelType = "ALLOSAURUS",
            hotspots = listOf(
                Hotspot3D("bone_dome", "25cm Solid Bone Dome", "Ten inches of solid bone capable of withstanding heavy impact with rivals.", Vector3D(0f, 1.25f, 1.5f)),
                Hotspot3D("cranial_spikes", "Snout Studs & Spikes", "Decorated the perimeter of the dome for defense and species identification.", Vector3D(0f, 0.95f, 2.0f))
            )
        )
    )

    fun getById(id: String): Dinosaur? = dinosaurs.find { it.id == id }

    fun filter(
        searchQuery: String,
        era: DinoEra,
        diet: DinoDiet
    ): List<Dinosaur> {
        return dinosaurs.filter { dino ->
            val matchesEra = (era == DinoEra.ALL || dino.era == era)
            val matchesDiet = (diet == DinoDiet.ALL || dino.diet == diet)
            val matchesQuery = searchQuery.isBlank() ||
                dino.name.contains(searchQuery, ignoreCase = true) ||
                dino.meaning.contains(searchQuery, ignoreCase = true) ||
                dino.periodDetail.contains(searchQuery, ignoreCase = true) ||
                dino.discoveryCountry.contains(searchQuery, ignoreCase = true) ||
                dino.description.contains(searchQuery, ignoreCase = true)

            matchesEra && matchesDiet && matchesQuery
        }
    }
}
