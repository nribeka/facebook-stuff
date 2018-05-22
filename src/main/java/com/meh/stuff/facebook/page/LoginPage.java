package com.meh.stuff.facebook.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/*
 * Perform login everywhere there is a facebook login page.
 */
public class LoginPage {

    public static final String EMAIL_INPUT_ID = "email";
    private static final String PASSWORD_INPUT_ID = "pass";
    private static final String SUBMIT_INPUT_ID = "loginbutton";

    private WebDriver webDriver;

    public LoginPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    private WebElement findPasswordElement() {
        return webDriver.findElement(By.id(PASSWORD_INPUT_ID));
    }

    private WebElement findSubmitElement() {
        return webDriver.findElement(By.id(SUBMIT_INPUT_ID));
    }

    private WebElement findEmailElement() {
        return webDriver.findElement(By.id(EMAIL_INPUT_ID));
    }

    public void performLoginAction(final String username, final String password) {
        WebElement usernameElement = findEmailElement();
        usernameElement.sendKeys(username);

        WebElement passwordElement = findPasswordElement();
        passwordElement.sendKeys(password);

        WebElement submitElement = findSubmitElement();
        submitElement.click();
    }

}
