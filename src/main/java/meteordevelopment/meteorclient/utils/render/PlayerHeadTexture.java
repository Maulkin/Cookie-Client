package meteordevelopment.meteorclient.utils.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.Http;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static meteordevelopment.meteorclient.CookieClient.mc;

public class PlayerHeadTexture extends Texture {
    private boolean needsRotate;

    public PlayerHeadTexture(String url) {
        super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);

        BufferedImage skin;
        try {
            skin = ImageIO.read(Http.get(url).sendInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte[] head = new byte[8 * 8 * 4];
        int[] pixel = new int[4];

        int i = 0;
        for (int x = 8; x < 16; x++) {
            for (int y = 8; y < 16; y++) {
                skin.getData().getPixel(x, y, pixel);

                for (int j = 0; j < 4; j++) {
                    head[i] = (byte) pixel[j];
                    i++;
                }
            }
        }

        i = 0;
        for (int x = 40; x < 48; x++) {
            for (int y = 8; y < 16; y++) {
                skin.getData().getPixel(x, y, pixel);

                if (pixel[3] != 0) {
                    for (int j = 0; j < 4; j++) {
                        head[i] = (byte) pixel[j];
                        i++;
                    }
                }
                else i += 4;
            }
        }

        upload(BufferUtils.createByteBuffer(head.length).put(head));

        needsRotate = true;
    }

    public PlayerHeadTexture() {
        super(8, 8, TextureFormat.RGBA8, FilterMode.NEAREST, FilterMode.NEAREST);

        try (InputStream inputStream = mc.getResourceManager().getResource(CookieClient.identifier("textures/steve.png")).get().getInputStream()) {
            ByteBuffer data = TextureUtil.readResource(inputStream);
            data.rewind();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer image = STBImage.stbi_load_from_memory(data, width, height, comp, 4);
                upload(image);
                STBImage.stbi_image_free(image);
            }
            MemoryUtil.memFree(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean needsRotate() {
        return needsRotate;
    }
}
