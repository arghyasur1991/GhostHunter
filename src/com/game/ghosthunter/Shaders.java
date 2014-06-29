/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.game.ghosthunter;

import android.opengl.GLES20;

/**
 *
 * @author sur
 */
public class Shaders {
    private int mProgram;
    private final GLCameraRenderer mRenderer;
    private final VertexShader mVertexShader;
    private final FragmentShader mFragmentShader;
    private int mTextureIndex;
    
    public Shaders(GLCameraRenderer renderer, String vShaderCode, String fShaderCode) {
        mRenderer = renderer;
        int vIndex = 0;
        int fIndex = 0;
        
        if(fShaderCode.equalsIgnoreCase(FragmentShader.texture()))
            fIndex = 1;
        else if(fShaderCode.equalsIgnoreCase(FragmentShader.texture2D()))
            fIndex = 2;
        else if (fShaderCode.equalsIgnoreCase(FragmentShader.textureBW()))
            fIndex = 3;
        else if (fShaderCode.equalsIgnoreCase(FragmentShader.textureChromaKey())) 
            fIndex = 4;
        else if (fShaderCode.equalsIgnoreCase(FragmentShader.textureChromaKeyYUV()))
            fIndex = 5;
        
        mVertexShader = new VertexShader(vIndex);
        mFragmentShader = new FragmentShader(fIndex);
    }
    
    public void setTextureIndex(int index) {
        mTextureIndex = index;
    }
    
    public String getVertexShaderCode() {
        return mVertexShader.getCode();
    }
    
    public String getFragmentShaderCode() {
        return mFragmentShader.getCode();
    }
    
    public void setProgram(int program) {
        mProgram = program;
    }
    
    public void doShaderSpecificTasks() {
        int texHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        //int chromaKeyHandle = GLES20.glGetUniformLocation(mProgram, "uKey");
        
        switch(mFragmentShader.getIndex()) {
            case 0:
                break;
            case 1:
            case 2:
            case 3:
                GLES20.glUniform1i(texHandle, mTextureIndex);
                break;
            case 4:
            case 5:
                //GLES20.glUniform4fv(chromaKeyHandle, 1, mRenderer.getKey(), 0);
                GLES20.glUniform1i(texHandle, mTextureIndex);
                break;
            default:
        }
    }
    
    public static class VertexShader {
        private final int mIndex;
        
        public VertexShader(int index) {
            mIndex = index;
        }

        public int getIndex() {
            return mIndex;
        }

        public String getCode() {
            switch (mIndex) {
                case 0:
                    return texture();
                default:
                    return "";
            }
        }
        
        public static String texture() {
            final String vertexShaderCode
                    = "uniform mat4 uMVPMatrix;\n"
                    + "uniform mat4 uSTMatrix;\n"
                    + "attribute vec4 aPosition;\n"
                    + "attribute vec4 aTextureCoord;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "void main() {\n"
                    + "    gl_Position = uMVPMatrix * aPosition;\n"
                    + "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n"
                    + "}\n";

            return vertexShaderCode;
        }
    }
    
    public static class FragmentShader {
        private final int mIndex;

        public FragmentShader(int index) {
            mIndex = index;
        }
        
        public int getIndex() {
            return mIndex;
        }

        public String getCode() {
            switch (mIndex) {
                case 0:
                    return color();
                case 1:
                    return texture();
                case 2:
                    return texture2D();
                case 3:
                    return textureBW();
                case 4:
                    return textureChromaKey();
                case 5: 
                    return textureChromaKeyYUV();
                default:
                    return "";
            }
        }
        
        public static String color() {
            final String fragmentShaderCode
                = "precision mediump float;"
                + "uniform vec4 vColor;"
                + "void main() {"
                + "  gl_FragColor = vColor;"
                + "}";
            
            return fragmentShaderCode;
        }
        
        public static String texture() {
            final String fragmentShaderCode
                    = "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
                    + "}\n";
            return fragmentShaderCode;
        }
        
        public static String texture2D() {
            final String fragmentShaderCode
                    = "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform sampler2D sTexture;\n"
                    + "void main() {\n"
                    + "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
                    + "}\n";
            return fragmentShaderCode;
        }
        
        public static String textureBW() {
            final String fragmentShaderCode
                    = "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "    vec4 Ca = texture2D(sTexture, vTextureCoord); \n"
                    + "    float lum = 0.2126 * Ca.r + 0.7152 * Ca.g + 0.0722 * Ca.b; \n"
                    + "    float alpha = 1.0; \n"
                    + "    gl_FragColor = vec4(lum, lum, lum, alpha);\n"
                    + "}\n";
            return fragmentShaderCode;
        }
        
        public static String textureChromaKey() {
            final String fragmentShaderCode
                    = "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform vec4 uKey;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "    vec4 Ca = texture2D(sTexture, vTextureCoord); \n"
                    + "float alpha = 1.0; \n"
                    + "float threshold = uKey.a; \n"
                    + "float redDiff = uKey.r - Ca.r; \n"
                    + "float greenDif = uKey.g - Ca.g; \n"
                    + "float blueDiff = uKey.b - Ca.b; \n"
                    + "if(abs(redDiff) < threshold && abs(greenDif) < threshold && abs(blueDiff) < threshold) \n"
                    + "alpha = 0.0; \n"
                    + "  gl_FragColor = vec4(Ca.r, Ca.g, Ca.b, alpha);\n"
                    + "}";
            return fragmentShaderCode;
        }
        
        public static String textureChromaKeyYUV() {
            final String fragmentShaderCode
                    = "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform vec4 uKey;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "    vec4 Ca = texture2D(sTexture, vTextureCoord); \n"
                    + "float yDiff = 0.299 * (Ca.r - uKey.r) + 0.587 * (Ca.g - uKey.g) + 0.114 * (Ca.b - uKey.b); \n"
                    + "float uDiff = -0.1471 * (Ca.r - uKey.r) - 0.28886 * (Ca.g - uKey.g) + 0.436 * (Ca.b - uKey.b); \n"
                    + "float vDiff = 0.615 * (Ca.r - uKey.r) - 0.51499 * (Ca.g - uKey.g) - 0.10001 * (Ca.b - uKey.b); \n"
                    + "float alpha = 1.0; \n"
                    + "float threshold = uKey.a; \n"
                    + "if(abs(yDiff) < 0.2 && abs(uDiff) < 0.15 && abs(vDiff) < 0.15) \n"
                    + "alpha = 0.0; \n"
                    + "  gl_FragColor = vec4(Ca.r, Ca.g, Ca.b, alpha);\n"
                    + "}";
            return fragmentShaderCode;
        }
    }
}
