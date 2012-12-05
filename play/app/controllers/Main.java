package controllers;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import play.data.Upload;
import play.mvc.Controller;

/*
 * Main controller for UI
 */
public class Main extends Controller {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    public static void index() {
        //List<StubbedResponse> responses = GenericStubController.RESPONSES;
        //render(responses); // ensure template parameter name is consistent
    }

    public static void upload(File fake) {
        List<Upload> files = (List<Upload>) request.args.get("__UPLOADS");
        for (Upload file : files) {
            LOGGER.info("Received uploaded file: " + file.getFileName());
        }
    }

}
