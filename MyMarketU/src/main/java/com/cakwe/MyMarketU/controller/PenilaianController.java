
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Penilaian;
import com.cakwe.MyMarketU.repository.PenilaianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penilaian")
public class PenilaianController {

    @Autowired
    private PenilaianRepository penilaianRepository;

    @PostMapping("/add")
    public ResponseEntity<Penilaian> addPenilaian(@RequestBody Penilaian penilaian) {
        Penilaian savedPenilaian = penilaianRepository.save(penilaian);
        return ResponseEntity.ok(savedPenilaian);
    }

    @GetMapping("/produk/{idProduk}")
    public ResponseEntity<List<Penilaian>> getByProduk(@PathVariable Long idProduk) {
        List<Penilaian> penilaianList = penilaianRepository.findByIdProduk(idProduk);
        return ResponseEntity.ok(penilaianList);
    }

    @GetMapping("/pelanggan/{idPelanggan}")
    public ResponseEntity<List<Penilaian>> getByPelanggan(@PathVariable Long idPelanggan) {
        List<Penilaian> penilaianList = penilaianRepository.findByIdPelanggan(idPelanggan);
        return ResponseEntity.ok(penilaianList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePenilaian(@PathVariable int id) {
        penilaianRepository.deleteById(id);
        return ResponseEntity.ok("Penilaian berhasil dihapus.");
    }
}


