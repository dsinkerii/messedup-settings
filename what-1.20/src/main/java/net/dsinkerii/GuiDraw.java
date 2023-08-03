package net.dsinkerii;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GuiDraw implements HudRenderCallback{
	public static void renderGui(DrawContext drawContext, float tickDelta,String text,boolean update,String updateStr) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if(!update){
			TextRenderer renderer = mc.textRenderer;
			text = text + "\n";
			for(int i = 0; i < text.split("\n").length; i++){
				drawContext.drawTextWithShadow(renderer, text.split("\n")[i], 10, 5*i, 0xFFFFFFFF);
			}
		}
		else {
			update_settings(mc,updateStr);
		}
	}
	@Override
	public void onHudRender(DrawContext drawContext, float tickDelta) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'onHudRender'");
	}
	static void update_settings(MinecraftClient client, String updateStr){
		client.options.load();
		client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS]§f Set "+updateStr.split(":")[0] + " §6to §f" + updateStr.split(":")[1]));
    }
}
