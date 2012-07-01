zoxial
======

Open Source for Socialmedia

Quick Start
===================
1.- Clone this project <br>
2.- Create your mysql schema importing this file: https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/SqlSchema/schema.sql

Then there are 2 projects:
 * The Crawler that retrives information from facebook.
https://github.com/melendeznicolas/zoxial/tree/master/Zoxial.Analytics/Crawler
 * The User Interface to show the charts.
https://github.com/melendeznicolas/zoxial/tree/master/Zoxial.Analytics/Fanpage-UI

First set up the crawler
change the configuration file to your needs <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Crawler/config/config.properties

<br> And the run the start script
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Crawler/start.sh
<br>
And should start fetching, you can check it in the logs, doing <br>
tail -f log/logging.log

<br><br>

With the UI is the same, 
config to ypur needs the config file : <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Fanpage-UI/config/config.properties
<br>
And the run the ./start.sh script : <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Fanpage-UI/start.sh

<br>

To import the projects to eclipse just do <br>
mvn eclipse:eclipse <br>in each project: Crawler and Fanpage-UI<br>

Any question send me an mail to : nfmelendez@gmail.com or twitter: @nfmelendez


Enjoy!

