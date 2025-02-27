package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
class ParseUtil {
    ParseUtil() {
    }

    static final String escape(String str) {
        return str.replaceAll("\"", "&quot;").replaceAll("'", "&apos").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;");
    }

    static final String getStringAttr(String str, XmlPullParser xmlPullParser) {
        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            if (xmlPullParser.getAttributeName(i).equals(str)) {
                return xmlPullParser.getAttributeValue(i);
            }
        }
        return null;
    }

    static final Float convertUnits(String str, XmlPullParser xmlPullParser, float f, float f2, float f3) {
        float f4;
        String stringAttr = getStringAttr(str, xmlPullParser);
        if (stringAttr == null) {
            return null;
        }
        if (stringAttr.endsWith("px")) {
            return Float.valueOf(Float.parseFloat(stringAttr.substring(0, stringAttr.length() - 2)));
        }
        if (stringAttr.endsWith("pt")) {
            return Float.valueOf((Float.valueOf(stringAttr.substring(0, stringAttr.length() - 2)).floatValue() * f) / 72.0f);
        }
        if (stringAttr.endsWith("pc")) {
            return Float.valueOf((Float.valueOf(stringAttr.substring(0, stringAttr.length() - 2)).floatValue() * f) / 6.0f);
        }
        if (stringAttr.endsWith("cm")) {
            return Float.valueOf((Float.valueOf(stringAttr.substring(0, stringAttr.length() - 2)).floatValue() * f) / 2.54f);
        }
        if (stringAttr.endsWith("mm")) {
            return Float.valueOf((Float.valueOf(stringAttr.substring(0, stringAttr.length() - 2)).floatValue() * f) / 254.0f);
        }
        if (stringAttr.endsWith("in")) {
            return Float.valueOf(Float.valueOf(stringAttr.substring(0, stringAttr.length() - 2)).floatValue() * f);
        }
        if (stringAttr.endsWith("%")) {
            Float valueOf = Float.valueOf(stringAttr.substring(0, stringAttr.length() - 1));
            if (str.contains("x") || str.equals("width")) {
                f4 = f2 / 100.0f;
            } else {
                f4 = (str.contains("y") || str.equals("height")) ? f3 / 100.0f : (f3 + f2) / 2.0f;
            }
            return Float.valueOf(valueOf.floatValue() * f4);
        }
        return Float.valueOf(stringAttr);
    }
}
