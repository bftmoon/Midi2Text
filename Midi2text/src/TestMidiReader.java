import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TestMidiReader {

    public static void main(String[] args) throws IOException, MidiException {
        System.out.println(new MidiFileReader(readPath()).getMidiInfo());
    }

    static String readPath(){
        Scanner scanner = new Scanner(System.in);
        String path;
        while (true) {
            System.out.println("Write Midi filepath (C:\\...\\...\\abc.mid)");
            path = scanner.nextLine();
            System.out.println(path);
            if (new File(path).exists()) {
                String[] str = path.split("[.]");
                if (str[str.length - 1].equalsIgnoreCase("mid")) {
                    return path;
                } else {
                    System.out.println("Wrong file format");
                }
            } else {
                System.out.println("Wrong filepath");
            }
        }
    }
}
