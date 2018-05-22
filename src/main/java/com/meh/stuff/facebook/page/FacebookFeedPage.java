package com.meh.stuff.facebook.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

/*
 * Representation of one facebook feed page. Delete feed method will click on the more option menu,
 * find the delete button and click on it. This page can only be invoked in a single feed page.
 */
public class FacebookFeedPage {

    private static final String OPTION_BUTTON_ATTRIBUTE_NAME = "data-testid";
    private static final String OPTION_BUTTON_ATTRIBUTE_VALUE = "post_chevron_button";

    private static final String FEED_OPTION_ATTRIBUTE_NAME = "data-feed-option-name";
    private static final String FEED_DELETE_ATTRIBUTE_VALUE = "FeedDeleteOption";
    private static final String FEED_SAVE_ATTRIBUTE_VALUE = "FeedSaveOption";
    private static final String FEED_MORE_ATTRIBUTE_VALUE = "additionalMoreOptionsExpander";

    private WebDriver webDriver;
    private Wait<WebDriver> wait;
    private boolean autoDeletePost;

    public FacebookFeedPage(final WebDriver webDriver, final Wait<WebDriver> wait, final boolean autoDeletePost) {
        this.webDriver = webDriver;
        this.wait = wait;
        this.autoDeletePost = autoDeletePost;
    }

    private By byOptionButtonSelector() {
        return By.cssSelector(
                "a" +
                "[" + OPTION_BUTTON_ATTRIBUTE_NAME + "='" + OPTION_BUTTON_ATTRIBUTE_VALUE + "']");
    }

    private By byDeleteButtonSelector() {
        return By.cssSelector(
                "a" +
                "[" + FEED_OPTION_ATTRIBUTE_NAME + "='" + FEED_DELETE_ATTRIBUTE_VALUE + "']");
    }

    private By byMoreButtonSelector() {
        return By.cssSelector(
                "a" +
                "[" + FEED_OPTION_ATTRIBUTE_NAME + "='" + FEED_MORE_ATTRIBUTE_VALUE + "']");
    }

    private By bySaveButtonSelector() {
        return By.cssSelector(
                "a" +
                        "[" + FEED_OPTION_ATTRIBUTE_NAME + "='" + FEED_SAVE_ATTRIBUTE_VALUE + "']");
    }

    private By byDeletePostDialog() {
        // find dialog div inside the uiLayer class
        return By.cssSelector(".uiLayer div[role='dialog']");
    }

    private By byDeletePostButtonSelector() {
        // the requirement is to use this selector from the context of the delete post dialog.
        // there's only one button in that dialog, the cancel and edit post is an <a> tag.
        return By.cssSelector("button");
    }

    public boolean performDeleteFeedAction() {
        Actions action = new Actions(webDriver);
        WebElement optionButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(byOptionButtonSelector()));
        action.moveToElement(optionButton);
        optionButton.click();

        WebElement saveButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(bySaveButtonSelector()));

        if (saveButton.isDisplayed()) {
            WebElement deleteButton = webDriver.findElement(byDeleteButtonSelector());
            if (!deleteButton.isDisplayed()) {
                // delete button is not displayed, look for the more option
                WebElement moreButton = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(byMoreButtonSelector()));
                action.moveToElement(moreButton);
                moreButton.click();

                // wait until you see the delete button
                deleteButton = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(byDeleteButtonSelector()));
            }
            action.moveToElement(deleteButton);
            deleteButton.click();

            boolean optionDismissed = wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(bySaveButtonSelector()));
            // wait until the option drop down is dismissed
            if (optionDismissed) {
                // wait until the dialog confirmation displayed
                WebElement deletePostDialog = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(byDeletePostDialog()));
                if (deletePostDialog.isDisplayed() && autoDeletePost) {
                    WebElement deletePostButton = deletePostDialog.findElement(byDeletePostButtonSelector());
                    action.moveToElement(deletePostButton);
                    deletePostButton.click();
                }

                return wait.until(ExpectedConditions.invisibilityOfElementLocated(byDeletePostDialog()));
            }
        }

        return false;
    }
}
