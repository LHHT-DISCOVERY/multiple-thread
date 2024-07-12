package com.example.multiple_thread.service;

import com.example.multiple_thread.entity.User;
import com.example.multiple_thread.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile multipartFile) throws Exception {
        long start = System.currentTimeMillis();
        List<User> users = parseCVSFile(multipartFile);
        logger.info("saving list of users of size {} {}", users.size(), Thread.currentThread().getName());
        userRepository.saveAll(users);
        long end = System.currentTimeMillis();
        logger.info("Total time {}", (end - start)     );
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers() {
        logger.info("get list of user by " + Thread.currentThread().getName());
        List<User> users = userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }


    private List<User> parseCVSFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("name-file");
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] values = line.split(",");
                    if (values.length == 4) {
                        final User user = User.builder()
                                .name(values[1])
                                .email(values[2])
                                .gender(values[3])
                                .build();
                        users.add(user);
                    } else {
                        throw new RuntimeException("Invalid CSV format, expected 4 columns: " + line);
                    }
                }
                return users;
            }

        } catch (final IOException e) {
            throw new RuntimeException("Could not read CSV file: " + file.getOriginalFilename());
        }
    }


}
