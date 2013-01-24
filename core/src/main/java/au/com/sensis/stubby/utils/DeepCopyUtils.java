package au.com.sensis.stubby.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeepCopyUtils {
    
    public static Object deepCopy(Object src) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            new ObjectOutputStream(outStream).writeObject(src);
            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            return new ObjectInputStream(inStream).readObject();
        } catch (Exception e) {
            throw new RuntimeException("Error performing deep copy", e);
        }
    }
    
}
