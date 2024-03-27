#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_SSD1306.h>
#include <DHT.h>

#define DHTPIN 19 
#define DHTTYPE DHT11

#define OLED_RESET    -1  // Không sử dụng chức năng reset
Adafruit_SSD1306 display(128, 32, &Wire, OLED_RESET);

DHT dht(DHTPIN, DHTTYPE);

const int sensorRain = 23;
const int sensorHuman = 18;

int rainValue, humanValue;
float humidity, temperature;

void setup() {
  Serial.begin(9600);
  pinMode(sensorRain, INPUT);
  pinMode(sensorHuman, INPUT);

  dht.begin();

  // Khởi tạo màn hình OLED
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 initialization failed"));
    for(;;);
  }

  // Hiển thị thông tin khởi động trên màn hình OLED
  display.display();
  delay(2000);
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0,0);
  display.println("ESP32 DHT11 OLED");
  display.display();
  delay(2000);
  display.clearDisplay();
}

void loop() {
  // Đọc dữ liệu từ cảm biến DHT11
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();

  // Đọc dữ liệu từ cảm biến mưa
  rainValue = digitalRead(sensorRain);
  // Đọc dữ liệu từ cảm biến nguoi
  humanValue = digitalRead(sensorHuman);
  seterrorSensor();
  // Hiển thị dữ liệu lên màn hình OLED
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0,0);
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
  display.display();
  
  delay(2000); // Chờ 2 giây trước khi đọc dữ liệu lại
}

void seterrorSensor(){
  if(isnan(humanValue)){
    display.print("LOI: cam bien nguoi!");
  }
  if(isnan(rainValue)){
    display.print("LOI: cam bien mua!");
  }
  if(isnan(humidity) ||  isnan(temperature)){
    display.print("LOI: cam bien do am!");
    display.print("LOI: cam bien nhiet do!");
  }
}

void setvalueRain(){
  if (rainValue == 0) {
    display.println("Co mua");
  } else {
    display.println("Khong mua");
  }
}

void setvalueHuman(){
  if (humanValue == 1) {
    display.println("Co nguoi");
  } else {
    display.println("Khong nguoi");
  }
}
