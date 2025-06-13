# messedup-settings
https://youtu.be/qbOCx4pdUnQ

# update 8:
  i matured

  also i unarchived the repo and plan on fixing a bunch of old repos i made over the years
  
# ‼️ READ BEFORE INSTALLING ‼️

## [Here's](https://github.com/dsinkerii/messedup-settings/releases/latest) the latest version of the mod if you dont know how to use github

🔴 IF you are the one who is going to edit the settings, make sure to follow these steps:
1. install [dotnet](https://dotnet.microsoft.com/en-us/download) (if u haven't already)
2. download an executable file according to your os (e.g. for windows it's `host.exe`)
3. paste in your password and change your nickname if you want to, and enjoy!

🔴 IF you are the one running the mod, allowing to change your settings, follow these steps:
1. make sure you have 1.21 fabric installed.
2. install [pehkui](https://www.curseforge.com/minecraft/mc-mods/pehkui/files/4821741) (additionally [slyde](https://modrinth.com/mod/slyde/version/1.7.1) and [libjf](https://modrinth.com/mod/libjf/version/3.10.0) to remove limits from options)
3. after launching the mod, you can now copy the password (in the main menu) needed for your friends that will be editing your settings!

every time you reboot your game, your password changes, so in case it gets leaked or lost, you can just close the game to reset your password.

**if** you have any issues with this mod or following any of the steps, feel free to open an issue.
(in case its an emergency (something like a security exploit) feel free to DM me in discord: @dsinkerii (dms always open!))

# ⚠️ Additional info... (ALSO IMPORTANT)⚠️

  ⚠️ (for best experience, install [slyde](https://modrinth.com/mod/slyde/version/1.7.1) and [libjf](https://modrinth.com/mod/libjf/version/3.10.0) to remove limit from options, allows to set any value more or less than intended)
  
  ❗ pehkui mod is now required!! download it at: https://www.curseforge.com/minecraft/mc-mods/pehkui/files/4821741
  
  ❗ also, the mod is now a 1.2**1**.x fabric mod

  ⭐ if you like this mod please hit that funny star button, i really appreciate that 

# ❓ it doesn't work/crashes!!
### if your game doesn't launch, please try:
- launching the game using a different launcher (prism launcher or the default launcher work the best)
- checking if you have pehkui installed, it's **important**
- setting the game's version to be on 1.21
### if you can't connect to the server (both in-game and in-app), try:
- setting the server to be different:
    in minecraft, set the server to be either `tcp://broker.hivemq.com:1883` or `tcp://eclipseprojects.io:1883` and hit refresh (others work too)
    in the app, set the server according to the one picked in minecraft, but without `tcp://` and `:1883` (e.g. `broker.hivemq.com`, `eclipseprojects.io`)
- checking your firewall, you most likely have ports 1883/8883 blocked
### if you can't send requests to the mod through the app (or the other way around), try:
- checking if you have the same server/password set
- check if you're on the same version, latest is [1.4](https://github.com/dsinkerii/messedup-settings/releases/latest)

### if all fails, shoot me a dm on discord at @dsinkerii, and attach the logs from the app (found by clicking on the clipboard next to the title), and from minecraft (found at `.minecraft/logs/latest.log`)


# how does it work
![what](https://github.com/dsinkerii/messedup-settings/assets/104655906/626da6e5-8ea3-47a4-ba86-8cf079f68bc8)

(very awesome drawing i made in krita at 3am)

In short, we have the host "victim" that will receive all messages from all clients "actors"

We use MQTT, due to the fact that using p2p may be not cool due to the fact your ip will be public for all "actors"

Also, here we use some basic encryption with encrypting all messsages using a simple password

(note, i know NOTHING about cryptology, feel free to open a pull request in case you know how to make it better)



![ezgif com-optimize (8)](https://github.com/user-attachments/assets/927cbc79-5c37-4293-b458-5ad8ce30754a)
