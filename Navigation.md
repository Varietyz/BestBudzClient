```
├─ 📄 BestBudz.xml
├─ 📄 BestBudzRSPS.exe
├─ 📄 build.gradle
├─ 📄 gradlew
├─ 📄 gradlew.bat
├─ 📝 Navigation.md
├─ 🐍 save_structure.py
├─ 📄 settings.gradle
└─ 📂 src
    ├─ 📂 libs
    │   └─ 📄 substance-5.3.jar
    └─ 📂 main
        ├─ 📂 java
        │   └─ 📂 com
        │       └─ 📂 bestbudz
        │           ├─ 📂 cache
        │           │   └─ 📄 Signlink.java
        │           ├─ 📂 client
        │           │   ├─ 📂 frame
        │           │   │   └─ 📄 UIDockFrame.java
        │           │   ├─ 📂 ui
        │           │   │   ├─ 📂 manager
        │           │   │   │   └─ 📄 UIPanelManager.java
        │           │   │   └─ 📂 panel
        │           │   │       ├─ 📄 AchievementsPanel.java
        │           │   │       ├─ 📄 InventoryPanel.java
        │           │   │       ├─ 📄 QuestTabPanel.java
        │           │   │       ├─ 📄 SettingsPanel.java
        │           │   │       └─ 📄 UIPanel.java
        │           │   └─ 📂 util
        │           │       └─ 📄 DockSync.java
        │           ├─ 📂 config
        │           │   ├─ 📄 ClientConstants.java
        │           │   ├─ 📄 ColorConstants.java
        │           │   ├─ 📄 Configuration.java
        │           │   └─ 📄 SettingHandler.java
        │           ├─ 📂 data
        │           │   ├─ 📄 AccountData.java
        │           │   ├─ 📄 AccountManager.java
        │           │   ├─ 📄 Item.java
        │           │   ├─ 📄 ItemDef.java
        │           │   └─ 📄 Skills.java
        │           ├─ 📂 engine
        │           │   ├─ 📄 Client.java
        │           │   ├─ 📄 ClientEngine.java
        │           │   ├─ 📄 ClientLauncher.java
        │           │   ├─ 📄 GameCanvas.java
        │           │   ├─ 📄 GameEngine.java
        │           │   ├─ 📄 GraphicsConfig.java
        │           │   ├─ 📂 input
        │           │   │   ├─ 📄 Keyboard.java
        │           │   │   ├─ 📄 MouseManager.java
        │           │   │   ├─ 📄 MouseScrollHandler.java
        │           │   │   └─ 📄 MouseState.java
        │           │   └─ 📄 LoginRenderer.java
        │           ├─ 📂 entity
        │           │   ├─ 📄 Entity.java
        │           │   ├─ 📄 EntityDef.java
        │           │   ├─ 📄 IdentityKit.java
        │           │   ├─ 📄 IdentityResolver.java
        │           │   ├─ 📄 Npc.java
        │           │   └─ 📄 Stoner.java
        │           ├─ 📂 graphics
        │           │   ├─ 📄 Background.java
        │           │   ├─ 📂 buffer
        │           │   │   └─ 📄 ImageProducer.java
        │           │   ├─ 📄 BufferedImage.java
        │           │   ├─ 📄 DrawingArea.java
        │           │   ├─ 📄 FogHandler.java
        │           │   ├─ 📄 FogUtil.java
        │           │   ├─ 📄 rsDrawingArea.java
        │           │   ├─ 📂 sprite
        │           │   │   ├─ 📄 Sprite.java
        │           │   │   └─ 📄 SpriteLoader.java
        │           │   ├─ 📂 text
        │           │   │   ├─ 📄 RSFont.java
        │           │   │   └─ 📄 TextDrawingArea.java
        │           │   └─ 📄 Texture.java
        │           ├─ 📂 network
        │           │   ├─ 📄 OnDemandData.java
        │           │   ├─ 📄 OnDemandFetcher.java
        │           │   ├─ 📄 OnDemandFetcherParent.java
        │           │   ├─ 📄 RSSocket.java
        │           │   ├─ 📄 Stream.java
        │           │   └─ 📄 StreamLoader.java
        │           ├─ 📂 rendering
        │           │   ├─ 📄 Animable.java
        │           │   ├─ 📄 Animable_Sub3.java
        │           │   ├─ 📄 Animable_Sub4.java
        │           │   ├─ 📄 Animable_Sub5.java
        │           │   ├─ 📂 animation
        │           │   │   ├─ 📄 Animation.java
        │           │   │   ├─ 📄 Class18.java
        │           │   │   ├─ 📄 Class29.java
        │           │   │   └─ 📄 Class40.java
        │           │   ├─ 📂 model
        │           │   │   ├─ 📄 Class33.java
        │           │   │   ├─ 📄 Class43.java
        │           │   │   ├─ 📄 Class47.java
        │           │   │   └─ 📄 Model.java
        │           │   ├─ 📄 OverlayFloor.java
        │           │   ├─ 📄 Rasterizer.java
        │           │   ├─ 📄 SequenceFrame.java
        │           │   └─ 📄 SpotAnim.java
        │           ├─ 📂 sound
        │           │   ├─ 📄 Sounds.java
        │           │   └─ 📂 synthesis
        │           │       ├─ 📄 Class39.java
        │           │       └─ 📄 Class6.java
        │           ├─ 📂 ui
        │           │   ├─ 📄 Console.java
        │           │   ├─ 📂 interfaces
        │           │   │   ├─ 📄 Bank.java
        │           │   │   ├─ 📄 BongBase.java
        │           │   │   ├─ 📄 Chatbox.java
        │           │   │   ├─ 📄 CustomInterfaces.java
        │           │   │   ├─ 📄 EquipmentTab.java
        │           │   │   ├─ 📂 minigames
        │           │   │   │   ├─ 📄 PestControl.java
        │           │   │   │   └─ 📄 WarriorGuild.java
        │           │   │   ├─ 📄 OptionsTab.java
        │           │   │   ├─ 📄 Prestiging.java
        │           │   │   ├─ 📄 QuickPrayers.java
        │           │   │   ├─ 📄 Shop.java
        │           │   │   ├─ 📄 Starter.java
        │           │   │   ├─ 📄 StatusOrbs.java
        │           │   │   └─ 📂 teleports
        │           │   │       ├─ 📄 BossTeleports.java
        │           │   │       ├─ 📄 MinigameTeleports.java
        │           │   │       ├─ 📄 OtherTeleports.java
        │           │   │       ├─ 📄 PvpTeleports.java
        │           │   │       ├─ 📄 SkillingTeleports.java
        │           │   │       └─ 📄 TrainingTeleports.java
        │           │   ├─ 📄 MouseDetection.bak
        │           │   ├─ 📄 PopupMenu.java
        │           │   ├─ 📄 RSInterface.java
        │           │   ├─ 📄 SystemTray.java
        │           │   ├─ 📄 TextInput.java
        │           │   └─ 📄 TrayIcon.java
        │           ├─ 📂 util
        │           │   ├─ 📄 ColorUtility.java
        │           │   ├─ 📂 compression
        │           │   │   ├─ 📄 Class13.java
        │           │   │   ├─ 📄 Class21.java
        │           │   │   └─ 📄 Class32.java
        │           │   ├─ 📄 CreateUID.java
        │           │   ├─ 📄 Decompressor.java
        │           │   ├─ 📄 FileUtility.java
        │           │   ├─ 📄 FormatHelpers.java
        │           │   ├─ 📄 ISAACRandomGen.java
        │           │   ├─ 📄 MRUNodes.java
        │           │   ├─ 📄 Node.java
        │           │   ├─ 📄 NodeCache.java
        │           │   ├─ 📄 NodeList.java
        │           │   ├─ 📄 NodeSub.java
        │           │   ├─ 📄 NodeSubList.java
        │           │   ├─ 📄 ResourceLoader.java
        │           │   ├─ 📄 SizeConstants.java
        │           │   └─ 📄 TextClass.java
        │           └─ 📂 world
        │               ├─ 📄 Class11.java
        │               ├─ 📄 Class30_Sub1.java
        │               ├─ 📄 Class4.java
        │               ├─ 📄 Floor.java
        │               ├─ 📄 Ground.java
        │               ├─ 📄 Object1.java
        │               ├─ 📄 Object2.java
        │               ├─ 📄 Object3.java
        │               ├─ 📄 Object4.java
        │               ├─ 📄 Object5.java
        │               ├─ 📄 ObjectDef.java
        │               ├─ 📄 ObjectManager.java
        │               ├─ 📄 VarBit.java
        │               ├─ 📄 Varp.java
        │               └─ 📄 WorldController.java
        └─ 📂 resources
            ├─ 📂 caches
            │   ├─ 📂 bestbudz
            │   │   ├─ 📄 main_file_cache.dat
            │   │   ├─ 📄 main_file_cache.idx0
            │   │   ├─ 📄 main_file_cache.idx1
            │   │   ├─ 📄 main_file_cache.idx2
            │   │   ├─ 📄 main_file_cache.idx3
            │   │   ├─ 📄 main_file_cache.idx4
            │   │   ├─ 📄 main_file_cache.idx5
            │   │   ├─ 📄 settings.dat
            │   │   ├─ 📄 sprites.dat
            │   │   └─ 📄 sprites.idx
            │   ├─ 📂 fixed
            │   │   ├─ 📄 .scape-settings.dat
            │   │   ├─ 📄 accounts.dat
            │   │   ├─ 📄 cacheVersion.dat
            │   │   ├─ 📄 settings.dat
            │   │   ├─ 📄 uid.dat
            │   │   └─ 📄 venran.dat
            │   └─ 📂 runelite
            │       ├─ 📄 main_file_cache.dat
            │       ├─ 📄 main_file_cache.idx0
            │       ├─ 📄 main_file_cache.idx1
            │       ├─ 📄 main_file_cache.idx2
            │       ├─ 📄 main_file_cache.idx3
            │       ├─ 📄 main_file_cache.idx4
            │       ├─ 📄 main_file_cache.idx5
            │       ├─ 📄 settings.dat
            │       ├─ 📄 sprites.dat
            │       └─ 📄 sprites.idx
            ├─ 📂 frame
            │   └─ 🖼️ logo.png
            ├─ 📂 loading
            │   ├─ 🖼️ background.png
            │   ├─ 🖼️ bar.png
            │   └─ 🖼️ login_background.png
            └─ 📂 sprites
```