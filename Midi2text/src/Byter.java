import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public class Byter {

    public static final int
            CHANNEL_MASK = 0X0F,
            FIRST_BYTE_MASK = 0b01111111,
            META_TYPE = 0xFF;
    public static final int
            NOTE_OFF_MASK = 0x80,
            NOTE_ON_MASK = 0x90; // voice
    public static final int
            QUARTER_FRAME = 0xF1,
            SYSEX_BEGIN = 0xF0,
            SYSEX_END = 0xF7; // system
    public static final int CHANNEL_PREFIX = 0x20, SET_TEMPO = 0x51, TIME_SIGNATURE = 0x58; // meta

    public static final Map<Integer, Integer> MesSizes = Map.ofEntries(
            entry(0x80, 3),
            entry(0x90, 3),
            entry(0xa0, 3),
            entry(0xb0, 3),
            entry(0xc0, 2),
            entry(0xd0, 2),
            entry(0xe0, 3),
            entry(0xf1, 2),
            entry(0xf2, 3),
            entry(0xf3, 2),
            entry(0xf6, 1),
            entry(0xf8, 1),
            entry(0xfa, 1),
            entry(0xfb, 1),
            entry(0xfc, 1),
            entry(0xfe, 1),
            entry(0xff, 1)
    );

    public static final int makeUnsigned(byte b){
        return b >= 0 ? b : b + 256;
    }
    public static final int readUnsignedBytes(FileInputStream stream) throws IOException {
        int res = stream.read();
        return res >= 0 ? res : res + 256;
    }
    public static final long readUnsignedBytes(FileInputStream stream, int count) throws IOException {
        long res = 0;
        int x;
        for (int i = 0; i < count; i++){
            x = stream.read();
            res = res * 256 + (x >= 0 ? x : x + 256);
        }
        return res;
    }
    public static final boolean isWrongTitle(FileInputStream stream, String right_title) throws IOException {
        byte[] bytes = new byte[4];
        try {
            stream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String(bytes);
        return !s.equals(right_title);
    }

    public static final long readVariableLong(FileInputStream stream) throws IOException {
        long res = 0;
        int b;
        while (true){
            b = readUnsignedBytes(stream);
            res = (res << 7) | (b & FIRST_BYTE_MASK);
            if (b <= FIRST_BYTE_MASK)
                return res;
        }
    }
}