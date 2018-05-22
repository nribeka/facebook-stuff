package com.meh.stuff.facebook.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

/*
 * Perform login everywhere there is a facebook login page.
 */
public class LoginPage {

    public static final String EMAIL_INPUT_ID = "email";
    private static final String PASSWORD_INPUT_ID = "pass";
    private static final String SUBMIT_INPUT_ID = "loginbutton";

    private WebDriver webDriver;
    private Wait<WebDriver> wait;

    public LoginPage(final WebDriver webDriver, final Wait<WebDriver> wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    private By byPasswordElement() {
        return By.id(PASSWORD_INPUT_ID);
    }

    private By bySubmitElement() {
        return By.id(SUBMIT_INPUT_ID);
    }

    private By byEmailElement() {
        return By.id(EMAIL_INPUT_ID);
    }

    private WebElement findPasswordElement() {
        return webDriver.findElement(byPasswordElement());
    }

    private WebElement findSubmitElement() {
        return webDriver.findElement(bySubmitElement());
    }

    private WebElement findEmailElement() {
        return webDriver.findElement(byEmailElement());
    }

    public void performLoginAction(final String username, final String password) {
        WebElement usernameElement = findEmailElement();
        usernameElement.sendKeys(username);

        if (password != null && !password.isEmpty()) {
            WebElement passwordElement = findPasswordElement();
            passwordElement.sendKeys(password);

            WebElement submitElement = findSubmitElement();
            submitElement.click();
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(bySubmitElement()));
    }

}
