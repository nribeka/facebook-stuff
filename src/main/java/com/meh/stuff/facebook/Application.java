package com.meh.stuff.facebook;

import com.meh.stuff.facebook.parameter.FeedParameter;
import com.meh.stuff.facebook.selenium.FeedCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in).useDelimiter("\\s");
            System.out.println("Entering interactive mode ...");

            FeedParameter feedParameter = new FeedParameter();
            System.out.print("Do you just want to go through your post? (Y/n): ");
            String reviewing = readAnswer(scanner);
            feedParameter.setReviewing(reviewing.equals("Y"));

            System.out.print("Do you want to take screenshot of your post? (Y/n)");
            String takeScreenshot = readAnswer(scanner);
            feedParameter.setTakeScreenshot(takeScreenshot.equals("Y"));
            if (takeScreenshot.equals("Y")) {
                System.out.println("Your screenshot will be saved under screenshot folder.");
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

                System.out.print("Do you want to keep any of posts? (Y/n): ");
                String keepAny = readAnswer(scanner);
                if (keepAny.equals("n")) {
                    feedParameter.setKeepCount(0);
                } else {
                    System.out.print("Do you want to keep by number of posts? (Y/n): ");
                    String keepByNumber = readAnswer(scanner);
                    if (keepByNumber.equals("Y")) {
                        System.out.print("How many post do you want to keep? (1 - 1000): ");
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

            log.info("Parameter -> keep count: {}, keep since: {}, reviewing: {}, auto delete: {}.",
                    feedParameter.getKeepCount(), feedParameter.getKeepSince(), feedParameter.isReviewing(),
                    feedParameter.isAutoDelete());

            FeedCleaner feedCleaner = new FeedCleaner(feedParameter);
            // char[] password = console.readPassword("%s", "Password: ");
            // feedCleaner.deleteAndBackupFeed("Your-Username", "Your-Password");
            // Arrays.fill(password, ' ');
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
