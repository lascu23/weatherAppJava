package com.example.weather.controller;

import com.example.weather.entity.City;
import com.example.weather.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URL;

@Controller
public class HomeController {

    private static final String apiKey = "9e2cdd8f4a024ee2fd8093fd06f46fd7";

    @GetMapping("")
    public String getHomePage(Model model){
        City city = new City();
        model.addAttribute("city", city);
        return "index";
    }

    @PostMapping("")
    public String getWeatherInfos(@ModelAttribute("city") City city, Model model){
        ObjectMapper objectMapper = new ObjectMapper();

        double lat = 0, lon = 0;

        LatAndLon latAndLon = null;
        try {
            //System.out.println(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s",city.getCity(),apiKey));
//            latAndLon = objectMapper.readValue(new URL(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s",city.getCity(),apiKey)), LatAndLon.class);
//            System.out.println(latAndLon);
            String apiUrl = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s",city.getCity(),apiKey);
            JsonNode responseJson = objectMapper.readTree(new URL(apiUrl));
            if(responseJson.isArray() && responseJson.size()>0){
                JsonNode firstCity = responseJson.get(0);
                lat = firstCity.get("lat").asDouble();
                lon = firstCity.get("lon").asDouble();
                System.out.println("Latitudine: " + lat + ", Longitudine: " + lon);

//                model.addAttribute("lat", lat);
//                model.addAttribute("lon", lon);
            }
        } catch (IOException e) {
            System.out.println("Problema la citirea din link");
            System.out.println(e.getMessage());
        }

        //https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric

        String weatherApiUrl = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric",lat,lon,apiKey);

        try{
            JsonNode jsonNode = objectMapper.readTree(new URL(weatherApiUrl));

            CityName cityName = new CityName();
            Main main = new Main();
            Weather weather = new Weather();
            Wind wind = new Wind();

            cityName.setCityName(jsonNode.get("name").asText());
            String cityNameForHtml = cityName.getCityName();

            JsonNode weatherArray = jsonNode.get("weather");
            JsonNode firstObjectFromWeatherArray = weatherArray.get(0);
            weather.setMain(firstObjectFromWeatherArray.get("main").asText());
            String mainFotHtml = weather.getMain();

            JsonNode mainObject = jsonNode.get("main");
            main.setTemp(mainObject.get("temp").asDouble());
            main.setHumidity(mainObject.get("humidity").asDouble());
            Double tempForHtml = main.getTemp();
            Double humidityForHtml = main.getHumidity();

            JsonNode windNode = jsonNode.get("wind");
            wind.setSpeed(windNode.get("speed").asDouble());
            Double speedForHtml = wind.getSpeed();

            model.addAttribute("cityName", cityNameForHtml);
            model.addAttribute("main",mainFotHtml);
            model.addAttribute("temp",tempForHtml);
            model.addAttribute("humidity",humidityForHtml);
            model.addAttribute("speed",speedForHtml);

        }catch (IOException e){
            System.out.println(e.getMessage());

        }

        return "index";
    }



}
