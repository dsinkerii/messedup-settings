# messedup-settings
https://youtu.be/qbOCx4pdUnQ

# Additional info...

  ⚠️ (for best experience, install https://git.frohnmeyer-wds.de/JfMods/Slyde to remove limit from options, allows to set any value more or less than intended)
  
  ❓ if you have any issues, open an issue here if you have a github account, or dm me at `@dsinkerii` on discord (but make sure you ping me on discord or smth if you cant use both methods)
  
  :trollface: also pehkui support on 10 stars lol (UPD: damn this was fast lol, wip)


# ‼️ READ BEFORE INSTALLING ‼️

🔴 IF you are the one who is going to edit the settings, make sure to follow these steps:
1. make sure you have python installed, its a very important step.
2. run in your console this command
```bash
python -m pip install paho.mqtt pycryptodome pillow dearpygui 
```
3. then, after installing it, install the `host.zip (or actor.zip in older versions)`, this contains all required files for the script.
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

If you have any issues with this mod or following any of the steps, feel free to open an issue.
(in case its an emergency (something like a security exploit) feel free to DM me in discord: @dsinkerii (dm's always open))

⭐ if you like this mod please hit that funny star button, i really appreciate that 
![](https://media.discordapp.net/attachments/1134600951999778940/1137065459196383403/image.png?width=20&height=20)

# UPDATE 4: if you know how to compile a script and you use windows (because i am a linux user) feel free to open a pull request with the compiled script in a zip file so i can add it as a .exe file. (i dont want to ask my friends who dont know python at all explain to do this every update)

# how does it work
![what](https://github.com/dsinkerii/messedup-settings/assets/104655906/626da6e5-8ea3-47a4-ba86-8cf079f68bc8)

(very awesome drawing i made in krita at 3am)

In short, we have the host "victim" that will receive all messages from all clients "actors"

We use MQTT, due to the fact that using p2p may be not cool due to the fact your ip will be public for all "actors"

Also, here we use some basic encryption with encrypting all messsages using a simple password

(note, i know NOTHING about cryptology, feel free to open a pull request in case you know how to make it better)




![wow](https://media.discordapp.net/attachments/1065674628636344420/1123644736922734632/makesweet-3nxz9e.gif?width=440&height=330)
