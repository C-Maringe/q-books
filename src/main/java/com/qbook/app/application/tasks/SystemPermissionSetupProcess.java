package com.qbook.app.application.tasks;

import com.qbook.app.domain.models.Permission;
import com.qbook.app.domain.models.PermissionFeature;
import com.qbook.app.domain.repository.PermissionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class SystemPermissionSetupProcess  implements CommandLineRunner {
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        if(permissionRepository.count() > 0L) {
            log.info("System permissions already loaded.");
        } else {
            log.info("Starting process to add system permissions.");
            Permission permission = new Permission();
            permission.setPermissionFeature(PermissionFeature.ANALYTICS);

            permissionRepository.save(permission);

            Permission bookings = new Permission();
            bookings.setPermissionFeature(PermissionFeature.BOOKINGS);

            permissionRepository.save(bookings);

            Permission configurations = new Permission();
            configurations.setPermissionFeature(PermissionFeature.CONFIGURATIONS);

            permissionRepository.save(configurations);

            Permission employees = new Permission();
            employees.setPermissionFeature(PermissionFeature.EMPLOYEES);

            permissionRepository.save(employees);

            Permission marketing = new Permission();
            marketing.setPermissionFeature(PermissionFeature.MARKETING);

            permissionRepository.save(marketing);

            Permission reporting = new Permission();
            reporting.setPermissionFeature(PermissionFeature.REPORTING);

            permissionRepository.save(reporting);

            Permission schedule = new Permission();
            schedule.setPermissionFeature(PermissionFeature.SCHEDULE);

            permissionRepository.save(schedule);

            Permission sales = new Permission();
            sales.setPermissionFeature(PermissionFeature.SALES);

            permissionRepository.save(sales);

            Permission treatments = new Permission();
            treatments.setPermissionFeature(PermissionFeature.TREATMENTS);

            permissionRepository.save(treatments);

            Permission clients = new Permission();
            clients.setPermissionFeature(PermissionFeature.CLIENT_MANAGEMENT);

            permissionRepository.save(clients);

            Permission products = new Permission();
            products.setPermissionFeature(PermissionFeature.PRODUCTS);

            permissionRepository.save(products);

            Permission goals = new Permission();
            goals.setPermissionFeature(PermissionFeature.GOALS);

            permissionRepository.save(goals);
            log.info("Completed registering permissions.");
        }
    }
}
