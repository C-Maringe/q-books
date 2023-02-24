package com.qbook.app.application.tasks;

import com.qbook.app.application.models.UserPermissions;
import com.qbook.app.application.models.employeeModels.NewEmployeeModel;
import com.qbook.app.application.models.userPermissionsModels.*;
import com.qbook.app.domain.models.ContactDetails;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.EmployeeType;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.EmployeeTypeRepository;
import com.qbook.app.utilities.factory.Factory;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SuperAdminSetupProcess implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final EmployeeTypeRepository employeeTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        if(!employeeRepository.findByUsername("nailsbyzahn.info@gmail.com").isPresent()) {
            registerSuperAdmin();
        }
    }

    private void registerSuperAdmin() {
        // register super admin
        NewEmployeeModel newEmployeeModel = new NewEmployeeModel();
        newEmployeeModel.setFirstName("Leezahn");
        newEmployeeModel.setLastName("Petersen");
        newEmployeeModel.setPassword("Ut634ybv");
        newEmployeeModel.setClearPassword("Ut634ybv");
        newEmployeeModel.setEmployeeLevel("Senior");
        ContactDetails contactDetails = new ContactDetails("nailsbyzahn.info@gmail.com", "0670574447");
        newEmployeeModel.setContactDetails(contactDetails);
        newEmployeeModel.setEmployeeType("Nail Technician");
        newEmployeeModel.setMustBookConsultationFirstTime(false);

        newEmployeeModel.setUserPermissions(buildSuperAdminPermissions());

        final Employee employee = Factory.buildNewEmployee(newEmployeeModel);
        employee.setRole("admin");
        final Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(newEmployeeModel.getEmployeeType());

        if (!employeeTypeOptional.isPresent()){
            EmployeeType employeeType = new EmployeeType();
            employeeType.setEmployeeType(newEmployeeModel.getEmployeeType());

            employeeTypeRepository.save(employeeType);
            employee.setEmployeeType(employeeType);
        }
        else {
            employee.setEmployeeType(employeeTypeOptional.get());
        }

        employeeRepository.save(employee);
    }

    private UserPermissions buildSuperAdminPermissions() {
        UserPermissions userPermissions = new UserPermissions();

        Analytics analytics = new Analytics();
        analytics.setCanRead(true);
        analytics.setCanWrite(true);
        userPermissions.setAnalytics(analytics);

        Bookings bookings = new Bookings();
        bookings.setCanRead(true);
        bookings.setCanWrite(true);
        userPermissions.setBookings(bookings);

        Clients clients = new Clients();
        clients.setCanRead(true);
        clients.setCanWrite(true);
        userPermissions.setClients(clients);

        Configurations configurations = new Configurations();
        configurations.setCanRead(true);
        configurations.setCanWrite(true);
        userPermissions.setConfigurations(configurations);

        Employees employees = new Employees();
        employees.setCanRead(true);
        employees.setCanWrite(true);
        userPermissions.setEmployees(employees);

        Marketing marketing = new Marketing();
        marketing.setCanRead(true);
        marketing.setCanWrite(true);
        userPermissions.setMarketing(marketing);

        Reporting reporting = new Reporting();
        reporting.setCanRead(true);
        reporting.setCanWrite(true);
        userPermissions.setReporting(reporting);

        Sales sales = new Sales();
        sales.setCanRead(true);
        sales.setCanWrite(true);
        userPermissions.setSales(sales);

        Schedule schedule = new Schedule();
        schedule.setCanRead(true);
        schedule.setCanWrite(true);
        userPermissions.setSchedule(schedule);

        Treatments treatments = new Treatments();
        treatments.setCanRead(true);
        treatments.setCanWrite(true);
        userPermissions.setTreatments(treatments);

        Products products = new Products();
        products.setCanRead(true);
        products.setCanWrite(true);
        userPermissions.setProducts(products);

        return userPermissions;
    }
}
