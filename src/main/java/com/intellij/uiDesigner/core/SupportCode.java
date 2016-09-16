package com.intellij.uiDesigner.core;

import javax.swing.*;
import java.lang.reflect.Method;

public final class SupportCode {
    public static TextWithMnemonic parseText(String textWithMnemonic) {
        if (textWithMnemonic == null) {
            throw new IllegalArgumentException("textWithMnemonic cannot be null");
        }
        int index = -1;
        StringBuffer plainText = new StringBuffer();
        for (int i = 0; i < textWithMnemonic.length(); i++) {
            char ch = textWithMnemonic.charAt(i);
            if (ch == '&') {
                i++;
                if (i >= textWithMnemonic.length()) {
                    break;
                }
                ch = textWithMnemonic.charAt(i);
                if (ch != '&') {
                    index = plainText.length();
                }
            }
            plainText.append(ch);
        }
        return new TextWithMnemonic(plainText.toString(), index);
    }

    public static final class TextWithMnemonic {
        public final String myText;
        public final int myMnemonicIndex;

        private TextWithMnemonic(String text, int index) {
            if (text == null) {
                throw new IllegalArgumentException("text cannot be null");
            }
            if ((index != -1) && ((index < 0) || (index >= text.length()))) {
                throw new IllegalArgumentException("wrong index: " + index + "; text = '" + text + "'");
            }
            this.myText = text;
            this.myMnemonicIndex = index;
        }

        public char getMnemonicChar() {
            if (this.myMnemonicIndex == -1) {
                throw new IllegalStateException("text doesn't contain mnemonic");
            }
            return Character.toUpperCase(this.myText.charAt(this.myMnemonicIndex));
        }
    }

    public static void setDisplayedMnemonicIndex(JComponent component, int index) {
        try {
            Method method = component.getClass().getMethod("setDisplayedMnemonicIndex", new Class[]{Integer.TYPE});
            method.setAccessible(true);
            method.invoke(component, new Object[]{new Integer(index)});
        }
        catch (Exception localException) {
        }
    }
}


