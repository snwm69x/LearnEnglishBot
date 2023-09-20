package com.snwm.englishbot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.snwm.englishbot.util.UserUtil;

import lombok.Data;

@Data
@Service
public class UserUtilService {
    private static final String USER_UTIL_DIR = "C:\\Users\\snwm1337\\Downloads\\englishbot\\englishbot\\src\\main\\java\\com\\snwm\\englishbot\\data\\user_util";
    private static final String USER_UTIL_FILE_EXTENSION = ".ser";

    public void saveUserUtil(UserUtil userUtil) {
        String fileName = USER_UTIL_DIR + File.separator + userUtil.getUser().getChatId() + USER_UTIL_FILE_EXTENSION;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(userUtil);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserUtil loadUserUtil(long chatId) {
        String fileName = USER_UTIL_DIR + File.separator + chatId + USER_UTIL_FILE_EXTENSION;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject();
            if (obj instanceof UserUtil) {
                UserUtil userUtil = (UserUtil) obj;
                return userUtil;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
