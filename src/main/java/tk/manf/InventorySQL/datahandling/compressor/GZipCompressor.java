/**
 * Copyright (c) 2013 Exo-Network
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 * 
 * manf                   info@manf.tk
 */

package tk.manf.InventorySQL.datahandling.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import tk.manf.InventorySQL.datahandling.Compressor;
import tk.manf.InventorySQL.datahandling.exceptions.CompressionException;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class GZipCompressor implements Compressor {
    public byte[] compress(byte[] uncomp) throws CompressionException {
        try {
            @Cleanup
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GZIPOutputStream compressor = new GZIPOutputStream(os);
            compressor.write(uncomp);
            compressor.close();
            return os.toByteArray();
        } catch (IOException ex) {
            throw new CompressionException(new String(uncomp), ex);
        }
    }

    public byte[] uncompress(byte[] comp) throws CompressionException {
        GZIPInputStream decompressor = null;
        try {
            @Cleanup
            ByteArrayInputStream is = new ByteArrayInputStream(comp);
            decompressor = new GZIPInputStream(is);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] b = new byte[256];
            int tmp;
            while ((tmp = decompressor.read(b)) != -1) {
                buffer.write(b, 0, tmp);
            }
            buffer.close();
            return buffer.toByteArray();
        } catch (IOException ex) {
            throw new CompressionException(new String(comp), ex);
        } finally {
            try {
                if (decompressor != null) {
                    decompressor.close();
                }
            } catch (IOException ex) {
                throw new CompressionException("closing decompressor!", ex);
            }
        }
    }

}
