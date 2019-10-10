package kornienko.controller;

import com.google.api.services.drive.model.File;
import kornienko.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("drive")
public class GoogleDriveController {
    @Autowired
    GoogleDriveService googleDriveService;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public List<File> getAllFiles(@RequestHeader (name = "Authorization") String jwt) throws IOException {
        return googleDriveService.getAllFiles(jwt.substring(7));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = { "multipart/form-data"})
    public @ResponseBody
    String handleFileUpload(@RequestParam("file") MultipartFile newFile,
                            @RequestParam("name") String newName,
                            @RequestHeader (name = "Authorization") String jwt) {
        return googleDriveService.uploadFile(newName, newFile, jwt.substring(7));
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    void handleFileUpload(@RequestParam("id") String newId,
                          HttpServletResponse response,
                          @RequestHeader (name = "Authorization") String jwt) throws IOException {
        googleDriveService.downloadFile(newId, response, jwt.substring(7));
    }
}
