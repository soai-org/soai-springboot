package com.team1.soai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;


@Service
public class SegmentationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public String getSegmentationImage(String instanceUuid) {
        try {
            String url = fastApiUrl + "/image/segmentation";
            // 요청 Body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("instanceUUID", instanceUuid);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // FASTAPI 호출
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );

            // Base64 변환
            String base64Image = Base64.getEncoder().encodeToString(response.getBody());
            return "data:image/png;base64," + base64Image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getSegmentationArray(String instanceUuid) {
        try {
            String url = fastApiUrl + "/image/segmentation_array";
            // 요청 JSON
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("instanceUUID", instanceUuid);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // POST 요청
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                throw new RuntimeException("No data returned from server");
            }

            String hexData = (String) body.get("data");

            // hex → byte[]
            byte[] bytes = hexStringToByteArray(hexData);

            // byte[] → int[][] 배열
            int[][] arr = new int[512][512];
            for (int i = 0; i < 512; i++) {
                for (int j = 0; j < 512; j++) {
                    arr[i][j] = Byte.toUnsignedInt(bytes[i * 512 + j]);
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("data", arr);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // helper: hex string → byte[]
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
//
//    public void saveMaskAsImage(int[][] mask, int height, int width, String filePath) throws Exception {
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int value = mask[y][x] & 0xFF;
//                int rgb = (value << 16) | (value << 8) | value; // grayscale
//                img.setRGB(x, y, rgb);
//            }
//        }
//        ImageIO.write(img, "png", new File(filePath));
//    }
