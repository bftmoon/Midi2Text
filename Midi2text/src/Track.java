import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Track {
    private long size;
    private ArrayList<Message> messages = new ArrayList<>();

    void setSize(long size) {
        this.size = size;
    }

    void addMetaMessage(FileInputStream stream, long delta, long real_time) throws IOException {
        int meta_type = Byter.readUnsignedBytes(stream);
        int length = Byter.readUnsignedBytes(stream);
        int[] data = new int[length];

        for (int i = 0; i < length; i++) {
            data[i] = Byter.readUnsignedBytes(stream);
        }
        if (meta_type == Byter.SET_TEMPO)// || (meta_type == Byter.TIME_SIGNATURE))
            messages.add(new Message(meta_type, data, delta, real_time));
    }

    void addMessage(FileInputStream stream, int status_byte, byte data_byte, long delta, long real_time) throws MidiException, IOException {
        int mask = (status_byte < 0xF0) ?  0xF0 : 0xFF;
            Integer size = Byter.MesSizes.get(status_byte & mask);
        if (!(size instanceof Integer))
            throw new MidiException("Error of Messages HashMap (Key not found)");
        size--; // status byte
        int[] data = new int[size];
        int i_of_begin = 0;
        if (data_byte != -1){
            data[i_of_begin] = data_byte;
            i_of_begin++;
        }
            for (int i = i_of_begin; i < size; i++) {
                data[i] = stream.read();
                if (data[i] > 127)
                    throw new MidiException("Status byte was read as data byte");
            }
            if (((status_byte & Byter.NOTE_OFF_MASK) == Byter.NOTE_OFF_MASK) || ((status_byte & Byter.NOTE_ON_MASK) == Byter.NOTE_ON_MASK))
                messages.add(new Message(status_byte, data, delta, real_time));
    }

    public Message[] getMessages() {
        return messages.toArray(new Message[messages.size()]);
    }
    
    public String getTrackInfo(){
        String res = "";
        for (Message message : messages) {
            res += message.getMessageInfo();
        }
        return res;
    }
}

