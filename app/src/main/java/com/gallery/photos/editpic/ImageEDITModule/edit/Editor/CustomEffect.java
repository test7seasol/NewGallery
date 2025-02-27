package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CustomEffect {
    private String mEffectName;
    private Map<String, Object> parametersMap;

    private CustomEffect(Builder builder) {
        this.mEffectName = builder.mEffectName;
        this.parametersMap = builder.parametersMap;
    }

    public static class Builder {
        public String mEffectName;
        public Map<String, Object> parametersMap = new HashMap();

        public Builder(String str) throws RuntimeException {
            if (!TextUtils.isEmpty(str)) {
                this.mEffectName = str;
                return;
            }
            throw new RuntimeException("Effect name cannot be empty.Please provide effect name from EffectFactory");
        }

        public Builder setParameter(String str, Object obj) {
            this.parametersMap.put(str, obj);
            return this;
        }

        public CustomEffect build() {
            return new CustomEffect(this);
        }
    }
}
