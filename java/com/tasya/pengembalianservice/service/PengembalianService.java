/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tasya.pengembalianservice.service;

import com.tasya.pengembalianservice.VO.Peminjaman;
import com.tasya.pengembalianservice.repository.PengembalianRepository;
import com.tasya.pengembalianservice.VO.ResponseTemplateVO;
import com.tasya.pengembalianservice.VO.ResponseTemplateVOPinjam;
import com.tasya.pengembalianservice.entity.Pengembalian;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author user
 */

@Service
public class PengembalianService {
    
    SimpleDateFormat formatTanggal = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private PengembalianRepository pengembalianRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Pengembalian savePengembalian(Pengembalian pengembalian) throws ParseException{
        Peminjaman peminjaman = 
                restTemplate.getForObject("http://localhost:9009/peminjaman/"
                +pengembalian.getPeminjamanId(), Peminjaman.class);
        String tglsekarang = formatTanggal.format(new Date());
        long terlambat = kurangTanggal(tglsekarang, peminjaman.getTglKembali());
        double denda = terlambat * 500;
        pengembalian.setTerlambat((int)terlambat);
        pengembalian.setDenda(denda);
        return pengembalianRepository.save(pengembalian);
    }
    

    private int kurangTanggal(String tglAwal, String tglAkhir) throws ParseException {
            Date tgl1 = formatTanggal.parse(tglAwal);
            Date tgl2 = formatTanggal.parse(tglAkhir);
            long selisih = tgl1.getTime() - tgl2.getTime();
            long selisihHari = selisih / (24*60*60*1000);
            return (int) selisihHari;
    }
    
    public ResponseTemplateVO getPengembalian(Long pengembalianId){
            ResponseTemplateVO vo = new ResponseTemplateVO();
        Pengembalian pengembalian =
                pengembalianRepository.findByPengembalianId(pengembalianId);
        Peminjaman peminjaman =
        restTemplate.getForObject("http://localhost:9005/peminjaman/"
                + pengembalian.getPeminjamanId(), Peminjaman.class);
        vo.setPeminjaman(peminjaman);
        vo.setPengembalian(pengembalian);
        return vo;
    }
}
