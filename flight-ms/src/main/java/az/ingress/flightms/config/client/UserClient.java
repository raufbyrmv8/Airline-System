package az.ingress.flightms.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "userClient", url = "${user-client.url}")
public interface UserClient {
    @GetMapping("/info/{id}")
    UserResponseDto getUserDetailsById(@PathVariable Long id);
}
