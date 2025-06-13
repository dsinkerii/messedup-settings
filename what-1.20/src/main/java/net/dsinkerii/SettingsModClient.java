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

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.eclipse.paho.client.mqttv3.*;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SettingsModClient implements ClientModInitializer {
    public String Text2 = "s";
    public String Password;
    public String server = "tcp://mqtt.eclipseprojects.io:1883";
    public static int IsConnectedAtMainMenu = 0;
    
    // Add these fields for connection management
    private static MqttClient mqttClient;
    private static Thread mqttThread;
    private static volatile boolean shouldReconnect = false;
    
    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            GuiDraw.renderGui(drawContext, tickDelta.getTickDelta(false), Text2, false, "", "");
        });
        
        String path = String.valueOf(FabricLoader.getInstance().getGameDir());

        // backup before starting (keep this for safety)
        Path pathOptions = Path.of(path + "/options.txt");
        String file2 = null;
        try {
            file2 = Files.readString(pathOptions);
            FileOutputStream fileOut = new FileOutputStream(FabricLoader.getInstance().getGameDir().resolve("options-backup.txt").toString());
            fileOut.write(file2.getBytes());
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File(FabricLoader.getInstance().getGameDir().resolve("password.txt").toString());
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            Password = bytesToHex(generateAESKey().getEncoded());
            br.write(Password);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        startMqttConnection();
    }

    public void startMqttConnection() {
        mqttThread = new Thread("Setup MQTT"){
            public void run(){
                while (true) {
                    System.out.println("attempting to connect...");
                    IsConnectedAtMainMenu = 0;
                    shouldReconnect = false;
                    
                    try {
                        //disconnect existing connection if any
                        if (mqttClient != null && mqttClient.isConnected()) {
                            mqttClient.disconnect();
                            mqttClient.close();
                        }
                        
                        //read server from file
                        Path ServerFile = Path.of(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
                        try {
                            String serverFromFile = Files.readString(ServerFile);
                            if(serverFromFile.isEmpty()){
                                File file = new File(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
                                try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
                                    br.write(server);
                                } catch (IOException e) {
                                    // Handle exception
                                }
                            }else{
                                server = serverFromFile;
                            }
                        }catch (java.nio.file.NoSuchFileException e) {}

                        String publisherId = UUID.randomUUID().toString();
                        String passwordId = UUID.randomUUID().toString();

                        mqttClient = new MqttClient(server, publisherId);
                        MqttConnectOptions options = new MqttConnectOptions();
                        options.setAutomaticReconnect(true);
                        options.setCleanSession(true);
                        options.setUserName(publisherId);
                        options.setPassword(passwordId.toCharArray());
                        options.setConnectionTimeout(10);
                        String Topic = "1.20settingsmodv1.3";
                        
                        mqttClient.setCallback(new MqttCallback() {
                            public void connectionLost(Throwable cause) {
                                System.out.println("connectionLost: " + cause.getMessage() + cause.getCause());
                                Text2 = "§4§lLost connection: "+ cause.getMessage() + "\n\n§l§4" + cause.getCause();
                                IsConnectedAtMainMenu = 2;
                            }

                            public void messageArrived(String topic, MqttMessage message) {
                                String decrypted = "";
                                String username = "";
                                System.out.println("Raw message: " + message.toString());
                                try{
                                    decrypted = decrypt(new String(message.toString()), Password);
                                    username = decrypted.split(":")[0];
                                    decrypted = decrypted.replace(username + ":", "");
                                }catch(Exception e) {
                                    System.out.println("Unable to decrypt, is someone else playing rn?");
                                }
                                
                                String DisplayDecrypted = decrypted.replace("pehkui::","");
                                System.out.println(new String(DisplayDecrypted).length());

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
                                
                                // REMOVED FILE MODIFICATION - now using runtime settings only
                                if(new String(decrypted).length() != 0){
                                    System.out.println("Processing setting update: " + decrypted);
                                    GuiDraw.renderGui(null, 0, Text2, true, decrypted, username);
                                }
                            }

                            public void deliveryComplete(IMqttDeliveryToken token) {
                            }
                        });
                        
                        mqttClient.connect(options);
                        mqttClient.subscribe(Topic, 0);
                        Text2 = "Connected to MQTT!\n\nServer§6: " + server + "§f\n";
                        System.out.println("connected to MQTT...");
                        IsConnectedAtMainMenu = 1;
                        
                        // Wait for reconnection signal
                        while (!shouldReconnect && mqttClient.isConnected()) {
                            Thread.sleep(1000);
                        }
                        
                    }catch(MqttException me) {
                        System.out.println("not connected to MQTT...");
                        IsConnectedAtMainMenu = 2;
                        System.out.println(me);
                        Text2 = "Not connected to MQTT... Error message: " + me;
                        
                        // wait before retrying if not a manual reconnect
                        if (!shouldReconnect) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ie) {
                                break;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        mqttThread.start();
    }

    public static void reconnectMqtt() {
        shouldReconnect = true;
        if (mqttClient != null) {
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect();
                }
            } catch (MqttException e) {
                System.out.println("Error disconnecting MQTT client: " + e.getMessage());
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
