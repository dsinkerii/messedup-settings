# messedup-settings
https://youtu.be/qbOCx4pdUnQ

# ‼️ READ BEFORE INSTALLING ‼️

🔴 IF you are the one who is going to edit the settings, make sure to follow these steps:
1. make sure you have python installed, its a very important step.
2. run in your console this command
```bash
pip install paho.mqtt Crypto pillow dearpygui
```
3. then, after installing it, install the `actor.zip`, this contains all required files for the script.
4. go to the unzipped folder in your console by running
```bash
cd {REPLACE HERE THE PATH TO THE FOLDER}
```
(make sure to actually replace the path to the folder.)
5. run:
```bash
python host.py
```
6. if everything before was done correctly, a window should pop out. Make sure to enter the secret key given by the mod.

🔴 IF you are the one running the mod, allowing to change your settings, follow these steps:
1. make sure you have 1.20 fabric installed.
2. After launching the mod, go to the .minecraft folder, look for the `password.txt` file. There will be a bunch of numbers and letters, `its your secret key.`
3. this key allows anyone to edit your settings, so be careful on who you give this secret key to.
4. PS. your old `options.txt` file wont be recovered once you close minecraft, make sure to make a copy of it if you plan on playing with it with someone.

# UPDATE 3: After a hella lot of time, it works! so happy.

# how does it work
![what](https://github.com/dsinkerii/messedup-settings/assets/104655906/626da6e5-8ea3-47a4-ba86-8cf079f68bc8)

(very awesome drawing i made in krita at 3am)

In short, we have the host "victim" that will receive all messages from all clients "actors"

We use MQTT, due to the fact that using p2p may be not cool due to the fact your ip will be public for all "actors"

Also, here we use some basic encryption with encrypting all messsages using a simple password

(note, i know NOTHING about cryptology, feel free to open a pull request in case you know how to make it better)




![wow](https://media.discordapp.net/attachments/1065674628636344420/1123644736922734632/makesweet-3nxz9e.gif?width=440&height=330)
