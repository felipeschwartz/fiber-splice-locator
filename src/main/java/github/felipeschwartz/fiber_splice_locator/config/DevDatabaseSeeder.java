package github.felipeschwartz.fiber_splice_locator.config;

import github.felipeschwartz.fiber_splice_locator.model.entities.*;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderPhotoRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderStatusDescriptionRepository;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Profile("dev")
public class DevDatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevDatabaseSeeder.class);

    private final UserRepository userRepository;
    private final CEORepository ceoRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderPhotoRepository serviceOrderPhotoRepository;
    private final ServiceOrderStatusDescriptionRepository serviceOrderStatusDescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDatabaseSeeder(
            UserRepository userRepository,
            CEORepository ceoRepository,
            ServiceOrderRepository serviceOrderRepository,
            ServiceOrderPhotoRepository serviceOrderPhotoRepository,
            ServiceOrderStatusDescriptionRepository serviceOrderStatusDescriptionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.ceoRepository = ceoRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.serviceOrderPhotoRepository = serviceOrderPhotoRepository;
        this.serviceOrderStatusDescriptionRepository = serviceOrderStatusDescriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping DevDatabaseSeeder.");
            return;
        }

        logger.info("Seeding development database...");

        List<User> users = seedUsers();
        List<CEO> ceos = seedCEOs();
        List<ServiceOrder> serviceOrders = seedServiceOrders(users, ceos);
        seedServiceOrderPhotos(serviceOrders);
        seedServiceOrderStatusDescriptions(serviceOrders);

        logger.info("Development database seeded successfully!");
    }

    private List<User> seedUsers() {
        User god = new User();
        god.setName("Felipe Schwartz");
        god.setEmail("god@fiberlocator.com");
        god.setPassword(passwordEncoder.encode("god123"));
        god.setRoles(Set.of("GOD_ADMIN"));
        god.setActive(true);

        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@fiberlocator.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of("ADMIN"));
        admin.setActive(true);

        User tech1 = new User();
        tech1.setName("Carlos Silva");
        tech1.setEmail("carlos.silva@fiberlocator.com");
        tech1.setPassword(passwordEncoder.encode("tech123"));
        tech1.setRoles(Set.of("FIELD_TECHNICIAN"));
        tech1.setActive(true);

        User tech2 = new User();
        tech2.setName("Mariana Souza");
        tech2.setEmail("mariana.souza@fiberlocator.com");
        tech2.setPassword(passwordEncoder.encode("tech123"));
        tech2.setRoles(Set.of("FIELD_TECHNICIAN"));
        tech2.setActive(true);

        User tech3 = new User();
        tech3.setName("João Pereira");
        tech3.setEmail("joao.pereira@fiberlocator.com");
        tech3.setPassword(passwordEncoder.encode("tech123"));
        tech3.setRoles(Set.of("FIELD_TECHNICIAN"));
        tech3.setActive(false);

        User tech4 = new User();
        tech4.setName("Ana Costa");
        tech4.setEmail("ana.costa@fiberlocator.com");
        tech4.setPassword(passwordEncoder.encode("tech123"));
        tech4.setRoles(Set.of("ADMIN"));
        tech4.setActive(true);

        return userRepository.saveAll(List.of(god, admin, tech1, tech2, tech3, tech4));
    }

    private List<CEO> seedCEOs() {
        CEO ceo1 = new CEO();
        ceo1.setBoxNumber("CEO-001");
        ceo1.setNotes("Caixa próxima ao poste 45B");
        ceo1.setAddress(buildAddress(
                "-30.034647,-51.217659", "Avenida", "Ipiranga", "1200",
                "Em frente ao posto de gasolina", "Praia de Belas", "Porto Alegre"));
        ceo1.setStatus(CEOStatus.STANDARDIZED);

        CEO ceo2 = new CEO();
        ceo2.setBoxNumber("CEO-002");
        ceo2.setNotes("Caixa subterrânea");
        ceo2.setAddress(buildAddress(
                "-30.027699,-51.229752", "Rua", "Dos Andradas", "500",
                "Ao lado da farmácia", "Centro Histórico", "Porto Alegre"));
        ceo2.setStatus(CEOStatus.DAMAGED);

        CEO ceo3 = new CEO();
        ceo3.setBoxNumber("CEO-003");
        ceo3.setNotes("Caixa aérea em poste duplo");
        ceo3.setAddress(buildAddress(
                "-30.037750,-51.212555", "Avenida", "Osvaldo Aranha", "800",
                "Próximo ao ponto de ônibus", "Bom Fim", "Porto Alegre"));
        ceo3.setStatus(CEOStatus.UNDER_MAINTENANCE);

        CEO ceo4 = new CEO();
        ceo4.setBoxNumber("CEO-004");
        ceo4.setNotes("Caixa instalada recentemente");
        ceo4.setAddress(buildAddress(
                "-30.017685,-51.180279", "Rua", "Padre Chagas", "300",
                "Em frente ao restaurante", "Moinhos de Vento", "Porto Alegre"));
        ceo4.setStatus(CEOStatus.CANCELLED);

        CEO ceo5 = new CEO();
        ceo5.setBoxNumber("CEO-005");
        ceo5.setNotes("Caixa com acesso restrito");
        ceo5.setAddress(buildAddress(
                "-30.043027,-51.220894", "Beco", "Do Salso", "45",
                "Muro lateral do prédio", "Cidade Baixa", "Porto Alegre"));
        ceo5.setStatus(CEOStatus.STANDARDIZED);

        return ceoRepository.saveAll(List.of(ceo1, ceo2, ceo3, ceo4, ceo5));
    }

    private Address buildAddress(
            String geoLocation, String addressType, String street, String streetNumber,
            String referencePoint, String neighborhood, String city
    ) {
        Address address = new Address();
        address.setGeoLocation(geoLocation);
        address.setAddressType(addressType);
        address.setStreet(street);
        address.setStreetNumber(streetNumber);
        address.setReferencePoint(referencePoint);
        address.setNeighborhood(neighborhood);
        address.setCity(city);
        return address;
    }

    private List<ServiceOrder> seedServiceOrders(List<User> users, List<CEO> ceos) {
        ServiceOrder order1 = buildServiceOrder(ceos.get(0), users.get(1), ServiceOrderStatus.OPEN, LocalDateTime.now().minusDays(5));
        ServiceOrder order2 = buildServiceOrder(ceos.get(1), users.get(2), ServiceOrderStatus.IN_PROGRESS, LocalDateTime.now().minusDays(4));
        ServiceOrder order3 = buildServiceOrder(ceos.get(2), users.get(3), ServiceOrderStatus.COMPLETED, LocalDateTime.now().minusDays(3));
        ServiceOrder order4 = buildServiceOrder(ceos.get(3), users.get(1), ServiceOrderStatus.CANCELLED, LocalDateTime.now().minusDays(2));
        ServiceOrder order5 = buildServiceOrder(ceos.get(4), users.get(4), ServiceOrderStatus.OPEN, LocalDateTime.now().minusDays(1));

        return serviceOrderRepository.saveAll(List.of(order1, order2, order3, order4, order5));
    }

    private ServiceOrder buildServiceOrder(CEO ceo, User user, ServiceOrderStatus status, LocalDateTime createdAt) {
        ServiceOrder order = new ServiceOrder();
        order.setCeo(ceo);
        order.setUser(user);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(createdAt);
        return order;
    }

    private void seedServiceOrderPhotos(List<ServiceOrder> serviceOrders) {
        List<ServiceOrderPhoto> photos = List.of(
                buildPhoto(serviceOrders.get(0), "foto1.jpg", 1),
                buildPhoto(serviceOrders.get(1), "foto2.jpg", 1),
                buildPhoto(serviceOrders.get(2), "foto3.jpg", 1),
                buildPhoto(serviceOrders.get(3), "foto4.jpg", 1),
                buildPhoto(serviceOrders.get(4), "foto5.jpg", 1)
        );

        serviceOrderPhotoRepository.saveAll(photos);
    }

    private ServiceOrderPhoto buildPhoto(ServiceOrder serviceOrder, String originalFilename, int photoOrder) {
        String storedFilename = UUID.randomUUID() + ".jpg";

        ServiceOrderPhoto photo = new ServiceOrderPhoto();
        photo.setServiceOrder(serviceOrder);
        photo.setOriginalFilename(originalFilename);
        photo.setStoredFilename(storedFilename);
        photo.setStoragePath(serviceOrder.getServiceOrderId() + "/" + storedFilename);
        photo.setContentType("image/jpeg");
        photo.setFileSize(204800L);
        photo.setPhotoOrder(photoOrder);
        photo.setCreatedAt(LocalDateTime.now());
        return photo;
    }

    private void seedServiceOrderStatusDescriptions(List<ServiceOrder> serviceOrders) {
        List<ServiceOrderStatusDescription> descriptions = List.of(
                buildStatusDescription(serviceOrders.get(0), "Ordem de serviço aberta, aguardando atendimento.", LocalDateTime.now().minusDays(5)),
                buildStatusDescription(serviceOrders.get(1), "Técnico em deslocamento até o local.", LocalDateTime.now().minusDays(4)),
                buildStatusDescription(serviceOrders.get(1), "Manutenção iniciada na caixa CEO-002.", LocalDateTime.now().minusDays(4).plusHours(2)),
                buildStatusDescription(serviceOrders.get(2), "Serviço concluído com sucesso.", LocalDateTime.now().minusDays(3)),
                buildStatusDescription(serviceOrders.get(3), "Ordem de serviço cancelada pelo cliente.", LocalDateTime.now().minusDays(2)),
                buildStatusDescription(serviceOrders.get(4), "Ordem de serviço aberta, aguardando atendimento.", LocalDateTime.now().minusDays(1))
        );

        serviceOrderStatusDescriptionRepository.saveAll(descriptions);
    }
    private ServiceOrderStatusDescription buildStatusDescription(ServiceOrder serviceOrder, String statusDescription, LocalDateTime createdAt) {
        ServiceOrderStatusDescription description = new ServiceOrderStatusDescription();
        description.setServiceOrder(serviceOrder);
        description.setStatusDescription(statusDescription);
        description.setCreatedAt(createdAt);
        description.setUpdatedAt(createdAt);
        return description;
    }
}