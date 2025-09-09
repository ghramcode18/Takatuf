package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@AuthenticationPrincipal UserDetails userDetails,@RequestBody Map<String, String> body) {
        String question = body.get("question");
        String answer = geminiService.getAnswer(question);
        return ResponseEntity.ok(answer);
    }
}
