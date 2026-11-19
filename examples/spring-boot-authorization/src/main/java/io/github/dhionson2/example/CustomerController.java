package io.github.dhionson2.example;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

record Customer(String id, String owner, String name) {}
@RestController @RequestMapping("/clientes")
class CustomerController {
    private final boolean vulnerable;
    CustomerController(org.springframework.core.env.Environment env){this.vulnerable=env.acceptsProfiles(org.springframework.core.env.Profiles.of("vulnerable"));}
    @GetMapping("/{id}") ResponseEntity<Customer> get(@PathVariable("id") String id, Authentication authentication){
        String user=authentication==null?null:authentication.getName();
        if(user==null||user.isBlank())return ResponseEntity.status(401).build();
        Customer customer=switch(id){case "a"->new Customer("a","alice","Alice Customer");case "b"->new Customer("b","bob","Bob Customer");default->null;};
        if(customer==null)return ResponseEntity.notFound().build();
        if(!vulnerable&&!customer.owner().equals(user))return ResponseEntity.notFound().build();
        return ResponseEntity.ok(customer);
    }
    @GetMapping("/admin") @PreAuthorize("hasRole('ADMIN')") String adminOnly(){return "admin";}
}
