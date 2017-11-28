package Test.QATestLab4;

import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class Script {

	final static String url = "http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/";

	final static String urlMain = " http://prestashop-automation.qatestlab.com.ua/";

	EventFiringWebDriver eventDriver;

	WebDriverWait waitElement;

	public String nameProduct;

	public Integer quantityProduct;

	public Double priceProduct;

	public String setName() {

		nameProduct = "Product " + System.currentTimeMillis();
		return nameProduct;

	}

	public Integer setQuantity() {

		quantityProduct = (int) ((Math.random() * 99) + 1);
		return quantityProduct;

	}

	public Double setPrice() {

		priceProduct = (Math.random() * 99.9) + 0.1;
		priceProduct = new BigDecimal(priceProduct).setScale(2, RoundingMode.UP).doubleValue();
		return priceProduct;

	}

	public static void log(String log) {
		Reporter.log(log);
	}

	@BeforeTest
	@Parameters("browser")
	public void setUp(String browser) {

		switch (browser) {
		case "Chrome": {
			System.setProperty("webdriver.chrome.driver", "Drivers" + File.separator + "chromedriver.exe");
			WebDriver driver = new ChromeDriver();
			driver.manage().window().maximize();
			eventDriver = new EventFiringWebDriver(driver);
			eventDriver.register(new EventDriver());
			waitElement = new WebDriverWait(eventDriver, 20);
			break;

		}
		case "Firefox": {
			System.setProperty("webdriver.gecko.driver", "Drivers" + File.separator + "geckodriver.exe");
			WebDriver driver = new FirefoxDriver();
			driver.manage().window().maximize();
			eventDriver = new EventFiringWebDriver(driver);
			eventDriver.register(new EventDriver());
			waitElement = new WebDriverWait(eventDriver, 20);
			break;

		}
		case "Edge": {
			System.setProperty("webdriver.edge.driver", "Drivers" + File.separator + "MicrosoftWebDriver.exe");
			WebDriver driver = new EdgeDriver();
			driver.manage().window().maximize();
			eventDriver = new EventFiringWebDriver(driver);
			eventDriver.register(new EventDriver());
			waitElement = new WebDriverWait(eventDriver, 20);
			break;

		}
		}

	}

	@Test(dataProvider = "loginPassword")
	public void authorization(String login, String password) {

		eventDriver.get(url);

		WebElement loginWeb = eventDriver.findElement(By.id("email"));
		log("Input login.");
		loginWeb.sendKeys(login);

		WebElement passwordWeb = eventDriver.findElement(By.id("passwd"));
		log("Input password.");
		passwordWeb.sendKeys(password);

		WebElement buttonWeb = eventDriver.findElement(By.name("submitLogin"));
		log("Click on button login.");
		buttonWeb.click();
	}

	@Test(dependsOnMethods = "authorization")
	public void katalogProducts() {

		log("Waiting loading.");
		boolean flag = true;
		while (flag) {
			try {
				WebElement loadingElement = eventDriver.findElement(By.xpath("//span[@id='ajax_running']"));
				String style = loadingElement.getAttribute("style");
				if (style.equals("display: none;"))
					flag = false;
			} catch (NoSuchElementException e) {
				flag = true;
			}
		}

		WebElement catalog = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='subtab-AdminCatalog']/a")));

		Actions act = new Actions(eventDriver);
		log("Hover cursor over the directory.");
		act.moveToElement(catalog).perform();

		WebElement products = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='subtab-AdminProducts']/a")));
		log("Select section goods.");
		act.click(products).perform();
	}

	@Test(dependsOnMethods = "katalogProducts")
	public void makeNewProduct() {

		WebElement newProduct = waitElement.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='page-header-desc-configuration-add']")));
		log("Click on the button to create a new product.");
		newProduct.click();

	}

	@Test(dependsOnMethods = "makeNewProduct")
	public void propertiesNewProduct() {

		WebElement nameProduct = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='form_step1_name_1']")));
		log("Entering the name of the new product.");
		nameProduct.sendKeys(setName());

		WebElement quantityProduct = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='form_step1_qty_0_shortcut']")));
		log("Entering the quantity of a new product.");
		quantityProduct.sendKeys(setQuantity().toString());

		WebElement priceProduct = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='form_step1_price_shortcut']")));
		log("Field clearing.");
		priceProduct.clear();
		log("New product price entry.");
		priceProduct.sendKeys(setPrice().toString());

	}

	@Test(dependsOnMethods = "propertiesNewProduct")
	public void activationAndSave() {

		WebElement buttonActivation = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class = 'col-lg-5']/div")));
		log("Click on the button to activate a new product.");
		buttonActivation.click();

		WebElement closeAlert = waitElement.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//div[@id = 'growls']//div[@class = 'growl-close']")));
		log("Close notification.");
		closeAlert.click();

		WebElement buttonSave = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='submit']")));
		log("Click on the save new product button.");
		buttonSave.click();

		WebElement closeAlertAgain = waitElement.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//div[@id = 'growls']//div[@class = 'growl-close']")));
		log("Close notification.");
		closeAlertAgain.click();

	}

	@Test(dependsOnMethods = "activationAndSave")
	public void goToMainPage() {

		eventDriver.get(urlMain);

		WebElement allProducts = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//section[@id='content']/section/a")));
		log("Select all products.");
		allProducts.click();

		WebElement selectNewProduct = waitElement
				.until(ExpectedConditions.elementToBeClickable(By.linkText(nameProduct)));

		Assert.assertEquals(selectNewProduct.getText(), nameProduct, "Product is not found");
		log("Choose a new product.");
		selectNewProduct.click();

	}

	@Test(dependsOnMethods = "goToMainPage")
	public void testProduct() {

		WebElement nameTestProduct = waitElement
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[@class='h1']")));

		String getTextName = nameTestProduct.getText();

		WebElement priceTestProduct = waitElement
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@itemprop = 'price']")));

		String getTextPrice = priceTestProduct.getAttribute("content");

		WebElement quantityTestProduct = waitElement.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'product-quantities']/span")));

		String getTextQuantity = quantityTestProduct.getText().substring(0, quantityTestProduct.getText().length() - 7);

		Assert.assertEquals(getTextName.toUpperCase(), nameProduct.toUpperCase(), "Different product name.");

		Assert.assertEquals(getTextPrice, priceProduct.toString(), "Different product price.");

		Assert.assertEquals(getTextQuantity, quantityProduct.toString(), "Different number of product.");

	}

	@DataProvider
	public Object[][] loginPassword() {
		return new Object[][] { new Object[] { "webinar.test@gmail.com", "Xcg7299bnSmMuRLp9ITw" } };
	}

	@AfterTest
	public void closeBrowser() {

		eventDriver.quit();

	}
}
