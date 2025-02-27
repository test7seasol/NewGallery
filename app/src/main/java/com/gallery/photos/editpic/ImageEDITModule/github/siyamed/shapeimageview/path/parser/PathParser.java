package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;


/* loaded from: classes.dex */
class PathParser {
    private static final String TAG = SvgToPath.TAG;

    PathParser() {
    }

    public static Path doPath(String str) {
        char c;
        int i;
        char c2;
        float f;
        Path path;
        RectF rectF;
        float f2;
        float f3;
        String str2 = str;
        int length = str.length();
        ParserHelper parserHelper = new ParserHelper(str2);
        parserHelper.skipWhitespace();
        Path path2 = new Path();
        RectF rectF2 = new RectF();
        float f4 = 0.0f;
        char c3 = 'x';
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        float f8 = 0.0f;
        float f9 = 0.0f;
        float f10 = 0.0f;
        while (parserHelper.pos < length) {
            char charAt = str2.charAt(parserHelper.pos);
            if (Character.isDigit(charAt) || charAt == '.' || charAt == '-') {
                if (c3 == 'M') {
                    c3 = 'L';
                } else if (c3 == 'm') {
                    c = 'l';
                }
                c = c3;
            } else {
                parserHelper.advance();
                c = charAt;
            }
            boolean z = true;
            path2.computeBounds(rectF2, true);
            switch (c) {
                case 'A':
                case 'a':
                    float nextFloat = parserHelper.nextFloat();
                    float nextFloat2 = parserHelper.nextFloat();
                    float nextFloat3 = parserHelper.nextFloat();
                    int nextFloat4 = (int) parserHelper.nextFloat();
                    int nextFloat5 = (int) parserHelper.nextFloat();
                    float nextFloat6 = parserHelper.nextFloat();
                    float nextFloat7 = parserHelper.nextFloat();
                    if (c == 'a') {
                        nextFloat6 += f5;
                        nextFloat7 += f6;
                    }
                    i = length;
                    float f11 = nextFloat7;
                    float f12 = nextFloat6;
                    c2 = c;
                    f = 0.0f;
                    path = path2;
                    rectF = rectF2;
                    drawArc(path2, f5, f6, f12, f11, nextFloat, nextFloat2, nextFloat3, nextFloat4 == 1, nextFloat5 == 1);
                    f6 = f11;
                    z = false;
                    f5 = f12;
                    break;
                case 'C':
                case 'c':
                    float nextFloat8 = parserHelper.nextFloat();
                    float nextFloat9 = parserHelper.nextFloat();
                    float nextFloat10 = parserHelper.nextFloat();
                    float nextFloat11 = parserHelper.nextFloat();
                    float nextFloat12 = parserHelper.nextFloat();
                    float nextFloat13 = parserHelper.nextFloat();
                    if (c == 'c') {
                        nextFloat8 += f5;
                        nextFloat10 += f5;
                        nextFloat12 += f5;
                        nextFloat9 += f6;
                        nextFloat11 += f6;
                        nextFloat13 += f6;
                    }
                    f9 = nextFloat10;
                    f10 = nextFloat11;
                    float f13 = nextFloat13;
                    float f14 = nextFloat12;
                    path2.cubicTo(nextFloat8, nextFloat9, f9, f10, f14, f13);
                    i = length;
                    f5 = f14;
                    f6 = f13;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    f = 0.0f;
                    break;
                case 'H':
                case 'h':
                    float nextFloat14 = parserHelper.nextFloat();
                    if (c == 'h') {
                        path2.rLineTo(nextFloat14, f4);
                        f5 += nextFloat14;
                        i = length;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                        break;
                    } else {
                        path2.lineTo(nextFloat14, f6);
                        i = length;
                        f5 = nextFloat14;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                    }
                case 'L':
                case 'l':
                    float nextFloat15 = parserHelper.nextFloat();
                    float nextFloat16 = parserHelper.nextFloat();
                    if (c == 'l') {
                        path2.rLineTo(nextFloat15, nextFloat16);
                        f5 += nextFloat15;
                        f6 += nextFloat16;
                        i = length;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                        break;
                    } else {
                        path2.lineTo(nextFloat15, nextFloat16);
                        i = length;
                        f5 = nextFloat15;
                        f6 = nextFloat16;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                    }
                case 'M':
                case 'm':
                    float nextFloat17 = parserHelper.nextFloat();
                    float nextFloat18 = parserHelper.nextFloat();
                    if (c == 'm') {
                        path2.rMoveTo(nextFloat17, nextFloat18);
                        f5 += nextFloat17;
                        f6 += nextFloat18;
                    } else {
                        path2.moveTo(nextFloat17, nextFloat18);
                        f5 = nextFloat17;
                        f6 = nextFloat18;
                    }
                    i = length;
                    f7 = f5;
                    f8 = f6;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    z = false;
                    break;
                case 'Q':
                case 'q':
                    float nextFloat19 = parserHelper.nextFloat();
                    float nextFloat20 = parserHelper.nextFloat();
                    float nextFloat21 = parserHelper.nextFloat();
                    float nextFloat22 = parserHelper.nextFloat();
                    if (c == 'q') {
                        nextFloat21 += f5;
                        nextFloat22 += f6;
                        nextFloat19 += f5;
                        nextFloat20 += f6;
                    }
                    f9 = nextFloat19;
                    f10 = nextFloat20;
                    f2 = nextFloat21;
                    f3 = nextFloat22;
                    path2.cubicTo(f5, f6, f9, f10, f2, f3);
                    i = length;
                    f5 = f2;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    f6 = f3;
                    break;
                case 'S':
                case 's':
                    float nextFloat23 = parserHelper.nextFloat();
                    float nextFloat24 = parserHelper.nextFloat();
                    float nextFloat25 = parserHelper.nextFloat();
                    float nextFloat26 = parserHelper.nextFloat();
                    if (c == 's') {
                        nextFloat23 += f5;
                        nextFloat25 += f5;
                        nextFloat24 += f6;
                        nextFloat26 += f6;
                    }
                    float f15 = nextFloat23;
                    float f16 = nextFloat24;
                    float f17 = nextFloat25;
                    float f18 = nextFloat26;
                    path2.cubicTo((f5 * 2.0f) - f9, (f6 * 2.0f) - f10, f15, f16, f17, f18);
                    i = length;
                    f9 = f15;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    f10 = f16;
                    f5 = f17;
                    f6 = f18;
                    break;
                case 'T':
                case 't':
                    float nextFloat27 = parserHelper.nextFloat();
                    float nextFloat28 = parserHelper.nextFloat();
                    if (c == 't') {
                        nextFloat27 += f5;
                        nextFloat28 += f6;
                    }
                    f2 = nextFloat27;
                    f3 = nextFloat28;
                    f9 = (f5 * 2.0f) - f9;
                    f10 = (2.0f * f6) - f10;
                    path2.cubicTo(f5, f6, f9, f10, f2, f3);
                    i = length;
                    f5 = f2;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    f6 = f3;
                    break;
                case 'V':
                case 'v':
                    float nextFloat29 = parserHelper.nextFloat();
                    if (c == 'v') {
                        path2.rLineTo(f4, nextFloat29);
                        f6 += nextFloat29;
                        i = length;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                        break;
                    } else {
                        path2.lineTo(f5, nextFloat29);
                        i = length;
                        f6 = nextFloat29;
                        f = f4;
                        c2 = c;
                        path = path2;
                        rectF = rectF2;
                        z = false;
                    }
                case 'Z':
                case 'z':
                    path2.close();
                    i = length;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    f5 = f7;
                    f6 = f8;
                    z = false;
                    break;
                default:
                    i = length;
                    f = f4;
                    c2 = c;
                    path = path2;
                    rectF = rectF2;
                    Log.w(TAG, "Invalid path command: " + c2);
                    parserHelper.advance();
                    z = false;
                    break;
            }
            if (!z) {
                f9 = f5;
                f10 = f6;
            }
            parserHelper.skipWhitespace();
            str2 = str;
            c3 = c2;
            length = i;
            f4 = f;
            path2 = path;
            rectF2 = rectF;
        }
        return path2;
    }

