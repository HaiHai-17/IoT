#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <DHT.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include <addons/RTDBHelper.h>
#include <addons/TokenHelper.h>

#define WIFI_SSID "HOI DONG PHE 2.4Ghz"
#define WIFI_PASSWORD "0336468470"

#define API_KEY "AIzaSyA1omfSWO-G6lS7tMdduZF3i78oiv_1wLM"
#define USER_EMAIL "kudohainguyen@gmail.com"
#define USER_PASSWORD "cadss14789@"
#define DATABASE_URL "esp32iot-3a610-default-rtdb.firebaseio.com"
//#define FIREBASE_AUTH "ce3k6CnmowW86AUF36xJVCK5jkaWDKPDqbSey4hz"

#define DHTPIN 19
#define DHTTYPE DHT11

#define OLED_RESET -1  // Không sử dụng chức năng reset
Adafruit_SSD1306 display(128, 32, &Wire, OLED_RESET);

DHT dht(DHTPIN, DHTTYPE);

const int sensorRain = 23;
const int sensorHuman = 18;

int rainValue, humanValue;
float humidity, temperature;

FirebaseData firebaseData;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long dataMillis = 0;
int count = 0;

void setup()
{
  Serial.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Đang kết nối với Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Kết nối thành công với IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;

  config.token_status_callback = tokenStatusCallback;
  Firebase.reconnectNetwork(true);
  firebaseData.setBSSLBufferSize(4096, 1024);
  Firebase.begin(&config, &auth);
  Firebase.setDoubleDigits(5);

  pinMode(sensorRain, INPUT);
  pinMode(sensorHuman, INPUT);

  dht.begin();

  // Khởi tạo màn hình OLED
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C))
  {
    Serial.println(F("Khoi tao SSD1306 that bai"));
    for (;;)
      ;
  }

  // Hiển thị thông tin khởi động trên màn hình OLED
  display.display();
  delay(2000);
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(3, 0);
  display.println("ESP32 DHT11 OLED");
  display.display();
  delay(2000);
  display.clearDisplay();
}

void loop()
{
  if (millis() - dataMillis > 1000)
  {
    dataMillis = millis();
    humidity = dht.readHumidity();
    temperature = dht.readTemperature();
    rainValue = digitalRead(sensorRain);
    humanValue = digitalRead(sensorHuman);
    if (!Firebase.ready())
    {
      Serial.println("Kết nối với Firebase thất bại.");
    }
    else
    {
      if (!isnan(humidity) && !isnan(temperature))
      {
        Firebase.setFloat(firebaseData, "/temperature", temperature);
        Firebase.setFloat(firebaseData, "/humidity", humidity);
      }
      else
      {
        Serial.println("Lỗi: Dữ liệu cảm biến không hợp lệ!");
      }
    }
    setDisplay();
  }
}

void seterrorSensor()
{
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  if (isnan(humanValue))
  {
    display.println("LOI: cam bien nguoi");
  }
  if (isnan(rainValue))
  {
    display.println("LOI: cam bien mua");
  }
  if (isnan(humidity) || isnan(temperature))
  {
    display.println("LOI: cam bien do");
  }
}

void setvalueRain()
{
  if (rainValue == 0)
  {
    display.println("CO");
  }
  else
  {
    display.println("KHONG");
  }
}

void setvalueHuman()
{
  if (humanValue == 1)
  {
    display.println("CO");
  }
  else
  {
    display.println("KHONG");
  }
}

void setDisplay()
{
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Nhiet do: ");
  display.print(temperature);
  display.println(" C");
  display.print("Do am: ");
  display.print(humidity);
  display.println(" %");
  display.print("Mua: ");
  setvalueRain();
  display.print("Nguoi: ");
  setvalueHuman();
  seterrorSensor(); // Hiển thị lỗi nếu có
  display.display();
}
