package dev.qxstack.radon.gui;

import dev.qxstack.radon.Argon;
import dev.qxstack.radon.gui.components.ModuleButton;
import dev.qxstack.radon.module.Category;
import dev.qxstack.radon.module.Module;
import dev.qxstack.radon.module.modules.client.ClickGUI;
import dev.qxstack.radon.utils.*;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Window {
    public List<ModuleButton> moduleButtons = new ArrayList<>();
    public int x, y;
    private final int width, height;
    public Color currentColor;
    private final Category category;
    public boolean dragging, extended;
    private int dragX, dragY;
    public ClickGui parent;

    public Window(int x, int y, int width, int height, Category category, ClickGui parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.parent = parent;
        this.dragging = false;
        this.extended = true;

        int offset = height;
        List<Module> sortedModules = new ArrayList<>(Radon.INSTANCE.getModuleManager().getModulesInCategory(category));

        for (Module module : sortedModules) {
            moduleButtons.add(new ModuleButton(this, module, offset));
            offset += height;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int toAlpha = ClickGUI.alphaWindow.getValueInt();

        if (currentColor == null)
            currentColor = new Color(0, 0, 0, 0);
        else
            currentColor = new Color(0, 0, 0, currentColor.getAlpha());

        if (currentColor.getAlpha() != toAlpha)
            currentColor = ColorUtils.smoothAlphaTransition(0.05F, toAlpha, currentColor);

        RenderUtils.renderRoundedQuad(context.getMatrices(), currentColor, x, y, x + width, y + height, ClickGUI.roundQuads.getValueInt(), ClickGUI.roundQuads.getValueInt(), 0, 0, 50);
        context.fill(x, y + (height - 2), x + width, y + height, Utils.getMainColor(255, moduleButtons.indexOf(moduleButtons.get(0))).getRGB());

        int centerX = x + (width / 2);
        int textWidth = TextRenderer.getWidth(category.name);
        TextRenderer.drawString(category.name, context, centerX - (textWidth / 2), y + 6, Color.WHITE.getRGB());

        updateButtons(delta);

        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.render(context, mouseX, mouseY, delta);
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton moduleButton : moduleButtons)
            moduleButton.keyPressed(keyCode, scanCode, modifiers);
    }

    public void onGuiClose() {
        currentColor = null;
        dragging = false;

        for (ModuleButton moduleButton : moduleButtons)
            moduleButton.onGuiClose();
    }

    public boolean isDraggingAlready() {
        for (Window window : parent.windows)
            if (window.dragging)
                return true;

        return false;
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            switch (button) {
                case 0 -> {
                    if (!parent.isDraggingAlready()) {
                        dragging = true;
                        dragX = (int) (mouseX - x);
                        dragY = (int) (mouseY - y);
                    }
                }
                case 1 -> {
                    if (!dragging) {
                        // Toggle extend logic (optional)
                        // extended = !extended;
                    }
                }
            }
        }

        if (extended) {
            for (ModuleButton moduleButton : moduleButtons) {
                moduleButton.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            x = (int) (mouseX - dragX);
            y = (int) (mouseY - dragY);
        }

        if (extended) {
            for (ModuleButton moduleButton : moduleButtons) {
                moduleButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging)
            dragging = false;

        for (ModuleButton moduleButton : moduleButtons)
            moduleButton.mouseReleased(mouseX, mouseY, button);
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.y += (int) (verticalAmount * 20);
    }

    public void updateButtons(float delta) {
        int offset = height;

        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.animation.animate(0.5 * delta, moduleButton.extended ? height * (moduleButton.settings.size() + 1) : height);
            moduleButton.offset = offset;
            offset += (int) moduleButton.animation.getValue();
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void updatePosition(double mouseX, double mouseY, float delta) {
        // No smooth dragging needed for Pojav — handled by instant in mouseDragged
    }
      }
