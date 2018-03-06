package com.soccer.util;

import java.util.Random;

/**
 * GenRandomNums 类用于随机产生六位验证码，包含大小写字母和数字
 * @author 002145
 *
 */
public class GenRandomNums {
	public  static String genRandomNum(int pwd_len){ 
		final int maxNum = 62; 
		int i; 
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 
				       'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				       'w', 'x', 'y', 'z', 
				       'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 
				       'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				       'W', 'X', 'Y', 'Z', 
				       '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random(); 
		while(count < pwd_len){ 
			i = Math.abs(r.nextInt(maxNum)); 
			if (i >= 0 && i < str.length) 
			{   pwd.append(str[i]); 
			    count ++;
			} 
	    } 
        return pwd.toString(); 
    }  
}