    private static void drawArc(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, boolean z, boolean z2) {
        double d8;
        double d9 = (d - d3) / 2.0d;
        double d10 = (d2 - d4) / 2.0d;
        double radians = Math.toRadians(d7 % 360.0d);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double d11 = (cos * d9) + (sin * d10);
        double d12 = ((-sin) * d9) + (d10 * cos);
        double abs = Math.abs(d5);
        double abs2 = Math.abs(d6);
        double d13 = abs * abs;
        double d14 = abs2 * abs2;
        double d15 = d11 * d11;
        double d16 = d12 * d12;
        double d17 = (d15 / d13) + (d16 / d14);
        if (d17 > 1.0d) {
            abs *= Math.sqrt(d17);
            abs2 *= Math.sqrt(d17);
            d13 = abs * abs;
            d14 = abs2 * abs2;
        }
        double d18 = z == z2 ? -1.0d : 1.0d;
        double d19 = d13 * d14;
        double d20 = d13 * d16;
        double d21 = d14 * d15;
        double d22 = ((d19 - d20) - d21) / (d20 + d21);
        if (d22 < 0.0d) {
            d22 = 0.0d;
        }
        double sqrt = d18 * Math.sqrt(d22);
        double d23 = ((abs * d12) / abs2) * sqrt;
        double d24 = sqrt * (-((abs2 * d11) / abs));
        double d25 = ((d + d3) / 2.0d) + ((cos * d23) - (sin * d24));
        double d26 = ((d2 + d4) / 2.0d) + (sin * d23) + (cos * d24);
        double d27 = (d11 - d23) / abs;
        double d28 = (d12 - d24) / abs2;
        double d29 = ((-d11) - d23) / abs;
        double d30 = ((-d12) - d24) / abs2;
        double d31 = (d27 * d27) + (d28 * d28);
        double degrees = Math.toDegrees((d28 < 0.0d ? -1.0d : 1.0d) * Math.acos(d27 / Math.sqrt(d31)));
        double degrees2 = Math.toDegrees(((d27 * d30) - (d28 * d29) < 0.0d ? -1.0d : 1.0d) * Math.acos(((d27 * d29) + (d28 * d30)) / Math.sqrt(d31 * ((d29 * d29) + (d30 * d30)))));
        if (z2 || degrees2 <= 0.0d) {
            d8 = 360.0d;
            if (z2 && degrees2 < 0.0d) {
                degrees2 += 360.0d;
            }
        } else {
            d8 = 360.0d;
            degrees2 -= 360.0d;
        }
        path.addArc(new RectF((float) (d25 - abs), (float) (d26 - abs2), (float) (d25 + abs), (float) (d26 + abs2)), (float) (degrees % d8), (float) (degrees2 % d8));
    }
}
