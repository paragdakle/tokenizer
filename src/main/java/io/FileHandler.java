package io;

import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileHandler {

    @Setter
    private String filePath = "";

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    public Collection<String> read() {
        List<String> content = new ArrayList<String>();
        if(filePath.equals("")) {
            return null;
        }
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = "";
            while ((line = reader.readLine()) != null) {
                content.add(line.trim());
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
