package itsjustaaron.movietogether;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Aaron on 2016-01-11.
 */
public class Util {
    public static void writeLine(Context context, String fileName, ArrayList<String> entries) {
        try {
            ArrayList<String> store = new ArrayList<String>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(context.getFileStreamPath(fileName)));
            String input = bufferedReader.readLine();
            while (input != null) {
                store.add(input);
                input = bufferedReader.readLine();
            }
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            for (int i = 0; i < store.size(); i++) {
                outputStream.write(store.get(i).getBytes());
                outputStream.write("\n".getBytes());
            }
            for (int i = 0; i < entries.size(); i++) {
                outputStream.write(entries.get(i).getBytes());
                outputStream.write("\n".getBytes());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeLine(Context context, String fileName, String entry) {
        try {
            ArrayList<String> store = new ArrayList<String>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(context.getFileStreamPath(fileName)));
            String input = bufferedReader.readLine();
            while (input != null) {
                store.add(input);
                input = bufferedReader.readLine();
            }
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            for (int i = 0; i < store.size(); i++) {
                outputStream.write(store.get(i).getBytes());
                outputStream.write("\n".getBytes());
            }
                outputStream.write(entry.getBytes());
                outputStream.write("\n".getBytes());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
