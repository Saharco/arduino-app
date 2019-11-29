# Columbus

This is a project for the course "236333 - Project in Arduino" and IoT of the Technion.
*Columbus* features an Arduino-controlled autonomous robot that explores a room and produces a 2D mapping of it for the user to view on their mobile device.
Overall, this is a specification of the SLAM problem: a chicken-or-egg problem where the map is needed for localization; whereas at the same time - a pose estimate in the map itself is needed for the creation of the map. One requirement of SLAM is a range measurement device for observing the environment around the robot. The most common form of measurement in similar, commercial projects is by either a laser scanner, radar or LiDAR. Our project will operate in small environments, so a LiDAR sensor (which combines laser and radar) is ideal for its triangulation technique.
(SLAM: https://en.wikipedia.org/wiki/Simultaneous_localization_and_mapping). This repository is dedicated for the Android app coupled to this project.
