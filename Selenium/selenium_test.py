import time
import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class SauceDemoTests(unittest.TestCase):

    def setUp(self):
        # Khởi tạo Chrome WebDriver
        self.driver = webdriver.Chrome()
        self.driver.maximize_window()
        self.driver.get("https://www.saucedemo.com/")
        self.wait = WebDriverWait(self.driver, 10)

    def test_01_login(self):
        print("Running Test 1: Login...")
        # Nhập username và password
        self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
        self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
        self.driver.find_element(By.ID, "login-button").click()

        # Kiểm tra đăng nhập thành công bằng cách tìm tiêu đề 'Products'
        title = self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "title"))).text
        self.assertEqual(title, "Products")
        print("-> Login Test Passed!")

    def test_02_add_to_cart(self):
        print("Running Test 2: Add to Cart...")
        # Login trước
        self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
        self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
        self.driver.find_element(By.ID, "login-button").click()

        # Thêm sản phẩm đầu tiên vào giỏ hàng
        add_to_cart_btn = self.wait.until(EC.element_to_be_clickable((By.ID, "add-to-cart-sauce-labs-backpack")))
        add_to_cart_btn.click()

        # Kiểm tra icon giỏ hàng hiển thị số 1
        cart_badge = self.driver.find_element(By.CLASS_NAME, "shopping_cart_badge").text
        self.assertEqual(cart_badge, "1")
        print("-> Add to Cart Test Passed!")

    def test_03_logout(self):
        print("Running Test 3: Logout...")
        # Login trước
        self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
        self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
        self.driver.find_element(By.ID, "login-button").click()

        # Mở menu và nhấn Logout
        self.driver.find_element(By.ID, "react-burger-menu-btn").click()
        logout_btn = self.wait.until(EC.element_to_be_clickable((By.ID, "logout_sidebar_link")))
        logout_btn.click()

        # Kiểm tra đã quay về trang login có nút login-button (chờ 1 chút để web chuyển trang)
        login_btn = self.wait.until(EC.presence_of_element_located((By.ID, "login-button")))
        self.assertTrue(login_btn.is_displayed())
        print("-> Logout Test Passed!")

    def tearDown(self):
        # Đóng trình duyệt sau mỗi test case
        time.sleep(2) # Dừng 2 giây để bạn kịp nhìn thấy thao tác tự động
        self.driver.quit()

if __name__ == "__main__":
    unittest.main()
