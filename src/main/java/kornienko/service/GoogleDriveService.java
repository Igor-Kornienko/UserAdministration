package kornienko.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import kornienko.listener.FileDownloadProgressListener;
import kornienko.listener.FileUploadProgressListener;
import kornienko.model.User;
import kornienko.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {
    @Autowired
    UserElasticsearchService elasticsearchService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    private final String APPLICATION_NAME = "UserAdministration";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT;
    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private final String CLIENT_SECRETS_FILE_PATH = "client_secret.json";

    private final GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;

    private GoogleDriveService() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(new java.io.File(CLIENT_SECRETS_FILE_PATH))));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .build();
    }

    private Drive getDrive(GoogleCredential credential) {
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private File createGoogleFile(GoogleCredential googleCredential, String googleFolderIdParent, String contentType,
                                  String customFileName, InputStream inputStream, long size) throws IOException {

        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        File fileMetadata = new File();
        fileMetadata.setName(customFileName);

        List<String> parents = Collections.singletonList(googleFolderIdParent);
        fileMetadata.setParents(parents);

        Drive driveService = getDrive(googleCredential);

        Drive.Files.Create createF = driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, webContentLink, webViewLink, parents");

        MediaHttpUploader uploader = createF.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(false);
        uploader.setChunkSize(1024 * 1024 * 8);
        uploader.setProgressListener(new FileUploadProgressListener(fileMetadata.getName(), size));

        return createF.execute();
    }

    public ResponseEntity<?> getAllFiles(String jwt) throws IOException {
        GoogleCredential googleCredential = getCredentialFromResponseToken(jwt);
        if (googleCredential != null) {
            Drive service = getDrive(googleCredential);
            FileList result = service.files().list()
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            return ResponseEntity.ok(result.getFiles());
        } else {
            return ResponseEntity.badRequest().body("Haven`t google auth");
        }
    }

    public ResponseEntity<?> uploadFile(String name, MultipartFile file, String jwt){
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            if (name.equals("")) {
                name = file.getOriginalFilename();
            }
            GoogleCredential googleCredential = getCredentialFromResponseToken(jwt);
            if (googleCredential != null) {
                return ResponseEntity.ok("File ID: " + createGoogleFile(googleCredential, null, file.getContentType(), name, file.getInputStream(), file.getSize()).getId());
            }
            return ResponseEntity.badRequest().body("Haven`t google auth");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    public ResponseEntity<?> downloadFile(String id, HttpServletResponse response, String jwt) throws IOException {
        GoogleCredential googleCredential = getCredentialFromResponseToken(jwt);
        if (googleCredential != null) {
            Drive service = getDrive(googleCredential);

            Drive.Files.Get fileGet = service.files().get(id);
            File gFile = fileGet.execute();

            response.setHeader("Content-Disposition", "attachment; filename=" + gFile.getName());
            response.setHeader("Content-Type", gFile.getMimeType());

            MediaHttpDownloader downloader = fileGet.getMediaHttpDownloader();
            downloader.setDirectDownloadEnabled(false);
            downloader.setChunkSize(1024 * 1024 * 8);
            downloader.setProgressListener(new FileDownloadProgressListener(gFile.getName()));
            fileGet.executeMediaAndDownloadTo(response.getOutputStream());

            return ResponseEntity.ok("Download started");
        }
        return ResponseEntity.badRequest().body("Haven`t google auth");
    }

    private GoogleCredential getCredentialFromResponseToken(String jwt){
        String email = jwtTokenProvider.getUserEmailFromJwt(jwt.substring(7));
        User user = elasticsearchService.findUserByEmail(email);
        if (user.getGoogleAuth()) {
            return new GoogleCredential().setFromTokenResponse(user.getGoogleTokenResponse());
        } else {
            return null;
        }
    }
}
