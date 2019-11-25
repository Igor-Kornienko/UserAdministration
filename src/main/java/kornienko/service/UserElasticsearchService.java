package kornienko.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kornienko.model.UserRole;
import kornienko.util.GoogleTokenResponseDeserializer;
import kornienko.util.UserDeserializer;
import kornienko.model.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserElasticsearchService {
    private TransportClient client;

    @Value("classpath:elasticsearchMapper/users-index.json")
    private Resource index;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(GoogleTokenResponse.class, new GoogleTokenResponseDeserializer())
            .registerTypeAdapter(User.class, new UserDeserializer())
            .create();

    public UserElasticsearchService() throws IOException {
        client = new PreBuiltTransportClient(
                Settings.builder()
                        .put("client.transport.sniff", true)
                        .put("cluster.name", "elasticsearch")
                        .build())
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //client.admin().indices().prepareDelete("users").execute().actionGet();
        IndicesExistsResponse existResponse = client.admin().indices().prepareExists("users").execute().actionGet();

        if (!existResponse.isExists()) {
            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                InputStream input = new FileInputStream(new File("src/main/resources/elasticsearchMapper/users-index.json"));
                IOUtils.copy(input, out);
                String text = new String(out.toByteArray(), StandardCharsets.UTF_8);
                final CreateIndexResponse response = client
                        .admin()
                        .indices()
                        .prepareCreate("users")
                        .setSource(text, XContentType.JSON)
                        .setTimeout(TimeValue.timeValueSeconds(1))
                        .get(TimeValue.timeValueSeconds(2));
            }
        }
    }


    public void checkBaseAccs() throws InterruptedException {
        if (userExist("guest") == null) {
            User user = new User();
            user.setEmail("guest");
            user.setPassHash(passwordEncoder.encode("guest"));
            user.setName("guest");
            user.setGoogleAuth(false);
            user.setRole(UserRole.GUEST.name().toLowerCase());

            saveUser(user);
            System.out.println("Created guest account");
        }

        if (userExist("admin") == null) {
            User user = new User();
            user.setEmail("admin");
            user.setPassHash(passwordEncoder.encode("admin"));
            user.setName("admin");
            user.setGoogleAuth(false);
            user.setRole(UserRole.ADMIN.name().toLowerCase());

            saveUser(user);
            System.out.println("Created admin account");
        }
    }

    public void saveUser(User user) throws InterruptedException {
        String jsonBuff = gson.toJson(user);
        IndexResponse response = client.prepareIndex("users", "userInfo")
                .setSource(jsonBuff, XContentType.JSON).get();

        System.out.print("Id: " + response.getId());
        System.out.print("Index: " + response.getIndex());
        System.out.print("Type: " + response.getType());
        System.out.print("Version: " + response.getVersion());
        System.out.println();

        Thread.sleep(1000);
    }

    public void updateUser(String index, GoogleTokenResponse googleTokenResponse) {
        User buff = gson.fromJson(client.prepareGet("users", "userInfo", index).get().getSourceAsString(), User.class);
        buff.setGoogleTokenResponse(googleTokenResponse);
        client.prepareUpdate("users", "userInfo", index)
                .setDoc(gson.toJson(buff), XContentType.JSON)
                .setRetryOnConflict(5)
                .execute()
                .actionGet();
    }

    public User findUserByEmail(String email) {
        SearchResponse response = client.prepareSearch("users")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("email", email))
                .get();

        if (response.getHits().getHits().length > 0) {
            return gson.fromJson(response.getHits().getHits()[0].getSourceAsString(), User.class);
        }
        return null;
    }

    public String userExist(String email) {
        SearchResponse response = client.prepareSearch("users")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("email", email))
                .get();

        if (response.getHits().getHits().length > 0) {
            return response.getHits().getHits()[0].getId();
        }
        return null;
    }

    public Map<String, User> searchAllTestData() {
        SearchResponse response = client.prepareSearch().setSize(1000).execute().actionGet();
        List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
        Map<String, User> users = new HashMap<>();
        searchHits.forEach(hit -> {
            users.put(hit.getId(), gson.fromJson(hit.getSourceAsString(), User.class));
        });
        return users;
    }

    public void deleteData() {
        client.admin().indices().delete(new DeleteIndexRequest("users")).actionGet();
    }

    public void deleteByEmail(String email){
        String id = userExist(email);
        if (id != null) {
            client.prepareDelete("users", "userInfo", id).get();
        }
    }
}