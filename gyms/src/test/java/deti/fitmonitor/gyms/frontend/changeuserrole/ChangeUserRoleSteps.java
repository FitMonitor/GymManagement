package deti.fitmonitor.gyms.frontend.changeuserrole;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;

public class ChangeUserRoleSteps {

    private WebDriver driver;
    private Wait<WebDriver> wait;
    private String createdUsername;

    @Given("the user is in the user management page")
    public void theUserIsInTheUserManagementPage() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:4200/user-management");
    }

    @When("the user sees the account {}")
    public void theUserSeesTheAccount(String username) {
        WebElement usernameElement = driver.findElement(By.xpath("//td[contains(text(), '" + username + "')]"));
        assertThat(usernameElement.getText(), containsString(username));
    }

    @Then("selects the role user for that account")
    public void selectsTheRoleUserForThatAccount() {
        WebElement roleSelect = driver.findElement(By.xpath("//select[@id='role']"));
        roleSelect.sendKeys("User");
    }

    @And("selects the role user for that account")
    public void clicksTheUpdateButton() {
        WebElement updateButton = driver.findElement(By.xpath("//button[@id='update']"));
        updateButton.click();
    }

    @Then("the role button turns green")
    public void theRoleButtonTurnsGreen() {
        WebElement roleButton = driver.findElement(By.xpath("//td[contains(text(), '" + createdUsername + "')]/following-sibling::td/button"));
        assertThat(roleButton.getAttribute("class"), containsString("btn-success"));
    }
}
