public class Message {
    int status_byte;
    long delta, real_time;
    int[] data;

    public Message(int status_byte, int[] data, long delta, long real_time) {
        this.status_byte = status_byte;
        this.data = data;
        this.delta = delta;
        this.real_time = real_time;
    }

    public int getStatus_byte() {
        return status_byte;
    }

    public long getDelta() {
        return delta;
    }

    public long getReal_time() {
        return real_time;
    }

    public int[] getData() {
        return data;
    }

    public String getMessageInfo(){
        if ((status_byte & Byter.SYSEX_BEGIN) == Byter.NOTE_OFF_MASK){
            return "Note off: channel - " + (status_byte & Byter.CHANNEL_MASK) + ", note value - " + data[0] + ", velocity - " + data[1] + ", delta time - " +  delta + ", real time - " + real_time + "\n";
        }
        if ((status_byte & Byter.SYSEX_BEGIN) == Byter.NOTE_ON_MASK){
            return "Note on: channel - " + (status_byte & Byter.CHANNEL_MASK) +  ", note value - " + data[0] + ", velocity - " + data[1] + ", delta time - " +  delta + ", real time - " + real_time + "\n";
        }
        return "";
    }
}