package com.meh.stuff.facebook.selenium;

import com.meh.stuff.facebook.page.FeedPage;
import com.meh.stuff.facebook.page.GraphExplorerPage;
import com.meh.stuff.facebook.page.LoginPage;
import com.meh.stuff.facebook.page.MainPage;
import com.meh.stuff.facebook.parameter.AppParameter;
import com.meh.stuff.facebook.parameter.FeedParameter;
import com.meh.stuff.facebook.util.Utils;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.Permission;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.conf.ConfigurationBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FeedCleaner {

    private static final int MAX_AUTH_RETRY = 3;

    private static final Logger log = LoggerFactory.getLogger(FeedCleaner.class.getSimpleName());
    private static final String GRAPH_EXPLORER_URL = "https://developers.facebook.com/tools/explorer";

    private static final String FACEBOOK_BASE_URL = "https://www.facebook.com";
    private static final String FACEBOOK_POST_URL_TEMPLATE = FACEBOOK_BASE_URL + "/%s/posts/%s";

    private WebDriver webDriver;
    private Wait<WebDriver> wait;

    private final AppParameter appParameter;
    private final FeedParameter feedParameter;

    private Facebook facebook;

    public FeedCleaner(final AppParameter appParameter, final FeedParameter feedParameter) {
        this.appParameter = appParameter;
        this.feedParameter = feedParameter;
        this.initializeService();
    }

    private void initializeService() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");

        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        webDriver = new ChromeDriver(chromeOptions);

        wait = new FluentWait<>(webDriver)
                .withTimeout(60, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .ignoreAll(Collections.singletonList(NoSuchElementException.class));
    }

    private void displayUsedPermissions() throws Exception {
        List<Permission> permissions = facebook.getPermissions();
        for (Permission permission : permissions) {
            log.info("Permission {} is granted: {}.", permission.getName(), permission.isGranted());
        }
    }

    private void processPosts() throws Exception {
        webDriver.get(FACEBOOK_BASE_URL);
        MainPage mainPage = new MainPage(webDriver, wait);
        String username = mainPage.getUsername();
        if (username == null || username.isEmpty()) {
            return;
        }

        int offset = 0;
        int limit = 10;
        int receivedCount;
        int postCount = 0;
        do {
            Reading reading = new Reading()
                    .fields("id", "created_time", "message", "type")
                    .limit(limit)
                    .offset(offset);
            ResponseList<Post> posts = facebook.getPosts(reading);
            receivedCount = posts.size();
            offset = offset + receivedCount;

            for (Post post : posts) {
                log.info("Post info: id: {}, created: {}, message: {}, type: {}.",
                        post.getId(), post.getCreatedTime(), post.getMessage(), post.getType());

                postCount ++;

                String compositeId = post.getId();
                String postId = compositeId.substring(compositeId.indexOf("_") + 1);
                String feedUrl = String.format(FACEBOOK_POST_URL_TEMPLATE, username, postId);
                webDriver.get(feedUrl);

                if (feedParameter.isDelayBetweenPost()) {
                    Utils.sleepSilently(feedParameter.getDelayBetweenPostInSecond() * 1000);
                }

                FeedPage feedPage = new FeedPage(webDriver, wait);
                if (feedParameter.isTakeScreenshot()) {
                    feedPage.takeScreenshot(compositeId);
                }

                log.info("Should this post with id {} be deleted: {}.", post.getId(), shouldDelete(post, postCount));
                if (!feedParameter.isReviewing()) {
                    if (feedParameter.isAutoDelete()) {
                        feedPage.setAutoDeletePost();
                    }
                    if (shouldDelete(post, postCount) && feedPage.performDeleteFeedAction()) {
                        log.info("Deleted feed with id: {}.", post.getId());
                    }
                }
            }
        } while (receivedCount >= limit);
    }

    private boolean shouldDelete(Post post, Integer postCount) {
        if (feedParameter.getKeepSince() != null) {
            return post.getCreatedTime().before(feedParameter.getKeepSince());
        } else if (feedParameter.getKeepCount() >= 0) {
            return postCount > feedParameter.getKeepCount();
        } else {
            return false;
        }
    }

    private void authorizeService(String username, String password) {
        webDriver.get(GRAPH_EXPLORER_URL);
        LoginPage loginPage = new LoginPage(webDriver, wait);
        GraphExplorerPage graphExplorerPage = new GraphExplorerPage(webDriver, wait);

        Actions actions = new Actions(webDriver);
        WebElement loginElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(graphExplorerPage.byLoginText()));
        actions.moveToElement(loginElement);
        loginElement.click();

        WebElement emailElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id(LoginPage.EMAIL_INPUT_ID)));
        log.info("Email {} element found, displayed: {}.", emailElement.getTagName(), emailElement.isDisplayed());
        loginPage.performLoginAction(username, password);

        WebElement accessTokenWebElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(graphExplorerPage.byAccessTokenText()));
        log.info("Access token element of {} tag is displayed: {}.",
                accessTokenWebElement.getTagName(), accessTokenWebElement.isDisplayed());

        String accessToken = graphExplorerPage.createAccessTokenIfNecessary();
        if (accessToken != null && !accessToken.isEmpty()) {
            log.info("Access token: {}.", accessToken);

            String clientId = appParameter.getClientId();
            String clientSecret = appParameter.getClientSecret();
            String baseUrl = "https://graph.facebook.com/v2.12/";

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthAppId(clientId);
            configurationBuilder.setOAuthAppSecret(clientSecret);
            configurationBuilder.setRestBaseURL(baseUrl);

            FacebookFactory facebookFactory = new FacebookFactory(configurationBuilder.build());
            facebook = facebookFactory.getInstance();
            facebook.setOAuthAccessToken(new AccessToken(accessToken));
        }
    }

    public void clean(final String email, final String password) throws Exception {
        try {
            int retryCount = 0;
            while (facebook == null && retryCount < MAX_AUTH_RETRY) {
                authorizeService(email, password);
                retryCount++;
            }

            if (facebook != null) {
                processPosts();
                displayUsedPermissions();
            }
        } finally {
            destroyService();
        }
    }

    private void destroyService() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
