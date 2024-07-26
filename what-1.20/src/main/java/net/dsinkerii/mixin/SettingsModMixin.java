package net.dsinkerii.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.util.Clipboard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(TitleScreen.class)
public class SettingsModMixin extends Screen {
	TextFieldWidget server;
	protected SettingsModMixin (Text title) {
		super(title);
	}

	@Inject(at = @At("RETURN"), method = "initWidgetsNormal")
	private void addModsButton(int y, int spacingY, CallbackInfo ci) {
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Get MQTT Password (settings mod)"), (button) -> {
			Copied(button);
		}).dimensions(0, this.height-60, 200, 20).build());
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		server = new TextFieldWidget(
				renderer, 1, this.height-85, 178, 20, Text.literal("Set MQTT Server")
		);
		server.setTooltip(Tooltip.of(Text.literal("Sets a new MQTT server.")));
		server.setMaxLength(100);
		server.setText("tcp://mqtt.eclipseprojects.io:1883");
		String path = String.valueOf(FabricLoader.getInstance().getGameDir());
		try {
			String serverFromFile = Files.readString(Path.of(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString()));
			if(!serverFromFile.isEmpty()){
				server.setText(serverFromFile);
			}
		}catch (java.nio.file.NoSuchFileException e) {}
		catch (Exception e) {}
		server.setEditable(true);
		server.setEditableColor(0xFFFF);
		addDrawableChild(server);
		ButtonWidget mqttserverbutton = this.addDrawableChild(ButtonWidget.builder(Text.literal("⇄"), (button) -> {
			button.setTooltip(Tooltip.of(Text.literal("New MQTT Server set! Restart the game to apply changes.")));
			File file = new File(FabricLoader.getInstance().getGameDir().resolve("server.txt").toString());
			try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
				br.write(server.getText());
			} catch (IOException e) {

			}
		}).dimensions(181, this.height-85, 20, 20).build());

		mqttserverbutton.setTooltip(Tooltip.of(Text.literal("Sets the MQTT server, if you don't know what that is, better leave it unchanged")));

		ButtonWidget backupbutton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Restore settings from backup"), (button) -> {
			Path pathOptions = Path.of(FabricLoader.getInstance().getGameDir().resolve("options-backup.txt").toString());
			String file2 = null;
			try {
				file2 = Files.readString(pathOptions);
				FileOutputStream fileOut = new FileOutputStream(FabricLoader.getInstance().getGameDir().resolve("options.txt").toString());
				fileOut.write(file2.getBytes());
				fileOut.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			BackedUp(button);
		}).dimensions(0, this.height - 35, 200, 20).build());
		backupbutton.setTooltip(Tooltip.of(Text.literal("Replaces current options.txt file with options-backup.txt, made when you have started the game")));
	}
	@Unique
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		server.renderButton(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
	}

	@Unique
	private void Copied(ButtonWidget button) {
		new Thread()
		{
			public void run()
			{
				//some code here.
				try {
					Clipboard clipboard = new Clipboard();
					String path = String.valueOf(FabricLoader.getInstance().getGameDir());

					String Password = Files.readString(Path.of(FabricLoader.getInstance().getGameDir().resolve("password.txt").toString()));

					clipboard.setClipboard(0,Password);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //more code here.
			}
		}.start();
	}

	@Unique
	private void BackedUp(ButtonWidget button) {
		new Thread()
		{
			public void run()
			{
				//some code here.
				try {
					if(button.getMessage().getString().equals("Reverted!")){
						return;
					}else{
						for(int i = 9; i > 0; i--){
							button.setMessage(Text.literal("Reverted!".substring(0,10-i)+"_"));
							Thread.sleep(25);
						}
						Thread.sleep(1500);
						for(int i = 0; i < 9; i++){
							button.setMessage(Text.literal("Reverted!".substring(0,9-i)+"_"));
							Thread.sleep(25);
						}
						button.setMessage(Text.literal("Restore settings from backup"));
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//more code here.
			}
		}.start();
	}

}