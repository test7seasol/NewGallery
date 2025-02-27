package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

/* loaded from: classes.dex */
class IdHandler {
    private static final String TAG = SvgToPath.TAG;
    private final XmlPullParser atts;
    final HashMap<String, String> idXml = new HashMap<>();
    private final Stack<IdRecording> idRecordingStack = new Stack<>();

    IdHandler(XmlPullParser xmlPullParser) {
        this.atts = xmlPullParser;
    }

    class IdRecording {
        final String id;
        int level = 0;
        final StringBuilder sb = new StringBuilder();

        public IdRecording(String str) {
            this.id = str;
        }
    }

    public void processIds() throws XmlPullParserException, IOException {
        int eventType = this.atts.getEventType();
        do {
            if (eventType != 0 && eventType != 1) {
                if (eventType == 2) {
                    startElement();
                } else if (eventType == 3) {
                    endElement();
                }
            }
            eventType = this.atts.next();
        } while (eventType != 1);
    }

    private void appendElementString(StringBuilder sb, String str, XmlPullParser xmlPullParser) {
        sb.append("<");
        sb.append(str);
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            sb.append(" ");
            sb.append(xmlPullParser.getAttributeName(i));
            sb.append("='");
            sb.append(ParseUtil.escape(xmlPullParser.getAttributeValue(i)));
            sb.append("'");
        }
        sb.append(">");
    }

    void startElement() {
        String name = this.atts.getName();
        String stringAttr = ParseUtil.getStringAttr("id", this.atts);
        if (stringAttr != null) {
            this.idRecordingStack.push(new IdRecording(stringAttr));
        }
        if (this.idRecordingStack.size() > 0) {
            IdRecording lastElement = this.idRecordingStack.lastElement();
            lastElement.level++;
            appendElementString(lastElement.sb, name, this.atts);
        }
    }

    void endElement() {
        String name = this.atts.getName();
        if (this.idRecordingStack.size() > 0) {
            IdRecording lastElement = this.idRecordingStack.lastElement();
            lastElement.sb.append("</");
            lastElement.sb.append(name);
            lastElement.sb.append(">");
            lastElement.level--;
            if (lastElement.level == 0) {
                String sb = lastElement.sb.toString();
                this.idXml.put(lastElement.id, sb);
                this.idRecordingStack.pop();
                if (this.idRecordingStack.size() > 0) {
                    this.idRecordingStack.lastElement().sb.append(sb);
                }
                Log.w(TAG, sb);
            }
        }
    }
}
