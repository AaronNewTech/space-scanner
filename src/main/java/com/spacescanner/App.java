package com.spacescanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spacescanner.service.SpaceXApiService;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class App {

    public static void main(String[] args) {
        
        SpaceXApiService spaceXApiService = new SpaceXApiService();
    
        String outputPath = "output.json";
    
        try (PrintStream originalSystemOut = System.out;
             PrintStream filePrintStream = new PrintStream(new File(outputPath))) {
    
            try {
                String spaceXApiResponse = spaceXApiService.getSpaceXApiResponse();
    
                ObjectMapper objectMapper = new ObjectMapper();
                Object json = objectMapper.readValue(spaceXApiResponse, Object.class);
    
                ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    
                System.out.println(objectWriter.writeValueAsString(json));
    
                objectWriter.writeValue(new File(outputPath), json);
    
                System.out.println("Output has been saved to: " + outputPath);
    
            } catch (IOException e) {
                
                e.printStackTrace();

            } catch (Exception e) { 
                e.printStackTrace();
            }
    
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
}
