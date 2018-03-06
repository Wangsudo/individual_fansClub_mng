package com.soccer.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BeanCopy {
     public static Object copy(Object o) throws IOException, ClassNotFoundException{
	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   ObjectOutputStream oos = new ObjectOutputStream(bos);
	   oos.writeObject(o);
	   ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
	   return ois.readObject();
	  }
}
