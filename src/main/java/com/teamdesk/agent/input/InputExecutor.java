package com.teamdesk.agent.input;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class InputExecutor {

    private final Robot robot;
    private final Dimension screenSize;

    public InputExecutor() throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(2);
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }

    public void mouseMove(double xRatio, double yRatio) {
        int x = (int) Math.round(xRatio * (screenSize.width - 1));
        int y = (int) Math.round(yRatio * (screenSize.height - 1));

        x = Math.max(0, Math.min(screenSize.width - 1, x));
        y = Math.max(0, Math.min(screenSize.height - 1, y));

        robot.mouseMove(x, y);
    }

    public void mouseDown(int button) {
        robot.mousePress(buttonMask(button));
    }

    public void mouseUp(int button) {
        robot.mouseRelease(buttonMask(button));
    }

    public void mouseClick(int button) {
        int mask = buttonMask(button);
        robot.mousePress(mask);
        robot.mouseRelease(mask);
    }

    public void mouseDoubleClick(int button) {
        int mask = buttonMask(button);
        robot.mousePress(mask);
        robot.mouseRelease(mask);
        robot.delay(80);
        robot.mousePress(mask);
        robot.mouseRelease(mask);
    }

    public void mouseWheel(int delta) {
        robot.mouseWheel(delta);
    }

    public void keyDown(String key) {
        Integer code = mapKey(key);
        if (code != null) {
            robot.keyPress(code);
        }
    }

    public void keyUp(String key) {
        Integer code = mapKey(key);
        if (code != null) {
            robot.keyRelease(code);
        }
    }

    private int buttonMask(int button) {
        return switch (button) {
            case 1 -> InputEvent.BUTTON1_DOWN_MASK;
            case 2 -> InputEvent.BUTTON2_DOWN_MASK;
            case 3 -> InputEvent.BUTTON3_DOWN_MASK;
            default -> InputEvent.BUTTON1_DOWN_MASK;
        };
    }

    private Integer mapKey(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        return switch (key) {
            case "Enter" -> KeyEvent.VK_ENTER;
            case "Backspace" -> KeyEvent.VK_BACK_SPACE;
            case "Tab" -> KeyEvent.VK_TAB;
            case "Escape" -> KeyEvent.VK_ESCAPE;
            case "Shift" -> KeyEvent.VK_SHIFT;
            case "Control" -> KeyEvent.VK_CONTROL;
            case "Alt" -> KeyEvent.VK_ALT;
            case "Meta" -> KeyEvent.VK_META;
            case "ArrowUp" -> KeyEvent.VK_UP;
            case "ArrowDown" -> KeyEvent.VK_DOWN;
            case "ArrowLeft" -> KeyEvent.VK_LEFT;
            case "ArrowRight" -> KeyEvent.VK_RIGHT;
            case "Delete" -> KeyEvent.VK_DELETE;
            case "Home" -> KeyEvent.VK_HOME;
            case "End" -> KeyEvent.VK_END;
            case "PageUp" -> KeyEvent.VK_PAGE_UP;
            case "PageDown" -> KeyEvent.VK_PAGE_DOWN;
            case " " -> KeyEvent.VK_SPACE;
            default -> {
                if (key.length() == 1) {
                    yield KeyEvent.getExtendedKeyCodeForChar(key.charAt(0));
                }
                yield null;
            }
        };
    }
}