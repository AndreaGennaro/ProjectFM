package src.model.nusmv;

import src.model.ModelGenerator;

import java.io.FileWriter;

public class NuSMVFileWriter {

    public static void main(String[] args){
        String code = ModelTranslator.fromModelToNuSMV("C:\\Users\\Francesco\\IdeaProjects\\ProjectFM\\FMProject\\hospital.xml");
        writeFile("C:\\Users\\Francesco\\IdeaProjects\\ProjectFM\\FMProject\\session.smv", code);
    }

    private static void writeFile(String filepath, String code){
        try{
            FileWriter writer = new FileWriter(filepath);
            writer.write(code);
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Success!");
    }

}
