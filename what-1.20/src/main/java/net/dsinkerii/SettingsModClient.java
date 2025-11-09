package net.dsinkerii;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.dsinkerii.mixin.ScreenAccessor;
import net.dsinkerii.client.gui.SettingsConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.eclipse.paho.client.mqttv3.*;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SettingsModClient implements ClientModInitializer {
    public static final String MOD_ID = "dsinkerii_settings_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean hideUI;
    private static KeyBinding toggleUIKey;
    private static KeyBinding clearTextKey;

    public String Text2 = "s";
    public String Password;
    public String server = "mqtt.emqx.io";
    public static int IsConnectedAtMainMenu = 0;

    private static MqttClient mqttClient;
    private static Thread mqttThread;
    private static volatile boolean shouldReconnect = false;

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            GuiDraw.renderGui(drawContext, tickDelta.getTickDelta(false), Text2, false, "", "");
        });
        toggleUIKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dsinkerii_settings_mod.toggle_ui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "key.categories.misc"
        ));

        clearTextKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dsinkerii_settings_mod.clear_text",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.categories.misc"
        ));
        //logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleUIKey.wasPressed() && (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS)) {
                hideUI = !hideUI;
            }

            if (clearTextKey.wasPressed() && (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS)) {
                Text2 = "";
            }
        });
        // render the button in controls
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            if (screen instanceof ControlsOptionsScreen) {
                ButtonWidget settingsButton = ButtonWidget.builder(
                                Text.literal("Messed up settings..."),
                                (button) -> {
                                    if (client != null) {
                                        client.setScreen(new SettingsConfigScreen(screen));
                                    }
                                }
                        )
                        .dimensions(5, 5, 150, 20)
                        .build();

                ((ScreenAccessor) screen).ds_invokeAddDrawableChild(settingsButton);
            }
        });

        String path = String.valueOf(FabricLoader.getInstance().getGameDir());

        // backup options.txt
        Path pathOptions = Path.of(path + "/options.txt");
        String file2 = null;
        try {
            if(new File(pathOptions.toString()).exists()) {
                file2 = Files.readString(pathOptions);
                FileOutputStream fileOut = new FileOutputStream(FabricLoader.getInstance().getGameDir().resolve("options-backup.txt").toString());
                fileOut.write(file2.getBytes());
                fileOut.close();
                LOGGER.info("[MessedUpSettings] created options.txt backup");
            }
        } catch (IOException e) {
            LOGGER.error("[MessedUpSettings] failed to create backup: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // Generate password
        File file = new File(FabricLoader.getInstance().getGameDir().resolve("password.txt").toString());
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            Password = bytesToHex(generateAESKey().getEncoded());
            br.write(Password);
        } catch (IOException e) {
            LOGGER.error("[MessedUpSettings] failed to write password: {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("[MessedUpSettings] failed to generate AES key: {}", e.getMessage());
            e.printStackTrace();
        }

        startMqttConnection();
        LOGGER.info("[MessedUpSettings] client initialization complete!");
    }

    public void startMqttConnection() {
        mqttThread = new Thread(){
            public void run(){
                while (true) {
                    LOGGER.info("[MessedUpSettings] attempting to connect...");
                    IsConnectedAtMainMenu = 0;
                    shouldReconnect = false;

                    try {
                        if (mqttClient != null && mqttClient.isConnected()) {
                            LOGGER.debug("[MessedUpSettings] MQTT: disconnecting existing connection");
                            mqttClient.disconnect();
                            mqttClient.close();
                        }

                        // read server from file
                        Path ServerFile = Path.of(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
                        try {
                            String serverFromFile = Files.readString(ServerFile);
                            if(serverFromFile.isEmpty()){
                                File file = new File(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
                                try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
                                    br.write(server);
                                } catch (IOException e) {
                                    LOGGER.warn("[MessedUpSettings] failed to write default server: {}", e.getMessage());
                                }
                            }else{
                                server = serverFromFile;
                                LOGGER.debug("[MessedUpSettings] loaded server from file: {}", server);
                            }
                        }catch (java.nio.file.NoSuchFileException e) {
                            LOGGER.debug("[MessedUpSettings] no server.txt found, using default: {}", server);
                        }

                        String publisherId = UUID.randomUUID().toString();
                        String passwordId = UUID.randomUUID().toString();

                        mqttClient = new MqttClient("ssl://"+server+":8883", publisherId);
                        MqttConnectOptions options = new MqttConnectOptions();
                        options.setAutomaticReconnect(true);
                        options.setCleanSession(true);
                        options.setUserName(publisherId);
                        options.setPassword(passwordId.toCharArray());
                        options.setConnectionTimeout(10);
                        String Topic = "1.20settingsmodv1.3";

                        LOGGER.info("[MessedUpSettings] MQTT: connecting to ssl://{}:8883", server);

                        mqttClient.setCallback(new MqttCallback() {
                            public void connectionLost(Throwable cause) {
                                LOGGER.error("[MessedUpSettings] MQTT: connection lost - {}!!", cause.getMessage());
                                Text2 = "§4§lLost connection: "+ cause.getMessage() + "\n\n§l§4" + cause.getCause();
                                IsConnectedAtMainMenu = 2;
                            }

                            public void messageArrived(String topic, MqttMessage message) {
                                String decrypted = "";
                                String username = "";
                                LOGGER.debug("[MessedUpSettings] MQTT: raw message received (length: {})", message.toString().length());

                                try{
                                    decrypted = decrypt(new String(message.toString()), Password);
                                    username = decrypted.split(":")[0];
                                    decrypted = decrypted.replace(username + ":", "");
                                }catch(Exception e) {
                                    LOGGER.warn("[MessedUpSettings] MQTT: failed to decrypt message - wrong password or incompatible client");
                                }

                                String DisplayDecrypted = decrypted.replace("pehkui::","");

                                if(new String(DisplayDecrypted).length() != 0){
                                    String settingName = DisplayDecrypted.split(":")[0];
                                    String newValue = DisplayDecrypted.substring(settingName.length() + 1);

                                    String[] lines = Text2.split("\n");
                                    boolean found = false;
                                    StringBuilder newText = new StringBuilder();

                                    for (String line : lines) {
                                        if (line.contains(">:§f " + settingName + ":")) {
                                            newText.append("§6["+username+"] >:§f ").append(DisplayDecrypted).append("\n");
                                            found = true;
                                        } else {
                                            newText.append(line).append("\n");
                                        }
                                    }

                                    if (!found) {
                                        newText.append("§6["+username+"] >:§f ").append(DisplayDecrypted);
                                    }

                                    Text2 = newText.toString();
                                }

                                if(new String(decrypted).length() != 0){
                                    LOGGER.info("[MessedUpSettings] processing setting update from [{}]", username);
                                    GuiDraw.renderGui(null, 0, Text2, true, decrypted, username);
                                }
                            }

                            public void deliveryComplete(IMqttDeliveryToken token) {
                            }
                        });

                        mqttClient.connect(options);
                        mqttClient.subscribe(Topic, 0);
                        Text2 = "Connected to MQTT!\n\nServer§6: " + server + "§f\n";
                        LOGGER.info("[MessedUpSettings] MQTT: connected successfully to topic '{}'", Topic);
                        IsConnectedAtMainMenu = 1;

                        while (!shouldReconnect && mqttClient.isConnected()) {
                            Thread.sleep(1000);
                        }

                    }catch(MqttException me) {
                        LOGGER.error("[MessedUpSettings] MQTT: connection failed - {} (error code: {})", me.getMessage(), me.getReasonCode());
                        IsConnectedAtMainMenu = 2;
                        Text2 = "Not connected to MQTT... Error message: " + me;

                        if (!shouldReconnect) {
                            try {
                                LOGGER.info("[MessedUpSettings] MQTT: Retrying in 5 seconds...");
                                Thread.sleep(5000);
                            } catch (InterruptedException ie) {
                                break;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        LOGGER.error("[MessedUpSettings] MQTT: unexpected error - {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        mqttThread.start();
    }

    public static void reconnectMqtt() {
        LOGGER.info("[MessedUpSettings] MQTT: manual reconnection requested!");
        shouldReconnect = true;
        if (mqttClient != null) {
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect();
                    LOGGER.debug("[MessedUpSettings] MQTT: disconnected for reconnection");
                }
            } catch (MqttException e) {
                LOGGER.error("[MessedUpSettings] MQTT: error during reconnection: {}", e.getMessage());
            }
        }
    }

    public static String decrypt(String encryptedText, String Password) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}