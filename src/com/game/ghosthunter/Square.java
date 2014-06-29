/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.ghosthunter;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
/**
 *
 * @author sur
 */
public class Square {
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final float ASPECT_RATIO = (float) 16/9;
    
    private final Shaders mShader;
    
    private final float[] mTriangleVerticesData = {
        // X, Y, Z, U, V
        -ASPECT_RATIO, -1.0f, 0, 1.f, 0.f,
        ASPECT_RATIO, -1.0f, 0, 0.f, 0.f,
        -ASPECT_RATIO, 1.0f, 0, 1.f, 1.f,
        ASPECT_RATIO, 1.0f, 0, 0.f, 1.f,};

    private final FloatBuffer mTriangleVertices;

    private final int mProgram;
    private final int muMVPMatrixHandle;
    private final int muSTMatrixHandle;
    private final int maPositionHandle;
    private final int maTextureHandle;

    public Square(Shaders shader) {
        mShader = shader;
        
        mTriangleVertices = ByteBuffer.allocateDirect(
                mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);

        int vertexShader = OpenGLESUtility.loadShader(GLES20.GL_VERTEX_SHADER, mShader.getVertexShaderCode());
        int fragmentShader = OpenGLESUtility.loadShader(GLES20.GL_FRAGMENT_SHADER, mShader.getFragmentShaderCode());

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
        
        mShader.setProgram(mProgram);
        
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
    }

    public void draw(float[] mMVPMatrix, float[] mSTMatrix) {
        GLES20.glUseProgram(mProgram);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        
        mShader.doShaderSpecificTasks();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glDisableVertexAttribArray(maTextureHandle);
    }
}
