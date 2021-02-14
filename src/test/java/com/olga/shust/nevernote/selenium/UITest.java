package com.olga.shust.nevernote.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UITest {

    public static String TEST_MEETING_NAME_1 = "meeting";
    public static String TEST_MEETING_1_TEXT = "thursday";
    public static String TEST_MEETING_NAME_2 = "dance";
    public static String TEST_MEETING_2_TEXT = "friday";

    @LocalServerPort
    private int port;

    WebDriver driver = null;

    @BeforeEach
    public void init() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(3 , TimeUnit.SECONDS);
        driver.get("http://localhost:" + port);
    }

    @AfterEach
    public void close() {
        driver.close();
    }

    public  void login(String username, String password) {
        WebElement usernameInputElement = driver.findElement(By.xpath("//div[@id='login-form-root']//input[@id='inputLogin']"));
        usernameInputElement.sendKeys(username);
        WebElement userPassword = driver.findElement(By.xpath("//div[@id='login-form-root']//input[@id='inputPassword']"));
        userPassword.sendKeys(password);
        driver.findElement(By.xpath("//div[@id='login-form-root']//button[@class='btn btn-primary']")).click();
    }

    public void createNote(String noteName, String noteText){
        driver.findElement(By.xpath("//div[@id='notes-form-root']//button[@id ='addNote']")).click();
        WebElement nameNote = driver.findElement(By.xpath("//div[@id='add-notes-form']//input[@id='addNoteName']"));
        nameNote.sendKeys(noteName);

        WebElement nameText = driver.findElement(By.xpath("//div[@id='add-notes-form']//textarea[@id='addNoteText']"));
        nameText.sendKeys(noteText);
        driver.findElement(By.xpath("//div[@id='add-notes-form']//button[@id='addNewNote']")).click();
    }

    public void checkAlertText(String text){
        new WebDriverWait(driver, 5)
                .ignoring(NoAlertPresentException.class)
                .until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String alertMessage= driver.switchTo().alert().getText();

        Assertions.assertEquals(alertMessage, text);
        alert.accept();
    }

    public void loginAndCheck(String username, String password) {
        login(username, password);
        String userNameTextElementValue = driver.findElement(By.xpath("//div[@id='notes-form']//p[@id='userNameText']")).getText();
        Assertions.assertEquals("HELLO " + username, userNameTextElementValue);
    }

    public void loginAndCheckIncorrectPassword(String username, String password) {
        login(username, password);
        checkAlertText("Password is not correct");
    }

    public void loginAndCheckInvalidPasswordAndPassword(String username, String password) {
        login(username, password);
        checkAlertText("Username or password is not valid");
    }

    public void createNoteAndCheck(String noteName, String noteText) {
        createNote(noteName,noteText);

        String noteNameElementValue = driver.findElement(By.xpath("//div[@id='note-widget" + "-" + noteName + "']" + "//div[@class='card-header']")).getText();
        Assertions.assertEquals(noteName, noteNameElementValue);

        String noteTextElementValue = driver.findElement(By.xpath("//div[@id='note-widget" +  "-" + noteName  + "']" + "//p[@class='card-text']")).getText();
        Assertions.assertEquals(noteText, noteTextElementValue);
    }

    public void logout() {
        driver.findElement(By.xpath("//div[@id='notes-form']//button[@id='logOut']")).click();
        List<WebElement> noteElements = driver.findElements(By.xpath("//div[@id='login-form-root']//div[@id='login-form']"));
        Assertions.assertEquals(noteElements.size(), 1);
    }

    public void deleteNoteAndCheck(String nameNote) {
        driver.findElement(By.xpath("//div[@id='note-widget-" + nameNote + "']//button[@id='deleteButton']")).click();
        List<WebElement> noteElements = driver.findElements(By.xpath("//div[@id='notes-widget-" + nameNote + "']"));
        Assertions.assertEquals(noteElements.size(), 0);

    }

    public void noteIsAlreadyExist(String noteName, String noteText){
        createNote(noteName,noteText);
        checkAlertText("This note name already exist");
    }


    @Test
    public void userAddsNotes() throws Exception {
        loginAndCheck("Olga", "1234");
        createNoteAndCheck(TEST_MEETING_NAME_1, TEST_MEETING_1_TEXT);
        createNoteAndCheck(TEST_MEETING_NAME_2, TEST_MEETING_2_TEXT);

        deleteNoteAndCheck(TEST_MEETING_NAME_1);
        deleteNoteAndCheck(TEST_MEETING_NAME_2);

        logout();
    }

    @Test
    public void userLoginWithIncorrectPassword() throws Exception {
        loginAndCheck("Olga", "1234");
        logout();
        loginAndCheckIncorrectPassword("Olga", "123");
    }

    @Test
    public void userLoginWithInvalidPassword() throws Exception {
        loginAndCheck("Olga", "1234");
        logout();
        loginAndCheckInvalidPasswordAndPassword("", "");
    }

    @Test
    public void noteAlreadyExist() throws Exception {
        loginAndCheck("Olga", "1234");
        createNoteAndCheck(TEST_MEETING_NAME_1, TEST_MEETING_1_TEXT);
        noteIsAlreadyExist(TEST_MEETING_NAME_1,TEST_MEETING_1_TEXT);
    }

}
