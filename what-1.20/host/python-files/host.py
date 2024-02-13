from paho.mqtt import client as mqtt_client
import random
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from PIL import ImageColor, Image
import base64
import os
import dearpygui.dearpygui as dpg # dearpygui my beloved...
import math

broker = 'mqtt.eclipseprojects.io'
port = 1883
topic = "1.20settingsmodv1.1"
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

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        pass
    else:
        print("Failed to connect, return code %d\n", rc)
#connect to mqtt
client = mqtt_client.Client(client_id)
client.on_connect = on_connect
client.connect(broker, port)
client.loop_start()

#LEFTOVER CODE.

#while True:
    #encMessage = encrypt(input("new message: "))
    #print(encMessage)

    #result = client.publish(topic, encMessage)
def push_settings(sender, app_data, user_data):
    print("attempting to send \"" + str(user_data) + "\"...", end="")
    if(user_data[0] == "fov"):
        encMessage = encrypt(f"fov:{(dpg.get_value(sender) - 70)/40}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "mouseSensitivity"):
        encMessage = encrypt(f"mouseSensitivity:{dpg.get_value(sender)/200}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "autoJump"):
        user_data[1] = not user_data[1]
        dpg.configure_item(sender, label=f"Auto Jump ({user_data[1]})")
        encMessage = encrypt(f"autoJump:{user_data[1]}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "invertYMouse"):
        user_data[1] = not user_data[1]
        dpg.configure_item(sender, label=f"Invert Mouse ({user_data[1]})")
        encMessage = encrypt(f"invertYMouse:{user_data[1]}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "soundCategory_master"):
        encMessage = encrypt(f"soundCategory_master:{dpg.get_value(sender)/100}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "maxFps"):
        encMessage = encrypt(f"maxFps:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "renderDistance"):
        encMessage = encrypt(f"renderDistance:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "gamma"):
        encMessage = encrypt(f"gamma:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "fovEffectScale"):
        encMessage = encrypt(f"fovEffectScale:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "damageTiltStrength"):
        encMessage = encrypt(f"damageTiltStrength:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "entityDistanceScaling"):
        encMessage = encrypt(f"entityDistanceScaling:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "chatScale"):
        encMessage = encrypt(f"chatScale:{dpg.get_value(sender)/100.0}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "key_key"):
        encMessage = encrypt(f"key_key.{user_data[1]}:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "lang"):
        encMessage = encrypt(f"lang:{dpg.get_value(sender).split(' ')[0]}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "guiScale"):
        encMessage = encrypt(f"guiScale:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::base"):
        encMessage = encrypt(f"pehkui::base:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::width"):
        encMessage = encrypt(f"pehkui::width:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::height"):
        encMessage = encrypt(f"pehkui::height:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::eye_height"):
        encMessage = encrypt(f"pehkui::eye_height:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::hitbox_width"):
        encMessage = encrypt(f"pehkui::hitbox_width:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::hitbox_height"):
        encMessage = encrypt(f"pehkui::hitbox_height:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::step_height"):
        encMessage = encrypt(f"pehkui::step_height:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::jump_height"):
        encMessage = encrypt(f"pehkui::jump_height:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::reach"):
        encMessage = encrypt(f"pehkui::reach:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::block_reach"):
        encMessage = encrypt(f"pehkui::block_reach:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::entity_reach"):
        encMessage = encrypt(f"pehkui::entity_reach:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::mining_speed"):
        encMessage = encrypt(f"pehkui::mining_speed:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::attack_speed"):
        encMessage = encrypt(f"pehkui::attack_speed:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::knockback"):
        encMessage = encrypt(f"pehkui::knockback:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::attack"):
        encMessage = encrypt(f"pehkui::attack:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::defense"):
        encMessage = encrypt(f"pehkui::defense:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)
    elif(user_data[0] == "pehkui::health"):
        encMessage = encrypt(f"pehkui::health:{dpg.get_value(sender)}")
        result = client.publish(topic, encMessage)
        print(result)

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
        lang = ("af_za (Afrikaans)","ar_sa (Arabic)", "ast_es (Asturian)", "az_az (Azerbaijani)",
        "ba_ru (Bashkir)", "bar (Bavarian)", "be_by (Belarusian)", "bg_bg (Bulgarian)", "br_fr (Breton)", "brb (Brabantian)",
        "bs_ba (Bosnian)", "ca_es (Catalan)", "cs_cz (Czech)","cy_gb (Welsh)", "da_dk (Danish)", "de_at (Austrian German)", 
        "de_ch (Swiss German)", "de_de (German)", "el_gr (Greek)", "en_au (Australian English)", "en_ca (Canadian English)",
        "en_gb (Bri'ish English)", "en_nz (New Zealand English)", "en_pt (Ahoy english)", "en_ud (True australian english)",
        "en_us (American English)", "enp (Modern English minus borrowed words)", "enws (Early Modern English)", "eo_uy (Esperanto)",
        "es_ar (Argentinian Spanish)", "es_cl (Chilean Spanish)", "es_ec (Ecuadorian Spanish)", "es_es (European Spanish)",
        "es_mx (Mexican Spanish)", "es_uy (Uruguayan Spanish)", "es_ve (Venezuelan Spanish)", "esan (Andalusian)",
        "et_ee (Estonian)", "eu_es (Basque)", "fa_ir (Persian)", "fi_fi (Finnish)", "fil_ph (Filipino)", "fo_fo (Faroese)",
        "fr_ca (Canadian French)", "fr_fr (AAA its french)", "fra_de (Franconian)", "fur_it (Friulian)", "fy_nl (Frisian)",
        "ga_ie (Irish)", "gd_gb (SCOTLAND FOREVER)", "gl_es (Galician)", "haw_us (Hawaiian)", "he_il (Hebrew)",
        "hi_in (Hindi)", "hr_hr (Croatian)", "hu_hu (Hungarian)", "hy_am (Armenian)", "id_id (Indonesian)", "ig_ng (Igbo)",
        "ig_ng (Igbo)", "io_en (Ido)", "is_is (Icelandic)", "isv (Interslavic)", "it_it (Italian)", "ja_jp (japanese)",
        "jbo_en (Lojban)", "ka_ge (Georgian)", "kk_kz (Kazakh)", "kn_in (Kannada)", "ko_kr (Korean)", "ksh (Kölsch/Ripuarian)",
        "kw_gb (Cornish)", "la_la (Latin)", "lb_lu (Luxembourgish)", "li_li (Limburgish)", "lmo (Lombard)", "lol_us (yahiacord language (lolcat))",
        "lt_lt (Lithuanian)", "lv_lv (Latvian)", "lzh (Classical Chinese)", "mk_mk (Macedonian)", "mn_mn (Mongolian)", "ms_my (Malay)",
        "mt_mt (Maltese)", "nah (Nahuatl)", "nds_de (Low German)", "nl_be (Dutch, Flemish)", "nl_nl (Dutch)", "nn_no (Norwegian Nynorsk)",
        "no_no‌ (Norwegian Bokmål)", "oc_fr (Occitan)", "ovd (Elfdalian)", "pl_pl (polish (oh wow))", "pt_br (Brazilian Portuguese)", "pt_pt (European Portuguese)",
        "qya_aa (Quenya (Form of Elvish from LOTR))", "ro_ro (Romanian)", "rpr (Russian (Pre-revolutionary))", "ru_ru (ok)",
        "ry_ua (Rusyn)", "se_no (Northern Sami)", "sk_sk (Slovak)", "sl_si (Slovenian)", "so_so (Somali)", "sq_al (Albanian)",
        "sr_sp (Serbian (Cyrillic))", "sv_se (Swedish)", "sxu (Upper Saxon German)", "szl (Silesian)", "ta_in (Tamil)", "th_th (Thai)", "tl_ph (Tagalog)",
        "tlh_aa (Klingon)", "tok (tik (Toki Pona))", "tr_tr (Turkish)", "tt_ru (Tatar)", "uk_ua (Ukrainian (hell yea))", "val_es (Valencian)",
        "vec_it (Venetian)", "vi_vn (Vietnamese)", "yi_de (Yiddish)", "yo_ng (Yoruba)", "zh_cn (Simplified (China; Mandarin))", "zh_hk (Chinese Traditional (Hong Kong; Mix))",
        "zh_tw (Chinese Traditional (Taiwan; Mandarin))", "zlm_arab (Malay (Jawi))")# impossible challenge: try to find the 1 missing language here :)
        dpg.add_combo(label="Switch language",width=300,tag="language",items=lang,callback=push_settings,user_data=["lang"])
        dpg.add_input_int(label='Set gui scale', width=300,default_value=2,tag="guiScale",callback=push_settings,user_data=["guiScale"])
        dpg.add_text("Pehkui stuff!\n", tag="title3")
        dpg.add_input_float(label='Base Scale', width=300,default_value=1.0,tag="pehkui::base",callback=push_settings,user_data=["pehkui::base"])
        dpg.add_input_float(label='Width', width=300,default_value=1.0,tag="pehkui::width",callback=push_settings,user_data=["pehkui::width"])
        dpg.add_input_float(label='Height', width=300,default_value=1.0,tag="pehkui::height",callback=push_settings,user_data=["pehkui::height"])
        dpg.add_input_float(label='Eye height', width=300,default_value=1.0,tag="pehkui::eye_height",callback=push_settings,user_data=["pehkui::eye_height"])
        dpg.add_input_float(label='Hitbox width', width=300,default_value=1.0,tag="pehkui::hitbox_width",callback=push_settings,user_data=["pehkui::hitbox_width"])
        dpg.add_input_float(label='Hitbox height', width=300,default_value=1.0,tag="pehkui::hitbox_height",callback=push_settings,user_data=["pehkui::hitbox_height"])
        dpg.add_input_float(label='Jump height', width=300,default_value=1.0,tag="pehkui::jump_height",callback=push_settings,user_data=["pehkui::jump_height"])
        dpg.add_input_float(label='Step height', width=300,default_value=1.0,tag="pehkui::step_height",callback=push_settings,user_data=["pehkui::step_height"])
        dpg.add_input_float(label='Reach', width=300,default_value=1.0,tag="pehkui::reach",callback=push_settings,user_data=["pehkui::reach"])
        dpg.add_input_float(label='Block reach', width=300,default_value=1.0,tag="pehkui::block_reach",callback=push_settings,user_data=["pehkui::block_reach"])
        dpg.add_input_float(label='Entity reach', width=300,default_value=1.0,tag="pehkui::entity_reach",callback=push_settings,user_data=["pehkui::entity_reach"])
        dpg.add_input_float(label='Mining speed', width=300,default_value=1.0,tag="pehkui::mining_speed",callback=push_settings,user_data=["pehkui::mining_speed"])
        dpg.add_input_float(label='Knockback', width=300,default_value=1.0,tag="pehkui::knockback",callback=push_settings,user_data=["pehkui::knockback"])
        dpg.add_input_float(label='Damage multiplier', width=300,default_value=1.0,tag="pehkui::attack",callback=push_settings,user_data=["pehkui::attack"])
        dpg.add_input_float(label='Defense multiplier', width=300,default_value=1.0,tag="pehkui::defense",callback=push_settings,user_data=["pehkui::defense"])
        dpg.add_input_float(label='Health', width=300,default_value=1.0,tag="pehkui::health",callback=push_settings,user_data=["pehkui::health"])
    dpg.bind_item_font("title2",font2)
dpg.set_primary_window("window",True)

dpg.setup_dearpygui()
dpg.show_viewport()
dpg.start_dearpygui()
dpg.destroy_context()
