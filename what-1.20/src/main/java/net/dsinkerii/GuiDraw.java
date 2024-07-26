package net.dsinkerii;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.MinecraftServer;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifiers;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;


public class GuiDraw implements HudRenderCallback{
	public static void renderGui(DrawContext drawContext, float tickDelta,String text,boolean update,String updateStr, String username) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if(!update){
			TextRenderer renderer = mc.textRenderer;
			text = text + "\n";
			for(int i = 0; i < text.split("\n").length; i++){
				drawContext.drawTextWithShadow(renderer, text.split("\n")[i], 10, 5*i, 0xFFFFFFFF);
			}
		}
		else {
			update_settings(mc,updateStr, username);
		}
	}
	@Override
	public void onHudRender(DrawContext drawContext, float tickDelta) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'onHudRender'");
	}
	static void update_settings(MinecraftClient client, String updateStr, String username){
        try {
            String newVal = updateStr.split(":")[1].length() > 8 ? updateStr.split(":")[1].substring(0, 8) : updateStr.split(":")[1];
            updateStr = updateStr.replace(updateStr.split(":")[1], newVal);
            client.options.load();
            System.out.println(updateStr);
            if (updateStr.contains("lang")) {
                client.getLanguageManager().setLanguage(updateStr.split(":")[1]);
                // Reload the language
                client.getLanguageManager().reload(client.getResourceManager());
            } else if (updateStr.contains("guiScale")) {
                client.inGameHud.setTitle(Text.literal("this thing is broken"));
                client.inGameHud.setSubtitle(Text.literal("please ignore ty"));
            } else if (updateStr.contains("pehkui::")) { //pehkui stuff yayy
                CommandManager commandManager = client.getServer().getCommandManager();
                ClientPlayerEntity player = client.player;
                net.minecraft.server.command.ServerCommandSource commandSource = client.getServer().getCommandSource();

                if (updateStr.contains("pehkui::base")) {
                    String val = updateStr.split("pehkui::base:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:base " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::width")) {
                    String val = updateStr.split("pehkui::width:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:width " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::height")) {
                    String val = updateStr.split("pehkui::height:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:height " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::eye_height")) {
                    String val = updateStr.split("pehkui::eye_height:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:eye_height " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::hitbox_width")) {
                    String val = updateStr.split("pehkui::hitbox_width:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:hitbox_width " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::hitbox_height")) {
                    String val = updateStr.split("pehkui::hitbox_height:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:hitbox_height " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::step_height")) {
                    String val = updateStr.split("pehkui::step_height:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:step_height " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::jump_height")) {
                    String val = updateStr.split("pehkui::jump_height:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:jump_height " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::reach")) {
                    String val = updateStr.split("pehkui::reach:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:reach " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::block_reach")) {
                    String val = updateStr.split("pehkui::block_reach:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:block_reach " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::entity_reach")) {
                    String val = updateStr.split("pehkui::entity_reach:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:entity_reach " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::mining_speed")) {
                    String val = updateStr.split("pehkui::mining_speed:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:mining_speed " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::knockback")) {
                    String val = updateStr.split("pehkui::knockback:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:knockback " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::attack")) {
                    String val = updateStr.split("pehkui::attack:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:attack " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::defense")) {
                    String val = updateStr.split("pehkui::defense:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:defense " + val + " " +  player.getName().getString());
                }
                else if (updateStr.contains("pehkui::health")) {
                    String val = updateStr.split("pehkui::health:")[1];
                    System.out.println("PEHKUI Value updated." + updateStr);
                    commandManager.executeWithPrefix(commandSource, "scale set pehkui:health " + val + " " +  player.getName().getString());
                }


            }
        } catch (Exception e) {
			client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS] (by §e" + username + ") §4§lThere was an issue trying to process the latest command. Please contact @dsinkerii on discord in order to resolve this issue. Issue: " + e));
        }
		updateStr = updateStr.replace("pehkui::", "pehkui-");
		client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS] §e" + username + "§f set "+updateStr.split(":")[0] + " §6to §f" + updateStr.split(":")[1]));
    }
}
