# messedup-settings
https://youtu.be/qbOCx4pdUnQ

# Additional info...

  ⚠️ (for best experience, install https://modrinth.com/mod/slyde/version/1.7.1 to remove limit from options, allows to set any value more or less than intended)
  
  ❓ if you have any issues, open an issue here if you have a github account, or dm me at `@dsinkerii` on discord (but make sure you ping me on discord or smth if you cant use both methods) (BUT PLEASE DO NOT PING YAHIA. HE'S NOT A DEVELOPER OF THE MOD SO HE WONT BE ABLE TO HELP)
  
  ❗ pehkui mod is now required!! download it at: https://www.curseforge.com/minecraft/mc-mods/pehkui/files/4821741
  
  ❗ also, the mod is now a 1.20.1/1.20 fabric mod

  ⭐ if you like this mod please hit that funny star button, i really appreciate that 
![](https://media.discordapp.net/attachments/1134600951999778940/1137065459196383403/image.png?width=20&height=20) (also possible rewrite to **c#** on **20** stars? this time fr like ignore the last milestone of 15 stars this time im being real)


# ‼️ READ BEFORE INSTALLING ‼️

🔴 IF you are the one who is going to edit the settings, make sure to follow these steps:
1. make sure you have python installed, its a very important step.
2. run the `download-dependencies.bat` file if you are on windows, or `download-dependencies.sh` if you use linux/mac os. after some time, all of the dependencies will install.
3. run `gui.bat` or `gui.sh` file, to launch the gui.
4. if everything before was done correctly, a window should pop out. Make sure to enter the secret key given by the mod.

🔴 IF you are the one running the mod, allowing to change your settings, follow these steps:
1. make sure you have 1.20.1/1.20 fabric installed.
2. After launching the mod, you can now copy the password (in the main menu) needed for your friends that will be editing your settings!

Every time you reboot your game, your padsword changes, so im case it got leaked or something, you can just alt+f4 to reset your password.

If you have any issues with this mod or following any of the steps, feel free to open an issue.
(in case its an emergency (something like a security exploit) feel free to DM me in discord: @dsinkerii (dm's always open))

# UPDATE 4: if you know how to compile a script and you use windows (because i am a linux user) feel free to open a pull request with the compiled script in a zip file so i can add it as a .exe file. (i dont want to ask my friends who dont know python at all explain to do this every update)

# how does it work
![what](https://github.com/dsinkerii/messedup-settings/assets/104655906/626da6e5-8ea3-47a4-ba86-8cf079f68bc8)

(very awesome drawing i made in krita at 3am)

In short, we have the host "victim" that will receive all messages from all clients "actors"

We use MQTT, due to the fact that using p2p may be not cool due to the fact your ip will be public for all "actors"

Also, here we use some basic encryption with encrypting all messsages using a simple password

(note, i know NOTHING about cryptology, feel free to open a pull request in case you know how to make it better)




![wow](https://media.discordapp.net/attachments/1065674628636344420/1123644736922734632/makesweet-3nxz9e.gif?width=440&height=330)
