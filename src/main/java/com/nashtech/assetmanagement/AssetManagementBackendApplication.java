package com.nashtech.assetmanagement;

import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@SpringBootApplication
public class AssetManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetManagementBackendApplication.class, args);
	}

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	AssetRepository assetRepository;

	@Bean
	public CommandLineRunner initData() {
		return (args) -> {
//			setUp();
		};
	}

	private void setUp() throws ParseException {
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");

		LocationEntity location1 = new LocationEntity();
		LocationEntity location2 = new LocationEntity();
		location1.setName(Location.HCM);
		location2.setName(Location.HN);
		locationRepository.save(location1);
		locationRepository.save(location2);


		RolesEntity role1 = new RolesEntity();
		RolesEntity role2 = new RolesEntity();
		role1.setName(RoleName.ROLE_ADMIN);
		role2.setName(RoleName.ROLE_STAFF);
		roleRepository.save(role1);
		roleRepository.save(role2);

		DepartmentEntity department1 = new DepartmentEntity();
		DepartmentEntity department2 = new DepartmentEntity();
		department1.setDeptCode("DEV");
		department1.setName("Developer");
		department2.setDeptCode("MAR");
		department2.setName("Marketing");
		departmentRepository.save(department1);
		departmentRepository.save(department2);


		UsersEntity user1 = new UsersEntity();
		UserDetailEntity userDetail1 = new UserDetailEntity();
		UsersEntity user2 = new UsersEntity();
		UserDetailEntity userDetail2 = new UserDetailEntity();
		user1.setRole(role1);
		userDetail1.setFirstName("Nguyen");
		userDetail1.setLastName("Kieu Trung");
		userDetail1.setGender(Gender.Male);
		userDetail1.setDateOfBirth(ft.parse("1999-02-12"));
		userDetail1.setJoinedDate(ft.parse("2021-10-12"));
		userDetail1.setEmail("nguyen@gmail.com");
		userDetail1.setLocation(location1);
		userDetail1.setState(UserState.Enable);
		userDetail1.setDepartment(department1);
		userDetail1.setUser(user1);
		user1.setUserDetail(userDetail1);
		user2.setRole(role1);
		userDetail2.setFirstName("Viet");
		userDetail2.setLastName("Van Ba");
		userDetail2.setGender(Gender.Male);
		userDetail2.setDateOfBirth(ft.parse("1999-09-04"));
		userDetail2.setJoinedDate(ft.parse("2021-10-12"));
		userDetail2.setEmail("baviet19@gmail.com");
		userDetail2.setLocation(location1);
		userDetail2.setState(UserState.Enable);
		userDetail2.setDepartment(department1);
		userDetail2.setUser(user2);
		user2.setUserDetail(userDetail2);
		userRepository.save(user1);
		userRepository.save(user2);


		CategoryEntity category1 = new CategoryEntity();
		category1.setPrefix("LA");
		category1.setName("Laptop");
		categoryRepository.save(category1);


		AssetEntity asset1 = new AssetEntity();
		asset1.setAssetName("Dell Latitude 7480");
		asset1.setSpecification("I5 7300U, 8GB, 256GB");
		asset1.setState(AssetState.AVAILABLE);
		asset1.setInstalledDate(ft.parse("2021-10-12"));
		asset1.setCategoryEntity(category1);
		asset1.setLocation(location1);
		assetRepository.save(asset1);


	}
}
