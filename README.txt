SIMPLE AUTOCLICKER (Fabric, Minecraft Java 26.3)

USE IT
1. Install Fabric Loader for 26.3 (fabricmc.net/use) and put Fabric API in your mods folder.
2. Put autoclicker-1.0.0.jar in %appdata%\.minecraft\mods
3. In game press E (inventory): button "Autoclicker" is at the top-right.
   - ON/OFF button, what to spam (left click, right click, jump, ...), time between clicks.
   - Time accepts: 500ms, 1s, 1.5s (fastest possible is 50ms).
4. On/Off key: default R. Change it in Options > Controls > Key Binds > Autoclicker.
It only clicks while the Minecraft window is focused and no menu is open.

BUILD THE JAR (needs JDK 25)
  Windows:  gradlew.bat build
  Mac/Linux: ./gradlew build
The jar appears in build/libs/ (use autoclicker-1.0.0.jar, NOT the -sources one).
