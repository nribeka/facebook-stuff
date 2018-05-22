## Facebook Cleaner

#### Building the project
The project is just your typical maven project.

```mvn package```

The output will be in the target folder. You will get the jar with
dependencies which can be distributed and executed run using:

```java -jar facebook-stuff-xxx-with-dependencies.jar```

PS: Make sure you have the chromedriver executable inside "chromedriver"
directory in the same directory with the jar file. 

#### Running the application
To run the application from the project:

```mvn exec:java```

To run the application from the packaged jar file:

```java -jar facebook-stuff-xxx-with-dependencies.jar```

PS: Make sure you have the chromedriver executable inside "chromedriver"
directory in the same directory with the jar file.

### Application parameter 

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
