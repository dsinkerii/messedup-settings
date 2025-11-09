package net.dsinkerii.client.gui;

import net.dsinkerii.SettingsModClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.Clipboard;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// NEW: Custom settings screen accessible from Controls
public class SettingsConfigScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("dsinkerii_settings_mod");
    private final Screen parent;
    private TextFieldWidget serverField;
    private TextWidget statusWidget;

    public SettingsConfigScreen(Screen parent) {
        super(Text.literal("Messed up settings configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        serverField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                startY + 20,
                200,
                20,
                Text.literal("Set MQTT Server")
        );
        serverField.setMaxLength(128);
        try {
            String serverFromFile = Files.readString(Path.of(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString()));
            serverField.setText(serverFromFile.isEmpty() ? "mqtt.emqx.io" : serverFromFile);
        } catch (Exception e) {
            serverField.setText("mqtt.emqx.io");
        }
        addDrawableChild(serverField);

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Apply & reconnect"),
                button -> {
                    try {
                        File file = new File(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
                        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
                            br.write(serverField.getText());
                            LOGGER.info("[MessedUpSettings] server updated to: {}", serverField.getText());
                        }
                        SettingsModClient.reconnectMqtt();
                    } catch (IOException e) {
                        LOGGER.error("[MessedUpSettings] failed to save server: {}", e.getMessage());
                    }
                }
        ).dimensions(centerX - 100, startY + 50, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Copy password"),
                this::Copied
        ).dimensions(centerX - 100, startY + 80, 200, 20).build());

        statusWidget = new TextWidget(centerX - 100, startY + 110, 200, 20,
                Text.literal(""), this.textRenderer);
        addDrawableChild(statusWidget);

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                button -> close()
        ).dimensions(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer, "MQTT Server:", this.width / 2, this.height / 4 + 8, 0xAAAAAA);

        updateStatusWidgets(statusWidget);
    }
    private void updateStatusWidgets(TextWidget statusSymbolWidget) {
        if (statusSymbolWidget == null) return;

        int statusColor = 0xFFB30BFF;
        String statusText;

        switch(SettingsModClient.IsConnectedAtMainMenu){
            case 0:
                statusColor = 0xFFFFB30B;
                statusText = "server hasn't begun connecting yet.";
                break;
            case 1:
                statusColor = 0xFF44ff0b;
                statusText = "connected to the server!";
                break;
            case 2:
                statusColor = 0xFFff340b;
                statusText = "couldn't connect to the server.";
                break;
            default:
                statusColor = 0xFFFFFFFF;
                statusText = "cant get info on server status.";
                break;
        }

        statusSymbolWidget.setMessage(Text.literal(statusText).withColor(statusColor));
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
    private void Copied(ButtonWidget button) {
        new Thread("Password-Copy-Thread")
        {
            public void run()
            {
                try {
                    Clipboard clipboard = new Clipboard();
                    String Password = Files.readString(Path.of(FabricLoader.getInstance().getGameDir().resolve("password.txt").toString()));

                    clipboard.setClipboard(0,Password);
                    LOGGER.info("[MessedUpSettings] Password copied to clipboard");

                    if(button.getMessage().getString().equals("Copied!")){
                        return;
                    }else{
                        for(int i = 7; i > 0; i--){
                            button.setMessage(Text.literal("Copied!".substring(0,8-i)+"_"));
                            Thread.sleep(25);
                        }
                        Thread.sleep(1500);
                        for(int i = 0; i < 7; i++){
                            button.setMessage(Text.literal("Copied!".substring(0,7-i)+"_"));
                            Thread.sleep(25);
                        }
                        button.setMessage(Text.literal("Get MQTT Password (settings mod)"));
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("[MessedUpSettings] copy animation interrupted: {}", e.getMessage());
                } catch (IOException e) {
                    LOGGER.error("[MessedUpSettings] failed to read password file: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
