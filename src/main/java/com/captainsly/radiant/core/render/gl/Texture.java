package com.captainsly.radiant.core.render.gl;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import com.captainsly.radiant.core.impl.Disposable;

public class Texture implements Disposable {

	private int textureId;
	private String texturePath;

	public Texture(int width, int height, ByteBuffer buffer) {
		this.texturePath = "";
		textureId = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, textureId);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	public Texture(String texturePath) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			this.texturePath = texturePath;
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer buffer = stbi_load(texturePath, w, h, channels, 4);
			if (buffer == null)
				throw new RuntimeException("Image file [" + texturePath + "] could not be loaded");

			int width = w.get();
			int height = h.get();

			System.out.println(texturePath);
			textureId = glGenTextures();

			glBindTexture(GL_TEXTURE_2D, textureId);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			glGenerateMipmap(GL_TEXTURE_2D);
			stbi_image_free(buffer);
		}
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	public String getTexturePath() {
		return texturePath;
	}

	@Override
	public void onDispose() {
		glDeleteTextures(textureId);
	}

}
