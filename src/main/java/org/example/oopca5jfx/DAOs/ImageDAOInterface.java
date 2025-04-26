// ImageDAOInterface.java
package org.example.oopca5jfx.DAOs;

import java.util.List;

public interface ImageDAOInterface {
    List<String> getAllImageNames();
    byte[] getImageData(String imageName);
}