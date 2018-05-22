package com.meh.stuff.facebook;

import com.meh.stuff.facebook.parameter.FeedParameter;
import com.meh.stuff.facebook.selenium.FeedCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.YEAR, 2018);

            FeedParameter feedParameter = new FeedParameter();
            feedParameter.setKeepSince(calendar.getTime());
            feedParameter.setReviewing(true);
            feedParameter.setAutoDelete(false);
            // Console console = System.console();
            FeedCleaner feedCleaner = new FeedCleaner(feedParameter);
            // char[] password = console.readPassword("%s", "Password: ");
            // feedCleaner.deleteAndBackupFeed("Your-Username", "Your-Password");
            // Arrays.fill(password, ' ');
        } catch (Exception e) {
            log.error("Unable to clean your account.", e);
        }
    }
}
