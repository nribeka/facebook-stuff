package com.meh.stuff.facebook.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

public class MainPage {

    private WebDriver webDriver;
    private Wait<WebDriver> wait;

    public MainPage(final WebDriver webDriver, final Wait<WebDriver> wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    private By byProfileIcon() {
        return By.cssSelector("div[data-click='profile_icon']");
    }

    public String getUsername() {
        String username = null;
        WebElement profileIconElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(byProfileIcon()));
        if (profileIconElement.isDisplayed()) {
            WebElement anchorElement = profileIconElement.findElement(By.cssSelector("a"));
            String hrefAttribute = anchorElement.getAttribute("href");
            if (hrefAttribute != null && !hrefAttribute.isEmpty()) {
                username = hrefAttribute.substring(hrefAttribute.lastIndexOf("/") + 1);
            }
        }

        return username;
    }
}
