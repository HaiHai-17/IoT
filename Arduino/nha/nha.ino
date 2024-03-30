#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <DHT.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

#define WIFI_SSID "HOI DONG PHE 2.4Ghz"
#define WIFI_PASSWORD "0336468470"

#define FIREBASE_HOST "esp32iot-3a610-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "ce3k6CnmowW86AUF36xJVCK5jkaWDKPDqbSey4hz"

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

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  pinMode(sensorRain, INPUT);
  pinMode(sensorHuman, INPUT);

  dht.begin();

  // Khởi tạo màn hình OLED
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C))
  {
    Serial.println(F("Khởi tạo SSD1306 thất bại"));
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
        Firebase.pushFloat(firebaseData, "/temperature", temperature);
        Firebase.pushFloat(firebaseData, "/humidity", humidity);
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
    display.println("Lỗi: Cảm biến người!");
  }
  if (isnan(rainValue))
  {
    display.println("Lỗi: Cảm biến mưa!");
  }
  if (isnan(humidity) || isnan(temperature))
  {
    display.println("Lỗi: Cảm biến độ ẩm hoặc nhiệt độ!");
  }
}

void setvalueRain()
{
  if (rainValue == 0)
  {
    display.println("Có mưa");
  }
  else
  {
    display.println("Không mưa");
  }
}

void setvalueHuman()
{
  if (humanValue == 1)
  {
    display.println("Có người");
  }
  else
  {
    display.println("Không có người");
  }
}

void setDisplay()
{
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Nhiệt độ: ");
  display.print(temperature);
  display.println(" C");
  display.print("Độ ẩm: ");
  display.print(humidity);
  display.println(" %");
  display.print("Mưa: ");
  setvalueRain();
  display.print("Người: ");
  setvalueHuman();
  seterrorSensor(); // Hiển thị lỗi nếu có
  display.display();
}
