package src.model.nusmv;

import src.model.ModelGenerator;

public class FileWriter {

    public static void main(String[] args){
        String code = ModelTranslator.fromModelToNuSMV("C:\\Users\\Francesco\\IdeaProjects\\ProjectFM\\FMProject\\hospital.xml");
        System.out.println(code);
    }

}
