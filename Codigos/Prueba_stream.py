from time import sleep
from picamera  import PiCamera
from google.cloud import storage
from firebase import firebase

import os.path
import threading
import datetime
import RPi.GPIO as GPIO
import Adafruit_DHT
import json
import os 
from functools import partial



def ExportData():
    while True:
        start=firebase.get('Remote/STAR_STOP',None)
        while (start=="1"):
            start=firebase.get('Remote/STAR_STOP',None)        
            humidity, temperature = Adafruit_DHT.read_retry(sensor, pin_Sensor)
            if humidity is not None and temperature is not None:        
                str_temp = '{0:0.2f}°C'.format(temperature)  
                str_hum  = '{0:0.2f}%'.format(humidity)
                print('Temp={0:0.1f}*C  Humidity={1:0.1f}%'.format(temperature, humidity))  
            else:
                print('Failed to get reading. Try again!')
            
            data = {"temp":str_temp,"Hum" :str_hum }
            firebase.patch('Remote',data)
            #firebase.put('' ,'Remote',data)
            print("datos subidos...")
            sleep(3)
    
def importData():
    activacion='1'
    while True:
        start=firebase.get('Remote/STAR_STOP',None)
        while (start=="1"):
            start=firebase.get('Remote/STAR_STOP',None)
            activacion=firebase.get('Remote/ON_OFF',None)
            if(activacion=='1'):
                GPIO.output(23,False)
            elif(activacion=='0'):
                GPIO.output(23,True)
            sleep(1)

def exportImage():
    i=0
    start=firebase.get('Remote/STAR_STOP',None)
    for filename in camera.capture_continuous('/home/pi/Scripts/Imagenes/img{counter:03d}.jpg',resize=(240,135)):
        i=i+1
        print('capture %s'% filename)
        imagePath = filename
        if(i==1): 
            imageBlob = bucket.blob('CurrentImage_1')
        elif(i==2):
            imageBlob = bucket.blob('CurrentImage_2')
        elif(i==3):
            imageBlob = bucket.blob('CurrentImage_3')
            i=0    
        imageBlob.upload_from_filename(imagePath)
        print('se ha subido una Imagen')
        sleep(1)

def servoControl():
    LAP=1
    while True:
        ciclo=0
        start=firebase.get('Remote/STAR_STOP',None)
        while (start=="1"):
            start=firebase.get('Remote/STAR_STOP',None)
            Direction= firebase.get('Remote/IZQ_DER',None)
            print(Direction)
            Sequence= firebase.get('Remote/MAN_AUT',None)
            print('Secuencia: '+ Sequence)
            if (Sequence== '1'):
                if(LAP==1):
                    for posicion in range(1,11,1):
                        if (Sequence=='1'and start=='1'):
                            Sequence= firebase.get('Remote/MAN_AUT',None)
                            start=firebase.get('Remote/STAR_STOP',None)
                            print('SecuenciaP: '+ Sequence)
                            ciclo=posicion
                            SerCam.ChangeDutyCycle(ciclo)
                            print('posicion:'+str(ciclo))    #Enviamos un pulso del 4.5% para girar el servo hacia la izquierda
                            sleep(3)           #pausa de medio segundo
                            if(posicion==10):
                                LAP=2
                        else:
                            posicion=20
            #Detenemos el servo
                elif(LAP==2):
                    for posicion in range(11,0,-1):
                        if (Sequence=='1'and start=='1'):
                            Sequence= firebase.get('Remote/MAN_AUT',None)
                            start=firebase.get('Remote/STAR_STOP',None)
                            print('Secuenciavuelta: '+ Sequence)
                            ciclo=posicion
                            SerCam.ChangeDutyCycle(ciclo)
                            print('posicion:'+str(ciclo))    #Enviamos un pulso del 4.5% para girar el servo hacia la izquierda
                            sleep(3)           #pausa de medio segundo
                            if(posicion==10):
                                LAP=1
                    else:
                        posicion=20
                        
            elif (Sequence=='0'):
                SerCam.ChangeDutyCycle(0)
                if(Direction=='0'):
                    ciclo=ciclo+1
                    if (ciclo>10):
                        ciclo=10
                    SerCam.ChangeDutyCycle(ciclo)
                    dato={'IZQ_DER': '2'}
                    firebase.patch('Remote',dato)
                    Direction='2'
                elif(Direction =='1') :
                    ciclo=ciclo-1
                    if(ciclo<0):
                        ciclo=0
                    SerCam.ChangeDutyCycle(ciclo)
                    dato={'IZQ_DER': '2'}
                    firebase.patch('Remote',dato)
                    Direction='2'

