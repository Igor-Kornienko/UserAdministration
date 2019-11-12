package kornienko.controller;

import kornienko.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("drive")
public class GoogleDriveController {
    @Autowired
    GoogleDriveService googleDriveService;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<?> getAllFiles(@RequestHeader (name = "Authorization") String jwt) throws IOException {
        return googleDriveService.getAllFiles(jwt);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = { "multipart/form-data"})
    public @ResponseBody ResponseEntity<?> handleFileUpload(@RequestHeader (name = "Authorization") String jwt,
                            @RequestParam("file") MultipartFile newFile,
                            @RequestParam("name") String newName) {
        return googleDriveService.uploadFile(newName, newFile, jwt);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody ResponseEntity<?> handleFileUpload(@RequestParam ("Authorization") String jwt,
                          @RequestParam("id") String newId,
                          HttpServletResponse response) throws IOException {
        System.out.println("download " + newId);
        return googleDriveService.downloadFile(newId, response, jwt);
    }
}
