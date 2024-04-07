#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <DHT.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include <addons/RTDBHelper.h>
#include <addons/TokenHelper.h>
#include <WebServer.h>
#include <ESPmDNS.h>
#include <HTTPUpdateServer.h>
#include <BluetoothSerial.h>
//Cấu hình server update OTA
const char* host = "ESP32";
const char* updatePath = "/update";
const char* updateUsername = "admin";
const char* updatePassword = "admin";
WebServer webServer(80);
HTTPUpdateServer httpUpdater;
BluetoothSerial SerialBT;
//Cấu hình wifi
#define WIFI_SSID "HOI DONG PHE 2.4Ghz"
#define WIFI_PASSWORD "0336468470"
//Cấu hình Firebase
#define API_KEY "AIzaSyA1omfSWO-G6lS7tMdduZF3i78oiv_1wLM"
#define USER_EMAIL "kudohainguyen@gmail.com"
#define USER_PASSWORD "cadss14789@"
#define DATABASE_URL "esp32iot-3a610-default-rtdb.firebaseio.com"
//Khởi tạo DHT11
#define DHTPIN 19
#define DHTTYPE DHT11
//Khởi tạo OLED
#define OLED_RESET -1  // Không sử dụng chức năng reset
Adafruit_SSD1306 display(128, 32, &Wire, OLED_RESET);
//Cấu hình DHT11
DHT dht(DHTPIN, DHTTYPE);
//Khai báo chân cảm biến
const int sensorRain = 23;
const int sensorHuman = 18;
//Khai báo giá trị
int rainValue, humanValue, pump1State, pump2State;
float humidity, temperature;
//Đặ biến Firebase
FirebaseData firebaseData;
FirebaseAuth auth;
FirebaseConfig config;
//Sử dụng milis thay cho delay
unsigned long dataMillis = 0;
int count = 0;
// Chân máy bơm
const int pump1 = 5;
const int pump2 = 17;
//Trang update OTA
const char MainPage[] PROGMEM = R"=====(
  <!DOCTYPE html> 
  <html>
   <head> 
       <title>ESP32</title> 
       <style> 
          body{
            text-align: center;
          }
       </style>
       <meta name="viewport" content="width=device-width,user-scalable=0" charset="UTF-8">
   </head>
   <body> 
      <div>
        <button onclick="window.location.href='/update'">UPLOAD FIRMWARE</button><br><br>
      </div>
      <script>
      </script>
   </body> 
  </html>
)=====";

void setup()
{
  Serial.begin(115200);
  SerialBT.begin("ESP32"); // Tên Bluetooth của ESP32
  //Kết nối wifi
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
  //Khởi động update OTA
  if (!MDNS.begin(host)) {
    Serial.println("Lỗi khởi động mDNS!");
    while (1) {
      delay(1000);
    }
  }

  MDNS.addService("http", "tcp", 80);

  httpUpdater.setup(&webServer, updatePath, updateUsername, updatePassword);
  webServer.on("/", [](){
    String s = MainPage;
    webServer.send(200, "text/html", s);
  });
  webServer.begin();
  Serial.println("Khởi động thành công Webserver.");
  //Thiết lập kết nối Firebase
  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);
  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;
  //Kết nối Firebase
  config.token_status_callback = tokenStatusCallback;
  Firebase.reconnectNetwork(true);
  firebaseData.setBSSLBufferSize(4096, 1024);
  Firebase.begin(&config, &auth);
  Firebase.setDoubleDigits(5);
  //Khai báo chân kết nối 
  pinMode(sensorRain, INPUT);
  pinMode(sensorHuman, INPUT);
  pinMode(pump1, OUTPUT);
  pinMode(pump2, OUTPUT);
  //Bắt đầu DHT11
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
  //MDNS.update();
  webServer.handleClient();
  if (SerialBT.available()) {
    String message = SerialBT.readStringUntil('\n');
    if (message.length() > 0) {
      changeWiFi(message); // Gọi hàm changeWiFi() khi nhận được dữ liệu từ Bluetooth
    }
  }
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
        Firebase.setFloat(firebaseData, "/dht11/temperature", temperature);
        Firebase.setFloat(firebaseData, "/dht11/humidity", humidity);
      }
      else
      {
        Serial.println("Lỗi: Dữ liệu cảm biến không hợp lệ!");
      }
    }
    setDisplay();
    setRelay();
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
    Firebase.setString(firebaseData, "/sensor/rain", "co");
  }
  else
  {
    display.println("KHONG");
    Firebase.setString(firebaseData, "/sensor/rain", "khong");
  }
}

void setvalueHuman()
{
  if (humanValue == 1)
  {
    display.println("CO");
    Firebase.setString(firebaseData, "/sensor/human", "co");
  }
  else
  {
    display.println("KHONG");
    Firebase.setString(firebaseData, "/sensor/human", "khong");
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

void setRelay() {
  if (Firebase.getInt(firebaseData, "/pump/pump1")) {
    if (firebaseData.dataType() == "int") {
      pump1State = firebaseData.intData();
      // Kiểm tra trạng thái hiện tại của máy bơm trước khi thay đổi
      if (pump1State != digitalRead(pump1)) {
        digitalWrite(pump1, pump1State);
        // Xác nhận rằng lệnh đã được thực hiện thành công
        if (digitalRead(pump1) == pump1State) {
          Serial.println("Đã kích hoạt máy bơm 1");
        } else {
          Serial.println("Lỗi: Không thể kích hoạt máy bơm 1");
        }
      }
    }
  } else {
    digitalWrite(pump1, LOW);
    Serial.println("Tắt máy bơm 1");
  }
  
  if (Firebase.getInt(firebaseData, "/pump/pump2")) {
    if (firebaseData.dataType() == "int") {
      pump2State = firebaseData.intData();
      // Kiểm tra trạng thái hiện tại của máy bơm trước khi thay đổi
      if (pump2State != digitalRead(pump2)) {
        digitalWrite(pump2, pump2State);
        // Xác nhận rằng lệnh đã được thực hiện thành công
        if (digitalRead(pump2) == pump2State) {
          Serial.println("Đã kích hoạt máy bơm 2");
        } else {
          Serial.println("Lỗi: Không thể kích hoạt máy bơm 2");
        }
      }
    }
  } else {
    digitalWrite(pump2, LOW);
    Serial.println("Tắt máy bơm 2");
  }
}


void changeWiFi(String data) {
  String name = data.substring(0, data.indexOf(','));
  String pass = data.substring(data.indexOf(',') + 1);

  // In thông tin nhận được từ Bluetooth
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Received name: ");
  display.println(name);
  display.print("Received pass: ");
  display.println(pass);

  // Thiết lập thông tin Wi-Fi mới
  WiFi.disconnect();
  delay(1000); // Đợi cho WiFi đóng kết nối

  WiFi.begin(name.c_str(), pass.c_str());

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    display.println("Connecting to new WiFi...");
  }

  display.println("Connected to new WiFi");
}

