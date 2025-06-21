```
├─ 📄 build.gradle
├─ 📄 cache_colors.txt
├─ 📄 gradlew
├─ 📄 gradlew.bat
├─ 📄 hijack-cpurender
├─ 📄 interface_dump.txt
├─ 📝 Navigation.md
├─ 📄 PERFROMANCE_ISSUES
├─ 🐍 save_structure.py
├─ 📄 settings.gradle
└─ 📂 src
    ├─ 📂 libs
    └─ 📂 main
        ├─ 📂 java
        │   └─ 📂 com
        │       └─ 📂 bestbudz
        │           ├─ 📂 cache
        │           │   ├─ 📄 IdentityKit.java
        │           │   ├─ 📄 IdentityResolver.java
        │           │   ├─ 📄 ResetIDKits.java
        │           │   ├─ 📄 Signlink.java
        │           │   └─ 📄 SpriteDumper.java
        │           ├─ 📂 data
        │           │   ├─ 📄 AccountData.java
        │           │   ├─ 📄 AccountManager.java
        │           │   ├─ 📄 Item.java
        │           │   ├─ 📄 ItemDef.java
        │           │   ├─ 📄 Skills.java
        │           │   └─ 📄 XP.java
        │           ├─ 📂 dock
        │           │   ├─ 📂 config
        │           │   │   └─ 📄 RegisteredPanels.java
        │           │   ├─ 📂 frame
        │           │   │   ├─ 📄 ToggleBarHelper.java
        │           │   │   ├─ 📄 TogglePreview.java
        │           │   │   ├─ 📄 UIDockFrame.java
        │           │   │   └─ 📄 UIDockHelper.java
        │           │   ├─ 📂 net
        │           │   │   └─ 📄 DockNetworkUtil.java
        │           │   ├─ 📂 ui
        │           │   │   ├─ 📂 manager
        │           │   │   │   ├─ 📄 UIDockLayoutState.java
        │           │   │   │   ├─ 📄 UIModalManager.java
        │           │   │   │   └─ 📄 UIPanelManager.java
        │           │   │   ├─ 📂 modal
        │           │   │   │   ├─ 📂 dialogue
        │           │   │   │   │   ├─ 📄 DialogueCore.java
        │           │   │   │   │   ├─ 📄 DialogueExtractor.java
        │           │   │   │   │   └─ 📄 DialogueModal.java
        │           │   │   │   └─ 📂 style
        │           │   │   │       └─ 📄 ModalStyle.java
        │           │   │   └─ 📂 panel
        │           │   │       ├─ 📂 bank
        │           │   │       │   ├─ 📄 BankItemGrid.java
        │           │   │       │   ├─ 📄 BankItemPanel.java
        │           │   │       │   ├─ 📄 BankPanel.java
        │           │   │       │   └─ 📄 ItemDefCache.java
        │           │   │       ├─ 📂 character
        │           │   │       │   ├─ 📄 AppearanceConfig.java
        │           │   │       │   ├─ 📄 AppearancePanel.java
        │           │   │       │   ├─ 📄 AppearanceStorage.java
        │           │   │       │   ├─ 📄 AppearanceStyle.java
        │           │   │       │   └─ 📄 AppearanceUtils.java
        │           │   │       ├─ 📂 client
        │           │   │       │   ├─ 📄 BubbleBudzPanel.java
        │           │   │       │   ├─ 📄 SettingsPanel.java
        │           │   │       │   └─ 📄 SettingsPanelConfig.java
        │           │   │       ├─ 📂 debug
        │           │   │       │   ├─ 📂 components
        │           │   │       │   │   ├─ 📄 DataRow.java
        │           │   │       │   │   └─ 📄 DiagnosticSection.java
        │           │   │       │   ├─ 📄 DiagnosticPanel.java
        │           │   │       │   ├─ 📂 diagnostics
        │           │   │       │   │   ├─ 📄 BaseDiagnostic.java
        │           │   │       │   │   ├─ 📄 CacheDiagnostic.java
        │           │   │       │   │   ├─ 📄 CameraDiagnostic.java
        │           │   │       │   │   ├─ 📄 DiagnosticManager.java
        │           │   │       │   │   ├─ 📄 EntityDiagnostic.java
        │           │   │       │   │   ├─ 📄 GPUDiagnostic.java
        │           │   │       │   │   ├─ 📄 PerformanceDiagnostic.java
        │           │   │       │   │   ├─ 📄 SystemDiagnostic.java
        │           │   │       │   │   └─ 📄 WorldDiagnostic.java
        │           │   │       │   └─ 📂 style
        │           │   │       │       ├─ 📄 DiagnosticStyle.java
        │           │   │       │       └─ 📄 ResponsiveLayout.java
        │           │   │       ├─ 📄 DockPanelMapping.java
        │           │   │       ├─ 📂 emote
        │           │   │       │   └─ 📄 EmotePanel.java
        │           │   │       ├─ 📂 example
        │           │   │       │   ├─ 📄 CreationGuide
        │           │   │       │   ├─ 📄 ExampleConfig.java
        │           │   │       │   ├─ 📄 ExamplePanel.java
        │           │   │       │   ├─ 📄 ExampleStyle.java
        │           │   │       │   └─ 📄 ExampleUtils.java
        │           │   │       ├─ 📂 game
        │           │   │       │   ├─ 📄 AchievementsPanel.java
        │           │   │       │   ├─ 📄 InfoTabPanel.java
        │           │   │       │   └─ 📄 SkillsPanel.java
        │           │   │       ├─ 📂 shops
        │           │   │       │   └─ 📄 ShopPanel.java
        │           │   │       ├─ 📂 social
        │           │   │       │   ├─ 📂 chat
        │           │   │       │   │   ├─ 📄 ChatCore.java
        │           │   │       │   │   ├─ 📄 ChatInteractionHandler.java
        │           │   │       │   │   └─ 📄 ChatRenderer.java
        │           │   │       │   ├─ 📄 ChatPanel.java
        │           │   │       │   └─ 📄 StonersPanel.java
        │           │   │       ├─ 📂 staff
        │           │   │       │   ├─ 📄 StaffCommands.java
        │           │   │       │   ├─ 📄 StaffConfig.java
        │           │   │       │   ├─ 📄 StaffPanel.java
        │           │   │       │   ├─ 📄 StaffStyle.java
        │           │   │       │   └─ 📄 StaffUtils.java
        │           │   │       └─ 📂 teleports
        │           │   │           └─ 📄 TeleportPanel.java
        │           │   └─ 📂 util
        │           │       ├─ 📄 ButtonHandler.java
        │           │       ├─ 📄 DockBlocker.java
        │           │       ├─ 📄 DockSync.java
        │           │       ├─ 📄 DockTextUpdatable.java
        │           │       ├─ 📄 InteractiveButtonUtil.java
        │           │       ├─ 📄 RainbowHoverUtil.java
        │           │       ├─ 📄 SpriteUtil.java
        │           │       └─ 📄 UIPanel.java
        │           ├─ 📂 engine
        │           │   ├─ 📄 ClientLauncher.java
        │           │   ├─ 📂 config
        │           │   │   ├─ 📄 ColorConfig.java
        │           │   │   ├─ 📄 EngineConfig.java
        │           │   │   ├─ 📄 NetworkConfig.java
        │           │   │   └─ 📄 SettingsConfig.java
        │           │   ├─ 📂 core
        │           │   │   ├─ 📄 Client.java
        │           │   │   ├─ 📄 ClientEngine.java
        │           │   │   ├─ 📄 GameCanvas.java
        │           │   │   ├─ 📄 GameEngine.java
        │           │   │   ├─ 📄 GameLoader.java
        │           │   │   ├─ 📂 gamerender
        │           │   │   │   ├─ 📄 Camera.java
        │           │   │   │   ├─ 📄 DrawingArea.java
        │           │   │   │   ├─ 📄 DrawingAreaCPU.bak
        │           │   │   │   ├─ 📄 ObjectManager.java
        │           │   │   │   ├─ 📄 Occluder.java
        │           │   │   │   ├─ 📄 Rasterizer.java
        │           │   │   │   ├─ 📄 Texture.java
        │           │   │   │   └─ 📄 WorldController.java
        │           │   │   ├─ 📄 GameState.java
        │           │   │   └─ 📂 login
        │           │   │       ├─ 📄 Login.java
        │           │   │       ├─ 📄 LoginRenderer.java
        │           │   │       ├─ 📂 logout
        │           │   │       │   └─ 📄 Logout.java
        │           │   │       └─ 📄 WelcomeScreen.java
        │           │   ├─ 📂 gpu
        │           │   │   ├─ 📄 GPUContextManager.java
        │           │   │   ├─ 📄 GPUMonitor.java
        │           │   │   ├─ 📄 GPURenderingEngine.java
        │           │   │   ├─ 📄 GPUShaders.java
        │           │   │   └─ 📄 OpenGLRasterizer.java
        │           │   └─ 📂 util
        │           │       └─ 📄 ClientDiagnostics.java
        │           ├─ 📂 entity
        │           │   ├─ 📄 Entity.java
        │           │   ├─ 📄 EntityDef.java
        │           │   ├─ 📄 EntityMovement.java
        │           │   ├─ 📄 Npc.java
        │           │   ├─ 📄 ParseAndUpdateEntities.java
        │           │   ├─ 📄 Stoner.java
        │           │   ├─ 📄 UpdateEntities.java
        │           │   └─ 📄 UpdateStoners.java
        │           ├─ 📂 graphics
        │           │   ├─ 📄 Background.java
        │           │   ├─ 📂 buffer
        │           │   │   ├─ 📄 ImageProducer.java
        │           │   │   └─ 📄 ImageProducerCPU.bak
        │           │   ├─ 📄 BufferedImage.java
        │           │   ├─ 📄 ClearExpiredProjectiles.java
        │           │   ├─ 📄 FogHandler.java
        │           │   ├─ 📄 FogUtil.java
        │           │   ├─ 📄 HeadIcon.java
        │           │   ├─ 📄 Hitmark.java
        │           │   ├─ 📄 MovingTextures.java
        │           │   ├─ 📄 rsDrawingArea.java
        │           │   ├─ 📂 sprite
        │           │   │   ├─ 📄 Sprite.java
        │           │   │   └─ 📄 SpriteLoader.java
        │           │   └─ 📂 text
        │           │       ├─ 📄 RSFont.java
        │           │       └─ 📄 TextDrawingArea.java
        │           ├─ 📂 network
        │           │   ├─ 📄 OnDemandData.java
        │           │   ├─ 📄 OnDemandFetcher.java
        │           │   ├─ 📄 OnDemandFetcherParent.java
        │           │   ├─ 📂 packets
        │           │   │   ├─ 📄 PacketParser.java
        │           │   │   └─ 📄 SendFrames.java
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
        │           │   ├─ 📄 GameObject.java
        │           │   ├─ 📂 model
        │           │   │   ├─ 📄 Class33.java
        │           │   │   ├─ 📄 Class43.java
        │           │   │   └─ 📄 Model.java
        │           │   ├─ 📄 OverlayFloor.java
        │           │   ├─ 📄 Roofing.java
        │           │   ├─ 📄 SequenceFrame.java
        │           │   ├─ 📄 SpotAnim.java
        │           │   └─ 📄 SpotAnim2.java
        │           ├─ 📂 ui
        │           │   ├─ 📄 BuildInterface.java
        │           │   ├─ 📄 BuildScreenMenu.java
        │           │   ├─ 📄 Console.java
        │           │   ├─ 📄 DialogHandling.java
        │           │   ├─ 📄 DrawInterface.java
        │           │   ├─ 📂 handling
        │           │   │   ├─ 📄 ActionHandler.java
        │           │   │   ├─ 📄 Errors.java
        │           │   │   ├─ 📂 input
        │           │   │   │   ├─ 📄 Keyboard.java
        │           │   │   │   ├─ 📄 MouseActions.java
        │           │   │   │   ├─ 📄 MouseManager.java
        │           │   │   │   ├─ 📄 MouseScrollHandler.java
        │           │   │   │   └─ 📄 MouseState.java
        │           │   │   ├─ 📄 RightClickMenu.java
        │           │   │   └─ 📄 SettingHandler.java
        │           │   ├─ 📄 InterfaceManagement.java
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
        │           │   │   ├─ 📄 Shop.java
        │           │   │   ├─ 📄 Starter.java
        │           │   │   ├─ 📄 StatusOrbs.java
        │           │   │   └─ 📂 teleports
        │           │   ├─ 📄 NotificationMessages.java
        │           │   ├─ 📄 PopupMenu.java
        │           │   ├─ 📄 RSInterface.java
        │           │   ├─ 📄 SystemTray.java
        │           │   ├─ 📄 TabArea.java
        │           │   ├─ 📄 TextInput.java
        │           │   └─ 📄 TrayIcon.java
        │           ├─ 📂 util
        │           │   ├─ 📄 ColorUtility.java
        │           │   ├─ 📂 compression
        │           │   │   ├─ 📄 Class13.java
        │           │   │   ├─ 📄 Class21.java
        │           │   │   └─ 📄 Class32.java
        │           │   ├─ 📄 CreateUID.java
        │           │   ├─ 📂 crypto
        │           │   │   ├─ 📄 AESUtil.java
        │           │   │   └─ 📄 LRUCache.java
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
        │               ├─ 📄 GroundItem.java
        │               ├─ 📄 InLocation.java
        │               ├─ 📄 Object1.java
        │               ├─ 📄 Object2.java
        │               ├─ 📄 Object3.java
        │               ├─ 📄 Object4.java
        │               ├─ 📄 Object5.java
        │               ├─ 📄 ObjectDef.java
        │               ├─ 📄 TerrainHeight.java
        │               ├─ 📄 VarBit.java
        │               ├─ 📄 Varp.java
        │               └─ 📄 WalkTo.java
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
            │   └─ 📂 fixed
            │       ├─ 📄 .scape-settings.dat
            │       ├─ 📄 accounts.dat
            │       ├─ 📄 appearance.dock
            │       ├─ 📄 bubblebudz.dat
            │       ├─ 📄 cacheVersion.dat
            │       ├─ 📄 settings.dat
            │       ├─ 📄 uid.dat
            │       └─ 📄 venran.dat
            ├─ 📂 frame
            │   └─ 🖼️ logo.png
            ├─ 📂 loading
            │   ├─ 🖼️ bar.png
            │   ├─ 🖼️ bubblebudz.png
            │   ├─ 🖼️ login_background.png
            │   └─ 🖼️ OLD_login_background.png
            ├─ 🖼️ logo.png
            └─ 📂 sprites
                ├─ 📄 items.zip
                ├─ 🖼️ quest-tab.png
                └─ 📂 skills
                    ├─ 🖼️ pet master.png
                    ├─ 🖼️ aegis.png
                    ├─ 🖼️ assault.png
                    ├─ 🖼️ bankstanding.png
                    ├─ 🖼️ consumer.png
                    ├─ 🖼️ cultivation.png
                    ├─ 🖼️ fisher.png
                    ├─ 🖼️ foodie.png
                    ├─ 🖼️ forging.png
                    ├─ 🖼️ handiness.png
                    ├─ 🖼️ life.png
                    ├─ 🖼️ lumbering.png
                    ├─ 🖼️ mage.png
                    ├─ 🖼️ mercenary.png
                    ├─ 🖼️ resonance.png
                    ├─ 🖼️ pyromaniac.png
                    ├─ 🖼️ quarrying.png
                    ├─ 🖼️ sagittarius.png
                    ├─ 🖼️ thc-hempistry.png
                    ├─ 🖼️ total.png
                    ├─ 🖼️ vigour.png
                    ├─ 🖼️ weedsmoking.png
                    └─ 🖼️ woodcarving.png
```