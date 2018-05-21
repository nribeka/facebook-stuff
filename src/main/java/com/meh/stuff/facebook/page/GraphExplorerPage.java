package com.meh.stuff.facebook.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GraphExplorerPage {

    private static final Logger log = LoggerFactory.getLogger(GraphExplorerPage.class.getSimpleName());

    private static final String BUTTON_LOGIN_TEXT = "Log In";
    private static final String BUTTON_GET_USER_TOKEN_TEXT = "Get User Access Token";
    private static final String BUTTON_GET_TOKEN_TEXT = "Get Access Token";
    private static final String BUTTON_CONTINUE_TEXT = "Continue as";

    private static final String LABEL_ACCESS_TOKEN_TEXT = "Access Token";

    private static final String[] CHECKBOX_USER_POSTS_PERMISSION_TEXT = new String[]{
            "user_posts", "user_photos", "user_likes", "user_location",
            // "user_religion_politics", "user_education_history", "user_about_me"
            "user_status", "user_tagged_places", "user_videos",
            // "user_relationship_details", "user_relationships",
            "user_friends"
    };

    private WebDriver webDriver;
    private Wait<WebDriver> wait;

    public GraphExplorerPage(WebDriver webDriver, Wait<WebDriver> wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    public By byLoginText() {
        return By.xpath("//a[contains(text(), '" + BUTTON_LOGIN_TEXT + "')]");
    }

    public By byAccessTokenText() {
        return By.xpath("//span[contains(text(), '" + LABEL_ACCESS_TOKEN_TEXT + "')]");
    }

    private By byContinueText() {
        return By.xpath("//button[contains(text(), '" + BUTTON_CONTINUE_TEXT + "')]");
    }

    private By byCreateToken() {
        return By.xpath("//button[contains(text(), '" + BUTTON_GET_TOKEN_TEXT + "')]");
    }

    private By byCreateUserTokenText() {
        return By.xpath("//span[contains(text(), '" + BUTTON_GET_USER_TOKEN_TEXT + "')]");
    }

    private By byUserPermissionText(String permission) {
        return By.xpath("//label[contains(text(), '" + permission + "')]");
    }

    private WebElement findAccessTokenInputElement() {
        WebElement webElement = webDriver.findElement(byAccessTokenText());
        WebElement parentElement = webElement.findElement(By.xpath(".."));
        return parentElement.findElement(By.tagName("input"));
    }

    private WebElement findCreateAccessTokenButton() {
        WebElement webElement = webDriver.findElement(byAccessTokenText());
        WebElement parentElement = webElement.findElement(By.xpath(".."));
        return parentElement.findElement(By.tagName("a"));
    }

    private WebElement findCreateUserTokenButton() {
        WebElement userTokenSpan = wait.until(
                ExpectedConditions.visibilityOfElementLocated(byCreateUserTokenText()));
        WebElement userTokenButton = userTokenSpan.findElement(By.xpath(".."));
        while(!"a".equalsIgnoreCase(userTokenButton.getTagName())) {
            log.info("Search for closest anchor tag. Current tag is: {}.", userTokenButton.getTagName());
            userTokenButton = userTokenButton.findElement(By.xpath(".."));
        }
        return userTokenButton;
    }

    private void enablePermission(Actions actions, String permission) {
        // make sure we have all of permissions is selected
        WebElement userPostLabel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(byUserPermissionText(permission)));
        WebElement userPostLabelParent = userPostLabel.findElement(By.xpath(".."));
        WebElement userPostCheckbox = userPostLabelParent.findElement(By.tagName("input"));
        if (!userPostCheckbox.isSelected()) {
            actions.moveToElement(userPostCheckbox);
            userPostCheckbox.click();
        }
        log.info("Permission {} is selected: {}.", permission, userPostCheckbox.isSelected());
    }

    public String createAccessTokenIfNecessary() {
        // find the access token input element
        WebElement accessTokenInputElement = findAccessTokenInputElement();
        String accessToken = accessTokenInputElement.getAttribute("value");
        if (accessToken != null) {
            // user token is not there yet, let's generate one.
            String currentWindowHandle = webDriver.getWindowHandle();

            // prepare the action used to click buttons
            Actions actions = new Actions(webDriver);

            // the create access token button and click it
            WebElement createAccessTokenButton = findCreateAccessTokenButton();
            actions.moveToElement(createAccessTokenButton);
            createAccessTokenButton.click();

            // select the user access token from the drop down
            WebElement userTokenButton = findCreateUserTokenButton();
            log.info("Expected tag to be anchor, found tag: {}.", userTokenButton.getTagName());
            actions.moveToElement(userTokenButton);
            userTokenButton.click();

            for (String permission : CHECKBOX_USER_POSTS_PERMISSION_TEXT) {
                enablePermission(actions, permission);
            }

            // click on get token button
            WebElement createTokenButton = webDriver.findElement(byCreateToken());
            actions.moveToElement(createTokenButton);
            createTokenButton.click();

            boolean permissionDialogVisible = wait.until(
                    ExpectedConditions.not(ExpectedConditions.visibilityOf(createTokenButton)));
            log.info("All permissions are set and dialog is dismissed: {}.", permissionDialogVisible);

            try {
                boolean onlyOneWindow = wait.until(
                        ExpectedConditions.numberOfWindowsToBe(1));
                log.info("Confirmation window displayed: {}.", onlyOneWindow);
            } catch (Exception e) {
                log.info("We are seeing the confirmation dialog window ...");
                // clicking it might trigger new window to confirm it is you ...
                Set<String> windowHandles = webDriver.getWindowHandles();
                for (String windowHandle : windowHandles) {
                    if (!windowHandle.equalsIgnoreCase(currentWindowHandle)) {
                        log.info("We're moving to the new window dialog ...");
                        webDriver.switchTo().window(windowHandle);

                        WebElement continueAsButton = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(byContinueText()));
                        log.info("Found continue as button of type {} and displayed: {}.",
                                continueAsButton.getTagName(), continueAsButton.isDisplayed());
                        actions.moveToElement(continueAsButton);
                        continueAsButton.click();

                        boolean windowClosed = wait.until(
                                ExpectedConditions.numberOfWindowsToBe(1));
                        if (windowClosed) {
                            log.info("Switching back to the previous window ...");
                            webDriver.switchTo().window(currentWindowHandle);
                        }
                    }
                }
            }

            // let's try again and see if we can get the access token
            accessToken = accessTokenInputElement.getAttribute("value");
        }

        return accessToken;
    }
}
