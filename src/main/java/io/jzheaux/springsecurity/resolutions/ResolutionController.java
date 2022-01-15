package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
    private final ResolutionRepository resolutions;

    public ResolutionController(ResolutionRepository resolutions) {
        this.resolutions = resolutions;
    }

    @GetMapping("/resolutions")
    @PreAuthorize("hasAnyAuthority('resolution:read')")
    @PostFilter("@post.filter(#root)")
    public Iterable<Resolution> read() {
        return this.resolutions.findAll();
    }

    @GetMapping("/resolution/{id}")
    @PreAuthorize("hasAnyAuthority('resolution:read')")
    @PostAuthorize("@post.authorize(#root)")
    public Optional<Resolution> read(@PathVariable("id") UUID id) {
        return this.resolutions.findById(id);
    }

    @PostMapping("/resolution")
    @PreAuthorize("hasAnyAuthority('resolution:write')")
    public Resolution make(@CurrentUsername String owner, @RequestBody String text) {
        System.out.println(owner);
        Resolution resolution = new Resolution(text, owner);
        return this.resolutions.save(resolution);
    }

    @PutMapping(path = "/resolution/{id}/revise")
    @PreAuthorize("hasAnyAuthority('resolution:write')")
    @PostAuthorize("@post.authorize(#root)")
    @Transactional
    public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
        this.resolutions.revise(id, text);
        return read(id);
    }

    @PutMapping("/resolution/{id}/complete")
    @PreAuthorize("hasAnyAuthority('resolution:write')")
    @PostAuthorize("@post.authorize(#root)")
    @Transactional
    public Optional<Resolution> complete(@PathVariable("id") UUID id) {
        this.resolutions.complete(id);
        return read(id);
    }
}
