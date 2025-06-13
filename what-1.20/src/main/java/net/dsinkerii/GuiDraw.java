package net.dsinkerii;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifiers;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

public abstract class GuiDraw implements HudRenderCallback{
    public static void renderGui(DrawContext drawContext, float tickDelta,String text,boolean update,String updateStr, String username) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(!update){
            TextRenderer renderer = mc.textRenderer;
            text = text + "\n";
            for(int i = 0; i < text.split("\n").length; i++){
                drawContext.drawTextWithShadow(renderer, text.split("\n")[i], 10, 10*i, 0xFFFFFFFF);
            }
        }
        else {
            update_settings(mc,updateStr, username);
        }
    }
    
    static void update_settings(MinecraftClient client, String updateStr, String username){
        try {
            String newVal = updateStr.split(":")[1].length() > 8 ? updateStr.split(":")[1].substring(0, 8) : updateStr.split(":")[1];
            updateStr = updateStr.replace(updateStr.split(":")[1], newVal);
            
            System.out.println("Processing setting: " + updateStr);
            
            String[] parts = updateStr.split(":", 2);
            if (parts.length != 2) return;
            
            String settingName = parts[0];
            String settingValue = parts[1];
            
            GameOptions options = client.options;
            
            // pehkui
            if (updateStr.contains("pehkui::")) {
                handlePehkuiSettings(client, updateStr, username);
                return;
            }
            
            // mc settings
            switch (settingName) {
                // slider settings
                case "fov":
                    try {
                        double fovValue = Double.parseDouble(settingValue);
                        //fovValue = (fovValue - 70.0) / 40.0;
                        options.getFov().setValue((int) fovValue);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid FOV value: " + settingValue);
                    }
                    break;
                    
                case "mouseSensitivity":
                    try {
                        double sensitivity = Double.parseDouble(settingValue);
                        sensitivity = sensitivity / 200.0;
                        options.getMouseSensitivity().setValue(sensitivity);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid mouse sensitivity: " + settingValue);
                    }
                    break;
                    
                case "soundCategory_master":
                    try {
                        double volume = Double.parseDouble(settingValue);
                        volume = volume / 100.0;
                        options.getSoundVolumeOption(SoundCategory.MASTER).setValue(volume);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid master volume: " + settingValue);
                    }
                    break;
                    
                case "maxFps":
                    try {
                        int maxFps = Integer.parseInt(settingValue);
                        options.getMaxFps().setValue(maxFps);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid max FPS: " + settingValue);
                    }
                    break;
                    
                case "renderDistance":
                    try {
                        int renderDist = Integer.parseInt(settingValue);
                        options.getViewDistance().setValue(renderDist);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid render distance: " + settingValue);
                    }
                    break;
                    
                case "entityDistanceScaling":
                    try {
                        double scaling = Double.parseDouble(settingValue);
                        scaling = scaling / 100.0;
                        options.getEntityDistanceScaling().setValue(scaling);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid entity distance scaling: " + settingValue);
                    }
                    break;
                    
                case "gamma":
                    try {
                        double gamma = Double.parseDouble(settingValue);
                        gamma = gamma / 100.0;
                        options.getGamma().setValue(gamma);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid gamma: " + settingValue);
                    }
                    break;
                    
                case "fovEffectScale":
                    try {
                        double fovEffect = Double.parseDouble(settingValue);
                        fovEffect = fovEffect / 100.0;
                        options.getFovEffectScale().setValue(fovEffect);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid FOV effect scale: " + settingValue);
                    }
                    break;
                    
                case "damageTiltStrength":
                    try {
                        double tiltStrength = Double.parseDouble(settingValue);
                        tiltStrength = tiltStrength / 100.0;
                        options.getDamageTiltStrength().setValue(tiltStrength);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid damage tilt strength: " + settingValue);
                    }
                    break;
                    
                case "guiScale":
                    try {
                        int guiScale = Integer.parseInt(settingValue);
                        options.getGuiScale().setValue(guiScale);
                        client.inGameHud.setTitle(Text.literal("GUI Scale Updated"));
                        client.inGameHud.setSubtitle(Text.literal("New scale: " + guiScale));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid GUI scale: " + settingValue);
                    }
                    break;
                    
                case "chatScale":
                    try {
                        double chatScale = Double.parseDouble(settingValue);
                        chatScale = chatScale / 100.0;
                        options.getChatScale().setValue(chatScale);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid chat scale: " + settingValue);
                    }
                    break;
                    
                case "autoJump":
                    try {
                        boolean autoJump = Boolean.parseBoolean(settingValue);
                        options.getAutoJump().setValue(autoJump);
                    } catch (Exception e) {
                        System.out.println("Invalid auto jump value: " + settingValue);
                    }
                    break;
                    
                case "invertYMouse":
                    try {
                        boolean invertY = Boolean.parseBoolean(settingValue);
                        options.getInvertYMouse().setValue(invertY);
                    } catch (Exception e) {
                        System.out.println("Invalid invert Y mouse value: " + settingValue);
                    }
                    break;
                    
                case "language":
                    try {
                        client.getLanguageManager().setLanguage(settingValue);
                        client.getLanguageManager().reload(client.getResourceManager());
                    } catch (Exception e) {
                        System.out.println("Invalid language: " + settingValue);
                    }
                    break;
                    
                case "key_key.forward":
                    setKeyBinding(options.forwardKey, settingValue);
                    break;
                case "key_key.back":
                    setKeyBinding(options.backKey, settingValue);
                    break;
                case "key_key.left":
                    setKeyBinding(options.leftKey, settingValue);
                    break;
                case "key_key.right":
                    setKeyBinding(options.rightKey, settingValue);
                    break;
                case "key_key.jump":
                    setKeyBinding(options.jumpKey, settingValue);
                    break;
                case "key_key.sneak":
                    setKeyBinding(options.sneakKey, settingValue);
                    break;
                case "key_key.sprint":
                    setKeyBinding(options.sprintKey, settingValue);
                    break;
                case "key_key.drop":
                    setKeyBinding(options.dropKey, settingValue);
                    break;
                case "key_key.inventory":
                    setKeyBinding(options.inventoryKey, settingValue);
                    break;
                case "key_key.chat":
                    setKeyBinding(options.chatKey, settingValue);
                    break;
                case "key_key.swapOffhand":
                    setKeyBinding(options.swapHandsKey, settingValue);
                    break;
                    
                default:
                    System.out.println("Unsupported setting: " + settingName);
                    break;
            }
            
        } catch (Exception e) {
            client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS] (by §e" + username + ") §4§lThere was an issue trying to process the latest command. Please contact @dsinkerii on discord in order to resolve this issue. Issue: " + e));
            e.printStackTrace();
        }
        
        String displayStr = updateStr.replace("pehkui::", "pehkui-");
        client.inGameHud.getChatHud().addMessage(Text.literal("§6[OPTIONS] §e" + username + "§f set " + displayStr.split(":")[0] + " §6to §f" + displayStr.split(":")[1]));
    }
    
    private static void handlePehkuiSettings(MinecraftClient client, String updateStr, String username) {
        try {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            
            if (client.getServer() == null) {
                // single player - use api directly
                handlePehkuiDirect(player, updateStr);
            } else {
                // multiplayer - use commands
                handlePehkuiCommands(client, updateStr);
            }
            
        } catch (Exception e) {
            System.out.println("Error handling Pehkui setting: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void handlePehkuiDirect(ClientPlayerEntity player, String updateStr) {
        try {
            if (updateStr.contains("pehkui::base")) {
                String val = updateStr.split("pehkui::base:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Base scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::width")) {
                String val = updateStr.split("pehkui::width:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.WIDTH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Width scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::height")) {
                String val = updateStr.split("pehkui::height:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.HEIGHT.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Height scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::eye_height")) {
                String val = updateStr.split("pehkui::eye_height:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.EYE_HEIGHT.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Eye height scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::hitbox_width")) {
                String val = updateStr.split("pehkui::hitbox_width:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.HITBOX_WIDTH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Hitbox width scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::hitbox_height")) {
                String val = updateStr.split("pehkui::hitbox_height:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Hitbox height scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::step_height")) {
                String val = updateStr.split("pehkui::step_height:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.STEP_HEIGHT.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Step height scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::jump_height")) {
                String val = updateStr.split("pehkui::jump_height:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.JUMP_HEIGHT.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Jump height scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::reach")) {
                String val = updateStr.split("pehkui::reach:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.REACH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Reach scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::block_reach")) {
                String val = updateStr.split("pehkui::block_reach:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.BLOCK_REACH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Block reach scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::entity_reach")) {
                String val = updateStr.split("pehkui::entity_reach:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.ENTITY_REACH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Entity reach scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::mining_speed")) {
                String val = updateStr.split("pehkui::mining_speed:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.MINING_SPEED.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Mining speed scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::knockback")) {
                String val = updateStr.split("pehkui::knockback:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.KNOCKBACK.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Knockback scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::attack")) {
                String val = updateStr.split("pehkui::attack:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.ATTACK.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Attack scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::defense")) {
                String val = updateStr.split("pehkui::defense:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.DEFENSE.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Defense scale updated to: " + value);
            }
            else if (updateStr.contains("pehkui::health")) {
                String val = updateStr.split("pehkui::health:")[1];
                float value = Float.parseFloat(val);
                ScaleData scaleData = ScaleTypes.HEALTH.getScaleData(player);
                scaleData.setScale(value);
                System.out.println("PEHKUI Health scale updated to: " + value);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Pehkui scale value: " + e.getMessage());
        }
    }
    
    private static void handlePehkuiCommands(MinecraftClient client, String updateStr) {
        try {
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
        } catch (Exception e) {
            System.out.println("Error executing Pehkui command: " + e.getMessage());
        }
    }
    
    private static void setKeyBinding(KeyBinding keyBinding, String keyValue) {
        try {
            int keyCode = getKeyCodeFromString(keyValue);
            if (keyCode != -1) {
                keyBinding.setBoundKey(net.minecraft.client.util.InputUtil.fromKeyCode(keyCode, 0));
                KeyBinding.updateKeysByCode();
                System.out.println("Updated keybinding to: " + keyValue);
            } else {
                System.out.println("Unknown key: " + keyValue);
            }
        } catch (Exception e) {
            System.out.println("Error setting keybinding: " + e.getMessage());
        }
    }
    
    private static int getKeyCodeFromString(String keyString) {
        switch (keyString.toLowerCase()) {
            case "key.keyboard.w": return GLFW.GLFW_KEY_W;
            case "key.keyboard.a": return GLFW.GLFW_KEY_A;
            case "key.keyboard.s": return GLFW.GLFW_KEY_S;
            case "key.keyboard.d": return GLFW.GLFW_KEY_D;
            case "key.keyboard.space": return GLFW.GLFW_KEY_SPACE;
            case "key.keyboard.left.shift": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "key.keyboard.left.control": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "key.keyboard.q": return GLFW.GLFW_KEY_Q;
            case "key.keyboard.e": return GLFW.GLFW_KEY_E;
            case "key.keyboard.t": return GLFW.GLFW_KEY_T;
            case "key.keyboard.f": return GLFW.GLFW_KEY_F;
            case "key.keyboard.enter": return GLFW.GLFW_KEY_ENTER;
            case "key.keyboard.escape": return GLFW.GLFW_KEY_ESCAPE;
            case "key.keyboard.tab": return GLFW.GLFW_KEY_TAB;
            // Add more key mappings as needed
            default: return -1;
        }
    }
}
