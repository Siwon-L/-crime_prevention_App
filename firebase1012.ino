#include <ESP8266WiFi.h>    //ESP8266WiFi를 사용하기 위한 헤더
#include <FirebaseESP8266.h>//FirebaseESP8266를 사용하기 위한 헤더
#include <DHT.h>            //온습도를 측정하기 위한 헤더

#define WIFI_SSID "id"
#define WIFI_PASSWORD "password"

#define FIREBASE_HOST ""
#define FIREBASE_AUTH ""

FirebaseData fbdo;
FirebaseJson json;

#define ECHO D2   //에코
#define TRIG D3   //트리거
#define DATAPIN D4 //온습도 데이터 핀

DHT myDHT11(DATAPIN, DHT11);

int sensor = D6;
int LED = D7;
int speaker = D8;
int human_value, flame_value = 0;
int flame = D5;

void setup() {
  
  pinMode(TRIG,OUTPUT);
  pinMode(ECHO,INPUT);
  pinMode(LED, OUTPUT);   //빨간색 LED 전구
  pinMode(speaker,OUTPUT);//피에조스피커
  pinMode(sensor, INPUT); //인체감지 센서
  pinMode(flame, INPUT);  //화염감지 센서
  
  Serial.begin(115200);
  
  myDHT11.begin();

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}
void loop()
{
  human_value = digitalRead(sensor);  //인체감지 값
  flame_value = digitalRead(flame);   //화염감지 값
  
  digitalWrite(TRIG,HIGH);  //트리거 신호 on
  delayMicroseconds(10);    //대기,십만분의 1초
  digitalWrite(TRIG,LOW);   //트리거 신호 off
  long distance = pulseIn(ECHO,HIGH)/58.2; //초음파 값 수치화

  int h = myDHT11.readHumidity(false);    //습도 값
  int t = myDHT11.readTemperature(false); //온도 값
  
  delay(500);
  
  if(distance >= 10) {
    if(Firebase.setInt(fbdo, "User/User_01/open", 1))
      {
        Serial.print("물체와의 거리 : ");
        Serial.println(distance);
      }
      
      digitalWrite(LED, 1); // LED ON
      tone(speaker, 262, 20); //speaker ON
    
  }

  Serial.println("======================================");
  if(distance < 10) {
    if(Firebase.setInt(fbdo, "User/User_01/open", 0))
      {
        Serial.print("물체와의 거리 : ");
        Serial.println(distance);
      }
      digitalWrite(LED, 0); // LED전구 OFF
  }

  if(human_value == HIGH) {
    if(Firebase.setInt(fbdo, "User/User_01/human", 1))
      {
        Serial.print("인체 감지 센서 on(1)/off(0) : ");
        Serial.println(human_value);
      }
  }
  
  if(human_value == LOW) {
    if(Firebase.setInt(fbdo, "User/User_01/human", 0))
      {
        Serial.print("인체 감지 센서 on(1)/off(0) : ");
        Serial.println(human_value);
      }
  }

  if(flame_value == HIGH) {
    if(Firebase.setInt(fbdo, "User/User_01/flame", 0))
      {
        Serial.print("화염 미감지 중 on(1)/off(0) : ");
        Serial.println(flame_value);
      }
  }
  
  if(flame_value == LOW) {
    if(Firebase.setInt(fbdo, "User/User_01/flame", 1))
      {
        Serial.print("화염 감지! on(1)/off(0) : ");
        Serial.println(flame_value);
      }
  }

    if(Firebase.setInt(fbdo, "User/User_01/temp", t))
      {
        Serial.print("현재 온도 : ");
        Serial.print(t);
        Serial.println("°C");
      }
      
    if(Firebase.setInt(fbdo, "User/User_01/humi", h))
      {
        Serial.print("현재 습도 : ");
        Serial.print(h);
        Serial.println("%");
      }
      
  Serial.println("======================================");
}
