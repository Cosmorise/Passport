package me.combimagnetron.passport.internal.entity.metadata.type;

import me.combimagnetron.passport.internal.network.ByteBuffer;

public record Byte(byte val) implements MetadataType {

    public static Byte of(byte val) {
        return new Byte(val);
    }

    @Override
    public byte[] bytes() {
        final ByteBuffer buffer = ByteBuffer.empty();
        buffer.write(ByteBuffer.Adapter.BYTE, val);
        return buffer.bytes();
    }
}
