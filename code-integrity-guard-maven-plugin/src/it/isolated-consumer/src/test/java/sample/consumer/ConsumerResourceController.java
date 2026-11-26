package sample.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class ConsumerResourceController {
    @GetMapping("/resource/{id}")
    Object read(@PathVariable("id") String id, @RequestHeader("X-Actor") String actor) {
        if (!"bob".equals(actor)) return ResponseEntity.notFound().build();
        return Map.of("id", id, "owner", "bob");
    }
}
