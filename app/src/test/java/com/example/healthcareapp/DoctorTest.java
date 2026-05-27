package com.example.healthcareapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class DoctorTest {

    @Test
    public void testDoctorConstructorAndGetters() {
        Doctor doctor = new Doctor("Nguyễn Văn A", "Tim mạch", "Hà Nội", "0123456789", 500000, "Sáng");
        
        assertEquals("Nguyễn Văn A", doctor.getName());
        assertEquals("Tim mạch", doctor.getSpecialty());
        assertEquals("Hà Nội", doctor.getAddress());
        assertEquals("0123456789", doctor.getPhone());
        assertEquals(500000, doctor.getFee(), 0.001);
        assertEquals("Sáng", doctor.getShift());
    }

    @Test
    public void testDoctorSetters() {
        Doctor doctor = new Doctor("Tên cũ", "Khoa cũ", "Địa chỉ cũ", "000", 0, "Chiều");

        doctor.setId(10);
        doctor.setShift("Cả ngày");
        
        assertEquals(10, doctor.getId());
        assertEquals("Cả ngày", doctor.getShift());
    }
}
