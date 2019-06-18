package src.model.nusmv;

import src.model.ModelGenerator;

public class FileWriter {

    public static void main(String[] args){
        String code = ModelTranslator.fromModelToNuSMV("C:\\Users\\Francesco\\IdeaProjects\\ProjectFM\\FMProject\\hospital.xml");
        if(code != null){
            System.out.println("No file");
        } else {
            System.out.println(code);
        }

    }

}
