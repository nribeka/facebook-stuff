package com.meh.stuff.facebook;

import com.meh.stuff.facebook.parameter.FeedParameter;
import com.meh.stuff.facebook.selenium.FeedCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) {
        try {
            FeedParameter feedParameter = new FeedParameter();
            // Console console = System.console();
            FeedCleaner feedCleaner = new FeedCleaner(feedParameter);
            // char[] password = console.readPassword("%s", "Password: ");
            // feedCleaner.deleteAndBackupFeed("Your-Username", "Your-Password");
            // Arrays.fill(password, ' ');
        } catch (Exception e) {
            log.error("Unable to delete and backup your account.", e);
        }
    }
}
