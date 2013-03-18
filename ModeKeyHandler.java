package autoplant;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ModeKeyHandler extends KeyHandler {

	static KeyBinding modeKeyBinding = new KeyBinding("AutoPlant", Keyboard.KEY_U);

	public ModeKeyHandler() {
		super(new KeyBinding[] { modeKeyBinding }, new boolean[] { false });
	}
	@Override
	public String getLabel() {
		return "AutoPlant";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		Minecraft mc = Minecraft.getMinecraft();
		if(kb != this.modeKeyBinding) return;
		if(tickEnd == false) return;
		if(mc.currentScreen != null) return;
		if(mc.ingameGUI.getChatGUI().getChatOpen()) return;

		AutoPlant.config.toggleMode();
		mc.ingameGUI.getChatGUI().printChatMessage("AutoPlant " + AutoPlant.config.getMode().toString());
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
