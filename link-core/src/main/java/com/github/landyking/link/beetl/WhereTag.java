package com.github.landyking.link.beetl;

import com.github.landyking.link.util.Texts;
import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;

/**
 * Created by landy on 2018/5/11.
 */
public class WhereTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        String body = getBodyContent().getBody();
        if (body != null) {
            String original = body.trim();
            String tmp = original.toUpperCase();
            if (tmp.startsWith("AND ")) {
                original = original.substring(4);
            } else if (tmp.startsWith("OR ")) {
                original = original.substring(3);
            }
            if (Texts.hasText(original)) {
                try {
                    ctx.byteWriter.writeString("where " + original);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
