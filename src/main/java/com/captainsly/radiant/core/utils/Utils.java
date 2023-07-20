package com.captainsly.radiant.core.utils;

import java.util.List;

public class Utils {

	public static float[] listFloatToArray(List<Float> list) {
		int size = list != null ? list.size() : 0;
		float[] floatArray = new float[size];
		for (int i = 0; i < size; i++) {
			floatArray[i] = list.get(i);
		}

		return floatArray;
	}

	public static int[] listIntToArray(List<Integer> list) {
		return list.stream().mapToInt((Integer v) -> v).toArray();
	}

}
