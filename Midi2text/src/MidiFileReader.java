import java.io.FileInputStream;
import java.io.IOException;

public class MidiFileReader {
    private int file_format, num_of_tracks;
    private  byte[] time_division = new byte[2];
    private Track[] tracks;
    private FileInputStream stream;

    public MidiFileReader(String filepath) throws IOException, MidiException {
        stream = new FileInputStream(filepath);
        readFileHeader();
        tracks = new Track[num_of_tracks];
        for (int i = 0; i < num_of_tracks; i++) {
            tracks[i] = readTrack();
        }
    }

    private void readFileHeader() throws IOException, MidiException {
        if (Byter.isWrongTitle(stream, "MThd"))
            throw new MidiException("Wrong Title (Must be MThd)");
        if (Byter.readUnsignedBytes(stream, 4) != 6)
            throw new MidiException("Wrong MThd Title size (Must be 6)");
        file_format = (int) Byter.readUnsignedBytes(stream, 2);
        num_of_tracks = (int) Byter.readUnsignedBytes(stream, 2);
        stream.read(time_division);
    }

    private Track readTrack() throws MidiException, IOException {
        if (Byter.isWrongTitle(stream, "MTrk"))
            throw new MidiException("Wrong Title (Must be MTrk)");
        long size = Byter.readUnsignedBytes(stream, 4);
        Track track = new Track();
        track.setSize(size);
        int last_status = -1;
        long real_time = 0;
        byte data_byte = -1;
        int tracks_stream_size = stream.available();
        while (tracks_stream_size - stream.available() < size) {
                long delta = Byter.readVariableLong(stream);
                real_time += delta;

                int status_byte = Byter.readUnsignedBytes(stream);

                // исправляет отсутствие опущенного статус-байта
                if (status_byte < Byter.FIRST_BYTE_MASK) {
                    if (last_status == -1)
                        throw new MidiException("Track reader error (Data-byte without status-byte)");
                    data_byte = (byte) status_byte;
                    status_byte = last_status;
                } else {
                    if (status_byte != Byter.META_TYPE) {
                        last_status = status_byte;
                    }
                    data_byte = -1;
                }

                // работает с самими сообщениями
                if (status_byte == Byter.META_TYPE) {
                    track.addMetaMessage(stream, delta, real_time);
                } else if ((status_byte == Byter.SYSEX_BEGIN) ){
                    Byter.readUnsignedBytes(stream, (int) Byter.readVariableLong(stream));
                } else {
                    track.addMessage(stream, status_byte, data_byte, delta, real_time);
                }
            }
            return track;
        }

    public byte[] getTimeDivision() {
        return time_division;
    }

    public Track[] getTracks() {
        return tracks;
    }

    public String getMidiInfo(){
        String res = "";
        for (int i = 0; i < tracks.length; i++) {
            res += "Track № " + i + "\n" + tracks[i].getTrackInfo();
        }
        return res;
    }
}
