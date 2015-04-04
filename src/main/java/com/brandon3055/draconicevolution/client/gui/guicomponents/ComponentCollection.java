package com.brandon3055.draconicevolution.client.gui.guicomponents;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 28/12/2014.
 */
public class ComponentCollection extends ComponentBase {

	protected List<ComponentBase> components = new ArrayList<ComponentBase>();
	protected int xSize;
	protected int ySize;
	protected List<String> deadGroups = new ArrayList<String>();
	protected GUIBase gui;


	public ComponentCollection(int x, int y, int xSize, int ySize, GUIBase gui) {
		super(x, y);
		this.xSize = xSize;
		this.ySize = ySize;
		this.gui = gui;
	}

	@Override
	public int getWidth() {
		return xSize;
	}

	@Override
	public int getHeight() {
		return ySize;
	}


	public ComponentBase addComponent(ComponentBase component) {
		components.add(component);
		component.setWorldAndResolution(Minecraft.getMinecraft(), width, height);
		return component;
	}

	private boolean isComponentEnabled(ComponentBase component) {
		return component != null && component.isEnabled();
	}

	private boolean isComponentCapturingMouse(ComponentBase component, int mouseX, int mouseY) {
		return isComponentEnabled(component) && component.isMouseOver(mouseX, mouseY);
	}

	@Override
	public final void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		for (ComponentBase component : components)
			if (isComponentEnabled(component)) {
				component.renderBackground(minecraft, offsetX + this.x, offsetY + this.y, mouseX - this.x, mouseY - this.y);
			}
	}

	@Override
	public final void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		for (ComponentBase component : components)
			if (isComponentEnabled(component)) {
				component.renderForground(minecraft, offsetX + this.x, offsetY + this.y, mouseX - this.x, mouseY - this.y);
			}
	}

	@Override
	public final void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		for (ComponentBase component : components)
			if (isComponentEnabled(component)) {
				component.renderFinal(minecraft, offsetX + this.x, offsetY + this.y, mouseX - this.x, mouseY - this.y);
			}
 	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		for (ComponentBase component : components){
			if (!gui.buttonPressed && component.isEnabled() && component.isMouseOver(x, y)) {
				component.mouseClicked(x, y, button);
			}
		}
		//removeScheduled();
	}

	public List<ComponentBase> getComponents() { return components; }

	public ComponentBase getComponent(String name){
		for (ComponentBase component : components){
			if (component.name != null && component.name.equals(name)) return component;
		}
		return null;
	}

	public void setGroupEnabled(String group, boolean enabled){
		for (ComponentBase component : components){
			if (component.getGroup().equals(group)) {
				component.setEnabled(enabled);
			}

		}
	}

	public void setOnlyGroupEnabled(String group){
		for (ComponentBase component : components){
			if (component.getGroup().equals(group)) {
				component.setEnabled(enabled);
			}else{
				component.setEnabled(false);
			}

		}
	}

	public void setComponentEnabled(String name, boolean enabled){
		for (ComponentBase component : components){
			if (component.name != null && component.name.equals(name)) {
				component.setEnabled(enabled);
			}
		}
	}

	public void removeGroup(String group){
		List<ComponentBase> list = new ArrayList<ComponentBase>();
		for (ComponentBase component : components){
			if (component.getGroup().equals(group)) list.add(component);
		}
		components.removeAll(list);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		for (ComponentBase component : components){
			component.setWorldAndResolution(mc, width, height);
		}
	}

	public void removeScheduled(){
		if (!deadGroups.isEmpty()){
			for (String s : deadGroups) removeGroup(s);
			deadGroups.clear();
		}
	}

	public void schedulRemoval(String group){
		deadGroups.add(group);
	}

	@Override
	public void updateScreen() {
		for (ComponentBase component : components){
			component.updateScreen();
		}
	}
}
