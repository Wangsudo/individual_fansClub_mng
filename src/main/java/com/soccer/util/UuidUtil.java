package com.soccer.util;

import java.util.UUID;

public class UuidUtil {
	public static final String newUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
