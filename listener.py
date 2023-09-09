from paho.mqtt import client as mqtt_client
import threading
import random

broker = 'broker.emqx.io'
port = 1883
topic = "1.20settingsmodv0.1"

print(mqtt_client.MQTT_ERR_SUCCESS)

# generate client ID with pub prefix randomly
client_id = f'python-mqtt-{random.randint(0, 1000)}'
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        pass
    else:
        print("Failed to connect, return code %d\n", rc)
def on_message(client, userdata, msg):
    print(msg.payload.decode() + " - raw message")
        

#config the client
client = mqtt_client.Client(client_id)
client.on_connect = on_connect
client.on_message = on_message
client.connect(broker, port)

def send():
    while True:
        call = client.publish(topic, input("send: "))
        print(type(call))
        print(call[0])
        print(call)

threading.Thread(target=send).start()

client.loop_forever()