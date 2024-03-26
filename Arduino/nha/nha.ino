#define BLYNK_TEMPLATE_ID "TMPL6IHMQSCKT"
#define BLYNK_TEMPLATE_NAME "VUON THI"
#define BLYNK_AUTH_TOKEN "rg3IsJBrkeswaJfAixPHDLg9KFAZbWms"

#include <WiFi.h>
#include <BlynkSimpleEsp32.h>
#include <DHT.h>

// DHT define
#define DHTTYPE DHT11   // DHT 11
#define DHTPIN 2
DHT dht(DHTPIN, DHTTYPE);

// Define LED
#define LED 4
WidgetLED LED_ON_APP(V2);
int button;

char auth[] = BLYNK_AUTH_TOKEN;
// Your WiFi credentials.
// Set password to "" for open networks.
char ssid[] = "HOI DONG PHE 2.4Ghz";
char pass[] = "0336468470";

void setup()
{
  // Debug console
  pinMode(LED, OUTPUT);
  Serial.begin(115200);
  dht.begin();

  Blynk.begin(auth, ssid, pass);
}

BLYNK_WRITE(V3) {
  button = param.asInt();
  if(button == 1) {
    digitalWrite(LED, HIGH);
    LED_ON_APP.on();
  } else {
    digitalWrite(LED, LOW);
    LED_ON_APP.off();
  }
}

void loop()
{
  Blynk.run();
  // Read Temp
  float t = dht.readTemperature();
  // Read Humi
  float h = dht.readHumidity();
  // Check isRead ?
  if (isnan(h) || isnan(t)) {
    delay(500);
    Serial.println("Failed to read from DHT sensor!\n");
    return;
  }

  Blynk.virtualWrite(V0, t);
  Blynk.virtualWrite(V1, h);

  Serial.print("\n");
  Serial.print("Humidity: " + String(h) + "%");
  Serial.print("\t");
  Serial.print("Temperature:" + String(t) + " C");
  delay(2000);
}
