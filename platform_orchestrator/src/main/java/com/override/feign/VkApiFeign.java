package com.override.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="vkApiFeignClient", url="https://api.vk.com")
public interface VkApiFeign {

    @GetMapping("/method/calls.start?access_token={accessToken}&v={versionVkApi}")
    ResponseEntity<String> createCall(@PathVariable String accessToken, @PathVariable String versionVkApi);
}
