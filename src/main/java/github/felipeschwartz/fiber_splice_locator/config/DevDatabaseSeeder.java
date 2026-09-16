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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Component
@Profile("dev")
public class DevDatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevDatabaseSeeder.class);

    private static final int TOTAL_CEOS = 500;

    private static final String[] STREET_TYPES = {"Rua", "Avenida", "Travessa", "Alameda"};

    private static final String[] STREET_NAMES = {
            "Ipiranga", "Osvaldo Aranha", "Independência", "Dos Andradas", "Assis Brasil",
            "Protásio Alves", "Padre Chagas", "Carlos Gomes", "General Câmara", "Marechal Floriano Peixoto",
            "Cristóvão Colombo", "Fernandes Vieira", "João Alfredo", "Getúlio Vargas", "Riachuelo",
            "Bento Gonçalves", "Voluntários da Pátria", "Loureiro da Silva", "Vinte de Setembro", "Praia de Belas",
            "Doutor Barcelos", "Wenceslau Escobar", "Coronel Bordini", "Goethe", "Félix da Cunha",
            "José do Patrocínio", "Sarmento Leite", "João Pessoa", "Botafogo", "Silva Só"
    };

    private static final String[] NEIGHBORHOODS = {
            "Centro Histórico", "Cidade Baixa", "Bom Fim", "Moinhos de Vento", "Petrópolis",
            "Menino Deus", "Praia de Belas", "Cristal", "Tristeza", "Ipanema",
            "Vila Assunção", "Jardim Botânico", "Partenon", "Santana", "Rio Branco",
            "Auxiliadora", "Higienópolis", "São Geraldo", "Navegantes", "Floresta",
            "Farroupilha", "Azenha", "Bela Vista", "Chácara das Pedras", "Três Figueiras", "Passo da Areia"
    };

    private static final String[] REFERENCE_POINTS = {
            "Próximo à praça", "Em frente à escola", "Ao lado do mercado", "Próximo ao ponto de ônibus",
            "Esquina com a farmácia", "Em frente à igreja", "Próximo ao posto de gasolina", "Ao lado da padaria",
            "Em frente ao supermercado", "Próximo à quadra de esportes", "Ao lado da academia", "Em frente ao posto de saúde"
    };

    private static final String[] CEO_NOTES = {
            "Caixa aérea em poste simples", "Caixa aérea em poste duplo", "Caixa subterrânea",
            "Caixa em muro lateral", "Instalação recente", "Caixa com acesso restrito",
            "Caixa próxima a cruzamento movimentado", "Caixa em condomínio residencial"
    };

    // Coordenadas centradas em Porto Alegre/RS, com variação suficiente pra
    // cobrir a região metropolitana sem sair muito da cidade.
    private static final double BASE_LATITUDE = -30.0346;
    private static final double BASE_LONGITUDE = -51.2177;

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
        tech4.setRoles(Set.of("FIELD_TECHNICIAN"));
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

        List<CEO> ceos = new ArrayList<>(List.of(ceo1, ceo2, ceo3, ceo4, ceo5));
        // CEO-002 (DAMAGED) e CEO-003 (UNDER_MAINTENANCE) acima já contam pra cota de 1%;
        // as 495 geradas aqui completam os 500 no total, com mais 3 nesses status.
        ceos.addAll(generateAdditionalCEOs(6, TOTAL_CEOS, 3));

        return ceoRepository.saveAll(ceos);
    }

    private List<CEO> generateAdditionalCEOs(int fromBoxNumber, int toBoxNumberInclusive, int extraDamagedOrUnderMaintenance) {
        int count = toBoxNumberInclusive - fromBoxNumber + 1;
        Random random = new Random(42);

        Set<Integer> damagedOrMaintenanceIndexes = new LinkedHashSet<>();
        while (damagedOrMaintenanceIndexes.size() < extraDamagedOrUnderMaintenance) {
            damagedOrMaintenanceIndexes.add(random.nextInt(count));
        }

        List<CEO> ceos = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            CEO ceo = new CEO();
            ceo.setBoxNumber(String.format("CEO-%03d", fromBoxNumber + i));
            ceo.setNotes(CEO_NOTES[random.nextInt(CEO_NOTES.length)]);
            ceo.setAddress(buildRandomAddress(random));
            ceo.setStatus(damagedOrMaintenanceIndexes.contains(i)
                    ? (i % 2 == 0 ? CEOStatus.DAMAGED : CEOStatus.UNDER_MAINTENANCE)
                    : CEOStatus.STANDARDIZED);
            ceos.add(ceo);
        }
        return ceos;
    }

    private Address buildRandomAddress(Random random) {
        String addressType = STREET_TYPES[random.nextInt(STREET_TYPES.length)];
        String street = STREET_NAMES[random.nextInt(STREET_NAMES.length)];
        String streetNumber = String.valueOf(100 + random.nextInt(9900));
        String neighborhood = NEIGHBORHOODS[random.nextInt(NEIGHBORHOODS.length)];
        String referencePoint = REFERENCE_POINTS[random.nextInt(REFERENCE_POINTS.length)];

        double latitude = BASE_LATITUDE + (random.nextDouble() - 0.5) * 0.12;
        double longitude = BASE_LONGITUDE + (random.nextDouble() - 0.5) * 0.14;
        String geoLocation = String.format(Locale.US, "%.6f,%.6f", latitude, longitude);

        return buildAddress(geoLocation, addressType, street, streetNumber, referencePoint, neighborhood, "Porto Alegre");
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