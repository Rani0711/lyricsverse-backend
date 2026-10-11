package com.lyricsverse.backend;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "http://localhost:5173")
public class SongController {

    private final SongRepository songRepository;

    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public record SongRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 150) String artist,
        @NotBlank @Size(max = 80) String language,
        @NotBlank @Size(max = 100000) String lyrics
    ) {}

    @GetMapping
    public List<Song> getSongs() {
        return songRepository.findAll();
    }

    @GetMapping("/{id}")
    public Song getSong(@PathVariable Long id) {
        return findSong(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Song addSong(@Valid @RequestBody SongRequest request) {
        Song song = new Song();
        updateSongFields(song, request);
        return songRepository.save(song);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Song updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequest request) {

        Song song = findSong(id);
        updateSongFields(song, request);

        return songRepository.save(song);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSong(@PathVariable Long id) {
        Song song = findSong(id);
        songRepository.delete(song);
    }

    private Song findSong(Long id) {
        return songRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Song not found"));
    }

    private void updateSongFields(Song song, SongRequest request) {
        song.setTitle(request.title().trim());
        song.setArtist(request.artist().trim());
        song.setLanguage(request.language().trim());
        song.setLyrics(request.lyrics().trim());
    }
}

