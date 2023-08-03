from paho.mqtt import client as mqtt_client
import random
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from PIL import ImageColor, Image
import base64
import os
import dearpygui.dearpygui as dpg # dearpygui my beloved...
import math

broker = 'broker.emqx.io'
port = 1883
topic = "1.20 settingsmodv0.1"
# generate client ID with pub prefix randomly
client_id = f'python-mqtt-{random.randint(0, 1000)}'
username = 'Actor'+str(random.randint(0, 100000))
password = str(random.randint(0, 100000))

dpg.create_context()
dpg.create_viewport(title='SettingsMenu', width=920, height=350)

#we love great themes, right?
with dpg.font_registry():
    with dpg.font("font.ttf", 20) as font1:
        dpg.add_font_range_hint(dpg.mvFontRangeHint_Cyrillic)
        dpg.add_char_remap(0x3084, 0x0025)
        dpg.bind_font(font1)
    with dpg.font("font.ttf", 34) as font2:
        dpg.add_font_range_hint(dpg.mvFontRangeHint_Cyrillic)
        dpg.add_char_remap(0x3084, 0x0025)
        dpg.bind_font(font1)
with dpg.theme() as global_theme:
    with dpg.theme_component(0):
        with open(os.path.abspath(os.getcwd())+"/theme",'r+') as f:
            lines = f.read()
            dpg.add_theme_color(dpg.mvThemeCol_WindowBg             , ImageColor.getcolor(lines.splitlines()[0], "RGB"))
            dpg.add_theme_color(dpg.mvThemeCol_ChildBg                   , ImageColor.getcolor(lines.splitlines()[1], "RGB"))
            dpg.add_theme_color(dpg.mvThemeCol_Border                   , ImageColor.getcolor(lines.splitlines()[2], "RGB"))
            dpg.add_theme_color(dpg.mvThemeCol_Button                    , ImageColor.getcolor(lines.splitlines()[3], "RGB"))
            dpg.add_theme_color(dpg.mvThemeCol_Text                        , ImageColor.getcolor(lines.splitlines()[4], "RGB"))
            dpg.add_theme_color(dpg.mvThemeCol_FrameBg                , ImageColor.getcolor(lines.splitlines()[5], "RGB"))
            dpg.add_theme_style(dpg.mvStyleVar_FrameRounding     , 5)

dpg.bind_theme(global_theme)
dpg.bind_theme(global_theme)

SECRET_KEY = b"" # will be edited later.
CIPHER_INSTANCE = AES.MODE_ECB

def encrypt(plain_text):
    cipher = AES.new(dpg.get_value("secret").encode(), CIPHER_INSTANCE)
    cipher_text = cipher.encrypt(pad(plain_text.encode(), AES.block_size))
    return base64.b64encode(cipher_text).decode()

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            pass
        else:
            print("Failed to connect, return code %d\n", rc)

#config the client
    client = mqtt_client.Client(client_id)
    client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client

#connect to mqtt
client = connect_mqtt()
client.loop_start()

#LEFTOVER CODE.

#while True:
    #encMessage = encrypt(input("new message: "))
    #print(encMessage)

    #result = client.publish(topic, encMessage)
