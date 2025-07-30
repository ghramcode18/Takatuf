package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.HomeService;
import geekcode.takatuf.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/get")
    public ResponseEntity<HomeResponse> getHome() {
        HomeResponse response = homeService.getHomeData();
        return ResponseEntity.ok(response);
    }

}
