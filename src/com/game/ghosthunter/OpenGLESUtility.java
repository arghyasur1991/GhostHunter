/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.game.ghosthunter;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author sur
 */
public class OpenGLESUtility {
    
    public static FloatBuffer createBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * (Float.SIZE / 8));
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }
    
    public static ShortBuffer createBuffer(short[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * (Short.SIZE / 8));
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = bb.asShortBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }
    
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    
    public static int[] getGLBuffer(int width, int height) {
        int b[] = new int[width * height];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        return b;
    }
    
    public static Bitmap getGLBitmap(int[] b, int width, int height) {
        int bt[] = new int[width * height];
        /*  remember, that OpenGL bitmap is incompatible with 
         Android bitmap and so, some correction need.
         */
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix = b[i * width + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                bt[(height - i - 1) * width + j] = pix1;
            }
        }
        Bitmap sb = Bitmap.createBitmap(bt, width, height, Bitmap.Config.ARGB_8888);
        return sb;
    }
    
    public static Bitmap getGLBitmapNonAlpha(int width, int height) {
        int screenshotSize = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, bb);
            //gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA,
        //       GL10.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        for(int i = 0; i < screenshotSize; i++) {
            pixelsBuffer[i] = Integer.rotateRight(pixelsBuffer[i], 8);
        }
        bb = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixelsBuffer, screenshotSize - width, -width, 0,
                0, width, height);
        pixelsBuffer = null;

        short sBuffer[] = new short[screenshotSize];
        ShortBuffer sb = ShortBuffer.wrap(sBuffer);
        bitmap.copyPixelsToBuffer(sb);

        // Making created bitmap (from OpenGL points) compatible with
        // Android bitmap
        for (int i = 0; i < screenshotSize; ++i) {
            short v = sBuffer[i];
            sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
        }
        sb.rewind();
        bitmap.copyPixelsFromBuffer(sb);
        return bitmap;
    }
}
