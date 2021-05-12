package kr.ac.ajou.cybersecurity.capstone5.monitorassistant.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.adapter.IntelligenceAdapter;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.config.JwtTokenUtil;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.entities.IntelligenceEntity;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.entities.UserEntity;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.repositories.IntelligenceRepository;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.repositories.UserRepository;
import kr.ac.ajou.cybersecurity.capstone5.monitorassistant.response.IntelligenceResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Tag(name = "intelligences", description = "첩보 API")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class IntelligenceController {

    @Autowired
    private IntelligenceRepository intelligenceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/intelligences")
    @Transactional
    public List<IntelligenceEntity> all() {
        return intelligenceRepository.findAll();
    }

    @PostMapping("/intelligences")
    @Transactional(readOnly = true)
    public IntelligenceResponse save(@RequestBody IntelligenceEntity entity, HttpServletRequest req) {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        String str = req.getHeader("Authorization");
        if(str.startsWith("Bearer ")) {
            str = str.substring(7);
        }
        String email = jwtTokenUtil.getUsernameFromToken(str);
        System.out.println(email);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        System.out.println(user);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        entity.setUserEntity(user.get());
        System.out.println(entity.getUserEntity());
        intelligenceRepository.save(entity);
        System.out.println(entity.getContent());
        return IntelligenceAdapter.intelligenceResponse(entity, null);
    }

    @GetMapping("/intelligences/{uid}")
    @Transactional
    public IntelligenceEntity findOne(@PathVariable String uid) {
        return intelligenceRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("No data"));
    }

    @DeleteMapping("/intelligences/{uid}")
    @Transactional
    public ResponseEntity<?> deleteOne(@PathVariable String uid) {
        Optional<IntelligenceEntity> targetEntity = intelligenceRepository.deleteByUid(uid);
        return ResponseEntity.ok(targetEntity);
    }
}
