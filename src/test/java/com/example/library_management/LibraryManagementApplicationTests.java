package com.example.library_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryManagementApplicationTests {

	@org.springframework.beans.factory.annotation.Autowired
	private com.example.library_management.repository.OduncIslemiRepository repo;

	@Test
	void contextLoads() {
		System.out.println("DEBUG_START");
		for(com.example.library_management.model.OduncIslemi o : repo.findAll()) {
			System.out.println("ID:" + o.getId() + 
				" T:" + o.getTeslimTarihi() + 
				" GT:" + o.getGercekTeslimTarihi() + 
				" D:" + o.getDurum() + 
				" GecGun:" + o.gecikmeGunSayisi() + 
				" Ceza:" + o.cezaTutari());
		}
		System.out.println("DEBUG_END");
	}

}
