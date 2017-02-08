package com.emojis.kavisherlock.wokringwithsms;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String message = "Here is a boy: :boy:!";
        Pattern pattern = Pattern.compile(":[a-z]+:");
        Matcher matcher = pattern.matcher(message);
        Emoji emoji1 = null;
        if(matcher.find()) {
            String alias = matcher.group(0);
            System.out.println(alias);
            emoji1 = EmojiManager.getForAlias(alias.substring(1, alias.length()-1));
        }
        System.out.println(emoji1.getTags());
    }
}