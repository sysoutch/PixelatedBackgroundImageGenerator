package ch.sysout.pixelatedbackgroundimagegenerator.util;

import java.awt.Color;

public class ColorUtil {

	public static int getComplementaryAsRGB(int baseColor) {
		int r = baseColor & 255;
		int g = (baseColor >> 8) & 255;
		int b = (baseColor >> 16) & 255;
		int a = (baseColor >> 24) & 255;
		r = 255 - r;
		g = 255 - g;
		b = 255 - b;
		return r + (g << 8) + (b << 16) + (a << 24);
	}

	public static int getComplementaryAsRGB(Color baseColor) {
		int intColor = baseColor.getRGB();
		return getComplementaryAsRGB(intColor);
	}

	public static Color getComplementaryAsColor(Color baseColor) {
		int intColor = baseColor.getRGB();
		return new Color(getComplementaryAsRGB(intColor));
	}
}
