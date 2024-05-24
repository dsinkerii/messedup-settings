package net.dsinkerii;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
		client.options.load();
		if(updateStr.contains("lang")) {
			client.getLanguageManager().setLanguage(updateStr.split(":")[1]);
			// Reload the language
			client.getLanguageManager().reload(client.getResourceManager());
		}else if(updateStr.contains("guiScale")){
			client.inGameHud.setTitle(Text.literal("this thing is broken"));
			client.inGameHud.setSubtitle(Text.literal("please ignore ty"));
		}else if(updateStr.contains("pehkui::")){ //pehkui stuff yayy
			if(updateStr.contains("pehkui::base")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.BASE;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::base:")[1]));
			}
			if(updateStr.contains("pehkui::width")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.WIDTH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::width:")[1]));
			}
			if(updateStr.contains("pehkui::height")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.HEIGHT;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::height:")[1]));
			}
			if(updateStr.contains("pehkui::eye_hight")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.EYE_HEIGHT;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::eye_height:")[1]));
			}
			if(updateStr.contains("pehkui::hitbox_width")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.HITBOX_WIDTH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::hitbox_width:")[1]));
			}
			if(updateStr.contains("pehkui::hitbox_height")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.HITBOX_HEIGHT;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::hitbox_height:")[1]));
			}
			if(updateStr.contains("pehkui::step_height")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.STEP_HEIGHT;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::step_height:")[1]));
			}
			if(updateStr.contains("pehkui::jump_height")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.JUMP_HEIGHT;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::jump_height:")[1]));
			}
			if(updateStr.contains("pehkui::reach")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.REACH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::reach:")[1]));
			}
			if(updateStr.contains("pehkui::block_reach")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.BLOCK_REACH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::block_reach:")[1]));
			}
			if(updateStr.contains("pehkui::entity_reach")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.ENTITY_REACH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::entity_reach:")[1]));
			}
			if(updateStr.contains("pehkui::mining_speed")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.MINING_SPEED;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::mining_speed:")[1]));
			}
			if(updateStr.contains("pehkui::knockback")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.KNOCKBACK;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::knockback:")[1]));
			}
			if(updateStr.contains("pehkui::attack")){
				final Entity Player = client.player;

				final ScaleType type = ScaleTypes.ATTACK;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::attack:")[1]));
			}
			if(updateStr.contains("pehkui::defense")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.DEFENSE;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::defense:")[1]));
			}
			if(updateStr.contains("pehkui::health")){
				final Entity Player = client.player;
				//data.setBaseScale(Float.parseFloat(updateStr.split("pehkui::size:")[1]));

				final ScaleType type = ScaleTypes.HEALTH;
				final ScaleData data = type.getScaleData(Player);
				data.setScale(Float.parseFloat(updateStr.split("pehkui::health:")[1]));
			}


		}
		updateStr = updateStr.replace("pehkui::", "pehkui-");
		client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS] §e" + username + "§f set "+updateStr.split(":")[0] + " §6to §f" + updateStr.split(":")[1]));
    }
}
