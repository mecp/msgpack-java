package org.msgpack.unpacker;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.CharacterCodingException;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.JSON;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.JSONPacker;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;
import org.msgpack.type.RawValue;
import org.msgpack.type.ValueFactory;

public class TestMalformedEncoding {
    private byte[][] malforms = new byte[][] {
        { (byte)0xc0, (byte)0xaf },             // '/' in 2 bytes
        { (byte)0xe0, (byte)0x80, (byte)0xaf }  // '/' in 3 bytes
    };

    @Test
    public void testRawValueGetString() throws Exception {
        for(byte[] malform : malforms) {
            RawValue r = ValueFactory.rawValue(malform);
            try {
                r.getString();
                fail("no exception");
            } catch (MessageTypeException expected) {
            }
            byte[] a = r.getByteArray();
            assertArrayEquals(malform, a);
        }
    }

    @Test
    public void testBufferUnpackerUnpackString() throws Exception {
        for(byte[] malform : malforms) {
            MessagePack msgpack = new MessagePack();
            BufferPacker pk = msgpack.createBufferPacker();
            pk.writeByteArray(malform);
            byte[] b = pk.toByteArray();
            Unpacker u = msgpack.createBufferUnpacker(b);
            try {
                u.readString();
                fail("no exception");
            } catch (MessageTypeException expected) {
            }
            byte[] a = u.readByteArray();
            assertArrayEquals(malform, a);
        }
    }

    @Test
    public void testUnpackerUnpackString() throws Exception {
        for(byte[] malform : malforms) {
            MessagePack msgpack = new MessagePack();
            BufferPacker pk = msgpack.createBufferPacker();
            pk.writeByteArray(malform);
            byte[] b = pk.toByteArray();
            Unpacker u = msgpack.createUnpacker(new ByteArrayInputStream(b));
            try {
                u.readString();
                fail("no exception");
            } catch (MessageTypeException expected) {
            }
            byte[] a = u.readByteArray();
            assertArrayEquals(malform, a);
        }
    }

    @Test
    public void testConverterUnpackString() throws Exception {
        for(byte[] malform : malforms) {
            MessagePack msgpack = new MessagePack();
            RawValue r = ValueFactory.rawValue(malform);
            Converter u = new Converter(msgpack, r);
            try {
                u.readString();
                fail("no exception");
            } catch (MessageTypeException expected) {
            }
            byte[] a = u.readByteArray();
            assertArrayEquals(malform, a);
        }
    }

    @Test
    public void testJSONPackerWriteString() throws Exception {
        for(byte[] malform : malforms) {
            JSON json = new JSON();
            Packer pk = json.createPacker(new ByteArrayOutputStream());
            try {
                pk.writeByteArray(malform);
                fail("no exception");
            } catch (CharacterCodingException expected) {
            }
        }
    }

    @Test
    public void testJSONBufferPackerWriteString() throws Exception {
        for(byte[] malform : malforms) {
            JSON json = new JSON();
            Packer pk = json.createBufferPacker();
            try {
                pk.writeByteArray(malform);
                fail("no exception");
            } catch (CharacterCodingException expected) {
            }
        }
    }
}

