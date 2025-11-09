package net.dsinkerii;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundCategory;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import static net.dsinkerii.SettingsModClient.LOGGER;

public abstract class GuiDraw implements HudRenderCallback{

    public static void renderGui(DrawContext drawContext, float tickDelta,String text,boolean update,String updateStr, String username) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(!update){
            if (mc.options.hudHidden || SettingsModClient.hideUI) {
                return;
            }
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
            String newVal = updateStr.split(":")[1];
            updateStr = updateStr.replace(updateStr.split(":")[1], newVal);

            System.out.println("processing setting: " + updateStr);

            String[] parts = updateStr.split(":", 2);
            if (parts.length != 2) return;

            String settingName = parts[0];
            String settingValue = parts[1];

            GameOptions options = client.options;

            if (updateStr.contains("pehkui::")) {
                handlePehkuiSettings(client, updateStr, username);
                return;
            }

            switch (settingName) {
                case "fov":
                    try {
                        double fovValue = Double.parseDouble(settingValue);
                        //fovValue = (fovValue - 70.0) / 40.0;
                        options.getFov().setValue((int) fovValue);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid FOV value: " + settingValue);
                    }
                    break;

                case "mouseSensitivity":
                    try {
                        double sensitivity = Double.parseDouble(settingValue);
                        sensitivity = sensitivity / 200.0;
                        options.getMouseSensitivity().setValue(sensitivity);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid mouse sensitivity: " + settingValue);
                    }
                    break;

                case "soundCategory_master":
                    try {
                        double volume = Double.parseDouble(settingValue);
                        volume = volume / 100.0;
                        options.getSoundVolumeOption(SoundCategory.MASTER).setValue(volume);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid master volume: " + settingValue);
                    }
                    break;

                case "maxFps":
                    try {
                        int maxFps = Integer.parseInt(settingValue);
                        options.getMaxFps().setValue(maxFps);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid max FPS: " + settingValue);
                    }
                    break;

                case "renderDistance":
                    try {
                        int renderDist = Integer.parseInt(settingValue);
                        options.getViewDistance().setValue(renderDist);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid render distance: " + settingValue);
                    }
                    break;

                case "entityDistanceScaling":
                    try {
                        double scaling = Double.parseDouble(settingValue);
                        scaling = scaling / 100.0;
                        options.getEntityDistanceScaling().setValue(scaling);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid entity distance scaling: " + settingValue);
                    }
                    break;

                case "gamma":
                    try {
                        double gamma = Double.parseDouble(settingValue);
                        gamma = gamma / 100.0;
                        options.getGamma().setValue(gamma);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid gamma: " + settingValue);
                    }
                    break;

                case "fovEffectScale":
                    try {
                        double fovEffect = Double.parseDouble(settingValue);
                        fovEffect = fovEffect / 100.0;
                        options.getFovEffectScale().setValue(fovEffect);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid FOV effect scale: " + settingValue);
                    }
                    break;

                case "damageTiltStrength":
                    try {
                        double tiltStrength = Double.parseDouble(settingValue);
                        tiltStrength = tiltStrength / 100.0;
                        options.getDamageTiltStrength().setValue(tiltStrength);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid damage tilt strength: " + settingValue);
                    }
                    break;

                case "guiScale":
                    try {
                        int guiScale = Integer.parseInt(settingValue);
                        options.getGuiScale().setValue(guiScale);
                        client.inGameHud.setTitle(Text.literal("GUI scale updated"));
                        client.inGameHud.setSubtitle(Text.literal("new scale: " + guiScale));
                    } catch (NumberFormatException e) {
                        System.out.println("invalid GUI scale: " + settingValue);
                    }
                    break;

                case "chatScale":
                    try {
                        double chatScale = Double.parseDouble(settingValue);
                        chatScale = chatScale / 100.0;
                        options.getChatScale().setValue(chatScale);
                    } catch (NumberFormatException e) {
                        System.out.println("invalid chat scale: " + settingValue);
                    }
                    break;

                case "autoJump":
                    try {
                        boolean autoJump = Boolean.parseBoolean(settingValue);
                        options.getAutoJump().setValue(autoJump);
                    } catch (Exception e) {
                        System.out.println("invalid auto jump value: " + settingValue);
                    }
                    break;

                case "invertYMouse":
                    try {
                        boolean invertY = Boolean.parseBoolean(settingValue);
                        options.getInvertYMouse().setValue(invertY);
                    } catch (Exception e) {
                        System.out.println("invalid invert Y mouse value: " + settingValue);
                    }
                    break;

                case "language":
                    try {
                        client.getLanguageManager().setLanguage(settingValue);
                        client.getLanguageManager().reload(client.getResourceManager());
                    } catch (Exception e) {
                        System.out.println("invalid language: " + settingValue);
                    }
                    break;

                // keybinds
                case "key_key.forward":
                    setKeyBinding(options.forwardKey, settingValue,"key_key.forward");
                    break;
                case "key_key.back":
                    setKeyBinding(options.backKey, settingValue,"key_key.back");
                    break;
                case "key_key.left":
                    setKeyBinding(options.leftKey, settingValue,"key_key.left");
                    break;
                case "key_key.right":
                    setKeyBinding(options.rightKey, settingValue,"key_key.right");
                    break;
                case "key_key.jump":
                    setKeyBinding(options.jumpKey, settingValue,"key_key.jump");
                    break;
                case "key_key.sneak":
                    setKeyBinding(options.sneakKey, settingValue,"key_key.sneak");
                    break;
                case "key_key.sprint":
                    setKeyBinding(options.sprintKey, settingValue,"key_key.sprint");
                    break;
                case "key_key.drop":
                    setKeyBinding(options.dropKey, settingValue,"key_key.drop");
                    break;
                case "key_key.inventory":
                    setKeyBinding(options.inventoryKey, settingValue,"key_key.inventory");
                    break;
                case "key_key.chat":
                    setKeyBinding(options.chatKey, settingValue,"key_key.chat");
                    break;
                case "key_key.swapOffhand":
                    setKeyBinding(options.swapHandsKey, settingValue,"key_key.swapOffhand");
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
                handlePehkuiDirect(player, updateStr);
            } else {
                handlePehkuiCommands(client, updateStr);
            }

        } catch (Exception e) {
            System.out.println("error handling Pehkui setting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handlePehkuiDirect(ClientPlayerEntity player, String updateStr) {
        try {
            String scaleType = updateStr.split("::")[1].split(":")[0];
            String val = updateStr.split("::")[1].split(":")[1];
            float value = Float.parseFloat(val);

            ScaleData scaleData = switch(scaleType) {
                case "base" -> ScaleTypes.BASE.getScaleData(player);
                case "width" -> ScaleTypes.WIDTH.getScaleData(player);
                case "height" -> ScaleTypes.HEIGHT.getScaleData(player);
                case "eye_height" -> ScaleTypes.EYE_HEIGHT.getScaleData(player);
                case "hitbox_width" -> ScaleTypes.HITBOX_WIDTH.getScaleData(player);
                case "hitbox_height" -> ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                case "step_height" -> ScaleTypes.STEP_HEIGHT.getScaleData(player);
                case "jump_height" -> ScaleTypes.JUMP_HEIGHT.getScaleData(player);
                case "reach" -> ScaleTypes.REACH.getScaleData(player);
                case "block_reach" -> ScaleTypes.BLOCK_REACH.getScaleData(player);
                case "entity_reach" -> ScaleTypes.ENTITY_REACH.getScaleData(player);
                case "mining_speed" -> ScaleTypes.MINING_SPEED.getScaleData(player);
                case "knockback" -> ScaleTypes.KNOCKBACK.getScaleData(player);
                case "attack" -> ScaleTypes.ATTACK.getScaleData(player);
                case "defense" -> ScaleTypes.DEFENSE.getScaleData(player);
                case "health" -> ScaleTypes.HEALTH.getScaleData(player);
                case "view_bobbing" -> ScaleTypes.VIEW_BOBBING.getScaleData(player);
                case "motion" -> ScaleTypes.MOTION.getScaleData(player);
                default -> null;
            };

            if (scaleData != null) {
                scaleData.setScale(value);
                System.out.println("PEHKUI " + scaleType + " scale updated to: " + value);
            }
        } catch (NumberFormatException e) {
            System.out.println("invalid Pehkui scale value: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("pehkui error: " + e.getMessage());
        }
    }

    private static void handlePehkuiCommands(MinecraftClient client, String updateStr) {
        try {
            CommandManager commandManager = client.getServer().getCommandManager();
            ClientPlayerEntity player = client.player;
            var commandSource = client.getServer().getCommandSource();

            String scaleType = updateStr.split("::")[1].split(":")[0];
            String val = updateStr.split("::")[1].split(":")[1];

            String command = "scale set pehkui:" + scaleType + " " + val + " " + player.getName().getString();
            LOGGER.info("[MessedUpSettings] executing pehkui command: /{}", command);
            commandManager.executeWithPrefix(commandSource, command);

        } catch (Exception e) {
            LOGGER.error("[MessedUpSettings] error executing Pehkui command: {}", e.getMessage(), e);
        }
    }

    private static void setKeyBinding(KeyBinding keyBinding, String keyValue, String keyName) {
        try {
            int keyCode = getKeyCodeFromString(keyValue);
            if (keyCode != -1) {
                keyBinding.setBoundKey(net.minecraft.client.util.InputUtil.fromKeyCode(keyCode, 0));
                KeyBinding.updateKeysByCode();
                LOGGER.info("[MessedUpSettings] keybind '{}' changed to: {}", keyName, keyValue);
            } else {
                LOGGER.warn("[MessedUpSettings] unknown key for '{}': {}", keyName, keyValue);
            }
        } catch (Exception e) {
            LOGGER.error("[MessedUpSettings] error setting keybinding '{}': {}", keyName, e.getMessage());
        }
    }

    private static int getKeyCodeFromString(String keyString) {
        return switch (keyString.toLowerCase()) {
            case "key.keyboard.q" -> GLFW.GLFW_KEY_Q;
            case "key.keyboard.w" -> GLFW.GLFW_KEY_W;
            case "key.keyboard.e" -> GLFW.GLFW_KEY_E;
            case "key.keyboard.r" -> GLFW.GLFW_KEY_R;
            case "key.keyboard.t" -> GLFW.GLFW_KEY_T;
            case "key.keyboard.y" -> GLFW.GLFW_KEY_Y;
            case "key.keyboard.u" -> GLFW.GLFW_KEY_U;
            case "key.keyboard.i" -> GLFW.GLFW_KEY_I;
            case "key.keyboard.o" -> GLFW.GLFW_KEY_O;
            case "key.keyboard.p" -> GLFW.GLFW_KEY_P;
            case "key.keyboard.a" -> GLFW.GLFW_KEY_A;
            case "key.keyboard.s" -> GLFW.GLFW_KEY_S;
            case "key.keyboard.d" -> GLFW.GLFW_KEY_D;
            case "key.keyboard.f" -> GLFW.GLFW_KEY_F;
            case "key.keyboard.g" -> GLFW.GLFW_KEY_G;
            case "key.keyboard.h" -> GLFW.GLFW_KEY_H;
            case "key.keyboard.j" -> GLFW.GLFW_KEY_J;
            case "key.keyboard.k" -> GLFW.GLFW_KEY_K;
            case "key.keyboard.l" -> GLFW.GLFW_KEY_L;
            case "key.keyboard.z" -> GLFW.GLFW_KEY_Z;
            case "key.keyboard.x" -> GLFW.GLFW_KEY_X;
            case "key.keyboard.c" -> GLFW.GLFW_KEY_C;
            case "key.keyboard.v" -> GLFW.GLFW_KEY_V;
            case "key.keyboard.b" -> GLFW.GLFW_KEY_B;
            case "key.keyboard.n" -> GLFW.GLFW_KEY_N;
            case "key.keyboard.m" -> GLFW.GLFW_KEY_M;
            case "key.keyboard.space" -> GLFW.GLFW_KEY_SPACE;
            case "key.keyboard.shift" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "key.keyboard.control" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "key.mouse.left" -> GLFW.GLFW_MOUSE_BUTTON_LEFT;
            case "key.mouse.right" -> GLFW.GLFW_MOUSE_BUTTON_RIGHT;
            default -> -1;
        };
    }
}