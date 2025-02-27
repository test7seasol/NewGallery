package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class SvgToPath {
    private static final float DPI = 72.0f;
    private static final Matrix IDENTITY_MATRIX = new Matrix();
    static final String TAG = "SvgToPath";
    private final XmlPullParser atts;
    private float height;
    private Path path;
    private float width;
    private HashMap<String, String> idXml = new HashMap<>();
    private final RectF rect = new RectF();
    private float dpi = DPI;
    private boolean hidden = false;
    private int hiddenLevel = 0;
    private boolean inDefsElement = false;
    private final Deque<Path> pathStack = new LinkedList();
    private final Deque<Matrix> matrixStack = new LinkedList();
    private PathInfo pathInfo = null;

    public static PathInfo getSVGFromInputStream(InputStream inputStream) {
        return parse(inputStream, true, DPI);
    }

    private static PathInfo parse(InputStream inputStream, boolean z, float f) {
        try {
            // Use Android's built-in XmlPullParserFactory
            XmlPullParser parser = Xml.newPullParser();  // ✅ Avoids dependency conflicts
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            SvgToPath svgToPath = new SvgToPath(parser);
            svgToPath.setDpi(f);

            if (z) {
                parser.setInput(new InputStreamReader(inputStream));
                svgToPath.processSvg();
            } else {
                CopyInputStream copyInputStream = new CopyInputStream(inputStream);
                XmlPullParser parser2 = Xml.newPullParser();
                parser2.setInput(new InputStreamReader(copyInputStream.getCopy()));
                IdHandler idHandler = new IdHandler(parser2);
                idHandler.processIds();
                svgToPath.idXml = idHandler.idXml;
                parser.setInput(new InputStreamReader(copyInputStream.getCopy()));
                svgToPath.processSvg();
            }
            return svgToPath.pathInfo;
        } catch (Exception e) {
            Log.w(TAG, "Parse error: " + e);
            throw new RuntimeException(e);
        }
    }

    private SvgToPath(XmlPullParser xmlPullParser) {
        this.atts = xmlPullParser;
    }

    void setDpi(float f) {
        this.dpi = f;
    }

    void processSvg() throws XmlPullParserException, IOException {
        int eventType = this.atts.getEventType();
        do {
            if (eventType == 2) {
                startElement();
            } else if (eventType == 3) {
                endElement();
            }
            eventType = this.atts.next();
        } while (eventType != 1);
    }

    private void pushTransform(XmlPullParser xmlPullParser) {
        String stringAttr = ParseUtil.getStringAttr("transform", xmlPullParser);
        this.matrixStack.push(stringAttr == null ? IDENTITY_MATRIX : TransformParser.parseTransform(stringAttr));
    }

    private void pushTransform(Matrix matrix) {
        if (matrix == null) {
            matrix = IDENTITY_MATRIX;
        }
        this.matrixStack.push(matrix);
    }

    private Matrix popTransform() {
        return this.matrixStack.pop();
    }

    private void pushPath() {
        Path path = new Path();
        this.path = path;
        this.pathStack.add(path);
    }

    private Path popPath() {
        Path pop = this.pathStack.pop();
        this.path = this.pathStack.peek();
        return pop;
    }

    void startElement() {
        String name = this.atts.getName();
        if (this.inDefsElement) {
            return;
        }
        if (name.equals("svg")) {
            this.width = Math.round(getFloatAttr("width", this.atts, Float.valueOf(0.0f)).floatValue());
            this.height = Math.round(getFloatAttr("height", this.atts, Float.valueOf(0.0f)).floatValue());
            NumberParse numberParseAttr = NumberParse.getNumberParseAttr("viewBox", this.atts);
            pushPath();
            Matrix matrix = IDENTITY_MATRIX;
            if (numberParseAttr != null && numberParseAttr.numbers != null && numberParseAttr.numbers.size() == 4) {
                float f = this.width;
                if (f < 0.1f || this.height < -0.1f) {
                    this.width = numberParseAttr.numbers.get(2).floatValue() - numberParseAttr.numbers.get(0).floatValue();
                    this.width = numberParseAttr.numbers.get(3).floatValue() - numberParseAttr.numbers.get(3).floatValue();
                } else {
                    matrix.setScale(f / (numberParseAttr.numbers.get(2).floatValue() - numberParseAttr.numbers.get(0).floatValue()), this.height / (numberParseAttr.numbers.get(3).floatValue() - numberParseAttr.numbers.get(1).floatValue()));
                }
            }
            pushTransform(matrix);
            return;
        }
        if (name.equals("defs")) {
            this.inDefsElement = true;
            return;
        }
        if (name.equals("use")) {
            String stringAttr = ParseUtil.getStringAttr("xlink:href", this.atts);
            String stringAttr2 = ParseUtil.getStringAttr("transform", this.atts);
            String stringAttr3 = ParseUtil.getStringAttr("x", this.atts);
            String stringAttr4 = ParseUtil.getStringAttr("y", this.atts);
            if (stringAttr2 != null || stringAttr3 != null || stringAttr4 != null) {
                if (stringAttr2 != null) {
                    ParseUtil.escape(stringAttr2);
                }
                if (stringAttr3 != null || stringAttr4 != null) {
                    if (stringAttr3 != null) {
                        ParseUtil.escape(stringAttr3);
                    }
                    if (stringAttr4 != null) {
                        ParseUtil.escape(stringAttr4);
                    }
                }
            }
            for (int i = 0; i < this.atts.getAttributeCount(); i++) {
                String attributeName = this.atts.getAttributeName(i);
                if (!"x".equals(attributeName) && !"y".equals(attributeName) && !"width".equals(attributeName) && !"height".equals(attributeName) && !"xlink:href".equals(attributeName) && !"transform".equals(attributeName)) {
                    ParseUtil.escape(this.atts.getAttributeValue(i));
                }
            }
            this.idXml.get(stringAttr.substring(1));
            return;
        }
        if (name.equals("g")) {
            if (this.hidden) {
                this.hiddenLevel++;
            }
            if ("none".equals(ParseUtil.getStringAttr("display", this.atts)) && !this.hidden) {
                this.hidden = true;
                this.hiddenLevel = 1;
            }
            pushTransform(this.atts);
            pushPath();
            return;
        }
        if (!this.hidden && name.equals("rect")) {
            Float floatAttr = getFloatAttr("x", this.atts, Float.valueOf(0.0f));
            Float floatAttr2 = getFloatAttr("y", this.atts, Float.valueOf(0.0f));
            Float floatAttr3 = getFloatAttr("width", this.atts);
            Float floatAttr4 = getFloatAttr("height", this.atts);
            Float floatAttr5 = getFloatAttr("rx", this.atts, Float.valueOf(0.0f));
            Float floatAttr6 = getFloatAttr("ry", this.atts, Float.valueOf(0.0f));
            Path path = new Path();
            if (floatAttr5.floatValue() <= 0.0f && floatAttr6.floatValue() <= 0.0f) {
                path.addRect(floatAttr.floatValue(), floatAttr2.floatValue(), floatAttr.floatValue() + floatAttr3.floatValue(), floatAttr2.floatValue() + floatAttr4.floatValue(), Path.Direction.CW);
            } else {
                this.rect.set(floatAttr.floatValue(), floatAttr2.floatValue(), floatAttr.floatValue() + floatAttr3.floatValue(), floatAttr2.floatValue() + floatAttr4.floatValue());
                path.addRoundRect(this.rect, floatAttr5.floatValue(), floatAttr6.floatValue(), Path.Direction.CW);
            }
            pushTransform(this.atts);
            path.transform(popTransform());
            this.path.addPath(path);
            return;
        }
        if (!this.hidden && name.equals("line")) {
            Float floatAttr7 = getFloatAttr("x1", this.atts);
            Float floatAttr8 = getFloatAttr("x2", this.atts);
            Float floatAttr9 = getFloatAttr("y1", this.atts);
            Float floatAttr10 = getFloatAttr("y2", this.atts);
            Path path2 = new Path();
            path2.moveTo(floatAttr7.floatValue(), floatAttr9.floatValue());
            path2.lineTo(floatAttr8.floatValue(), floatAttr10.floatValue());
            pushTransform(this.atts);
            path2.transform(popTransform());
            this.path.addPath(path2);
            return;
        }
        if (!this.hidden && name.equals("circle")) {
            Float floatAttr11 = getFloatAttr("cx", this.atts);
            Float floatAttr12 = getFloatAttr("cy", this.atts);
            Float floatAttr13 = getFloatAttr("r", this.atts);
            if (floatAttr11 == null || floatAttr12 == null || floatAttr13 == null) {
                return;
            }
            Path path3 = new Path();
            path3.addCircle(floatAttr11.floatValue(), floatAttr12.floatValue(), floatAttr13.floatValue(), Path.Direction.CW);
            pushTransform(this.atts);
            path3.transform(popTransform());
            this.path.addPath(path3);
            return;
        }
        if (!this.hidden && name.equals("ellipse")) {
            Float floatAttr14 = getFloatAttr("cx", this.atts);
            Float floatAttr15 = getFloatAttr("cy", this.atts);
            Float floatAttr16 = getFloatAttr("rx", this.atts);
            Float floatAttr17 = getFloatAttr("ry", this.atts);
            if (floatAttr14 == null || floatAttr15 == null || floatAttr16 == null || floatAttr17 == null) {
                return;
            }
            this.rect.set(floatAttr14.floatValue() - floatAttr16.floatValue(), floatAttr15.floatValue() - floatAttr17.floatValue(), floatAttr14.floatValue() + floatAttr16.floatValue(), floatAttr15.floatValue() + floatAttr17.floatValue());
            Path path4 = new Path();
            path4.addOval(this.rect, Path.Direction.CW);
            pushTransform(this.atts);
            path4.transform(popTransform());
            this.path.addPath(path4);
            return;
        }
        if (!this.hidden && (name.equals("polygon") || name.equals("polyline"))) {
            NumberParse numberParseAttr2 = NumberParse.getNumberParseAttr("points", this.atts);
            if (numberParseAttr2 != null) {
                Path path5 = new Path();
                ArrayList<Float> arrayList = numberParseAttr2.numbers;
                if (arrayList.size() > 1) {
                    path5.moveTo(arrayList.get(0).floatValue(), arrayList.get(1).floatValue());
                    for (int i2 = 2; i2 < arrayList.size(); i2 += 2) {
                        path5.lineTo(arrayList.get(i2).floatValue(), arrayList.get(i2 + 1).floatValue());
                    }
                    if (name.equals("polygon")) {
                        path5.close();
                    }
                    pushTransform(this.atts);
                    path5.transform(popTransform());
                    this.path.addPath(path5);
                    return;
                }
                return;
            }
            return;
        }
        if (!this.hidden && name.equals("path")) {
            Path doPath = PathParser.doPath(ParseUtil.getStringAttr("d", this.atts));
            pushTransform(this.atts);
            doPath.transform(popTransform());
            this.path.addPath(doPath);
            return;
        }
        if ((this.hidden || !name.equals("metadata")) && !this.hidden) {
            Log.d(TAG, String.format("Unrecognized tag: %s (%s)", name, showAttributes(this.atts)));
        }
    }

    private String showAttributes(XmlPullParser xmlPullParser) {
        String str = "";
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            str = str + " " + xmlPullParser.getAttributeName(i) + "='" + xmlPullParser.getAttributeValue(i) + "'";
        }
        return str;
    }

    void endElement() {
        String name = this.atts.getName();
        if (this.inDefsElement) {
            if (name.equals("defs")) {
                this.inDefsElement = false;
                return;
            }
            return;
        }
        if (name.equals("svg")) {
            Path popPath = popPath();
            popPath.transform(popTransform());
            this.pathInfo = new PathInfo(popPath, this.width, this.height);
        } else if (name.equals("g")) {
            if (this.hidden) {
                int i = this.hiddenLevel - 1;
                this.hiddenLevel = i;
                if (i == 0) {
                    this.hidden = false;
                }
            }
            Path popPath2 = popPath();
            popPath2.transform(popTransform());
            this.path.addPath(popPath2);
        }
    }

    final Float getFloatAttr(String str, XmlPullParser xmlPullParser) {
        return getFloatAttr(str, xmlPullParser, null);
    }

    final Float getFloatAttr(String str, XmlPullParser xmlPullParser, Float f) {
        Float convertUnits = ParseUtil.convertUnits(str, xmlPullParser, this.dpi, this.width, this.height);
        return convertUnits == null ? f : convertUnits;
    }
}
