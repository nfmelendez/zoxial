Open.Zoxial.Analytics
======================

Open Source Analytics for Facebook.

License
===============
Under the Apache License, Version 2.0,  
Can be used by commercial companies.

Tech we love
===================
* Akka for thread safe, distributed crawling.
* Wicket for User interfase.
* Eclipse as default IDE
* Mysql as database.
* Twitter bootstrap for UI
* All code is java
* Maven is our dependecy manager
* Github
* Jetty as out application server.

Quick Start
===================
1.- Clone this project <br>
2.- Create the  mysql schema by importing this file: https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/SqlSchema/schema.sql

Then there are 2 projects:
 * <b>The Crawler: </b> Retrives information from facebook.
https://github.com/melendeznicolas/zoxial/tree/master/Zoxial.Analytics/Crawler
 * <b>Fanpage-UI: </b>The User Interface to show the charts.
https://github.com/melendeznicolas/zoxial/tree/master/Zoxial.Analytics/Fanpage-UI

First set up the crawler<br>
1. Change the configuration file to your needs <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Crawler/config/config.properties
<br>2. And the run the start script
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Crawler/start.sh
<br>
And should start fetching, you can check it in the logs, doing <br>
tail -f log/logging.log
<br>

With the Fanpage-UI is the same <br>
1. config to ypur needs the config file : <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Fanpage-UI/config/config.properties
<br>
2. And the run the ./start.sh script : <br>
https://github.com/melendeznicolas/zoxial/blob/master/Zoxial.Analytics/Fanpage-UI/start.sh
<br>

To import the projects to eclipse just do <br>
mvn eclipse:eclipse <br>in each project: Crawler and Fanpage-UI<br>

Any question send me an mail to : nfmelendez@gmail.com or twitter: @nfmelendez


Enjoy!

