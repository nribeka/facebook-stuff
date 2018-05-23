package com.meh.stuff.facebook;

import com.meh.stuff.facebook.parameter.AppParameter;
import com.meh.stuff.facebook.parameter.FeedParameter;
import com.meh.stuff.facebook.selenium.FeedCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

import static com.meh.stuff.facebook.util.Utils.SCREENSHOT_PARENT_DIRECTORY;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class.getSimpleName());
    private static final String CONFIGURATION_FILE_NAME = "cleaner.properties";

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Application
                    .class
                    .getClassLoader()
                    .getResourceAsStream(CONFIGURATION_FILE_NAME));

            InputStream cleanerLocalProperties = Application
                    .class
                    .getClassLoader()
                    .getResourceAsStream("cleaner-local.properties");
            if (cleanerLocalProperties != null) {
                properties.load(cleanerLocalProperties);
            }

            File userProperties = new File(CONFIGURATION_FILE_NAME);
            if (userProperties.exists()) {
                properties.load(new FileInputStream(userProperties));
            }

            AppParameter appParameter = new AppParameter();
            appParameter.loadFromProperties(properties);

            if (appParameter.getClientId() == null || appParameter.getClientSecret() == null
                    || appParameter.getClientId().isEmpty() || appParameter.getClientSecret().isEmpty()) {
                System.out.println("You need to set the client id and client secret in the property file.");
                System.exit(0);
            }

            FeedParameter feedParameter = new FeedParameter();
            feedParameter.loadFromProperties(properties);

            if (!"true".equalsIgnoreCase(properties.getProperty("skipInteractive"))) {

                Scanner scanner = new Scanner(System.in).useDelimiter("\\s");
                System.out.println("Entering interactive mode ...");

                System.out.print("Do you just want to go through your post? (Y/n): ");
                String reviewing = readAnswer(scanner);
                feedParameter.setReviewing(reviewing.equals("Y"));

                System.out.print("Do you want to take screenshot of your post? (Y/n)");
                String takeScreenshot = readAnswer(scanner);
                feedParameter.setTakeScreenshot(takeScreenshot.equals("Y"));
                if (takeScreenshot.equals("Y") && SCREENSHOT_PARENT_DIRECTORY.mkdirs()) {
                    System.out.println("Your screenshot will be saved under '" +
                            SCREENSHOT_PARENT_DIRECTORY.getAbsolutePath() + "' folder.");
                }

                if (reviewing.equals("n")) {
                    System.out.print("Have you backup your facebook data? (Y/n): ");
                    String backedUp = readAnswer(scanner);

                    boolean confirmed = backedUp.equals("Y");
                    if (backedUp.equals("n")) {
                        System.out.println("You should back up your facebook data first before attempting to delete them.");
                        System.out.print("Do you still want to proceed? (Y/n): ");
                        String confirmation = readAnswer(scanner);
                        confirmed = confirmation.equals("Y");
                    }

                    if (!confirmed) {
                        System.exit(0);
                    }

                    System.out.print("Do you want to manually press the delete button in the post? (Y/n): ");
                    String manualDelete = readAnswer(scanner);
                    feedParameter.setAutoDelete(manualDelete.equals("n"));

                    System.out.print("Do you want to keep any of your public posts? (Y/n): ");
                    String keepAny = readAnswer(scanner);
                    if (keepAny.equals("n")) {
                        feedParameter.setKeepCount(0);
                    } else {
                        System.out.print("Do you want to keep by number of public posts? (Y/n): ");
                        String keepByNumber = readAnswer(scanner);
                        if (keepByNumber.equals("Y")) {
                            System.out.print("How many posts do you want to keep? (1 - 1000): ");
                            int keepCount = scanner.nextInt();
                            feedParameter.setKeepCount(keepCount);
                        } else {
                            System.out.println("Ok, we're keeping posts by their date then.");
                            System.out.print("What will be the last post date you want to keep? (dd/mm/yyyy): ");
                            String keepSince = scanner.next();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            feedParameter.setKeepSince(simpleDateFormat.parse(keepSince));
                        }
                    }
                }
            }

            if (feedParameter.getKeepCount() == -1 && feedParameter.getKeepSince() == null) {
                log.info("Parameter keepSince and keepCount is not set. Running in reviewing mode.");
            }

            log.info("Skip interactive: {}.", properties.getProperty("skipInteractive"));

            log.info("Delay between opening post: {}.", feedParameter.isDelayBetweenPost());
            log.info("Amount of delay between post: {}s.", feedParameter.getDelayBetweenPostInSecond());

            log.info("I'm just here reviewing my old posts: {}.", feedParameter.isReviewing());
            log.info("Take screenshot of my old posts: {}.", feedParameter.isTakeScreenshot());

            if (!feedParameter.isReviewing()) {
                log.info("Number of post to skip before start deleting: {}.", feedParameter.getKeepCount());
                log.info("Oldest date of post to skip before start deleting: {}.", feedParameter.getKeepSince());
                log.info("Automate post deletion process: {}.", feedParameter.isAutoDelete());
            }

            FeedCleaner feedCleaner = new FeedCleaner(appParameter, feedParameter);
            feedCleaner.clean("Enter your username", null);
        } catch (Exception e) {
            log.error("Unable to clean your account.", e);
        }
    }

    private static String readAnswer(Scanner scanner) {
        String input = scanner.next();
        while (!input.equals("Y") && !input.equals("n")) {
            System.out.print("Unrecognized answer. Please enter Y or n: ");
            input = scanner.next();
        }
        return input;
    }
}