def push_settings(sender, app_data, user_data):
    if(user_data[0] == "fov"):
        encMessage = encrypt(f"fov:{(dpg.get_value(sender) - 70)/40}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "mouseSensitivity"):
        encMessage = encrypt(f"mouseSensitivity:{dpg.get_value(sender)/200}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "autoJump"):
        user_data[1] = not user_data[1]
        dpg.configure_item(sender, label=f"Auto Jump ({user_data[1]})")
        encMessage = encrypt(f"autoJump:{user_data[1]}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "invertYMouse"):
        user_data[1] = not user_data[1]
        dpg.configure_item(sender, label=f"Invert Mouse ({user_data[1]})")
        encMessage = encrypt(f"invertYMouse:{user_data[1]}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "soundCategory_master"):
        encMessage = encrypt(f"soundCategory_master:{dpg.get_value(sender)/100}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "maxFps"):
        encMessage = encrypt(f"maxFps:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "renderDistance"):
        encMessage = encrypt(f"renderDistance:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "gamma"):
        encMessage = encrypt(f"gamma:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "fovEffectScale"):
        encMessage = encrypt(f"fovEffectScale:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "damageTiltStrength"):
        encMessage = encrypt(f"damageTiltStrength:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "entityDistanceScaling"):
        encMessage = encrypt(f"entityDistanceScaling:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "chatScale"):
        encMessage = encrypt(f"chatScale:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
    elif(user_data[0] == "key_key"):
        encMessage = encrypt(f"key_key.{user_data[1]}:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)

with dpg.window(label='SettingsMenu',tag="window") as window:
    dpg.add_text("            Welcome to messed up settings mod menu (rewrite)\n", tag="title")
    dpg.bind_item_font("title",font2)
    with dpg.child_window(label="boring stuff", height=75, autosize_x=True,parent=window) as child_window_boring:
        dpg.add_input_text(label='Secret Key', width=300,default_value="",password=True,tag="secret")
        dpg.add_text("(get it from the .minecraft/password.txt file generated by the mod)",color=[150,150,155])
    dpg.add_text("                                         Mess with settings!\n", tag="title2")
    with dpg.child_window(label="fun stuff", autosize_x=True,parent=window) as child_window_boring:
        dpg.add_input_int(label='Fov (breaks after 110)', width=300,default_value=100,tag="fov",callback=push_settings,user_data=["fov"])
        dpg.add_input_int(label='Mouse Sensitivity', width=300,default_value=100,tag="mouseSensitivity",callback=push_settings,user_data=["mouseSensitivity"])
        autojump = False
        dpg.add_button(label='Auto Jump (false)',tag="autoJump",callback=push_settings,user_data=["autoJump",autojump])
        invertYMouse = False
        dpg.add_button(label='Invert Mouse (false)',tag="invertYMouse",callback=push_settings,user_data=["invertYMouse",invertYMouse])
        dpg.add_input_int(label='Main audio volume', width=300,default_value=100,tag="soundCategory_master",callback=push_settings,user_data=["soundCategory_master"])
        dpg.add_input_int(label='Max fps', width=300,default_value=160,tag="maxFps",callback=push_settings,user_data=["maxFps"])
        dpg.add_input_int(label='Render Distance', width=300,default_value=12,tag="renderDistance",callback=push_settings,user_data=["renderDistance"])
        dpg.add_input_int(label='Entity Distance', width=300,default_value=100,tag="entityDistanceScaling",callback=push_settings,user_data=["entityDistanceScaling"])
        dpg.add_input_int(label='Gamma', width=300,default_value=50,tag="gamma",callback=push_settings,user_data=["gamma"])
        dpg.add_input_int(label='Fov Effect Scale', width=300,default_value=100,tag="fovEffectScale",callback=push_settings,user_data=["fovEffectScale"])
        dpg.add_input_int(label='Damage Tilt', width=300,default_value=100,tag="damageTiltStrength",callback=push_settings,user_data=["damageTiltStrength"])
        dpg.add_input_int(label='Text size', width=300,default_value=100,tag="chatScale",callback=push_settings,user_data=["chatScale"])
        keybings = ("key.keyboard.q","key.keyboard.w","key.keyboard.e","key.keyboard.r","key.keyboard.t","key.keyboard.y","key.keyboard.u","key.keyboard.i","key.keyboard.o","key.keyboard.p","key.keyboard.a","key.keyboard.s","key.keyboard.d","key.keyboard.f","key.keyboard.g","key.keyboard.h","key.keyboard.j","key.keyboard.k","key.keyboard.l","key.keyboard.z","key.keyboard.x","key.keyboard.c","key.keyboard.v","key.keyboard.b","key.keyboard.n","key.keyboard.m","key.keyboard.shift","key.keyboard.control","key.keyboard.space","key.mouse.left","key.mouse.right")
        dpg.add_combo(label="Rebind walking forward",width=300,tag="forwardCombo",items=keybings,callback=push_settings,user_data=["key_key","forward"])
        dpg.add_combo(label="Rebind walking backward",width=300,tag="backCombo",items=keybings,callback=push_settings,user_data=["key_key","back"])
        dpg.add_combo(label="Rebind walking left",width=300,tag="leftCombo",items=keybings,callback=push_settings,user_data=["key_key","left"])
        dpg.add_combo(label="Rebind walking right",width=300,tag="rightCombo",items=keybings,callback=push_settings,user_data=["key_key","right"])
        dpg.add_combo(label="Rebind jump",width=300,tag="jumpCombo",items=keybings,callback=push_settings,user_data=["key_key","jump"])
        dpg.add_combo(label="Rebind sneak",width=300,tag="sneakCombo",items=keybings,callback=push_settings,user_data=["key_key","sneak"])
        dpg.add_combo(label="Rebind sprint",width=300,tag="sprintCombo",items=keybings,callback=push_settings,user_data=["key_key","sprint"])
        dpg.add_combo(label="Rebind drop",width=300,tag="dropCombo",items=keybings,callback=push_settings,user_data=["key_key","drop"])
        dpg.add_combo(label="Rebind inventory",width=300,tag="inventory",items=keybings,callback=push_settings,user_data=["key_key","inventory"])
        dpg.add_combo(label="Rebind chat",width=300,tag="chat",items=keybings,callback=push_settings,user_data=["key_key","chat"])
        dpg.add_combo(label="Rebind swapOffhand",width=300,tag="swapOffhand",items=keybings,callback=push_settings,user_data=["key_key","chaswapOffhandt"])
    dpg.bind_item_font("title2",font2)
dpg.set_primary_window("window",True)

dpg.setup_dearpygui()
dpg.show_viewport()
dpg.start_dearpygui()
dpg.destroy_context()