def upDownControl():
    while True:
        i=0
        ciclo_2=0
        SerCam_2.ChangeDutyCycle(0)
        start=firebase.get('Remote/STAR_STOP',None)
        while (start=="1"):
            start=firebase.get('Remote/STAR_STOP',None)
            
            UpDown= firebase.get('Remote/UP_DOW',None)
            if(UpDown=='1'):
                ciclo_2=ciclo_2 + 1
                if (ciclo_2>4):
                    ciclo_2=4
                SerCam_2.ChangeDutyCycle(ciclo_2)
                print("Up_DOWN:"+str(ciclo_2))
                dato_2={'UP_DOW': '2'}
                firebase.patch('Remote',dato_2)
                UpDown='2'
            elif(UpDown=='0') :
                ciclo_2=ciclo_2 - 1
                if(ciclo_2<0):
                    ciclo_2=0
                SerCam_2.ChangeDutyCycle(ciclo_2)
                print("Up_DOWN:"+str(ciclo_2))
                dato_2={'UP_DOW': '2'}
                firebase.patch('Remote',dato_2)
                UpDown='2'
#Instancio el objeto de la libreŕia piCamera para el uso de la misma
camera= PiCamera()
camera.vflip=True
#Configuro las credenciales y el acceso a la base de datos en firebase en particular
os.environ["GOOGLE_APPLICATION_CREDENTIALS"]='/home/pi/Scripts/credencial.json'
firebase = firebase.FirebaseApplication('https://iot-prueba-8d2f8.firebaseio.com/')
client = storage.Client()
bucket = client.get_bucket('iot-prueba-8d2f8.appspot.com')
imageBlob = bucket.blob("/")

sleep(0.5)

#Configuramos la RPI en modo board In-Outs
GPIO.setmode(GPIO.BCM)   #Ponemos la Raspberry en modo BOARd
GPIO.cleanup() 
GPIO.setwarnings(False)
#Confiramos el Servo 
GPIO.setup(17,GPIO.OUT)    #Ponemos el pin 17 como salida
SerCam = GPIO.PWM(17,50)  #Ponemos el pin 21 en modo PWM y enviamos 50 PUL/SEG
SerCam.start(0)

GPIO.setup(24 ,GPIO.OUT)    #Ponemos el pin 17 como salida
SerCam_2 = GPIO.PWM(24,50)  #Ponemos el pin 21 en modo PWM y enviamos 50 PUL/SEG
SerCam_2.start(0)

GPIO.setup(23 ,GPIO.OUT)    #Ponemos el pin 23 como salida para la acivacion o no del equipo

#Configuramos el sensor
sensor = Adafruit_DHT.DHT11 #instanciamos el objeto de la librería
pin_Sensor = 21 #declaramos pin 21 para el uso del sensor
humidity, temperature = Adafruit_DHT.read_retry(sensor, pin_Sensor)

#Declaramos los Hilos y las funciones que ehecutan
Thr_ExpoImage=threading.Thread(target=exportImage)
Thr_Servo=threading.Thread(target=servoControl)
Thr_ImpData=threading.Thread(target=importData)
Thr_ExpoData= threading.Thread(target=ExportData)
Thr_Servo_2=threading.Thread(target=upDownControl)
Sequence ='1'
LAP=1
Thr_ExpoImage.start()
Thr_Servo.start()
Thr_ImpData.start()
Thr_ExpoData.start()
Thr_Servo_2.start()
