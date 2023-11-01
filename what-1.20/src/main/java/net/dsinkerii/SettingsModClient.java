package net.dsinkerii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.paho.client.mqttv3.*;

import com.mojang.authlib.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.Base64;

import net.fabricmc.api.ClientModInitializer;
//import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
public class SettingsModClient implements ClientModInitializer {
    public String Text2 = "s";
    public String Password;
    //private net.minecraft.client.MinecraftClient client;
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            GuiDraw.renderGui(drawContext, tickDelta,Text2,false,"");
        });
        String path = String.valueOf(FabricLoader.getInstance().getGameDir());
        File file = new File(path+"/password.txt");
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            Password = bytesToHex(generateAESKey().getEncoded());
			br.write(Password);
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Thread thread = new Thread("Setup MQTT"){
            public void run(){
                    System.out.println("attempting to connect...");
                    try {
                        String publisherId = UUID.randomUUID().toString();
                        String passwordId = UUID.randomUUID().toString();
                        MqttClient sampleClient = new MqttClient("tcp://broker.emqx.io:1883", publisherId);
                        MqttConnectOptions options = new MqttConnectOptions();
                        options.setAutomaticReconnect(true);
                        options.setCleanSession(true);
                        options.setUserName(publisherId);
                        options.setPassword(passwordId.toCharArray());
                        options.setConnectionTimeout(10);
                        String Topic = "1.20settingsmodv1.1";
                        sampleClient.setCallback(new MqttCallback() {
                            public void connectionLost(Throwable cause) {
                                System.out.println("connectionLost: " + cause.getMessage() + cause.getCause());
                                Text2 = "§4§lLost connection: "+ cause.getMessage() + "\n\n§l§4" + cause.getCause();
                            }
                        
                            public void messageArrived(String topic, MqttMessage message) {
                                String decrypted = "";
                                System.out.println("Raw message: " + message.toString());
                                try{
                                    decrypted = decrypt(new String(message.toString()), Password);
                                }catch(Exception e) {
                                    System.out.println("Unable to decrypt, is someone else playing rn?");
                                }
                                String DisplayDecrypted = decrypted.replace("pehkui::","");
                                System.out.println(new String(DisplayDecrypted).length());
                                if(!Text2.contains(new String(DisplayDecrypted).split(":")[0]) && new String(DisplayDecrypted).length() != 0){
                                    Text2 += "\n§6>:§f " + DisplayDecrypted + "\n";
                                }else{
                                    if(new String(DisplayDecrypted).length() != 0){
                                        System.out.println(new String(DisplayDecrypted).split(":")[0] + Text2.split(new String(DisplayDecrypted).split(":")[0])[1].split("\n")[0]);
                                        System.out.println(new String(DisplayDecrypted));
                                        System.out.println(Text2.replace(new String(DisplayDecrypted).split(":")[0] + Text2.split(new String(DisplayDecrypted).split(":")[0])[1].split("\n")[0], new String(DisplayDecrypted)));
                                        Text2 = Text2.replace(new String(DisplayDecrypted).split(":")[0] + Text2.split(new String(DisplayDecrypted).split(":")[0])[1].split("\n")[0], new String(DisplayDecrypted));
                                    }
                                    //Text2 = Text2.replace(new String(decrypted).split(":")[0] + ":" + Text2.split(new String(decrypted).split(":")[0])[1].split("\n")[0], new String(decrypted).split(":")[0] + ":" + new String(decrypted).split(":")[1]);
                                }
                                //path+"options.txt"
                                if(!(new String(decrypted).contains("pehkui::"))) {
                                    try {
                                        Path pathOptions = Path.of(path + "/options.txt");
                                        String file2 = Files.readString(pathOptions);
                                        int lineNumber = -1;
                                        for (String line : file2.split("\n")) {
                                            if (line.contains(new String(decrypted).split(":")[0])) {
                                                file2 = file2.replace(new String(decrypted).split(":")[0] + ":" + file2.split("\n")[lineNumber + 1].split(":")[1], new String(decrypted).split(":")[0] + ":" + new String(decrypted).split(":")[1]);
                                                FileOutputStream fileOut = new FileOutputStream(path + "/options.txt");
                                                fileOut.write(file2.getBytes());
                                                fileOut.close();
                                                break;
                                            }
                                            lineNumber++;
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Problem reading file: " + e);
                                    }
                                }
                                if(new String(decrypted).length() != 0){
                                    System.out.println(decrypted);
                                    GuiDraw.renderGui(null, 0,Text2,true, decrypted);
                                }
                            }
                        
                            public void deliveryComplete(IMqttDeliveryToken token) {
                                // Implement your logic for delivery completion here
                            }
                        });
                        sampleClient.connect(options);
                        sampleClient.subscribe(Topic, 0);
                        Text2 = "Connected to MQTT!\n";
                        System.out.println("contnected to MQTT...");
                    }catch(MqttException me) {
                        System.out.println("not contnected to MQTT...");
                        System.out.println(me);
                        Text2 = "Not connected to MQTT... Error message: " + me;
                    }
                    String Topic = "SettingsMod 1.20";
                    //IMqttClient publisher = new MqttClient("tcp://iot.eclipse.org:1883",publisherId);
                    //while(true){
                    //}
                }

        };
        thread.start();
    }
    public static String decrypt(String encryptedText, String Password) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // You can use 128, 192, or 256 bits key size for AES
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
