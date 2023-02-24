package com.qbook.app.utilities.factory;

import com.qbook.app.application.models.UserPermissions;
import com.qbook.app.application.models.employeeModels.NewEmployeeModel;
import com.qbook.app.application.models.productModels.NewProductModel;
import com.qbook.app.application.models.treatmentModels.NewTreatmentModel;
import com.qbook.app.domain.models.*;
import org.bson.types.ObjectId;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Factory {

	public static Employee buildNewEmployee(NewEmployeeModel newEmployeeModel) {
		StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();;
		Employee e = new Employee();
		e.setContactDetails(newEmployeeModel.getContactDetails());
		e.setEmployeeLevel(newEmployeeModel.getEmployeeLevel());
		e.setUsername(newEmployeeModel.getContactDetails().getEmailAddress());
		e.setFirstName(newEmployeeModel.getFirstName().trim());
		e.setLastName(newEmployeeModel.getLastName().trim());
		e.setMustBookConsultationFirstTime(newEmployeeModel.isMustBookConsultationFirstTime());
		e.setPassword(strongPasswordEncryptor.encryptPassword(newEmployeeModel.getPassword().trim()));
		e.setDateRegistered(new DateTime().getMillis());
		e.setRole("employee");
		e.setUserPermissionList(setupUserPermissionsFromModel(newEmployeeModel.getUserPermissions()));
		e.setActive(true);

		return e;
	}

	public static List<UserPermission> setupUserPermissionsFromModel(UserPermissions userPermissions) {
		List<UserPermission> userPermissionList = new ArrayList<>();

		UserPermission permission = new UserPermission();
		permission.setPermissionFeature(PermissionFeature.ANALYTICS.getName());
		permission.setCanRead(userPermissions.getAnalytics().isCanRead());
		permission.setCanWrite(userPermissions.getAnalytics().isCanWrite());
		userPermissionList.add(permission);

		UserPermission bookings = new UserPermission();
		bookings.setPermissionFeature(PermissionFeature.BOOKINGS.getName());
		bookings.setCanRead(userPermissions.getBookings().isCanRead());
		bookings.setCanWrite(userPermissions.getBookings().isCanWrite());
		userPermissionList.add(bookings);

		UserPermission clients = new UserPermission();
		clients.setPermissionFeature(PermissionFeature.CLIENT_MANAGEMENT.getName());
		clients.setCanRead(userPermissions.getClients().isCanRead());
		clients.setCanWrite(userPermissions.getClients().isCanWrite());
		userPermissionList.add(clients);

		UserPermission configurations = new UserPermission();
		configurations.setPermissionFeature(PermissionFeature.CONFIGURATIONS.getName());
		configurations.setCanRead(userPermissions.getConfigurations().isCanRead());
		configurations.setCanWrite(userPermissions.getConfigurations().isCanWrite());
		userPermissionList.add(configurations);

		UserPermission employees = new UserPermission();
		employees.setPermissionFeature(PermissionFeature.EMPLOYEES.getName());
		employees.setCanRead(userPermissions.getEmployees().isCanRead());
		employees.setCanWrite(userPermissions.getEmployees().isCanWrite());
		userPermissionList.add(employees);

		UserPermission marketing = new UserPermission();
		marketing.setPermissionFeature(PermissionFeature.MARKETING.getName());
		marketing.setCanRead(userPermissions.getMarketing().isCanRead());
		marketing.setCanWrite(userPermissions.getMarketing().isCanWrite());
		userPermissionList.add(marketing);

		UserPermission reporting = new UserPermission();
		reporting.setPermissionFeature(PermissionFeature.REPORTING.getName());
		reporting.setCanRead(userPermissions.getReporting().isCanRead());
		reporting.setCanWrite(userPermissions.getReporting().isCanWrite());
		userPermissionList.add(reporting);

		UserPermission schedule = new UserPermission();
		schedule.setPermissionFeature(PermissionFeature.SCHEDULE.getName());
		schedule.setCanRead(userPermissions.getSchedule().isCanRead());
		schedule.setCanWrite(userPermissions.getSchedule().isCanWrite());
		userPermissionList.add(schedule);

		UserPermission sales = new UserPermission();
		sales.setPermissionFeature(PermissionFeature.SALES.getName());
		sales.setCanRead(userPermissions.getSales().isCanRead());
		sales.setCanWrite(userPermissions.getSales().isCanWrite());
		userPermissionList.add(sales);

		UserPermission treatments = new UserPermission();
		treatments.setPermissionFeature(PermissionFeature.TREATMENTS.getName());
		treatments.setCanRead(userPermissions.getTreatments().isCanRead());
		treatments.setCanWrite(userPermissions.getTreatments().isCanWrite());
		userPermissionList.add(treatments);

		UserPermission products = new UserPermission();
		products.setPermissionFeature(PermissionFeature.PRODUCTS.getName());
		products.setCanRead(userPermissions.getProducts().isCanRead());
		products.setCanWrite(userPermissions.getProducts().isCanWrite());
		userPermissionList.add(products);

		UserPermission goals = new UserPermission();
		goals.setPermissionFeature(PermissionFeature.GOALS.getName());
		goals.setCanRead(userPermissions.getProducts().isCanRead());
		goals.setCanWrite(userPermissions.getProducts().isCanWrite());
		userPermissionList.add(goals);

		return userPermissionList;
	}

	public static Product buildProduct(NewProductModel productModel){
		ModelMapper modelMapper = new ModelMapper();
		Product product = modelMapper.map(productModel, Product.class);
		product.setId(new ObjectId());
		return product;
	}
	public static String createRandomCode(){
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZWabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new SecureRandom();
		for (int i = 0; i < 8; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}
}
