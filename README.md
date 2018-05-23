## Facebook Cleaner

### Build and Running

#### Prerequisite
You need to get facebook client secret and client id in order to use this
application. If you don't have one, I might be able to help you set the values.
Hit me with an email.
 
If you already have one, you can put it in the `cleaner.properties` file if
you're planning to run it from the repo directly. Or create a new
`cleaner-user.properties` properties file and put it in the same directory as
the `facebook-stuff-xxx-with-dependencies.jar` file. The values from this
properties file will override the default values in `cleaner.properties` file. 

#### Building the project
The project is just your typical maven project.

```mvn package```

The output will be in the target folder. You will get the jar with
dependencies which can be distributed and executed run using:

```java -jar facebook-stuff-xxx-with-dependencies.jar```

PS: Make sure you have the chromedriver executable inside "chromedriver"
directory in the same directory with the jar file. 

#### Running the application
To run the application from the project after you build it:

```mvn exec:java```

To run the application from the packaged jar file:

```java -jar facebook-stuff-xxx-with-dependencies.jar```

PS: Make sure you have the chromedriver executable inside "chromedriver"
directory in the same directory with the jar file.

### Parameters

Default parameter of the application will make the application run in
reviewing mode. All of the parameters are exposed through properties or
through the interactive mode.

* _delay.betweenPost_ (true | false)

  Whether the application should put a pause between posts. Loading a public
  post can be fast (depending on internet speed). Setting this flag to true
  will ensure that the application will stop processing and allowing user to
  interact with the public post. You could check number of likes, check the
  comments section.
  
  **PS:** If you're on delete mode, make sure you don't touch the top right
  contextual menu because this application is using that context menu to delete
  the public post. 
   
* _delay.betweenPostInSecond_ (1 - 1000)

  Delay between post in **seconds**. Adding longer delay will make processing
  all of your public posts slower (obviously). 
  
* _mode.reviewing_ (true | false)
  
  Flag to determine whether the application should just go through your public
  posts or attempt to delete them.

* _keep.count_ (1 - 1000)

  Number of public posts to skip until start performing deletion on the public
  posts. The first X will be running in review mode, the rest will run in delete
  mode.

* _keep.since_ (dd/mm/yyyy, e.g. 01/01/2018)

  Last posted date of the public posts that will be skipped by the delete mode.

* _autoDelete_ (true | false)

  Flag to determine whether the user will perform the delete or the application
  will automate that for the user.

* _takeScreenshot_ (true | false)

  Flag to determine whether the application should take a screenshot of the
  public post. This flag works for both review and delete mode.

* _skipInteractive_ (true | false)

  Flag to skip the interactive mode and use values from the properties file.
  
* _startFrom_ (dd/mm/yyyy, e.g. 01/01/2018)

  Earliest date of the first post to pull from facebook. For example, if you
  set this date to 01/01/2017, the application will pull posts from that date
  going back all the way to your first post. 

* _app.clientId_
  
  Client id for facebook developer. This is needed by facebook4j to pull the
  user's public posts data.
  
  **PS:** Hit me with an email if you need this properties.
  
* _app.clientSecret_
  
  Client secret for facebook developer. This is needed by facebook4j to pull the
  user's public posts data.
  
  **PS:** Hit me with an email if you need this properties.

#### Reviewing mode

In the reviewing mode, the application will go through all of your facebook
post and do nothing with it. In the review mode, you can also take screenshot
of your facebook post and store it under the screenshot folder.

#### Deleting mode

In the deleting mode, the application will go through all of your facebook
post and give you the option to whether manually delete them or let the 
application delete the post for you. The process will remove your post
publicly but facebook most likely will still retain your data forever and
just marking it as deleted.


The delete mode will allow you to keep some of your post and to control it,
you're given 2 options:

* Keep X number of post

  You will be prompted to enter number of post you want to keep. The
  application will start counting down and only start deleting your post if
  the threshold number of posts have been reached.
  
* Keep post since date X

  You will be prompted to enter the date of the last post you want to keep.
  Post older than this cutoff date will be deleted.


### Username and password
In order to access all of your public posts, the application will be
utilizing the token from the graph explorer and feed them to facebook4j
library.

And in order to get the token from graph explorer, you will need to enter your
username and password either in the code (if you're comfortable with it) or
during the authentication process to graph explorer.

Enter your credentials when you see the following log message:

```[main] INFO  FeedCleaner - Email input element found, displayed: true.```

This means the application was able to reach graph explorer and get to the
login screen. Enter your username and password there and then press the
submit button. After that, the application will take over again and automate
the rest of the process.

This application will not send or store your information anywhere.
